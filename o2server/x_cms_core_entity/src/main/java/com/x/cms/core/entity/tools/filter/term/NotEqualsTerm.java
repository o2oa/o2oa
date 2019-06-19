package com.x.cms.core.entity.tools.filter.term;

public class NotEqualsTerm {

	private String name = null;
	
	private Object value = null;
	
	public NotEqualsTerm() {}
	
	public NotEqualsTerm( String name, Object value ) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public Object getValue() {
		return value;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setValue(Object value) {
		this.value = value;
	}

}
