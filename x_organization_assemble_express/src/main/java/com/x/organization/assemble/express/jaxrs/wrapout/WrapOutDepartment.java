package com.x.organization.assemble.express.jaxrs.wrapout;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Department;

@Wrap(Department.class)
public class WrapOutDepartment extends GsonPropertyObject {

	private String name;
	private String display;
	private String company;
	private String superior;

	private Long departmentCount;
	private Long identityCount;

	private List<WrapOutDepartment> departmentList;
	private List<WrapOutIdentity> identityList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public Long getDepartmentCount() {
		return departmentCount;
	}

	public void setDepartmentCount(Long departmentCount) {
		this.departmentCount = departmentCount;
	}

	public Long getIdentityCount() {
		return identityCount;
	}

	public void setIdentityCount(Long identityCount) {
		this.identityCount = identityCount;
	}

	public List<WrapOutDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<WrapOutDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<WrapOutIdentity> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<WrapOutIdentity> identityList) {
		this.identityList = identityList;
	}



}