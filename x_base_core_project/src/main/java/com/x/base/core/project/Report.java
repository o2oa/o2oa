package com.x.base.core.project;

import com.x.base.core.gson.GsonPropertyObject;

public class Report extends GsonPropertyObject {

	private String className;
	private String node;
	private String token;
	private Integer weight;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

}
