package com.zxw.auto.elasticsearch.utl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ESUtil {
	public static String clusterName = "mycluster";
	public static String indexName   = "test";
	public static String typeName    = "user";
	
	public static String HOSTNAME    = "10.116.27.131";
	
	@SuppressWarnings({ "resource", "unchecked" })
	public static TransportClient getTransportClient() throws UnknownHostException {
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
		        .put("cluster.name", clusterName).build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(HOSTNAME), 9300));
		return client;
	}
	
}
