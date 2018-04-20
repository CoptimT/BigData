package com.zxw.auto.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * client初试
 */
public class ClientInstance {
	@SuppressWarnings({ "resource", "unchecked" })
	public static void main(String[] args) {
		TransportClient client = null;
		try {
			Settings settings = Settings.builder()
					.put("client.transport.sniff", true)//自动嗅探整个集群状态，把其他ES节点ip添加到本地的客户端列表中
			        .put("cluster.name", "mycluster").build();
			//对于ES Client，有两种形式，一个是TransportClient，一个是NodeClient
			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node3.leap.com"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node4.leap.com"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node5.leap.com"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node6.leap.com"), 9300));
			
			Map<String, Object> json = new HashMap<String, Object>();
			json.put("factory","雪铁龙(进口)");
			json.put("brand","雪铁龙");
			json.put("series","雪铁龙C4 Aircross");
			IndexResponse response = client.prepareIndex("test", "bbs", "1").setSource(json).get();
			
			// Index name
			String _index = response.getIndex();
			// Type name
			String _type = response.getType();
			// Document ID (generated or not)
			String _id = response.getId();
			// Version (if it's the first time you index this document, you will get: 1)
			long _version = response.getVersion();
			// status has stored current instance statement.
			RestStatus status = response.status();
			
			System.out.println(_index+" - "+_type+" - "+_id+" - "+_version+" - "+status);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}finally {
			if(client != null) {
				client.close();
			}
		}
	}

}
