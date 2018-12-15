package com.x.base.core.project.test.lambda;

import org.junit.Test;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Book extends GsonPropertyObject {

	public Book(String name, Integer sn) {
		this.name = name;
		this.sn = sn;

	}

	private String name;

	private Integer sn;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSn() {
		return sn;
	}

	public void setSn(Integer sn) {
		this.sn = sn;
	}

	@Test
	public void test() {

	}

}
