package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageApi extends LinkedHashMap<String, MessageApi.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageApi defaultInstance() {
		MessageApi messageApi = new MessageApi();
		messageApi.put("o2oa", new Item());
		return messageApi;
	}

	public static class Item {

		public Item() {
			this.application = DEFAULT_APPLICATION;
			this.path = DEFAULT_PATH;
			this.method = DEFAULT_METHOD;
		}

		public static final String DEFAULT_APPLICATION = "";
		public static final String DEFAULT_PATH = "";
		public static final String DEFAULT_METHOD = "get";

		@FieldDescribe("应用")
		private String application;
		@FieldDescribe("路径")
		private String path;
		@FieldDescribe("方法")
		private String method;

		public String getApplication() {
			return StringUtils.isBlank(this.application) ? DEFAULT_APPLICATION : this.application;
		}

		public String getPath() {
			return StringUtils.isBlank(this.path) ? DEFAULT_PATH : this.path;
		}

		public String getMethod() {
			return StringUtils.isBlank(this.method) ? DEFAULT_METHOD : this.method;
		}

	}

}
