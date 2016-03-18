package cn.zxw.kafka.consumer;

import kafka.consumer.ConsumerConfig;  
import kafka.consumer.KafkaStream;  
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;  
import java.util.List;  
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerGroupExample {
    private final ConsumerConnector consumer;  
    private final String topic;  
    private  ExecutorService executor;  
   
    public ConsumerGroupExample(String a_zookeeper, String a_groupId, String a_topic) { 
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(a_zookeeper, a_groupId));  
        this.topic = a_topic;  
    }  
   
    public void shutdown() {  
        if (consumer != null) consumer.shutdown();  
        if (executor != null) executor.shutdown();  
    }  
   
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void run(int a_numThreads) {  
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();  
        topicCountMap.put(topic, new Integer(a_numThreads));  
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);  
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);  
   
        // 启动所有线程  
        executor = Executors.newFixedThreadPool(a_numThreads);  
   
        // 开始消费消息  
        int threadNumber = 0;  
        for (final KafkaStream stream : streams) {
            executor.submit(new ConsumerTest(stream, threadNumber));  
            threadNumber++;  
        }  
    }  
   
    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {  
        Properties props = new Properties();  
        props.put("zookeeper.connect", a_zookeeper);
        //props.put("zk.connect", "192.168.2.225:2183/config/mobile/mq/mafka");
        props.put("group.id", a_groupId);  
        props.put("zookeeper.session.timeout.ms", "60000");  
        props.put("zookeeper.sync.time.ms", "2000");  
        props.put("auto.commit.interval.ms", "1000");  
   
        return new ConsumerConfig(props);  
    }
   
    public static void main(String[] args) {  
        String zooKeeper = args[0];  
        String groupId = args[1];  
        String topic = args[2];  
        int threads = Integer.parseInt(args[3]);
   
        ConsumerGroupExample example = new ConsumerGroupExample(zooKeeper, groupId, topic);
        example.run(threads);  
   
        try {
            Thread.sleep(10000);  
        } catch (InterruptedException ie) {  
   
        }
        example.shutdown(); 
    }  
}