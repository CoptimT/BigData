package cn.zxw.hw01;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HelloWorld01 extends Configured implements Tool{

	public static void main(String[] args) throws Exception {
		int res=ToolRunner.run(new Configuration(), new HelloWorld01(), args);
		System.exit(res);
	}

	
	public int run(String[] arg0) throws Exception {
		Configuration conf=getConf();
		
		Job job=new Job(conf, "helloworld01");
		job.setJarByClass(HelloWorld01.class);
		
		FileInputFormat.addInputPath(job, new Path(arg0[0]));
		FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
		
		job.setMapperClass(Map.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.waitForCompletion(true);
		
		return job.isSuccessful()?0:1;
	}
	enum Counter{
		LINESKIP
	}
	public static class Map extends Mapper<LongWritable, Text, NullWritable, Text>{

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line=value.toString();
			try {
				String[] lineSplt=line.split(" ");
				String month=lineSplt[0];
				String time=lineSplt[2];
				String mac=lineSplt[4];
				Text out=new Text(month+" "+time+" "+mac);
				context.write(NullWritable.get(), out);
			} catch (Exception e) {
				context.getCounter(Counter.LINESKIP).increment(1);
				e.printStackTrace();
			}
			//super.map(key, value, context);
		}
		
	}
}
