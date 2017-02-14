package com.x.cms.assemble.control.jaxrs.search;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.Document;

@Wrap( Document.class )
public class WrapOutSearchFilter {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

	private List<AppFilter> appfileter_list = null;
	
	private List<CatagoryFilter> catagoryfileter_list = null;
	
	private List<CompanyFilter> companyfileter_list = null;
	
	private List<DepartmentFilter> departmentfileter_list = null;
	
	public List<DepartmentFilter> getDepartmentfileter_list() {
		return departmentfileter_list;
	}

	public void setDepartmentfileter_list(List<DepartmentFilter> departmentfileter_list) {
		this.departmentfileter_list = departmentfileter_list;
	}

	public List<CompanyFilter> getCompanyfileter_list() {
		return companyfileter_list;
	}

	public void setCompanyfileter_list(List<CompanyFilter> companyfileter_list) {
		this.companyfileter_list = companyfileter_list;
	}

	public List<AppFilter> getAppfileter_list() {
		return appfileter_list;
	}

	public void setAppfileter_list(List<AppFilter> appfileter_list) {
		this.appfileter_list = appfileter_list;
	}

	public List<CatagoryFilter> getCatagoryfileter_list() {
		return catagoryfileter_list;
	}

	public void setCatagoryfileter_list(List<CatagoryFilter> catagoryfileter_list) {
		this.catagoryfileter_list = catagoryfileter_list;
	}
	
}