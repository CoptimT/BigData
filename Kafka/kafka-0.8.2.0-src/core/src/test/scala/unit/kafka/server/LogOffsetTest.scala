/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.server

import java.io.File
import kafka.utils._
import junit.framework.Assert._
import java.util.{Random, Properties}
import kafka.consumer.SimpleConsumer
import kafka.message.{NoCompressionCodec, ByteBufferMessageSet, Message}
import kafka.zk.ZooKeeperTestHarness
import org.scalatest.junit.JUnit3Suite
import kafka.admin.AdminUtils
import kafka.api.{PartitionOffsetRequestInfo, FetchRequestBuilder, OffsetRequest}
import kafka.utils.TestUtils._
import kafka.common.{ErrorMapping, TopicAndPartition}
import org.junit.After
import org.junit.Before
import org.junit.Test

class LogOffsetTest extends JUnit3Suite with ZooKeeperTestHarness {
  val random = new Random() 
  var logDir: File = null
  var topicLogDir: File = null
  var server: KafkaServer = null
  var logSize: Int = 100
  val brokerPort: Int = 9099
  var simpleConsumer: SimpleConsumer = null
  var time: Time = new MockTime()

  @Before
  override def setUp() {
    super.setUp()
    val config: Properties = createBrokerConfig(1, brokerPort)
    val logDirPath = config.getProperty("log.dir")
    logDir = new File(logDirPath)
    time = new MockTime()
    server = TestUtils.createServer(new KafkaConfig(config), time)
    simpleConsumer = new SimpleConsumer("localhost", brokerPort, 1000000, 64*1024, "")
  }

  @After
  override def tearDown() {
    simpleConsumer.close
    server.shutdown
    Utils.rm(logDir)
    super.tearDown()
  }

  @Test
  def testGetOffsetsForUnknownTopic() {
    val topicAndPartition = TopicAndPartition("foo", 0)
    val request = OffsetRequest(
      Map(topicAndPartition -> PartitionOffsetRequestInfo(OffsetRequest.LatestTime, 10)))
    val offsetResponse = simpleConsumer.getOffsetsBefore(request)
    assertEquals(ErrorMapping.UnknownTopicOrPartitionCode,
                 offsetResponse.partitionErrorAndOffsets(topicAndPartition).error)
  }

  @Test
  def testGetOffsetsBeforeLatestTime() {
    val topicPartition = "kafka-" + 0
    val topic = topicPartition.split("-").head
    val part = Integer.valueOf(topicPartition.split("-").last).intValue

    // setup brokers in zookeeper as owners of partitions for this test
    AdminUtils.createTopic(zkClient, topic, 1, 1)

    val logManager = server.getLogManager
    waitUntilTrue(() => logManager.getLog(TopicAndPartition(topic, part)).isDefined,
                  "Log for partition [topic,0] should be created")
    val log = logManager.getLog(TopicAndPartition(topic, part)).get

    val message = new Message(Integer.toString(42).getBytes())
    for(i <- 0 until 20)
      log.append(new ByteBufferMessageSet(NoCompressionCodec, message))
    log.flush()

    val offsets = server.apis.fetchOffsets(logManager, TopicAndPartition(topic, part), OffsetRequest.LatestTime, 10)
    assertEquals(Seq(20L, 18L, 15L, 12L, 9L, 6L, 3L, 0), offsets)

    waitUntilTrue(() => isLeaderLocalOnBroker(topic, part, server), "Leader should be elected")
    val topicAndPartition = TopicAndPartition(topic, part)
    val offsetRequest = OffsetRequest(
      Map(topicAndPartition -> PartitionOffsetRequestInfo(OffsetRequest.LatestTime, 10)),
      replicaId = 0)
    val consumerOffsets =
      simpleConsumer.getOffsetsBefore(offsetRequest).partitionErrorAndOffsets(topicAndPartition).offsets
    assertEquals(Seq(20L, 18L, 15L, 12L, 9L, 6L, 3L, 0), consumerOffsets)

    // try to fetch using latest offset
    val fetchResponse = simpleConsumer.fetch(
      new FetchRequestBuilder().addFetch(topic, 0, consumerOffsets.head, 300 * 1024).build())
    assertFalse(fetchResponse.messageSet(topic, 0).iterator.hasNext)
  }

