package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageMail extends LinkedHashMap<String, MessageMail.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageMail defaultInstance() {
		MessageMail messageMail = new MessageMail();
		messageMail.put("o2oa", new Item());
		return messageMail;
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
			return StringUtils.isBlank(this.host) ? DEFAULT_HOST : this.host;
		}

		public Integer getPort() {
			return null == port ? DEFAULT_PORT : this.port;
		}

		public Boolean getSslEnable() {
			return null == sslEnable ? DEFAULT_SSLENABLE : this.sslEnable;
		}

		public Boolean getAuth() {
			return null == auth ? DEFAULT_AUTH : this.auth;
		}

		public String getFrom() {
			return StringUtils.isBlank(this.from) ? DEFAULT_FROM : this.from;
		}

		public String getPassword() {
			return password;
		}

	}

}
