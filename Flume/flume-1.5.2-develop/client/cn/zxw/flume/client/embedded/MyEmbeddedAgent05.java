package cn.zxw.flume.client.embedded;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.agent.embedded.EmbeddedAgent;
import org.apache.flume.event.EventBuilder;

import com.google.common.collect.Lists;

/**
 * example 05
 * 
 * 嵌入式代理
 */
public class MyEmbeddedAgent05 {

	public static void main(String[] args) throws EventDeliveryException {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("channel.type", "memory");
		properties.put("channel.capacity", "200");
		properties.put("sinks", "sink1 sink2");
		properties.put("sink1.type", "avro");
		properties.put("sink2.type", "avro");
		properties.put("sink1.hostname", "192.168.73.128");
		properties.put("sink1.port", "5565");
		properties.put("sink2.hostname", "192.168.73.128");
		properties.put("sink2.port",  "5566");
		properties.put("processor.type", "load_balance");

		EmbeddedAgent agent = new EmbeddedAgent("myagent");
		agent.configure(properties);
		agent.start();

		List<Event> events = Lists.newArrayList();
		for(int i=1;i<=5;i++){
			Event event = EventBuilder.withBody("data-"+i, Charset.forName("UTF-8"));
			events.add(event);
		}
		
		agent.putAll(events);

		agent.stop();
		
	}

}
