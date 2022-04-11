package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageTable extends LinkedHashMap<String, MessageTable.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageTable defaultInstance() {
		MessageTable messageTable = new MessageTable();
		messageTable.put("o2oa", new Item());
		return messageTable;
	}

	public static class Item {

		private static final String DEFAULT_TABLE = "";

		public Item() {
			this.table = DEFAULT_TABLE;
		}

		@FieldDescribe("自建表")
		private String table;

		public String getTable() {
			return StringUtils.isBlank(this.table) ? DEFAULT_TABLE : this.table;
		}

	}

}
