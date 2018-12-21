package com.x.cms.assemble.control.jaxrs.search;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AppFilter  implements Serializable {
	private String id;
	private String appName;
	private long count;
	
	public AppFilter( String _id, String _appName, long _count ){
		this.id = _id;
		this.appName = _appName;
		this.count = _count;
	}
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	
}
