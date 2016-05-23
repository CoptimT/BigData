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
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cn.zxw.hadoop.writable.DBRecord;

/**
 * 将MySQL中的数据写入到HDFS中
 * @author hadoop
 */
public class Mysql2HDFS extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		// 启动Job
		ToolRunner.run(new Mysql2HDFS(), args);
	}

	@Override
	public int run(String[] args) throws Exception {
		// 验证job启动时的参数合法性
		if (args.length != 5) {
			System.out.println("Usage:hadoop jar reyunMysql2HDFS.jar driver jdbc username password output");
			return 1;
		}
		// 连接数据库的驱动类型
		String driver = args[0];
		// JDBC信息
		String jdbc = args[1];
		// 连接数据库使用的用户
		String username = args[2];
		// 连接数据库使用的密码
		String password = args[3];
		// 数据写入到HDFS的目录
		String output = args[4];

		// 初始化conf和job对象
		Configuration conf = new Configuration();
		// 设置Map任务数为1
		conf.set("mapred.map.tasks", "1");
		
		DBConfiguration.configureDB(conf, driver, jdbc, username, password);
		Job job = Job.getInstance(conf, Mysql2HDFS.class.getSimpleName());

		// 设置job的输入数据类型为数据库类型
		job.setInputFormatClass(DBInputFormat.class);

		// 设置mysql的输入数据源，tb_publisher_industry为表名，first,second,mapping_id为字段名
		DBInputFormat.setInput(job, DBRecord.class, "tb_name", null, "mapping_id", "first,second,mapping_id");

		// 设置打jar的主类
		job.setJarByClass(Mysql2HDFS.class);

		// 设置job的mapper类
		job.setMapperClass(MyMapper.class);

		// 设置Map任务的输出类型
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);

		// 设置Reducer任务的个数
		job.setNumReduceTasks(0);

		// 设置job的输入目录
		FileOutputFormat.setOutputPath(job, new Path(output));

		return job.waitForCompletion(true) == true ? 0 : 1;
	}

	/**
	 * 自定义Mapper类，实现从Mysql读取数据，写入到HDFS
	 * @author hadoop
	 */
	public static class MyMapper extends Mapper<LongWritable, DBRecord, Text, NullWritable> {
		@Override
		protected void map(LongWritable key, DBRecord value, Mapper<LongWritable, DBRecord, Text, NullWritable>.Context context) throws IOException,
				InterruptedException {
			context.write(new Text(value.toString()), NullWritable.get());
		}
	}
}
