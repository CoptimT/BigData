package cn.zxw.hadoop.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cn.zxw.hadoop.writable.TypeDataFlag;

/**
 * 多个输入文件与自定义数据类型
 */
public class MultipleInputMain extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
		ToolRunner.run(new MultipleInputMain(), args);
	}

	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("mapred.min.split.size", "536870912");
		Job job = Job.getInstance(conf, MultipleInputMain.class.getSimpleName());
		
		job.setJarByClass(MultipleInputMain.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(TypeDataFlag.class);
		 
		job.setReducerClass(MappingTagsReduce.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setNumReduceTasks(1);
		
		MultipleInputs.addInputPath(job, new Path(args[0]),TextInputFormat.class, MappingTagsMap1.class);
		MultipleInputs.addInputPath(job, new Path(args[1]),TextInputFormat.class, MappingTagsMap2.class);
		 
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		job.waitForCompletion(true);
		return 0;
	}
	
	public static class MappingTagsMap1 extends Mapper<LongWritable, Text, Text, TypeDataFlag> {
		private String delimiter;
		
		protected void setup(Mapper<LongWritable, Text, Text, TypeDataFlag>.Context context)
				throws IOException, InterruptedException {
			delimiter = context.getConfiguration().get("delimiter", "\\s+");
		}
		
		protected void map(LongWritable key, Text value,Mapper<LongWritable, Text, Text, TypeDataFlag>.Context context)
				throws IOException, InterruptedException {
			String line = value.toString().trim();
			if (line.length() > 0) {
				String[] str = line.split(delimiter);
					//从app_name表中获取前两列  （名称和包名）
					context.write(new Text(str[1].trim()),new TypeDataFlag(str[0].trim(), 0));
			}
		}
	}
	
	public static class MappingTagsMap2 extends Mapper<LongWritable, Text, Text, TypeDataFlag> {
		private String delimiter; // 设置分隔符
		
		@Override
		protected void setup(Mapper<LongWritable, Text, Text, TypeDataFlag>.Context context)
				throws IOException, InterruptedException {
			delimiter = context.getConfiguration().get("delimiter", "\\s+");
		}
		
		@Override
		protected void map(LongWritable key, Text value,Mapper<LongWritable, Text, Text, TypeDataFlag>.Context context)
			throws IOException, InterruptedException {
				String line = value.toString().trim();
				if (line.length() > 0) {
					String[] str = line.split(delimiter);
					if (str.length == 3) {
					context.write(new Text(str[0].trim()),new TypeDataFlag(str[2].trim(), 1));
				}
			}
		}
	}
	
	public static class MappingTagsReduce extends Reducer<Text, TypeDataFlag, NullWritable, Text> {
		@Override
		protected void reduce(Text key, Iterable<TypeDataFlag> values,Reducer<Text, TypeDataFlag, NullWritable, Text>.Context context)
				throws IOException, InterruptedException {
			//最后输出的格式为: 应用名称，应用包名，标签值
			//str[0]代表pkg里边的标签，str[1]是all里边的名称
			String[] str = new String[2];
			for (TypeDataFlag value : values) {
				if (value.getFlag() == 1) {
					str[0] = value.getValue();
				} else {
					str[1] = value.getValue();
				}
			}
			if(str[0]==null || str[1]==null){
				return;
			}
			System.out.println(str[1] +"\t" +key.toString() +"\t"+ str[0]);
			context.write(NullWritable.get(),
					new Text(str[1] +"\t" +key.toString() +"\t"+ str[0]));
		}
	}
}
