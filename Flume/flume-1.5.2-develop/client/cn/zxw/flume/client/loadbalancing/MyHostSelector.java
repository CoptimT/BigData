package cn.zxw.flume.client.loadbalancing;

import java.util.Iterator;
import java.util.List;

import org.apache.flume.api.HostInfo;
import org.apache.flume.api.LoadBalancingRpcClient.HostSelector;

public class MyHostSelector implements HostSelector{

	@Override
	public void setHosts(List<HostInfo> hosts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<HostInfo> createHostIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void informFailure(HostInfo failedHost) {
		// TODO Auto-generated method stub
		
	}

}
