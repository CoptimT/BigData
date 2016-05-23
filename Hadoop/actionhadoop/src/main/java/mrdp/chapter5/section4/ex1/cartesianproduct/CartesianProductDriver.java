package mrdp.chapter5.section4.ex1.cartesianproduct;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

/**
 * 笛卡尔乘积
 * @author connor
 *
 */
public class CartesianProductDriver {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// Configure the join type
		JobConf conf = new JobConf("Cartesian Product");
		conf.setJarByClass(CartesianProductDriver.class);
		conf.setMapperClass(CartesianMapper.class);
		conf.setNumReduceTasks(0);
		conf.setInputFormat(CartesianInputFormat.class);
		// Configure the input format
		CartesianInputFormat.setLeftInputInfo(conf, TextInputFormat.class, args[0]);
		CartesianInputFormat.setRightInputInfo(conf, TextInputFormat.class, args[0]);
		TextOutputFormat.setOutputPath(conf, new Path(args[1]));
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		RunningJob job = JobClient.runJob(conf);
		while (!job.isComplete()) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		System.exit(job.isSuccessful() ? 0 : 1);

	}

}
