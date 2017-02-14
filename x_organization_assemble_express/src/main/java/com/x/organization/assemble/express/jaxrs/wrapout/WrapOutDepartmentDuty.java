package com.x.organization.assemble.express.jaxrs.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.DepartmentDuty;

@Wrap(DepartmentDuty.class)
public class WrapOutDepartmentDuty extends GsonPropertyObject {

	private String name;
	private List<String> identityList = new ArrayList<>();
	private String department;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getIdentityList() {
		return identityList;
	}
	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}

}