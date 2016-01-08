package cn.zxw.bigdata.flume.client;

import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

/**
 * Default Configure Client Instance
 */
public class MyDefaultConfigClient {
	
	public static void main(String[] args) {
		MyConfigRpcClientFacade client = new MyConfigRpcClientFacade();
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

class MyConfigRpcClientFacade {
	private RpcClient client;
	
	public void init() {
		Properties props = new Properties();
		props.put("client.type", "default");//default (for avro) or thrift (for thrift)
		// List of hosts (space-separated list of user-chosen host aliases)
		props.put("hosts", "h1");
		// host/port pair for each host alias
		props.put("hosts.h1", "192.168.73.128:5158");
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