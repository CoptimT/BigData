package cn.zxw.flume.client.loadbalancing;

import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

/**
 * example 04
 * 
 * 负载均衡客户端
 */
public class MyLoadBalance04 {
	
	public static void main(String[] args) {
		MyRpcClientFacade04 client = new MyRpcClientFacade04();
		client.init();
		for (int i = 0; i < 10; i++) {
			client.sendDataToFlume("Hello Flume " + i + " !");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		client.cleanUp();
	}
}

class MyRpcClientFacade04 {
	private RpcClient client;
	
	public void init() {
		Properties props = new Properties();
		props.put("client.type", "default_loadbalance");
		props.put("hosts", "h1 h2");
		props.put("hosts.h1", "192.168.73.128:5158");
		props.put("hosts.h2", "192.168.73.128:5159");
		props.put("host-selector", "random");//round_robin
		//props.put("host-selector", "cn.zxw.flume.client.loadbalancing.MyHostSelector");
		//props.put("backoff", "true");// Disabled by default.
		//props.put("maxBackoff", "10000");
		
		props.put("max-attempts", "3");
		props.put("batch-size", "100");
		props.put("connect-timeout", "1000");
		props.put("request-timeout", "1000");
		
		this.client = RpcClientFactory.getInstance(props);
	}
	public void sendDataToFlume(String data) {
		Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
		try {
			client.append(event);
		} catch (EventDeliveryException e) {
			client.close();
			client = null;
			init();
		}
	}
	public void cleanUp() {
		client.close();
	}
}