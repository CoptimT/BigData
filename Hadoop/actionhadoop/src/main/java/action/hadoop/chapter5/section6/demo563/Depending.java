package action.hadoop.chapter5.section6.demo563;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Depending {
	public static void main(String[] args) throws Exception {
		Path job1inpath=new Path(args[0]);
        Path job1outpath=new Path(args[1]);
        Path job2inpath=new Path(args[2]);
        Path job2outpath=new Path(args[3]);
        Path job3outpath=new Path(args[4]);
        
		Configuration confx = new Configuration();
		Job job1=new Job(confx);
		job1.setJarByClass(Depending.class);
        FileInputFormat.addInputPath(job1, job1inpath);
        FileOutputFormat.setOutputPath(job1, job1outpath);
	    ControlledJob jobx = new ControlledJob(job1,null);
	    jobx.setJobName("jobx");
	    
	    Configuration confy = new Configuration();
	    Job job2=new Job(confy);
		job2.setJarByClass(Depending.class);
		FileInputFormat.addInputPath(job2, job2inpath);
        FileOutputFormat.setOutputPath(job2, job2outpath);
	    ControlledJob joby = new ControlledJob(job2,null);
	    joby.setJobName("joby");
	    
	    Configuration confz = new Configuration();
	    Job job3=new Job(confz);
		job3.setJarByClass(Depending.class);
		FileInputFormat.addInputPath(job3, job1outpath);//
		FileInputFormat.addInputPath(job3, job2outpath);//
        FileOutputFormat.setOutputPath(job3, job3outpath);
	    ControlledJob jobz = new ControlledJob(job3,null);
	    jobz.setJobName("jobz");
	    
	    //依赖
	    jobz.addDependingJob(jobx);
	    jobz.addDependingJob(joby);
	    
	    JobControl jobControl=new JobControl("jobzdependonjobxy");
	    jobControl.addJob(jobx);
	    jobControl.addJob(joby);
	    jobControl.addJob(jobz);
	    Thread controller = new Thread(jobControl);
	    controller.start();
	    while (!jobControl.allFinished()) {
	       try {
	          Thread.sleep(100);
	       } catch (InterruptedException e) {}
	    }
	    System.out.println("jobx.getJobState():"+jobx.getJobState());
	    System.out.println("joby.getJobState():"+joby.getJobState());
	    System.out.println("jobz.getJobState():"+jobz.getJobState());
	    jobControl.stop();
	}
	
}
