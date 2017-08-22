package cn.zxw.kafka.offset;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhangxw on 2017/8/21.
 */
public class GetOffsetByTimeStamp {
    public static void main(String[] args) {
        String brokerList = "";
        String topic = "";
        int partitionId = 2;
        long timestamp_begin = 1503313200000l;

        Properties props = new Properties();
        props.put("bootstrap.servers", brokerList);
        props.put("group.id", "groupId-getOffsetByTimeStamp-");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer kafkaConsumer = new KafkaConsumer(props);

        TopicPartition topicPartitionBegin = new TopicPartition(topic, partitionId);

        Map timestampsToSerachMap = new HashMap<TopicPartition, Long>();

        timestampsToSerachMap.put(topicPartitionBegin, new Long(timestamp_begin));

        Map<TopicPartition, OffsetAndTimestamp> offsetAndTimestampMaps = kafkaConsumer.offsetsForTimes(timestampsToSerachMap);

        OffsetAndTimestamp offsetAndTimestampBegin = offsetAndTimestampMaps.get(topicPartitionBegin);

        long offsetsBegin = offsetAndTimestampBegin.offset();




    }
}
