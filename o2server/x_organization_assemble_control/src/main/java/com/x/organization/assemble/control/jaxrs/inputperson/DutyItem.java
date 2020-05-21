package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.gson.GsonPropertyObject; 

public class DutyItem extends GsonPropertyObject {

	private String name;
	private String unique;
	private String description;

	private Integer row;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}
}
