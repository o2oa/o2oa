package com.x.base.core.project.config;

import java.util.TreeMap;

import com.x.base.core.project.annotation.FieldDescribe;

public class Web extends ConfigObject {

	private static final long serialVersionUID = -1549522764856326338L;

	public static Web defaultInstance() {
		return new Web();
	}

	public Web() {

	}

	@FieldDescribe("使用Post模拟Put,Get模拟Delete的模块.")
	private Mock mock = new Mock();

	public static class Mock extends TreeMap<String, MockItem> {

	}

	public Mock getMock() {
		return mock;
	}

	public void setMock(Mock mock) {
		this.mock = mock;
	}

	public static class MockItem {
		private MockItemObject put;
		private MockItemObject delete;
	}

	public static class MockItemObject {
		private String to;
		private String append;
	}
}
