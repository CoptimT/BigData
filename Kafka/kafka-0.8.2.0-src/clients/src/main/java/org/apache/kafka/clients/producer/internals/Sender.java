/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.kafka.clients.producer.internals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.ClientRequest;
import org.apache.kafka.clients.ClientResponse;
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InvalidMetadataException;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.metrics.Measurable;
import org.apache.kafka.common.metrics.MetricConfig;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.common.metrics.stats.Avg;
import org.apache.kafka.common.metrics.stats.Max;
import org.apache.kafka.common.metrics.stats.Rate;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.ProduceRequest;
import org.apache.kafka.common.requests.ProduceResponse;
import org.apache.kafka.common.requests.RequestSend;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The background thread that handles the sending of produce requests to the Kafka cluster. This thread makes metadata
 * requests to renew its view of the cluster and then sends produce requests to the appropriate nodes.
 */
public class Sender implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    /* the state of each nodes connection */
    private final KafkaClient client;

    /* the record accumulator that batches records */
    private final RecordAccumulator accumulator;

    /* the metadata for the client */
    private final Metadata metadata;

    /* the maximum request size to attempt to send to the server */
    private final int maxRequestSize;

    /* the number of acknowledgements to request from the server */
    private final short acks;

    /* the max time in ms for the server to wait for acknowlegements */
    private final int requestTimeout;

    /* the number of times to retry a failed request before giving up */
    private final int retries;

    /* the clock instance used for getting the time */
    private final Time time;

    /* true while the sender thread is still running */
    private volatile boolean running;

    /* metrics */
    private final SenderMetrics sensors;

    /* param clientId of the client */
    private String clientId;

    public Sender(KafkaClient client,
                  Metadata metadata,
                  RecordAccumulator accumulator,
                  int maxRequestSize,
                  short acks,
                  int retries,
                  int requestTimeout,
                  Metrics metrics,
                  Time time,
                  String clientId) {
        this.client = client;
        this.accumulator = accumulator;
        this.metadata = metadata;
        this.maxRequestSize = maxRequestSize;
        this.running = true;
        this.requestTimeout = requestTimeout;
        this.acks = acks;
        this.retries = retries;
        this.time = time;
        this.clientId = clientId;
        this.sensors = new SenderMetrics(metrics);
    }

    /**
     * The main run loop for the sender thread
     */
    public void run() {
        log.debug("Starting Kafka producer I/O thread.");

        // main loop, runs until close is called
        while (running) {
            try {
                run(time.milliseconds());
            } catch (Exception e) {
                log.error("Uncaught error in kafka producer I/O thread: ", e);
            }
        }

        log.debug("Beginning shutdown of Kafka producer I/O thread, sending remaining records.");

        // okay we stopped accepting requests but there may still be
        // requests in the accumulator or waiting for acknowledgment,
        // wait until these are completed.
        while (this.accumulator.hasUnsent() || this.client.inFlightRequestCount() > 0) {
            try {
                run(time.milliseconds());
            } catch (Exception e) {
                log.error("Uncaught error in kafka producer I/O thread: ", e);
            }
        }

        this.client.close();

        log.debug("Shutdown of Kafka producer I/O thread has completed.");
    }

    /**
     * Run a single iteration of sending
     * 
     * @param now The current POSIX time in milliseconds
     */
    public void run(long now) {
        Cluster cluster = metadata.fetch();
        // get the list of partitions with data ready to send
        RecordAccumulator.ReadyCheckResult result = this.accumulator.ready(cluster, now);

        // if there are any partitions whose leaders are not known yet, force metadata update
        if (result.unknownLeadersExist)
            this.metadata.requestUpdate();

        // remove any nodes we aren't ready to send to
        Iterator<Node> iter = result.readyNodes.iterator();
        long notReadyTimeout = Long.MAX_VALUE;
        while (iter.hasNext()) {
            Node node = iter.next();
            if (!this.client.ready(node, now)) {
                iter.remove();
                notReadyTimeout = Math.min(notReadyTimeout, this.client.connectionDelay(node, now));
            }
        }

        // create produce requests
        Map<Integer, List<RecordBatch>> batches = this.accumulator.drain(cluster, result.readyNodes, this.maxRequestSize, now);
        List<ClientRequest> requests = createProduceRequests(batches, now);
        sensors.updateProduceRequestMetrics(requests);

        // If we have any nodes that are ready to send + have sendable data, poll with 0 timeout so this can immediately
        // loop and try sending more data. Otherwise, the timeout is determined by nodes that have partitions with data
        // that isn't yet sendable (e.g. lingering, backing off). Note that this specifically does not include nodes
        // with sendable data that aren't ready to send since they would cause busy looping.
        long pollTimeout = Math.min(result.nextReadyCheckDelayMs, notReadyTimeout);
        if (result.readyNodes.size() > 0) {
            log.trace("Nodes with data ready to send: {}", result.readyNodes);
            log.trace("Created {} produce requests: {}", requests.size(), requests);
            pollTimeout = 0;
        }

        // if some partitions are already ready to be sent, the select time would be 0;
        // otherwise if some partition already has some data accumulated but not ready yet,
        // the select time will be the time difference between now and its linger expiry time;
        // otherwise the select time will be the time difference between now and the metadata expiry time;
        List<ClientResponse> responses = this.client.poll(requests, pollTimeout, now);
        for (ClientResponse response : responses) {
            if (response.wasDisconnected())
                handleDisconnect(response, now);
            else
                handleResponse(response, now);
        }
    }

    /**
     * Start closing the sender (won't actually complete until all data is sent out)
     */
    public void initiateClose() {
        this.running = false;
        this.accumulator.close();
        this.wakeup();
    }

    private void handleDisconnect(ClientResponse response, long now) {
        log.trace("Cancelled request {} due to node {} being disconnected", response, response.request().request().destination());
        int correlation = response.request().request().header().correlationId();
        @SuppressWarnings("unchecked")
        Map<TopicPartition, RecordBatch> responseBatches = (Map<TopicPartition, RecordBatch>) response.request().attachment();
        for (RecordBatch batch : responseBatches.values())
            completeBatch(batch, Errors.NETWORK_EXCEPTION, -1L, correlation, now);
    }

    /**
     * Handle a produce response
     */
    private void handleResponse(ClientResponse response, long now) {
        int correlationId = response.request().request().header().correlationId();
        log.trace("Received produce response from node {} with correlation id {}",
                  response.request().request().destination(),
                  correlationId);
        @SuppressWarnings("unchecked")
        Map<TopicPartition, RecordBatch> batches = (Map<TopicPartition, RecordBatch>) response.request().attachment();
        // if we have a response, parse it
        if (response.hasResponse()) {
            ProduceResponse produceResponse = new ProduceResponse(response.responseBody());
            for (Map.Entry<TopicPartition, ProduceResponse.PartitionResponse> entry : produceResponse.responses().entrySet()) {
                TopicPartition tp = entry.getKey();
                ProduceResponse.PartitionResponse partResp = entry.getValue();
                Errors error = Errors.forCode(partResp.errorCode);
                RecordBatch batch = batches.get(tp);
                completeBatch(batch, error, partResp.baseOffset, correlationId, now);
            }
            this.sensors.recordLatency(response.request().request().destination(), response.requestLatencyMs());
        } else {
            // this is the acks = 0 case, just complete all requests
            for (RecordBatch batch : batches.values())
                completeBatch(batch, Errors.NONE, -1L, correlationId, now);
        }
    }

    /**
     * Complete or retry the given batch of records.
     * @param batch The record batch
     * @param error The error (or null if none)
     * @param baseOffset The base offset assigned to the records if successful
     * @param correlationId The correlation id for the request
     * @param now The current POSIX time stamp in milliseconds
     */
    private void completeBatch(RecordBatch batch, Errors error, long baseOffset, long correlationId, long now) {
        if (error != Errors.NONE && canRetry(batch, error)) {
            // retry
            log.warn("Got error produce response with correlation id {} on topic-partition {}, retrying ({} attempts left). Error: {}",
                     correlationId,
                     batch.topicPartition,
                     this.retries - batch.attempts - 1,
                     error);
            this.accumulator.reenqueue(batch, now);
            this.sensors.recordRetries(batch.topicPartition.topic(), batch.recordCount);
        } else {
            // tell the user the result of their request
            batch.done(baseOffset, error.exception());
            this.accumulator.deallocate(batch);
            if (error != Errors.NONE)
                this.sensors.recordErrors(batch.topicPartition.topic(), batch.recordCount);
        }
        if (error.exception() instanceof InvalidMetadataException)
            metadata.requestUpdate();
    }

    /**
     * We can retry a send if the error is transient and the number of attempts taken is fewer than the maximum allowed
     */
    private boolean canRetry(RecordBatch batch, Errors error) {
        return batch.attempts < this.retries && error.exception() instanceof RetriableException;
    }

    /**
     * Transfer the record batches into a list of produce requests on a per-node basis
     */
    private List<ClientRequest> createProduceRequests(Map<Integer, List<RecordBatch>> collated, long now) {
        List<ClientRequest> requests = new ArrayList<ClientRequest>(collated.size());
        for (Map.Entry<Integer, List<RecordBatch>> entry : collated.entrySet())
            requests.add(produceRequest(now, entry.getKey(), acks, requestTimeout, entry.getValue()));
        return requests;
    }

    /**
     * Create a produce request from the given record batches
     */
    private ClientRequest produceRequest(long now, int destination, short acks, int timeout, List<RecordBatch> batches) {
        Map<TopicPartition, ByteBuffer> produceRecordsByPartition = new HashMap<TopicPartition, ByteBuffer>(batches.size());
        Map<TopicPartition, RecordBatch> recordsByPartition = new HashMap<TopicPartition, RecordBatch>(batches.size());
        for (RecordBatch batch : batches) {
            TopicPartition tp = batch.topicPartition;
            ByteBuffer recordsBuffer = batch.records.buffer();
            recordsBuffer.flip();
            produceRecordsByPartition.put(tp, recordsBuffer);
            recordsByPartition.put(tp, batch);
        }
        ProduceRequest request = new ProduceRequest(acks, timeout, produceRecordsByPartition);
        RequestSend send = new RequestSend(destination, this.client.nextRequestHeader(ApiKeys.PRODUCE), request.toStruct());
        return new ClientRequest(now, acks != 0, send, recordsByPartition);
    }

    /**
     * Wake up the selector associated with this send thread
     */
    public void wakeup() {
        this.client.wakeup();
    }

    /**
     * A collection of sensors for the sender
     */
    private class SenderMetrics {

        private final Metrics metrics;
        public final Sensor retrySensor;
        public final Sensor errorSensor;
        public final Sensor queueTimeSensor;
        public final Sensor requestTimeSensor;
        public final Sensor recordsPerRequestSensor;
        public final Sensor batchSizeSensor;
        public final Sensor compressionRateSensor;
        public final Sensor maxRecordSizeSensor;

        public SenderMetrics(Metrics metrics) {
            this.metrics = metrics;
            Map<String, String> metricTags = new LinkedHashMap<String, String>();
            metricTags.put("client-id", clientId);
            String metricGrpName = "producer-metrics";

            this.batchSizeSensor = metrics.sensor("batch-size");
            MetricName m = new MetricName("batch-size-avg", metricGrpName, "The average number of bytes sent per partition per-request.", metricTags);
            this.batchSizeSensor.add(m, new Avg());
            m = new MetricName("batch-size-max", metricGrpName, "The max number of bytes sent per partition per-request.", metricTags);
            this.batchSizeSensor.add(m, new Max());

            this.compressionRateSensor = metrics.sensor("compression-rate");
            m = new MetricName("compression-rate-avg", metricGrpName, "The average compression rate of record batches.", metricTags);
            this.compressionRateSensor.add(m, new Avg());

            this.queueTimeSensor = metrics.sensor("queue-time");
            m = new MetricName("record-queue-time-avg", metricGrpName, "The average time in ms record batches spent in the record accumulator.", metricTags);
            this.queueTimeSensor.add(m, new Avg());
            m = new MetricName("record-queue-time-max", metricGrpName, "The maximum time in ms record batches spent in the record accumulator.", metricTags);
            this.queueTimeSensor.add(m, new Max());

            this.requestTimeSensor = metrics.sensor("request-time");
            m = new MetricName("request-latency-avg", metricGrpName, "The average request latency in ms", metricTags);
            this.requestTimeSensor.add(m, new Avg());
            m = new MetricName("request-latency-max", metricGrpName, "The maximum request latency in ms", metricTags);
            this.requestTimeSensor.add(m, new Max());

            this.recordsPerRequestSensor = metrics.sensor("records-per-request");
            m = new MetricName("record-send-rate", metricGrpName, "The average number of records sent per second.", metricTags);
            this.recordsPerRequestSensor.add(m, new Rate());
            m = new MetricName("records-per-request-avg", metricGrpName, "The average number of records per request.", metricTags);
            this.recordsPerRequestSensor.add(m, new Avg());

            this.retrySensor = metrics.sensor("record-retries");
            m = new MetricName("record-retry-rate", metricGrpName, "The average per-second number of retried record sends", metricTags);
            this.retrySensor.add(m, new Rate());

            this.errorSensor = metrics.sensor("errors");
            m = new MetricName("record-error-rate", metricGrpName, "The average per-second number of record sends that resulted in errors", metricTags);
            this.errorSensor.add(m, new Rate());

            this.maxRecordSizeSensor = metrics.sensor("record-size-max");
            m = new MetricName("record-size-max", metricGrpName, "The maximum record size", metricTags);
            this.maxRecordSizeSensor.add(m, new Max());
            m = new MetricName("record-size-avg", metricGrpName, "The average record size", metricTags);
            this.maxRecordSizeSensor.add(m, new Avg());

            m = new MetricName("requests-in-flight", metricGrpName, "The current number of in-flight requests awaiting a response.", metricTags);
            this.metrics.addMetric(m, new Measurable() {
                public double measure(MetricConfig config, long now) {
                    return client.inFlightRequestCount();
                }
            });
            m = new MetricName("metadata-age", metricGrpName, "The age in seconds of the current producer metadata being used.", metricTags);
            metrics.addMetric(m, new Measurable() {
                public double measure(MetricConfig config, long now) {
                    return (now - metadata.lastUpdate()) / 1000.0;
                }
            });
        }

        public void maybeRegisterTopicMetrics(String topic) {
            // if one sensor of the metrics has been registered for the topic,
            // then all other sensors should have been registered; and vice versa
            String topicRecordsCountName = "topic." + topic + ".records-per-batch";
            Sensor topicRecordCount = this.metrics.getSensor(topicRecordsCountName);
            if (topicRecordCount == null) {
                Map<String, String> metricTags = new LinkedHashMap<String, String>();
                metricTags.put("client-id", clientId);
                metricTags.put("topic", topic);
                String metricGrpName = "producer-topic-metrics";

                topicRecordCount = this.metrics.sensor(topicRecordsCountName);
                MetricName m = new MetricName("record-send-rate", metricGrpName , metricTags);
                topicRecordCount.add(m, new Rate());

                String topicByteRateName = "topic." + topic + ".bytes";
                Sensor topicByteRate = this.metrics.sensor(topicByteRateName);
                m = new MetricName("byte-rate", metricGrpName , metricTags);
                topicByteRate.add(m, new Rate());

                String topicCompressionRateName = "topic." + topic + ".compression-rate";
                Sensor topicCompressionRate = this.metrics.sensor(topicCompressionRateName);
                m = new MetricName("compression-rate", metricGrpName , metricTags);
                topicCompressionRate.add(m, new Avg());

                String topicRetryName = "topic." + topic + ".record-retries";
                Sensor topicRetrySensor = this.metrics.sensor(topicRetryName);
                m = new MetricName("record-retry-rate", metricGrpName , metricTags);
                topicRetrySensor.add(m, new Rate());

                String topicErrorName = "topic." + topic + ".record-errors";
                Sensor topicErrorSensor = this.metrics.sensor(topicErrorName);
                m = new MetricName("record-error-rate", metricGrpName , metricTags);
                topicErrorSensor.add(m, new Rate());
            }
        }

        public void updateProduceRequestMetrics(List<ClientRequest> requests) {
            long now = time.milliseconds();
            for (int i = 0; i < requests.size(); i++) {
                ClientRequest request = requests.get(i);
                int records = 0;

                if (request.attachment() != null) {
                    Map<TopicPartition, RecordBatch> responseBatches = (Map<TopicPartition, RecordBatch>) request.attachment();
                    for (RecordBatch batch : responseBatches.values()) {

                        // register all per-topic metrics at once
                        String topic = batch.topicPartition.topic();
                        maybeRegisterTopicMetrics(topic);

                        // per-topic record send rate
                        String topicRecordsCountName = "topic." + topic + ".records-per-batch";
                        Sensor topicRecordCount = Utils.notNull(this.metrics.getSensor(topicRecordsCountName));
                        topicRecordCount.record(batch.recordCount);

                        // per-topic bytes send rate
                        String topicByteRateName = "topic." + topic + ".bytes";
                        Sensor topicByteRate = Utils.notNull(this.metrics.getSensor(topicByteRateName));
                        topicByteRate.record(batch.records.sizeInBytes());

                        // per-topic compression rate
                        String topicCompressionRateName = "topic." + topic + ".compression-rate";
                        Sensor topicCompressionRate = Utils.notNull(this.metrics.getSensor(topicCompressionRateName));
                        topicCompressionRate.record(batch.records.compressionRate());

                        // global metrics
                        this.batchSizeSensor.record(batch.records.sizeInBytes(), now);
                        this.queueTimeSensor.record(batch.drainedMs - batch.createdMs, now);
                        this.compressionRateSensor.record(batch.records.compressionRate());
                        this.maxRecordSizeSensor.record(batch.maxRecordSize, now);
                        records += batch.recordCount;
                    }
                    this.recordsPerRequestSensor.record(records, now);
                }
            }
        }

        public void recordRetries(String topic, int count) {
            long now = time.milliseconds();
            this.retrySensor.record(count, now);
            String topicRetryName = "topic." + topic + ".record-retries";
            Sensor topicRetrySensor = this.metrics.getSensor(topicRetryName);
            if (topicRetrySensor != null)
                topicRetrySensor.record(count, now);
        }

        public void recordErrors(String topic, int count) {
            long now = time.milliseconds();
            this.errorSensor.record(count, now);
            String topicErrorName = "topic." + topic + ".record-errors";
            Sensor topicErrorSensor = this.metrics.getSensor(topicErrorName);
            if (topicErrorSensor != null)
                topicErrorSensor.record(count, now);
        }

        public void recordLatency(int node, long latency) {
            long now = time.milliseconds();
            this.requestTimeSensor.record(latency, now);
            if (node >= 0) {
                String nodeTimeName = "node-" + node + ".latency";
                Sensor nodeRequestTime = this.metrics.getSensor(nodeTimeName);
                if (nodeRequestTime != null)
                    nodeRequestTime.record(latency, now);
            }
        }
    }

}
