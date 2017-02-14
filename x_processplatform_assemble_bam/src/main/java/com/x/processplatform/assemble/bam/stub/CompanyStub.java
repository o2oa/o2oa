package com.x.processplatform.assemble.bam.stub;

import com.x.base.core.gson.GsonPropertyObject;

public class CompanyStub extends GsonPropertyObject {

	private String name;
	private String value;
	private Integer level;

	private DepartmentStubs departmentStubs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DepartmentStubs getDepartmentStubs() {
		return departmentStubs;
	}

	public void setDepartmentStubs(DepartmentStubs departmentStubs) {
		this.departmentStubs = departmentStubs;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

}
