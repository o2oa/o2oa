package com.x.base.core.project.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Mock extends ConfigObject {

	private static final long serialVersionUID = -5734325116526391069L;

	private Map<String, Item> mock = new ConcurrentHashMap<>();

	public Map<String, Item> getMock() {
		return mock;
	}

	public void setMock(Map<String, Item> mock) {
		this.mock = mock;
	}

	public static Mock defaultInstance() {
		return new Mock();
	}

	public Mock() {
		this.mock.put("x_processplatform_assemble_surface", new Item());
		this.mock.put("x_cms_assemble_control", new Item());
		this.mock.put("x_query_assemble_surface", new Item());
		this.mock.put("x_organization_assemble_authentication", new Item());
		this.mock.put("x_organization_assemble_personal", new Item());
		this.mock.put("x_organization_assemble_control", new Item());
	}

	public static class Item {

		public Item() {
			this.put = new Method("post", "mockputtopost");
			this.delete = new Method("delete", "mockdeletetoget");
		}

		private Method put;
		private Method delete;

		public Method getPut() {
			return put;
		}

		public void setPut(Method put) {
			this.put = put;
		}

		public Method getDelete() {
			return delete;
		}

		public void setDelete(Method delete) {
			this.delete = delete;
		}

	}

	public static class Method {

		public Method(String to, String append) {
			this.to = to;
			this.append = append;
		}

		private String to;
		private String append;

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getAppend() {
			return append;
		}

		public void setAppend(String append) {
			this.append = append;
		}

	}

}