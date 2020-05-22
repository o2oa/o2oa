package com.x.organization.assemble.control.jaxrs.inputperson;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject; 

public class DutyItem extends GsonPropertyObject {

	private String name;
	private String unique;
	private String description;
	private String unit;
	private List<String> identityList;

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
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}
}
