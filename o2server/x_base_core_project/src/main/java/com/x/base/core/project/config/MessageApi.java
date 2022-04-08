package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageApi extends LinkedHashMap<String, MessageApi.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageApi defaultInstance() {
		return new MessageApi();
	}

	public static class Item {

		@FieldDescribe("应用")
		private String application;
		@FieldDescribe("路径")
		private String path;
		@FieldDescribe("方法")
		private String method;

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

	}

}
