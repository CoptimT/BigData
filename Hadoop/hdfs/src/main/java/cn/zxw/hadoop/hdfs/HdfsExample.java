package cn.zxw.hadoop.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsExample {

	public static void main(String[] args) {
		String inputPath = args[0];
		String date = args[1];
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			String fsPath = inputPath+"/day="+date;
			Path path = new Path(fsPath);
			if(fs.exists(path)){
				System.out.println("InputPath add: " + fsPath);
			}else{
				System.out.println("InputPath not exist: " + fsPath);
			}
			fs.close();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
