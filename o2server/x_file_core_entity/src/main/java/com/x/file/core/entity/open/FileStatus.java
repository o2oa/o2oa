package com.x.file.core.entity.open;

/**
 *
 */
public enum FileStatus {

	VALID("1", "正常"), INVALID("0", "已删除");
	private String value;
	private String name;

	private FileStatus(String value, String name) {
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
