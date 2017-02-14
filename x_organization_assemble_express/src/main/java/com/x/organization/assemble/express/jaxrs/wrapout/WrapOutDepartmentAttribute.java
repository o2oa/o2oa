package com.x.organization.assemble.express.jaxrs.wrapout;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.DepartmentAttribute;

@Wrap(DepartmentAttribute.class)
public class WrapOutDepartmentAttribute extends GsonPropertyObject {

	private String name;
	private List<String> attributeList;
	private String department;

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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

}