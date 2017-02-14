package com.x.processplatform.core.entity.query;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.bean.NameIdPair;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.utils.ListTools;

public class WhereEntry extends GsonPropertyObject {

	public WhereEntry() {
		this.applicationList = new ArrayList<NameIdPair>();
		this.processList = new ArrayList<NameIdPair>();
		this.companyList = new ArrayList<NameIdPair>();
		this.departmentList = new ArrayList<NameIdPair>();
		this.identityList = new ArrayList<NameIdPair>();
		this.personList = new ArrayList<NameIdPair>();
	}

	private List<NameIdPair> applicationList;
	private List<NameIdPair> processList;
	private List<NameIdPair> companyList;
	private List<NameIdPair> departmentList;
	private List<NameIdPair> personList;
	private List<NameIdPair> identityList;

	public Boolean available() {
		if (ListTools.isEmpty(this.applicationList) && ListTools.isEmpty(this.processList)
				&& ListTools.isEmpty(this.companyList) && ListTools.isEmpty(this.departmentList)
				&& ListTools.isEmpty(this.identityList) && ListTools.isEmpty(this.personList)) {
			return false;
		}
		return true;
	}

	public List<NameIdPair> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<NameIdPair> applicationList) {
		this.applicationList = applicationList;
	}

	public List<NameIdPair> getProcessList() {
		return processList;
	}

	public void setProcessList(List<NameIdPair> processList) {
		this.processList = processList;
	}

	public List<NameIdPair> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<NameIdPair> companyList) {
		this.companyList = companyList;
	}

	public List<NameIdPair> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<NameIdPair> departmentList) {
		this.departmentList = departmentList;
	}

	public List<NameIdPair> getPersonList() {
		return personList;
	}

	public void setPersonList(List<NameIdPair> personList) {
		this.personList = personList;
	}

	public List<NameIdPair> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<NameIdPair> identityList) {
		this.identityList = identityList;
	}

}