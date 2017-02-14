package com.x.cms.assemble.control.jaxrs.search;

public class DepartmentFilter {
	
	private String id;
	private String departmentName;	
	private long count;
	
	public DepartmentFilter(String _id, String _departmentName, long _count ){
		this.id = _id;
		this.departmentName = _departmentName;
		this.count = _count;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	
	
}
