package com.x.cms.core.entity.tools.filter.term;

import java.util.Date;
import java.util.List;

public class DateBetweenTerm{

	private String name = null;
	
	private List<Date> value = null;

	public DateBetweenTerm() {}
	
	public DateBetweenTerm( String name, List<Date> value ) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public List<Date> getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(List<Date> value) {
		this.value = value;
	}	
}
