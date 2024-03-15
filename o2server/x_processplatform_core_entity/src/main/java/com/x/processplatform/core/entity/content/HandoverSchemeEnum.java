package com.x.processplatform.core.entity.content;


/**
 * @author sword
 */
public enum HandoverSchemeEnum {

	ALL("all", "所有有权限的文档"),
	APPLICATION("application", "指定应用下有权限的文档"),
	PROCESS("process", "指定流程下有权限的文档"),
	JOB("job", "指定有权限的文档");

	private String value;
	private String name;

	private HandoverSchemeEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(String value) {
		for (HandoverSchemeEnum e : HandoverSchemeEnum.values()) {
			if (e.getValue().equals(value)) {
				return e.name;
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
