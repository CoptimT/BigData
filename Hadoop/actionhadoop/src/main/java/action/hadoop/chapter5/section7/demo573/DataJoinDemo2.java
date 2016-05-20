package action.hadoop.chapter5.section7.demo573;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;

public class DataJoinDemo2 {
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: DataJoinDemo2 2 parameters");
			System.exit(2);
		}
		// 分布式缓存放在job实例化之前
		
		DistributedCache.createSymlink(conf);
		//DistributedCache.addFileToClassPath(new Path(""), conf);
		DistributedCache.addFileToClassPath(new Path("/root/test/a.jar"),conf, FileSystem.get(URI.create("hdfs://192.168.47.129:9000/"),conf)); 
		DistributedCache.addCacheFile(new URI("/root/test/cache.txt#cacheFile"), conf);
		DistributedCache.addCacheArchive(new URI(""), conf);
		  
		conf.set("", "");
		 

		/**创建一个job，起个名字以便跟踪查看任务执行情况**/
		Job job = new Job(conf, "word count");
		job.setJarByClass(DataJoinDemo2.class);
		/**
		 * 当在hadoop集群上运行作业时，需要把代码打包成一个jar文件（hadoop会在集群分发这
		 * 个文件），通过job的setJarByClass设置一个类，hadoop根据这个类找到所在的 jar 文件
		 **/
		/** 设置要使用的map、combiner、reduce类型 **/
		//job.setMapperClass(WordCountMapper.class);
		//job.setCombinerClass(WordCountReducer.class);

		// job.setPartitionerClass(MyPartition.class);//test

		//job.setReducerClass(WordCountReducer.class);

		// job.setNumReduceTasks(2);//test

		/**
		 * 设置map和reduce函数的输入类型，这里没有代码是因为我们使用默认的 TextInputFormat ，
		 * 针对文本文件，按行将文本文件切割成 InputSplits, 并用 LineRecordReader 将 InputSplit 解析 成
		 * <key,value>: 对，key 是行在文件中的位置，value 是文件中的一行
		 **/
		/** 设置map和reduce 函数的输出键和输出值类型 **/
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		/** 设置输入和输出路径 **/
		//FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		//FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		/** 提交作业并等待它完成 **/
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
