package com.x.cms.assemble.control.jaxrs.search;

public class CategoryFilter {
	private String appId;
	private String appName;
	private String categoryId;
	private String cataogryName;	
	private long count;
	
	public CategoryFilter(String _appId, String _appName, String _categoryId, String _categoryName, long _count ){
		this.appId = _appId;
		this.appName = _appName;
		this.categoryId = _categoryId;
		this.cataogryName = _categoryName;
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
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCataogryName() {
		return cataogryName;
	}
	public void setCataogryName(String cataogryName) {
		this.cataogryName = cataogryName;
	}
}
