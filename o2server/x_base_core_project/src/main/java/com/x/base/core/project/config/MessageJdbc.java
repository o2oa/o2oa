package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageJdbc extends LinkedHashMap<String, MessageJdbc.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageJdbc defaultInstance() {
		MessageJdbc messageJdbc = new MessageJdbc();
		messageJdbc.put("o2oa", new Item());
		return messageJdbc;
	}

	public static class Item {

		public Item() {
			this.driverClass = DEFAULT_DRIVERCLASS;
			this.url = DEFAULT_URL;
			this.username = DEFAULT_USERNAME;
			this.password = DEFAULT_PASSWORD;
			this.catalog = DEFAULT_CATALOG;
			this.schema = DEFAULT_SCHEMA;
			this.table = DEFAULT_TABLE;
		}

		public static final String DEFAULT_DRIVERCLASS = "com.mysql.cj.jdbc.Driver";

		public static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/TEST?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8";

		public static final String DEFAULT_USERNAME = "root";

		public static final String DEFAULT_PASSWORD = "password";

		public static final String DEFAULT_CATALOG = "";

		public static final String DEFAULT_SCHEMA = "";

		public static final String DEFAULT_TABLE = "NEWTABLE";

		@FieldDescribe("驱动类")
		private String driverClass;

		@FieldDescribe("地址")
		private String url;

		@FieldDescribe("用户名")
		private String username;

		@FieldDescribe("密码")
		private String password;

		@FieldDescribe("catalog")
		private String catalog;

		@FieldDescribe("schema")
		private String schema;

		@FieldDescribe("表名")
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
