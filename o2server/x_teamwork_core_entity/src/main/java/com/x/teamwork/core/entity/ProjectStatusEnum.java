package com.x.teamwork.core.entity;

/**
 *
 * @author sword
 */
public enum ProjectStatusEnum {
	PROCESSING("processing", "进行中"),
	DELAY("delay", "已搁置"),
	COMPLETED("completed", "已完成"),
	CANCELED("canceled", "已取消"),
	ARCHIVED("archived", "已归档");
	private String value;
	private String name;

	private ProjectStatusEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static boolean isEndStatus(String status){
		if(COMPLETED.getValue().equals(status) || CANCELED.getValue().equals(status) || ARCHIVED.getValue().equals(status)){
			return true;
		}
		return false;
	}

	public static ProjectStatusEnum getByValue(String value) {
		for (ProjectStatusEnum e : ProjectStatusEnum.values()) {
			if (e.getValue().equals(value)) {
				return e;
			}
		}
		return null;
	}

	public static String getNameByValue(String value) {
		for (ProjectStatusEnum e : ProjectStatusEnum.values()) {
			if (e.getValue().equals(value)) {
				return e.getName();
			}
		}
		return value;
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
