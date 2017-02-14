package com.x.cms.assemble.control.jaxrs.search;

public class CompanyFilter {
	
	private String id;
	private String companyName;	
	private long count;

	public CompanyFilter(String _id, String _companyName, long _count ){
		this.id = _id;
		this.companyName = _companyName;
		this.count = _count;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	
}
