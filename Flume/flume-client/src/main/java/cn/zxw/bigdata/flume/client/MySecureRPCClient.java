package cn.zxw.bigdata.flume.client;

import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientConfigurationConstants;
import org.apache.flume.api.SecureRpcClientFactory;
import org.apache.flume.event.EventBuilder;

public class MySecureRPCClient {
	public static void main(String[] args) {
		MySecureRpcClientFacade client = new MySecureRpcClientFacade();
		// Initialize client with the remote Flume agent's host, port
		Properties props = new Properties();
		props.setProperty(RpcClientConfigurationConstants.CONFIG_CLIENT_TYPE, "thrift");
		props.setProperty("hosts", "h1");
		props.setProperty("hosts.h1", "client.example.org" + ":" + String.valueOf(41414));

		// Initialize client with the kerberos authentication related properties
		props.setProperty("kerberos", "true");
		props.setProperty("client-principal","flumeclient/client.example.org@EXAMPLE.ORG");
		props.setProperty("client-keytab", "/tmp/flumeclient.keytab");
		props.setProperty("server-principal","flume/server.example.org@EXAMPLE.ORG");
		client.init(props);

		// Send 10 events to the remote Flume agent. That agent should be
		// configured to listen with an ThriftSource.
		String sampleData = "Hello Flume!";
		for (int i = 0; i < 10; i++) {
			client.sendDataToFlume(sampleData);
		}
		client.cleanUp();
	}

	static class MySecureRpcClientFacade {
		private RpcClient client;
		private Properties properties;

		public void init(Properties properties) {
			this.properties = properties;
			// Create the ThriftSecureRpcClient instance by using SecureRpcClientFactory
			this.client = SecureRpcClientFactory.getThriftInstance(properties);
		}

		public void sendDataToFlume(String data) {
			Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
			try {
				client.append(event);
			} catch (EventDeliveryException e) {
				client.close();
				client = null;
				client = SecureRpcClientFactory.getThriftInstance(properties);
			}
		}

		public void cleanUp() {
			client.close();
		}
	}
}
