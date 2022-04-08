package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageRestful extends LinkedHashMap<String, MessageRestful.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageRestful defaultInstance() {
		MessageRestful messageRestful = new MessageRestful();
		messageRestful.put("o2oa", new Item());
		return messageRestful;
	}

	public static class Item {

		private static final String DEFAULT_URL = "";
		private static final String DEFAULT_METHOD = "get";

		public Item() {
			this.url = DEFAULT_URL;
			this.method = DEFAULT_METHOD;
		}

		@FieldDescribe("地址")
		private String url;

		@FieldDescribe("方法")
		private String method;

		public String getUrl() {
			return StringUtils.isBlank(this.url) ? DEFAULT_URL : this.url;
		}

		public String getMethod() {
			return StringUtils.isBlank(this.method) ? DEFAULT_METHOD : this.method;
		}

	}

}
