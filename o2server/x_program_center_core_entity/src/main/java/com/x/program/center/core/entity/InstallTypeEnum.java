package com.x.program.center.core.entity;

/**
 *
 * @author sword
 */
public enum InstallTypeEnum {

	DATA("data", "初始化数据"),
	CUSTOM("custom", "自定义服务"),
	XAPP("xapp", "平台安装包"),
	CONFIG("config", "配置文件"),
	WEB("web", "前端资源");

	private String value;
	private String name;

	private InstallTypeEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static InstallTypeEnum getByValue(String value) {
		for (InstallTypeEnum e : InstallTypeEnum.values()) {
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
