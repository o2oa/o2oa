package com.x.teamwork.core.entity;

/**
 *
 * @author sword
 */
public enum ProjectRoleEnum {

	MANAGER("manager", "管理员"),
	READER("reader", "阅读人");

	private String value;
	private String name;

	private ProjectRoleEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static ProjectRoleEnum getByValue(String value) {
		for (ProjectRoleEnum e : ProjectRoleEnum.values()) {
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
