package action.hadoop.chapter5.section9;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


/**
 * 关系数据库的连接与访问
 * @author connor
 *
 */
public class Main {

	public static void main(String[] args) 
			throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount 2 parameters");
			System.exit(2);
		}
		

		//DistributedCache.createSymlink(conf);
		//DistributedCache.addFileToClassPath(new Path(""), conf);
		//DistributedCache.addFileToClassPath(new Path("/root/test/a.jar"),conf, FileSystem.get(URI.create("hdfs://192.168.47.129:9000/"),conf)); 
		//DistributedCache.addCacheFile(new URI("/root/test/cache.txt#cacheFile"), conf);
		//DistributedCache.addCacheArchive(new URI(""), conf);
		
		 
		Job job = new Job(conf, "RDB");
		job.setJarByClass(Main.class);
		job.setInputFormatClass(DBInputFormat.class);
		job.setOutputFormatClass(DBOutputFormat.class);
		
		DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver", 
				"jdbc:mysql://localhost:3305/hadoop", "root", "123456");
		DBOutputFormat.setOutput(job, "test", "name","phone");
		
		//job.setMapperClass(WordCountMapper.class);
		//job.setCombinerClass(WordCountReducer.class);
		// job.setPartitionerClass(MyPartition.class);//test
		//job.setReducerClass(WordCountReducer.class);
		// job.setNumReduceTasks(2);


		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
