package cn.zxw.flume.client.defaultcli;

import java.nio.charset.Charset;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

/**
 * example 01
 * 
 * 缺省客户端发送数据示例
 */
public class MyApp01 {

	public static void main(String[] args) {
		MyRpcClientFacade client = new MyRpcClientFacade();
		client.init("192.168.73.128", 5158);
		String sampleData = "Hello Flume!";
		for (int i = 0; i < 10; i++) {
			client.sendDataToFlume(sampleData+i);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		client.cleanUp();
	}
}

class MyRpcClientFacade {
	private RpcClient client;
	private String hostname;
	private int port;
	
	public void init(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		// NettyAvroRpcClient and ThriftRpcClient
		this.client = RpcClientFactory.getDefaultInstance(hostname, port);
		// Use the following method to create a thrift client (instead of the above line):
		// this.client = RpcClientFactory.getThriftInstance(hostname, port);
	}
	public void sendDataToFlume(String data) {
		// Create a Flume Event object that encapsulates the sample data
		Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
		// Send the event
		try {
			client.append(event);
		} catch (EventDeliveryException e) {
			client.close();
			client = null;
			client = RpcClientFactory.getDefaultInstance(hostname, port);
		}
	}
	public void cleanUp() {
		client.close();
	}
}