package cn.zxw.bigdata.flume.client.event;

import org.apache.flume.event.SimpleEvent;

public class MyEvent extends SimpleEvent{
	private String type=null;

	public MyEvent(String type) {
		super();
		this.type=type;
	}
	public MyEvent() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
