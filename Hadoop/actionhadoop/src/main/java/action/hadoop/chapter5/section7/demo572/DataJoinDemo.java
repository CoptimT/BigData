package action.hadoop.chapter5.section7.demo572;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * <p>用 DataJoin 类实现Reduce端连接</p>
 * @author connor
 *
 */
public class DataJoinDemo {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: Require 2 parameters");
			System.exit(2);
		}
		Job job = new Job(conf, "DataJoinDemo");
		job.setJarByClass(DataJoinDemo.class);
		//job.setInputFormatClass(DBInputFormat.class);
		//job.setOutputFormatClass(DBOutputFormat.class);
		
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
