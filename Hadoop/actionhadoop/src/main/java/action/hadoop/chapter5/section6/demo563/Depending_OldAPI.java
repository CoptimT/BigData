package action.hadoop.chapter5.section6.demo563;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapred.jobcontrol.JobControl;

public class Depending_OldAPI {
	public static void main(String[] args) throws Exception {
		Path job1inpath=new Path(args[0]);
        Path job1outpath=new Path(args[1]);
        Path job2inpath=new Path(args[2]);
        Path job2outpath=new Path(args[3]);
        Path job3outpath=new Path(args[4]);
        
		Configuration conf = new Configuration();
		JobConf confx=new JobConf(conf,Depending_OldAPI.class);
		FileInputFormat.addInputPath(confx, job1inpath);
		FileOutputFormat.setOutputPath(confx, job1outpath);
	    Job jobx = new Job(confx);
	    jobx.setJobName("jobx");
	    
	    JobConf confy=new JobConf(conf,Depending_OldAPI.class);
	    FileInputFormat.addInputPath(confy, job2inpath);
		FileOutputFormat.setOutputPath(confy, job2outpath);
	    Job joby = new Job(confy);
	    jobx.setJobName("joby");
	    
	    JobConf confz=new JobConf(conf,Depending_OldAPI.class);
	    FileInputFormat.addInputPath(confz, job1outpath);
	    FileInputFormat.addInputPath(confz, job2outpath);
		FileOutputFormat.setOutputPath(confz, job3outpath);
	    Job jobz = new Job(confz);
	    jobx.setJobName("jobz");
	    
	    //依赖
	    jobz.addDependingJob(jobx);
	    jobz.addDependingJob(joby);
	    
	    JobControl jobControl=new JobControl("jobzdependonjobxy");
	    jobControl.addJob(jobx);
	    jobControl.addJob(joby);
	    jobControl.addJob(jobz);
	    Thread theController = new Thread(jobControl);
	    theController.start();
	    while (!jobControl.allFinished()) {
	      try {
	        Thread.sleep(500);
	      } catch (Exception e) {}
	    }
	    System.out.println("Some jobs failed:"+jobControl.getFailedJobs().size());
	    jobControl.stop();
	}
	
}
