package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageRestful extends LinkedHashMap<String, MessageRestful.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageRestful defaultInstance() {
		return new MessageRestful();
	}

	public static class Item {

		@FieldDescribe("地址")
		private String url;
		@FieldDescribe("方法")
		private String method;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}
	}

}
