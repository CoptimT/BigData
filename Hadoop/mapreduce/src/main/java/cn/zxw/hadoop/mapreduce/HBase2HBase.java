package cn.zxw.hadoop.mapreduce;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * HBase to HBase
 */
@SuppressWarnings("deprecation")
public class HBase2HBase extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new HBase2HBase(), args);
		System.exit(result);
	}

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 5) {
			System.out.println("Usage hadoop jar hbase2HBase.jar quorum inputTable outputTable cacheFilePath");
			return 1;
		}

		// 获取zk地址
		String quorum = args[0];
		// 获取输入数据的HBase表名
		String inputTable = args[1];
		// 获取输出数据的HBase表名
		String outputTable = args[2];
		// 获取分布式缓存在HDFS上的目录
		String cacheFilePath = args[3];

		Configuration conf = HBaseConfiguration.create();
		// 设置HBase的连接信息
		conf.set("hbase.zookeeper.quorum", quorum);
		// 设置HBase输入的表名
		conf.set(TableInputFormat.INPUT_TABLE, inputTable);
		// 设置HBase输出的表名
		conf.set(TableOutputFormat.OUTPUT_TABLE, outputTable);
		// 设置HBase客户端连接超时时间
		conf.set("dfs.client.socket-timeout", "180000");

		// 设置分布式缓存文件的存放路径
		Path inPath = new Path(cacheFilePath);
		String inPathLink = inPath.toUri().toString() + "#" + "DIYFileName";
		DistributedCache.addCacheFile(new URI(inPathLink), conf);

		Job job = Job.getInstance(conf, this.getClass().getSimpleName());
		job.setJarByClass(HBase2HBase.class);

		job.setInputFormatClass(TableInputFormat.class);

		// 设置job的Map类
		job.setMapperClass(MyMapper.class);

		// 设置Map类的输出类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		// 设置mapper端压缩
		conf.set("mapreduce.map.output.compress", "true");
		
		// 设置job的Reducer类
		job.setReducerClass(MyReducer.class);
		job.setOutputFormatClass(TableOutputFormat.class);
		return job.waitForCompletion(true) == true ? 0 : 1;
	}

	/**
	 * 自定义mapper类,接收HBase的数据并处理
	 */
	public static class MyMapper extends TableMapper<Text, Text> {
		// 存储mysql分布式缓存中的数据
		private Map<String, String> mysqlDataMap = new HashMap<>();
		// 定义map输出的key
		private Text rowKey = new Text();
		// 定义map输出的value
		private Text column = new Text();

		@SuppressWarnings("resource")
		@Override
		protected void setup(Mapper<ImmutableBytesWritable, Result, Text, Text>.Context context) throws IOException, InterruptedException {
			// 获得当前作业的DistributedCache相关文件
			String dmpInfo = null;
			// 读取缓存文件数据并放入HashMap中
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("DIYFileName")));
			while (null != (dmpInfo = br.readLine())) {
				// 每行数据格式：办公商务_其他 30001
				String[] splits = dmpInfo.split("\t");
				if (splits.length == 2) {
					mysqlDataMap.put(splits[0], splits[1]);
				}
			}
		}

		@Override
		protected void map(ImmutableBytesWritable key, Result value, Mapper<ImmutableBytesWritable, Result, Text, Text>.Context context) 
				throws IOException,InterruptedException {
			// 获取HBase中的age列内容
			byte[] age = value.getValue(Bytes.toBytes("info"), Bytes.toBytes("age"));
			// 获取HBase中的性别列内容
			byte[] gender = value.getValue(Bytes.toBytes("info"), Bytes.toBytes("gender"));
			
			rowKey.set(key.get());
			column.set(age + "\t" + gender);
			context.write(rowKey, column);
		}
	}

	/**
	 * 接收mapper端输出的数据,并将数据写入到另一个HBase表中
	 */
	public static class MyReducer extends TableReducer<Text, Text, NullWritable> {
		@Override
		protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, NullWritable, Mutation>.Context context)
				throws IOException,InterruptedException {
			for (Text value : values) {
				String line = value.toString();
				// 按制表符拆分value内容：第一列为age，第二列为gender
				String[] splitd = line.split("\t");

				// 组装HBase的一行内容
				Put put = new Put(Bytes.toBytes(new StringBuilder(key.toString()).reverse().toString()));
				if (!"0".equals(splitd[0])) {
					put.add(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes(splitd[0]));
				}
				if (!"0".equals(splitd[1])) {
					put.add(Bytes.toBytes("info"), Bytes.toBytes("gender"),Bytes.toBytes(splitd[1]));
				}
				
				// 向app列族中添加一个列app:_0,否则这个列族一列数据都没有时，使用sql查询is null时出查询不到
				put.add(Bytes.toBytes("app"), "_0".getBytes(), "".getBytes());

				context.write(NullWritable.get(), put);
			}
		}
	}
}
