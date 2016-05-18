package cn.zxw.hadoop.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HBaseBatchImport extends Configured implements Tool {
	
	public int run(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Usage hadoop jar HBaseBatchImport.jar quorum hbase_table inputPath");
			return 1;
		}
		String quorum = args[0];
		String hbaseTable = args[1];
		String inputPath = args[2];

		final Configuration conf = new Configuration();
		// 设置HBase的连接信息
		conf.set("hbase.zookeeper.quorum", quorum);
		conf.set(TableOutputFormat.OUTPUT_TABLE, hbaseTable);
		conf.set("dfs.client.socket-timeout", "180000");

		Job job = Job.getInstance(conf);
		TableMapReduceUtil.addDependencyJars(job);
		job.setJarByClass(HBaseBatchImport.class);

		// 设置job的Map类
		job.setMapperClass(BatchImportMapper.class);

		// 设置Map类的输出类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		// 设置输入数据的路径
		FileInputFormat.setInputPaths(job, new Path(inputPath));

		// 设置job的Reducer类
		job.setReducerClass(BatchImportReducer.class);
		job.setOutputFormatClass(TableOutputFormat.class);
		return job.waitForCompletion(true) == true ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		// 启动job
		ToolRunner.run(new HBaseBatchImport(), args);
	}

	/**
	 * 自定义Mapper类
	 * @author hadoop
	 *
	 */
	public static class BatchImportMapper extends Mapper<LongWritable, Text, Text, Text> {
		private Text row_key = new Text();
		private Text col_data = new Text();

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String[] splits = value.toString().split("\t");
			if (splits.length == 2) {
				row_key.set(splits[0]);
				col_data.set(splits[1]);
				context.write(row_key,col_data);
			}
		}
	}
	
	/**
	 * 自定义Reducer类
	 * @author hadoop
	 *
	 */
	public static class BatchImportReducer extends TableReducer<Text, Text, NullWritable> {
		@Override
		protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, NullWritable, Mutation>.Context context) 
				throws IOException,InterruptedException {
		    for (Text text : values) {
		        Put put = new Put(Bytes.toBytes(key.toString()));
		        //列族为family，列名为qualifier
		        put.add(Bytes.toBytes("basic"), Bytes.toBytes("name"), Bytes.toBytes(text.toString()));
		        context.write(NullWritable.get(), put);
		    }
		}
	}
}
