package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.gson.GsonPropertyObject;

public class IdentityItem extends GsonPropertyObject {

	private String name;
	private String person;
	private String unit;
	private String unitName;
	private Integer unitLevel;
	private String unitLevelName;
	
	private String personCode;
	private String unitCode;
	private String dutyCode;
	private Boolean major;
	

	private Integer row;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Integer getUnitLevel() {
		return unitLevel;
	}

	public void setUnitLevel(Integer unitLevel) {
		this.unitLevel = unitLevel;
	}
	
	public String getUnitLevelName() {
		return unitLevelName;
	}

	public void setUnitLevelName(String unitLevelName) {
		this.unitLevelName = unitLevelName;
	}
	
	public String getPersonCode() {
		return personCode;
	}

	public void setPersonCode(String personCode) {
		this.personCode = personCode;
	}
	
	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	
	public String getDutyCode() {
		return dutyCode;
	}

	public void setDutyCode(String dutyCode) {
		this.dutyCode = dutyCode;
	}

	public Boolean getMajor() {
		return major;
	}

	public void setMajor(Boolean major) {
		this.major = major;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}
}
