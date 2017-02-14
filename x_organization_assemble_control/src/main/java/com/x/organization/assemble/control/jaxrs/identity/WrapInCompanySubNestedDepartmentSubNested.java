package com.x.organization.assemble.control.jaxrs.identity;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap
public class WrapInCompanySubNestedDepartmentSubNested extends GsonPropertyObject {

	private List<String> companyList;

	private List<String> departmentList;

	public List<String> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<String> companyList) {
		this.companyList = companyList;
	}

	public List<String> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<String> departmentList) {
		this.departmentList = departmentList;
	}

}
