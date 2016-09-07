package cn.zxw.hadoop.mapreduce.feature;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Counter特性之定义、设置与获取
 */
public class MRCounter extends Configured implements Tool {
	//定义方式1-枚举
	enum RecordCounter{
		SOURCE,MALFORMED
	}
	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage: hadoop jar dmpLog.jar inputPath outputPath");
			return 1;
		}
		String inputPath = args[0];
		String outputPath = args[1];
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, MRCounter.class.getSimpleName());
		
		job.setJarByClass(MRCounter.class);

		job.setMapperClass(ApplistMapper.class);
		job.setReducerClass(ApplistReducer.class);
		job.setNumReduceTasks(1);
		
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
        
		FileInputFormat.setInputPaths(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		int result = job.waitForCompletion(true) == true ? 0 : 1;
		
		//Counters获取
		Counters counters = job.getCounters();
		System.out.println("source record " + counters.findCounter(RecordCounter.SOURCE).getValue());
		System.out.println("malformed record "+counters.findCounter(RecordCounter.MALFORMED).getValue());
		System.out.println("dynamic counters "+counters.findCounter("province","guangdong").getValue());
		
		return result;
	}

	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new MRCounter(), args);
		System.exit(result);
	}
	
	/**
	 * 日志清洗的Mapper类
	 */
	public static class ApplistMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
		Text v = new Text();

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			try {
				//枚举counter设置值
				context.getCounter(RecordCounter.SOURCE).increment(1);
				String parsed = value.toString();
				if (null != parsed) {
					v.set(parsed);
					context.write(key, v);
				}else{
					context.getCounter(RecordCounter.MALFORMED).increment(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 日志清洗的Reducer类
	 */
	public static class ApplistReducer extends Reducer<LongWritable, Text, Text, NullWritable> {
		@Override
		protected void reduce(LongWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text value : values) {
				if(value.toString().contains("广东")){
					//定义方式2-Dynamic counters 定义与设置值
					context.getCounter("province","guangdong").increment(1);
				}
				context.write(value, NullWritable.get());
			}
		}
	}
}
