package com.x.cms.assemble.control.jaxrs.search;

public class CatagoryFilter {
	private String appId;
	private String appName;
	private String catagoryId;
	private String cataogryName;	
	private long count;
	
	public CatagoryFilter(String _appId, String _appName, String _catagoryId, String _catagoryName, long _count ){
		this.appId = _appId;
		this.appName = _appName;
		this.catagoryId = _catagoryId;
		this.cataogryName = _catagoryName;
		this.count = _count;
	}
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getCatagoryId() {
		return catagoryId;
	}
	public void setCatagoryId(String catagoryId) {
		this.catagoryId = catagoryId;
	}
	public String getCataogryName() {
		return cataogryName;
	}
	public void setCataogryName(String cataogryName) {
		this.cataogryName = cataogryName;
	}
}
