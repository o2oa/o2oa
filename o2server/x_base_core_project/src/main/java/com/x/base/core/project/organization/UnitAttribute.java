package com.x.base.core.project.organization;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class UnitAttribute extends GsonPropertyObject {

	@FieldDescribe("个人属性名称")
	private String name;

	@FieldDescribe("组织")
	private String unit;

	@FieldDescribe("属性值")
	private List<String> attributeList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
