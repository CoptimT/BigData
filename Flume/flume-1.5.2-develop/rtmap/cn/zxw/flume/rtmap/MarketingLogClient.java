package cn.zxw.flume.rtmap;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import cn.zxw.flume.util.DateUtils;

public class MarketingLogClient {
	
	public static final String PATH="C:\\Users\\zhangxw\\Desktop\\log\\market_log_data";
	
	public static void main(String[] args) throws ParseException {
		if(args != null && args.length > 0){
			for(String day : args){
				uploadLogByDay(day);
			}
		}else{
			/** 昨天 **/
			String yesterday = DateUtils.formatDate(DateUtils.getPreDay(null), "yyyy-MM-dd");
			uploadLogByDay(yesterday);
		}
		
	}
	
	public static void uploadLogByDay(String day){
		
		String year=day.split("-")[0];
		System.out.println("开始上传日志："+day);//log
		/** Flume Client **/
		MyRpcClientFacade client = new MyRpcClientFacade();
		client.init();
		/** 根目录 **/
		File path=new File(PATH);
		File[] buildsPath=path.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.startsWith("86") && name.length()==18){
					return true;
				}
				System.out.println("建筑根目录名称不满足条件："+name);//log
				return false;
			}
		});
		//建筑目录
		for(File buildPath:buildsPath){
			String buildId=buildPath.getName();
			if(!"860100010010300001".equals(buildId)){//test
				continue;
			}
			System.out.println(buildId);//log
			File[] logTypes=buildPath.listFiles();
			//日志类型目录
			for(File logTypePath:logTypes){
				String logType=logTypePath.getName();
				System.out.println(logType);//log
				//具体日志文件
				String pathname = logTypePath.getAbsolutePath()+File.separator+year+File.separator+day+".csv";
				File file=new File(pathname);
				if(file.exists()){
					System.out.println(pathname);//log
					Map<String,String> headers=new HashMap<String, String>();
					headers.put("build", buildId);
					headers.put("logType", logType);
					headers.put("year", year);
					try {
						client.sendDataToFlume(FileUtils.readFileToString(file), headers);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		//关闭客户端
		client.cleanUp();
		System.out.println("上传日志结束："+day);
	}
	
	static class MyRpcClientFacade {
		
		private RpcClient client;
		
		public void init() {
			Properties props = new Properties();
			props.put("client.type", "default");
			props.put("hosts", "h1");
			props.put("hosts.h1", "192.168.73.128:5158");
			props.put("batch-size", "10");
			props.put("connect-timeout", "20000");
			props.put("request-timeout", "20000");
			
			this.client = RpcClientFactory.getInstance(props);
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
		}
	}
}
