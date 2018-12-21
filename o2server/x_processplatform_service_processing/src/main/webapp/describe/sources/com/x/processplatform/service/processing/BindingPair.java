package com.x.processplatform.service.processing;

public class BindingPair {

	public BindingPair(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	private String name;

	private Object value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
