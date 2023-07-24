package com.x.cms.core.express.tools.filter.term;

public class MemberTerm {

	private String name = null;
	
	private String value = null;

	public MemberTerm() {}
	
	public MemberTerm( String name, String value ) {
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
