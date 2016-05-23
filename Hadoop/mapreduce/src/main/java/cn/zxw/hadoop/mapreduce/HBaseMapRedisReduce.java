package cn.zxw.hadoop.mapreduce;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cn.zxw.hadoop.mapreduce.output.ReidsOutputFormat;

/**
 * 分布式缓存与HBase关联结果，保存Redis
 * @author hadoop
 */
@SuppressWarnings("deprecation")
public class HBaseMapRedisReduce extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new HBaseMapRedisReduce(), args);
	}

	public int run(String[] args) throws Exception {
		if (args.length != 4) {
			System.out.println("Usage:hadoop jar HBaseRedis.jar quorum tableName cacheFilePath outputPath");
		}

		// Zookeeper的连接地址
		String quorum = args[0];
		// HBase的表名
		String tableName = args[1];
		// 分布式文件的路径
		String cacheFilePath = args[2];
		// 输出目录
		String outputPath = args[3];

		final Configuration conf = new Configuration();
		// 设置HBase的连接信息
		conf.set("hbase.zookeeper.quorum", quorum);
		conf.set("dfs.client.socket-timeout", "180000");

		// 设置分布式缓存文件的存放路径
		Path inPath = new Path(cacheFilePath);
		String inPathLink = inPath.toUri().toString() + "#" + "DIYFileName";
		//String inPathLink = inPath.toUri().toString();
		DistributedCache.addCacheFile(new URI(inPathLink), conf);

		// 初始化Scan变量并设置参数
		Scan scan = new Scan();
		scan.setCaching(1000);
		scan.setCacheBlocks(false);

		Job job = Job.getInstance(conf);
		TableMapReduceUtil.addDependencyJars(job);
		job.setJarByClass(HBaseMapRedisReduce.class);

		TableMapReduceUtil.initTableMapperJob(tableName, scan, MyMapper.class, Text.class, Text.class, job);

		// 自定义redis的outputformat类，写入到redis
		job.setReducerClass(MyReducer.class);
		job.setOutputFormatClass(ReidsOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		// 设置输出数据的路径
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		return job.waitForCompletion(true) == true ? 0 : 1;
	}

	public static class MyMapper extends TableMapper<Text, Text> {
		private Map<String, String> adGroupIds = new HashMap<String, String>();
		private Text imeiText = new Text();
		private Text adGroupIdsText = new Text();

		@SuppressWarnings("resource")
		@Override
		protected void setup(Mapper<ImmutableBytesWritable, Result, Text, Text>.Context context) throws IOException, InterruptedException {
			String adGroupIdInfo = null;
			// 读取缓存文件数据并放入hashmap中
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("DIYFileName")));
			while (null != (adGroupIdInfo = br.readLine())) {
				String[] splits = adGroupIdInfo.split("\t");
				if (splits.length == 2) {
					adGroupIds.put(splits[0], splits[1]);
				}
			}
		}

		@Override
		protected void map(ImmutableBytesWritable key, Result value, Mapper<ImmutableBytesWritable, Result, Text, Text>.Context context) throws IOException,
				InterruptedException {
			HashSet<String> hbaseTagSet = new HashSet<String>();
			StringBuilder adGroupIdsSb = new StringBuilder();
			// 从HBase的tags列中获取数据
			String tags = new String(value.getValue(Bytes.toBytes("data"), Bytes.toBytes("reyun_tags")));
			String[] splitTags = tags.split(",");
			for (String tag : splitTags) {
				hbaseTagSet.add(tag);
			}

			// 将adGroupIds中的每一条数据处理放入set后，看是否包含在hbaseTagSet中，如包含，则将adGroupId加入到sb对象中
			for (String adGroupId : adGroupIds.keySet()) {
				HashSet<String> adGroupIdsSet = new HashSet<>();
				String category = adGroupIds.get(adGroupId);
				String[] splits = category.split(",");
				for (String split : splits) {
					adGroupIdsSet.add(split);
				}
				if (hbaseTagSet.containsAll(adGroupIdsSet)) {
					adGroupIdsSb.append(adGroupId).append(",");
				}
			}

			// 数据输出
			if (adGroupIdsSb.length() != 0) {
				// 因为存入HBase时，行键进行了反转，所以此处要反转回来
				imeiText.set(new StringBuilder(new String(key.get())).reverse().toString());
				// 取消append操作时最后添加的逗号
				String adGroupIdsStr = adGroupIdsSb.toString();
				adGroupIdsText.set(adGroupIdsStr.substring(0, adGroupIdsStr.length() - 1));
				context.write(imeiText, adGroupIdsText);
			}
		}
	}
	
	public static class MyReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text imei, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
			 for(Text value : values){
				 context.write(new Text(imei), new Text(value));
			 }
		}
	}
}
