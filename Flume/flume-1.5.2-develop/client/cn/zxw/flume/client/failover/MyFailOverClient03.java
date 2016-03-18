package cn.zxw.flume.client.failover;

import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

/**
 * example 03
 * 
 * 容错客户端
 */
public class MyFailOverClient03 {
	
	public static void main(String[] args) {
		MyRpcClientFacade03 client = new MyRpcClientFacade03();
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

class MyRpcClientFacade03 {
	private RpcClient client;
	
	public void init() {
		Properties props = new Properties();
		props.put("client.type", "default_failover");//default (for avro) or thrift (for thrift)
		// List of hosts (space-separated list of user-chosen host aliases)
		props.put("hosts", "h1 h2");
		// host/port pair for each host alias
		props.put("hosts.h1", "192.168.73.128:5158");
		props.put("hosts.h2", "192.168.73.128:5159");
		props.put("max-attempts", "3");
		props.put("batch-size", "100");
		props.put("connect-timeout", "20000");
		props.put("request-timeout", "20000");
		
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