  @Test
  def testEmptyLogsGetOffsets() {
    val topicPartition = "kafka-" + random.nextInt(10)
    val topicPartitionPath = getLogDir.getAbsolutePath + "/" + topicPartition
    topicLogDir = new File(topicPartitionPath)
    topicLogDir.mkdir

    val topic = topicPartition.split("-").head

    // setup brokers in zookeeper as owners of partitions for this test
    createTopic(zkClient, topic, numPartitions = 1, replicationFactor = 1, servers = Seq(server))

    var offsetChanged = false
    for(i <- 1 to 14) {
      val topicAndPartition = TopicAndPartition(topic, 0)
      val offsetRequest =
        OffsetRequest(Map(topicAndPartition -> PartitionOffsetRequestInfo(OffsetRequest.EarliestTime, 1)))
      val consumerOffsets =
        simpleConsumer.getOffsetsBefore(offsetRequest).partitionErrorAndOffsets(topicAndPartition).offsets

      if(consumerOffsets(0) == 1) {
        offsetChanged = true
      }
    }
    assertFalse(offsetChanged)
  }

  @Test
  def testGetOffsetsBeforeNow() {
    val topicPartition = "kafka-" + random.nextInt(3)
    val topic = topicPartition.split("-").head
    val part = Integer.valueOf(topicPartition.split("-").last).intValue

    // setup brokers in zookeeper as owners of partitions for this test
    AdminUtils.createTopic(zkClient, topic, 3, 1)

    val logManager = server.getLogManager
    val log = logManager.createLog(TopicAndPartition(topic, part), logManager.defaultConfig)
    val message = new Message(Integer.toString(42).getBytes())
    for(i <- 0 until 20)
      log.append(new ByteBufferMessageSet(NoCompressionCodec, message))
    log.flush()

    val now = time.milliseconds + 30000 // pretend it is the future to avoid race conditions with the fs

    val offsets = server.apis.fetchOffsets(logManager, TopicAndPartition(topic, part), now, 10)
    assertEquals(Seq(20L, 18L, 15L, 12L, 9L, 6L, 3L, 0L), offsets)

    waitUntilTrue(() => isLeaderLocalOnBroker(topic, part, server), "Leader should be elected")
    val topicAndPartition = TopicAndPartition(topic, part)
    val offsetRequest = OffsetRequest(Map(topicAndPartition -> PartitionOffsetRequestInfo(now, 10)), replicaId = 0)
    val consumerOffsets =
      simpleConsumer.getOffsetsBefore(offsetRequest).partitionErrorAndOffsets(topicAndPartition).offsets
    assertEquals(Seq(20L, 18L, 15L, 12L, 9L, 6L, 3L, 0L), consumerOffsets)
  }

  @Test
  def testGetOffsetsBeforeEarliestTime() {
    val topicPartition = "kafka-" + random.nextInt(3)
    val topic = topicPartition.split("-").head
    val part = Integer.valueOf(topicPartition.split("-").last).intValue

    // setup brokers in zookeeper as owners of partitions for this test
    AdminUtils.createTopic(zkClient, topic, 3, 1)

    val logManager = server.getLogManager
    val log = logManager.createLog(TopicAndPartition(topic, part), logManager.defaultConfig)
    val message = new Message(Integer.toString(42).getBytes())
    for(i <- 0 until 20)
      log.append(new ByteBufferMessageSet(NoCompressionCodec, message))
    log.flush()

    val offsets = server.apis.fetchOffsets(logManager, TopicAndPartition(topic, part), OffsetRequest.EarliestTime, 10)

    assertEquals(Seq(0L), offsets)

    waitUntilTrue(() => isLeaderLocalOnBroker(topic, part, server), "Leader should be elected")
    val topicAndPartition = TopicAndPartition(topic, part)
    val offsetRequest =
      OffsetRequest(Map(topicAndPartition -> PartitionOffsetRequestInfo(OffsetRequest.EarliestTime, 10)))
    val consumerOffsets =
      simpleConsumer.getOffsetsBefore(offsetRequest).partitionErrorAndOffsets(topicAndPartition).offsets
    assertEquals(Seq(0L), consumerOffsets)
  }

  private def createBrokerConfig(nodeId: Int, port: Int): Properties = {
    val props = new Properties
    props.put("broker.id", nodeId.toString)
    props.put("port", port.toString)
    props.put("log.dir", getLogDir.getAbsolutePath)
    props.put("log.flush.interval.messages", "1")
    props.put("enable.zookeeper", "false")
    props.put("num.partitions", "20")
    props.put("log.retention.hours", "10")
    props.put("log.retention.check.interval.ms", (5*1000*60).toString)
    props.put("log.segment.bytes", logSize.toString)
    props.put("zookeeper.connect", zkConnect.toString)
    props
  }

  private def getLogDir(): File = {
    val dir = TestUtils.tempDir()
    dir
  }

}
