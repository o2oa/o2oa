package com.x.teamwork.core.entity.tools.filter.term;

public class NotMemberTerm{

	private String name = null;
	
	private String value = null;

	public NotMemberTerm() {}
	
	public NotMemberTerm( String name, String value ) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}	
}
