package storm.analytics.utilities;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class NavigationEntry implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String pageType;
	private Map otherData;

	public NavigationEntry(String userId, String pageType, Map otherData) {
		this.userId = userId;
		this.pageType = pageType;
		this.otherData = otherData;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPageType() {
		return pageType;
	}
	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	
	public Map getOtherData() {
		return otherData;
	}
	
	public void setOtherData(Map otherData) {
		this.otherData = otherData;
	}
	
	@Override
	public String toString() {
		String ret = "User:" + userId + " navigating a " + pageType;
		if(otherData!=null)
			ret += " page with "+otherData.toString();
		return ret;
	}
	
}
