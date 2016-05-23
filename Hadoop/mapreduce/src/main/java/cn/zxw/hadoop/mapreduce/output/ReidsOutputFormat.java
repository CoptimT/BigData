package cn.zxw.hadoop.mapreduce.output;

import java.io.IOException;

import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import redis.clients.jedis.Jedis;

public class ReidsOutputFormat<K, V> extends FileOutputFormat<K, V> {
	
	protected static class RedisRecordWriter<K, V> extends RecordWriter<K, V>{
		
		 private Jedis jedis;
		 
		 public RedisRecordWriter(Jedis jedis){
	            this.jedis = jedis;
	        }
		 
		@Override
		public void write(K key, V value) throws IOException, InterruptedException {
	            jedis.set(key.toString(), value.toString());
		}
		
		@Override
		public void close(TaskAttemptContext job) throws IOException, InterruptedException {
			 if (jedis != null) jedis.disconnect();
		}
	}

	@Override
	public RecordWriter<K, V> getRecordWriter(TaskAttemptContext job) throws IOException, InterruptedException {
		Jedis jedis = new Jedis("10.10.15.64",6379);//Redis IP
		jedis.auth("");//Redis Password
		jedis.select(15);
		return new RedisRecordWriter<K, V>(jedis);
		
	}
}
