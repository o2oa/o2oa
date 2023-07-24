package com.x.processplatform.core.entity.content;

public enum DocSignStatus {

	STATUS_1(1, "暂存"),
	STATUS_2(2, "签批正文不可以修改"),
	STATUS_3(3, "签批正文可以修改");
	private Integer value;
	private String name;

	private DocSignStatus(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}
