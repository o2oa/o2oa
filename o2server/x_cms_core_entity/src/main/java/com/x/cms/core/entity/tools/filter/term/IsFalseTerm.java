package com.x.cms.core.entity.tools.filter.term;

public class IsFalseTerm {

	private String name = null;
	
	public IsFalseTerm() {}
	
	public IsFalseTerm( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
