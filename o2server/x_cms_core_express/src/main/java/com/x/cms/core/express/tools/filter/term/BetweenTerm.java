package com.x.cms.core.express.tools.filter.term;

import java.util.Collection;

public class BetweenTerm{

	private String name = null;
	
	private Collection<Object> value = null;

	public BetweenTerm() {}
	
	public BetweenTerm( String name, Collection<Object> value ) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public Collection<Object> getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Collection<Object> value) {
		this.value = value;
	}	
}
