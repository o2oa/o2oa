package com.x.pan.core.entity;

/**
 *
 * @author sword
 */
public enum ZoneRoleEnum {

	ADMIN("admin", "可管理"),
	EDITOR("editor", "可编辑"),
	READER("reader", "可查看/可下载"),
	VIEWER("viewer", "可查看");

	private String value;
	private String name;

	private ZoneRoleEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static ZoneRoleEnum getByValue(String value) {
		for (ZoneRoleEnum e : ZoneRoleEnum.values()) {
			if (e.getValue().equals(value)) {
				return e;
			}
		}
		return null;
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
