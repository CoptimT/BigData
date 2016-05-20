package action.hadoop.chapter5.section6.demo564;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapred.jobcontrol.JobControl;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;

public class Chain {
	
	public static void main(String[] args) throws Exception {
		Path jobinpath=new Path(args[0]);
        Path joboutpath=new Path(args[1]);
        
        JobConf job = new JobConf(Chain.class);
        job.setJobName("Chain");
        job.setInputFormat(TextInputFormat.class);
        job.setOutputFormat(TextOutputFormat.class);
        FileInputFormat.setInputPaths(job, jobinpath);
        FileOutputFormat.setOutputPath(job, joboutpath);
        
        JobConf mapper1 = new JobConf();
        JobConf mapper2 = new JobConf();
        JobConf mapper3 = new JobConf();
        JobConf mapper4 = new JobConf();
        JobConf reducerConf = new JobConf();
        
        ChainMapper.addMapper(job, Mappper1.class, LongWritable.class, Text.class,
                LongWritable.class, Text.class, true, mapper1);
        ChainMapper.addMapper(job, Mappper2.class, LongWritable.class, Text.class,
        		Text.class, IntWritable.class, true, mapper2);
        ChainReducer.setReducer(job, MyReducer.class, Text.class, IntWritable.class, 
        		Text.class, IntWritable.class, true, reducerConf);
        ChainReducer.addMapper(job, Mappper3.class, Text.class, IntWritable.class,
        		Text.class, IntWritable.class, true, mapper3);
        ChainReducer.addMapper(job, Mappper4.class, Text.class, IntWritable.class,
        		Text.class, IntWritable.class, true, mapper4);
        
		Job job1 = new Job(job);
		JobControl jobControl = new JobControl("test");
		jobControl.addJob(job1);
		Thread controller = new Thread(jobControl);
		controller.start();
		while (!jobControl.allFinished()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}
		}
		jobControl.stop();
		//JobClient.runJob(job);
	}
	/**
	 * 去掉特殊字符
	 * @author connor
	 *
	 */
	public static class Mappper1 extends MapReduceBase implements 
					Mapper<LongWritable, Text, LongWritable, Text>{
		Text line=new Text();
		
		public void map(LongWritable key, Text value,
				OutputCollector<LongWritable, Text> output, Reporter reporter)
				throws IOException {
			line.set(cutSpecialChar(value.toString()));
			output.collect(key, line);
		}
		public String cutSpecialChar(String str){
			return str.replaceAll(",", " ").replaceAll("(", " ").replaceAll("-", " ").replaceAll("“", " ");
		}
	}
	/**
	 * 分词计数
	 * @author connor
	 *
	 */
	public static class Mappper2 extends MapReduceBase implements 
					Mapper<LongWritable, Text, Text, IntWritable>{
		Text word=new Text();
		IntWritable one=new IntWritable(1);
		@Override
		public void map(LongWritable key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken().toLowerCase());
				output.collect(word, one);
			}
			
		}
		
	}

	public static class MyReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable result = new IntWritable();
		@Override
		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			int sum = 0;
			while(values.hasNext()) {
				sum += values.next().get();
			}
			result.set(sum);
			output.collect(key, result);
		}
		
	}

	public static class Mappper3 extends MapReduceBase implements Mapper<Text, IntWritable, Text, IntWritable>{

		@Override
		public void map(Text key, IntWritable value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			key.set("["+key.toString()+"]");
			output.collect(key, value);
		}
		
	}

	public static class Mappper4 extends MapReduceBase implements Mapper<Text, IntWritable, Text, IntWritable>{

		@Override
		public void map(Text key, IntWritable value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			String word=key.toString();
			if(word.length()<20){
				for(int i=20-word.length();i>0;i--){
					word+=" ";
				}
			}
			key.set(word);
			output.collect(key, value);
		}
		
	}

}
