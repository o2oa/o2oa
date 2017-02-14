package com.x.processplatform.assemble.bam.stub;

import com.x.base.core.gson.GsonPropertyObject;

public class DepartmentStub extends GsonPropertyObject {

	private String name;
	private String value;
	private Integer level;

	private String companyName;
	private String companyValue;
	private Integer companyLevel;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyValue() {
		return companyValue;
	}

	public void setCompanyValue(String companyValue) {
		this.companyValue = companyValue;
	}

	public Integer getCompanyLevel() {
		return companyLevel;
	}

	public void setCompanyLevel(Integer companyLevel) {
		this.companyLevel = companyLevel;
	}

}
