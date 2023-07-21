package com.x.cms.assemble.control.jaxrs.search;

public class UnitNameFilter {
	
	private String id;
	private String unitName;	
	private long count;
	
	public UnitNameFilter(String _id, String _unitName, long _count ){
		this.id = _id;
		this.unitName = _unitName;
		this.count = _count;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	
	
}
