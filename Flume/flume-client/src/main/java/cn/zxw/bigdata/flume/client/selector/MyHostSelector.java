package cn.zxw.bigdata.flume.client.selector;

import java.util.Iterator;
import java.util.List;

import org.apache.flume.api.HostInfo;
import org.apache.flume.api.LoadBalancingRpcClient.HostSelector;

public class MyHostSelector implements HostSelector{

	public void setHosts(List<HostInfo> hosts) {
		// TODO Auto-generated method stub
		
	}

	public Iterator<HostInfo> createHostIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public void informFailure(HostInfo failedHost) {
		// TODO Auto-generated method stub
		
	}

}
