package cn.zxw.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * MR相关配置
 */
public class MRConfigure extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
		ToolRunner.run(new MRConfigure(), args);
	}

	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		//map输入数据最小切分(老版本配置mapred.min.split.size)
		conf.set("mapreduce.input.fileinputformat.split.minsize", "536870912");//512M
		//设置map任务数(老版本配置mapred.map.tasks)
		conf.set("mapreduce.job.maps", "1");//如DB导出的job
		//设置map端压缩
		conf.set("mapreduce.map.output.compress", "true");
				
		Job job = Job.getInstance(conf, MRConfigure.class.getSimpleName());
		job.setJarByClass(MRConfigure.class);
		//...
		return job.waitForCompletion(true) == true ? 0 : 1;
	}
	
}
