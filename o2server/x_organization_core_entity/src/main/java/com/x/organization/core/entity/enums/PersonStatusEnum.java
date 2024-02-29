package com.x.organization.core.entity.enums;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author sword
 */
public enum PersonStatusEnum {

	NORMAL("0", "正常"),
	LOCK("1", "锁定"),
	BAN("2", "禁止"),
	DELETE("3", "删除");

	private String value;
	private String name;

	private PersonStatusEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static boolean isNormal(String status){
		return StringUtils.isBlank(status) || NORMAL.getValue().equals(status);
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
