package com.x.teamwork.core.entity.tools.filter.term;

public class EqualsTerm {

	private String name = null;
	
	private Object value = null;
	
	public EqualsTerm() {}
	
	public EqualsTerm( String name, Object value ) {
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
