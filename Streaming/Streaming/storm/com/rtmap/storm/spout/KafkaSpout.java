package com.rtmap.storm.spout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

@SuppressWarnings("all")
public class KafkaSpout implements IRichSpout {
      private static final long serialVersionUID = -5569857211173547938L;
      
      private SpoutOutputCollector collector;
      private ConsumerConnector consumer;
      private String topic;
      
      public KafkaSpout(String topic) {
           this.topic = topic;
      }
      
      @Override
      public void open(Map conf, TopologyContext context,SpoutOutputCollector collector) {
           this.collector = collector;
      }
 
	private ConsumerConfig createConsumerConfig() {
           Properties props = new Properties();
           props.put("zookeeper.connect","datanode1.rtmap.com:2181");
           props.put("group.id","kafkaConsumerGroup");
           props.put("zookeeper.session.timeout.ms","10000");
           //props.put("zookeeper.sync.time.ms", "200");
           //props.put("auto.commit.interval.ms", "1000");
           return new ConsumerConfig(props);
      }
 
      @Override
      public void close() {
           System.out.println("KafkaSpout is going to be shutdown.");
      }
 
      @Override
      public void activate() {
           this.consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
           Map<String, Integer> topickMap = new HashMap<String,Integer>();
           topickMap.put(topic,new Integer(1));
           Map<String, List<KafkaStream<byte[],byte[]>>> streamMap = consumer.createMessageStreams(topickMap);
           KafkaStream<byte[],byte[]> stream = streamMap.get(topic).get(0);
           ConsumerIterator<byte[],byte[]> it = stream.iterator();
           while (it.hasNext()) {
                 String value = new String(it.next().message());
                 System.out.println("(consumer)-->" + value);//log
                 collector.emit(new Values(value), value);
           }
      }
 
      @Override
      public void deactivate() {
          System.out.println("KafkaSpout has been deactivated!");
      }
 
      @Override
      public void nextTuple() {
    	  Utils.sleep(4*1000);
      }
 
      @Override
      public void ack(Object msgId) {
           System.out.println(topic + " message process success,msgId="+msgId);
      }
 
      @Override
      public void fail(Object msgId) {
    	  System.out.println(topic + "message process failed,msgId="+msgId);
      }
 
      @Override
      public void declareOutputFields(OutputFieldsDeclarer declarer) {
           declarer.declare(new Fields("line"));
      }
 
      @Override
      public Map<String, Object> getComponentConfiguration() {
           return null;
      }

}
 
