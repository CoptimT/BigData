package mrdp.chapter5.section1.ex1.reduceside;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import mrdp.util.MRDPUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

/**
 * Reduce端连接示例
 * @author connor
 *
 */
public class ReduceSideJoinDriver {
	/**
	 * 1.MultipleInputs使用
	 * 2.Configuration传参
	 * 3.Reduce端连接逻辑及方式
	 */
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
        Job job = new Job(conf, "ReduceSideJoin");
        job.setJarByClass(ReduceSideJoinDriver.class);
		// Use MultipleInputs to set which input uses what mapper
		// This will keep parsing of each data set separate from a logical standpoint
		// The first two elements of the args array are the two inputs
		MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class,
		UserJoinMapper.class);
		MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class,
		CommentJoinMapper.class);
		job.getConfiguration().set("join.type", args[2]);

	}
	
	public static class UserJoinMapper extends Mapper<Object, Text, Text, Text> {
		private Text outkey = new Text();
		private Text outvalue = new Text();
		
		public void map(Object key, Text value, Context context)
						throws IOException, InterruptedException {
			// Parse the input string into a nice map
			Map<String, String> parsed =
			MRDPUtils.transformXmlToMap(value.toString());
			String userId = parsed.get("Id");
			// The foreign join key is the user ID
			outkey.set(userId);
			// Flag this record for the reducer and then output
			outvalue.set("A" + value.toString());
			context.write(outkey, outvalue);
		}
		
	}
	
	public static class CommentJoinMapper extends Mapper<Object, Text, Text, Text> {
		private Text outkey = new Text();
		private Text outvalue = new Text();
		
		public void map(Object key, Text value, Context context)
						throws IOException, InterruptedException {
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());
			// The foreign join key is the user ID
			outkey.set(parsed.get("UserId"));
			// Flag this record for the reducer and then output
			outvalue.set("B" + value.toString());
			context.write(outkey, outvalue);
		}
		
	}
	
	public static class UserJoinReducer extends Reducer<Text, Text, Text, Text> {
		private static final Text EMPTY_TEXT = new Text("");
		private Text tmp = new Text();
		private ArrayList<Text> listA = new ArrayList<Text>();
		private ArrayList<Text> listB = new ArrayList<Text>();
		private String joinType = null;
		
		public void setup(Context context) {
			// Get the type of join from our configuration
			joinType = context.getConfiguration().get("join.type");
		}
		
		public void reduce(Text key, Iterable<Text> values, Context context)
								throws IOException, InterruptedException {
			// Clear our lists
			listA.clear();
			listB.clear();
			// iterate through all our values, binning each record based on what
			// it was tagged with. Make sure to remove the tag!
			Iterator<Text> iterator=values.iterator();
			while (iterator.hasNext()) {
				tmp = iterator.next();
				if (tmp.charAt(0) == 'A') {
					listA.add(new Text(tmp.toString().substring(1)));
				} else if (tmp.charAt('0') == 'B') {
					listB.add(new Text(tmp.toString().substring(1)));
				}
			}
			// Execute our join logic now that the lists are filled
			executeJoinLogic(context);
		}
		
		/**
		 * 内连接,左外连接，右外连接，全外连接<br>
		 * 交叉连接 cross join (得到的结果是两表记录数的乘积，即A表10条，B表20表之话，记录数：200)<br>
		 * 还有自身连接,只是同一个表自己连自己<br>
		 * 
		 * @param context
		 * @throws IOException
		 * @throws InterruptedException
		 */
		private void executeJoinLogic(Context context)
					throws IOException, InterruptedException {
			//内连接
			if (joinType.equalsIgnoreCase("inner")) {
				// If both lists are not empty, join A with B
				if (!listA.isEmpty() && !listB.isEmpty()) {
					for (Text A : listA) {
						for (Text B : listB) {
							context.write(A, B);
						}
					}
				}
			}
			//左外连接
			if (joinType.equalsIgnoreCase("leftouter")) {
				// For each entry in A,
				for (Text A : listA) {
					// If list B is not empty, join A and B
					if (!listB.isEmpty()) {
						for (Text B : listB) {
							context.write(A, B);
						}
					} else {
						// Else, output A by itself
						context.write(A, EMPTY_TEXT);
					}
				}
			}
			//右外连接
			if (joinType.equalsIgnoreCase("rightouter")) {
				// For each entry in B,
				for (Text B : listB) {
					// If list A is not empty, join A and B
					if (!listA.isEmpty()) {
						for (Text A : listA) {
							context.write(A, B);
						}
					} else {
						// Else, output B by itself
						context.write(EMPTY_TEXT,B);
					}
				}
			}
			//全外连接
			if (joinType.equalsIgnoreCase("fullouter")) {
				// If list A is not empty
				if (!listA.isEmpty()) {
					// For each entry in A
					for (Text A : listA) {
						// If list B is not empty, join A with B
						if (!listB.isEmpty()) {
							for (Text B : listB) {
								context.write(A, B);
							}
						} else {
							// Else, output A by itself
							context.write(A, EMPTY_TEXT);
						}
					}
				} else {
					// If list A is empty, just output B
					for (Text B : listB) {
						context.write(EMPTY_TEXT, B);
					}
				}
			}
			//
		}
	}
}
