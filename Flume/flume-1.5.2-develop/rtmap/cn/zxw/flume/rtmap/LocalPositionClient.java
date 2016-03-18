package cn.zxw.flume.rtmap;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import cn.zxw.flume.util.DateUtils;

/**
 * 本地集群环境,Flume客户端发送定位数据
 * @author zhangxw
 */
public class LocalPositionClient {
	
	public static final String POS_PATH = "C:\\Temp\\zjr\\";
	public static final String HOST = "datanode1:5158";
	
	public static void main(String[] args) throws ParseException {
		if(args == null || args.length == 0){
			args = new String[]{ DateUtils.formatDate(DateUtils.getPreDay(null), "yyyy-MM-dd") };/** 昨天 **/
		}
		System.out.println("client start : " + Arrays.toString(args));
		for(String day : args){
			uploadLogByDay(day);
		}
		
	}
	
	public static void uploadLogByDay(String day){
		System.out.println("start date : " + day);
		/** Flume Client **/
		MyRpcClientFacade client = new MyRpcClientFacade();
		client.init();
		/** 根目录 **/
		File path=new File(POS_PATH);
		File[] buildsPath=path.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.startsWith("86") && name.length()==18){
					return true;
				}
				System.out.println("原始定位数据目录下文件名称不满足建筑ID条件："+name);
				return false;
			}
		});
		//建筑目录
		for(File buildPath:buildsPath){
			String buildId=buildPath.getName();
			if(!buildPath.isDirectory()){
				System.out.println("原始定位数据目录下文件不是目录,不满足条件：" + buildId);
				continue;
			}
			System.out.println("start build : " + buildId);
			File[] dateFiles=buildPath.listFiles();
			for(File dateFile:dateFiles){
				String date = dateFile.getName();
				if(!day.equals(date) || !dateFile.isDirectory()){
					continue;
				}
				File[] files=dateFile.listFiles();
				for(File file:files){
					Map<String,String> headers=new HashMap<String, String>();
					headers.put("file", file.getName());
					List<String> lines = null;
					try {
						lines = FileUtils.readLines(file);
						for(String line : lines){
							client.sendDataToFlume(line, headers);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("file finish : " + file.getName());
				}
			}
		}
		client.cleanUp();//关闭客户端
		System.out.println("finish : " + day);
	}
	
	static class MyRpcClientFacade {
		
		private RpcClient client;
		
		public void init() {
			Properties props = new Properties();
			props.put("client.type", "default");
			props.put("hosts", "h1");
			props.put("hosts.h1", HOST);
			props.put("batch-size", "10");
			props.put("connect-timeout", "20000");
			props.put("request-timeout", "20000");
			
			this.client = RpcClientFactory.getInstance(props);
			System.out.println("RpcClient init finish!");
		}
		
		public void sendDataToFlume(String data, Map<String,String> headers) {
			Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
			event.setHeaders(headers);
			try {
				client.append(event);
			} catch (EventDeliveryException e) {
				cleanUp();
				init();
				e.printStackTrace();
			}
		}
		
		public void cleanUp() {
			if(client != null){
				client.close();
				client = null;
			}
			System.out.println("RpcClient clean up finish!");
		}
	}
}
