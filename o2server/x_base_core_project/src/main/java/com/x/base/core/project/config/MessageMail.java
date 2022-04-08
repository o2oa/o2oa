package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageMail extends LinkedHashMap<String, MessageMail.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageMail defaultInstance() {
		return new MessageMail();
	}

	public static class Item {

		public Item() {
			host = DEFAULT_HOST;
			port = DEFAULT_PORT;
			sslEnable = DEFAULT_SSLENABLE;
			auth = DEFAULT_AUTH;
			from = DEFAULT_FROM;
			password = DEFAULT_PASSWORD;
		}

		public static final String DEFAULT_HOST = "";
		public static final Integer DEFAULT_PORT = 465;
		public static final Boolean DEFAULT_SSLENABLE = true;
		public static final Boolean DEFAULT_AUTH = true;
		public static final String DEFAULT_FROM = "admin@o2oa.net";
		public static final String DEFAULT_PASSWORD = "password";

		@FieldDescribe("smtp主机.")
		private String host;

		@FieldDescribe("smtp端口.")
		private Integer port;

		@FieldDescribe("smtp 使用ssl加密.")
		private Boolean sslEnable;

		@FieldDescribe("stmp启用认证.")
		private Boolean auth;

		@FieldDescribe("发件人.")
		private String from;

		@FieldDescribe("发件人密码.")
		private String password;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public Boolean getSslEnable() {
			return sslEnable;
		}

		public void setSslEnable(Boolean sslEnable) {
			this.sslEnable = sslEnable;
		}

		public Boolean getAuth() {
			return auth;
		}

		public void setAuth(Boolean auth) {
			this.auth = auth;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

}
