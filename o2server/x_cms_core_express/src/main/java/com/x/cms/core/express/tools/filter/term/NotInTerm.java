package com.x.cms.core.express.tools.filter.term;

import java.util.List;

public class NotInTerm{

	private String name = null;
	
	private List<Object> value = null;

	public NotInTerm() {}
	
	public NotInTerm( String name, List<Object> value ) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public List<Object> getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(List<Object> value) {
		this.value = value;
	}	

}
