package com.x.cms.core.entity.enums;

/**
 * 内容管理文档状态
 * @author sword
 */
public enum DocumentStatus {

	DRAFT("draft", "草稿"),
	PUBLISHED("published", "已发布"),
	WAIT_PUBLISH("waitPublish", "待发布");
	private String value;
	private String name;

	private DocumentStatus(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}
