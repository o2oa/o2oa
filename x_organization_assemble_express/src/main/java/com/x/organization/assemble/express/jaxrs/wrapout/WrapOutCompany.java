package com.x.organization.assemble.express.jaxrs.wrapout;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Company;

@Wrap(Company.class)
public class WrapOutCompany extends GsonPropertyObject {

	private String name;
	private String display;
	private String superior;

	private Long companyCount;
	private Long departmentCount;

	private List<WrapOutCompany> companyList;
	private List<WrapOutDepartment> departmentList;

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

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public Long getCompanyCount() {
		return companyCount;
	}

	public void setCompanyCount(Long companyCount) {
		this.companyCount = companyCount;
	}

	public Long getDepartmentCount() {
		return departmentCount;
	}

	public void setDepartmentCount(Long departmentCount) {
		this.departmentCount = departmentCount;
	}

	public List<WrapOutCompany> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<WrapOutCompany> companyList) {
		this.companyList = companyList;
	}

	public List<WrapOutDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<WrapOutDepartment> departmentList) {
		this.departmentList = departmentList;
	}

}