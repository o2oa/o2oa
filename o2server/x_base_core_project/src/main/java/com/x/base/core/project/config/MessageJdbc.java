package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageJdbc extends LinkedHashMap<String, MessageJdbc.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageJdbc defaultInstance() {
		MessageJdbc messageMq = new MessageJdbc();
		messageMq.put("o2oa", new Item());
		return messageMq;
	}

	public static class Item {

		public Item() {
			 
		}

		public static final String TYPE_KAFKA = "kafka";
		public static final String TYPE_ACTIVEMQ = "activeMQ";

		public static final String DEFAULT_TYPE = TYPE_KAFKA;

		public static final String DEFAULT_KAFKABOOTSTRAPSERVERS = "";

		public static final String DEFAULT_KAFKATOPIC = "";

		public static final String DEFAULT_KAFKAACKS = "";

		public static final Integer DEFAULT_KAFKARETRIES = 3;

		public static final String DEFAULT_KAFKABATCHSIZE = "";

		public static final Integer DEFAULT_KAFKALINGERMS = 5000;

		public static final String DEFAULT_KAFKABUFFERMEMORY = "";

		public static final String DEFAULT_ACTIVEMQUSERNAME = "";

		public static final String DEFAULT_ACTIVEMQPASSWORD = "";

		public static final String DEFAULT_ACTIVEMQURL = "";

		public static final String DEFAULT_ACTIVEMQQUEUENAME = "";

		@FieldDescribe("类型,kafka或者activeMQ")
		private String driverClass;

		@FieldDescribe("服务器地址")
		private String url;

		@FieldDescribe("主题")
		private String username;

		@FieldDescribe("用户名")
		private String password;

		@FieldDescribe("密码")
		private String catalog;

		@FieldDescribe("服务器地址")
		private String schema;

		@FieldDescribe("消息队列名")
		private String table;

		public String getDriverClass() {
			return driverClass;
		}

		public void setDriverClass(String driverClass) {
			this.driverClass = driverClass;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getCatalog() {
			return catalog;
		}

		public void setCatalog(String catalog) {
			this.catalog = catalog;
		}

		public String getSchema() {
			return schema;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		public String getTable() {
			return table;
		}

		public void setTable(String table) {
			this.table = table;
		}

	}

}
