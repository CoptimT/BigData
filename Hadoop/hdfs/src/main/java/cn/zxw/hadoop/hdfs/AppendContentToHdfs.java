package cn.zxw.hadoop.hdfs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class AppendContentToHdfs {
	public static void main(String[] args) {
		try {
			appendToHdfs("this is test!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void appendToHdfs(String msg) throws IOException {
		Configuration conf = new Configuration();
		String filePath = "hdfs://10.10.25.14:8020/user/zhangxw/test/test.txt";
		FileSystem fs = FileSystem.get(URI.create(filePath), conf);
		conf.setBoolean("dfs.support.append", true);
		OutputStream out = fs.create(new Path(filePath));
		InputStream in = new BufferedInputStream(new ByteArrayInputStream(msg.getBytes()));
		IOUtils.copyBytes(in, out, 4096, true);
	}
}