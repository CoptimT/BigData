package cn.zxw.hadoop.mapreduce;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.FileSystem;
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
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Progressable;

public class WordCount_Old {

	public static class TokenizerMapper extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable count = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				output.collect(word, count);
			}
		}
	}

	public static class IntSumReducer extends MapReduceBase implements
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			int sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}
			result.set(sum);
			output.collect(key, result);
		}
	}

	public static class WordCountOutputFormat extends
			MultipleOutputFormat<Text, IntWritable> {
		private TextOutputFormat<Text, IntWritable> output = null;

		@Override
		protected RecordWriter<Text, IntWritable> getBaseRecordWriter(
				FileSystem fs, JobConf job, String name,
				Progressable progressable) throws IOException {
			if (output == null) {
				output = new TextOutputFormat<Text, IntWritable>();
			}
			return output.getRecordWriter(fs, job, name, progressable);
		}

		@Override
		protected String generateFileNameForKeyValue(Text key,
				IntWritable value, String name) {
			char c = key.toString().toLowerCase().charAt(0);
			if (c >= 'a' && c <= 'z') {
				return c + ".txt";
			}
			return "result.txt";
		}
	}

	public static void main(String[] args) throws Exception {
		JobConf job = new JobConf(WordCount_Old.class);
		job.setJobName("WordCount_Old");
		String[] otherArgs = new GenericOptionsParser(job, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		job.setJarByClass(WordCount_Old.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setOutputFormat(WordCountOutputFormat.class);// ���������ʽ
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		RunningJob runningJob = JobClient.runJob(job);
		runningJob.waitForCompletion();
	}

}
