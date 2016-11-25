package cn.zxw.hadoop.mapreduce.partition;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class PartitionTo32Redis extends Partitioner<Text, Text>{

	@Override
	public int getPartition(Text key, Text value, int numPartitions) {
		return (key.toString().split("_")[1].hashCode() & Integer.MAX_VALUE) % 32;
	}

}
