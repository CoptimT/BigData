package mrdp.chapter5.section1.ex1.reduceside;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import mrdp.util.MRDPUtils;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;

/**
 * Reduce端连接示例,Map端采用BloomFilter过滤
 * @author connor
 *
 */
public class ReduceSideJoinWithBloomFilterDriver {

	/**
	 * 1.Configuration传参
	 * 2.BloomFilter使用
	 * 3.分布式缓存
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static class UserJoinMapper extends Mapper<Object, Text, Text, Text> {
		private Text outkey = new Text();
		private Text outvalue = new Text();
		
		public void map(Object key, Text value, Context context)
					throws IOException, InterruptedException {
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());
			// If the reputation is greater than 1,500,
			// output the user ID with the value
			//通过reputation过滤
			if (Integer.parseInt(parsed.get("Reputation")) > 1500) {
				outkey.set(parsed.get("Id"));
				outvalue.set("A" + value.toString());
				context.write(outkey, outvalue);
			}
		}
	}
	
	public static class CommentJoinMapperWithBloom extends
					Mapper<Object, Text, Text, Text> {
		private BloomFilter bfilter = new BloomFilter();
		private Text outkey = new Text();
		private Text outvalue = new Text();
		
		public void setup(Context context) throws IOException {
			Path[] files = DistributedCache.getLocalCacheFiles(context.getConfiguration());
			DataInputStream strm = new DataInputStream(
			new FileInputStream(new File(files[0].toString())));
			bfilter.readFields(strm);
		}
		
		public void map(Object key, Text value, Context context)
						throws IOException,InterruptedException {
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());
			String userId = parsed.get("UserId");
			//通过BloomFilter过滤
			if (bfilter.membershipTest(new Key(userId.getBytes()))) {
				outkey.set(userId);
				outvalue.set("B" + value.toString());
				context.write(outkey, outvalue);
			}
		}
	}
}
