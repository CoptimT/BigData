package action.hadoop.chapter5.section6.demo562;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Shunxu {

	public static void main(String[] args) throws Exception {
		Path job1inpath=new Path(args[0]);
        Path job1outpath=new Path(args[1]);
        Path job2outpath=new Path(args[2]);
        Path job3outpath=new Path(args[3]);
        
		Configuration conf1 = new Configuration();
        Job job1 = new Job(conf1, "job1");
        job1.setJarByClass(Shunxu.class);
        FileInputFormat.addInputPath(job1, job1inpath);
        FileOutputFormat.setOutputPath(job1, job1outpath);
        job1.waitForCompletion(true);
        
        Configuration conf2 = new Configuration();
        Job job2 = new Job(conf2, "job2");
        job2.setJarByClass(Shunxu.class);
        FileInputFormat.addInputPath(job2, job1outpath);//
        FileOutputFormat.setOutputPath(job2, job2outpath);
        job2.waitForCompletion(true);
        
        Configuration conf3 = new Configuration();
        Job job3 = new Job(conf3, "job3");
        job3.setJarByClass(Shunxu.class);
        FileInputFormat.addInputPath(job3, job2outpath);
        FileOutputFormat.setOutputPath(job3, job3outpath);
        job3.waitForCompletion(true);
	}

}
