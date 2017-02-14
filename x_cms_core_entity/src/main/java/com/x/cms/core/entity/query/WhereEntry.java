package com.x.cms.core.entity.query;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.bean.NameIdPair;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.utils.ListTools;

public class WhereEntry extends GsonPropertyObject {

	public WhereEntry() {
		this.appIdList = new ArrayList<NameIdPair>();
		this.catagoryIdList = new ArrayList<NameIdPair>();
		this.docIdList = new ArrayList<NameIdPair>();
		this.companyList = new ArrayList<NameIdPair>();
		this.departmentList = new ArrayList<NameIdPair>();
		this.identityList = new ArrayList<NameIdPair>();
		this.personList = new ArrayList<NameIdPair>();
	}

	private List<NameIdPair> appIdList;
	private List<NameIdPair> catagoryIdList;
	private List<NameIdPair> docIdList;
	private List<NameIdPair> companyList;
	private List<NameIdPair> departmentList;
	private List<NameIdPair> personList;
	private List<NameIdPair> identityList;

	public Boolean available() {
		if (ListTools.isEmpty(this.appIdList) && ListTools.isEmpty(this.catagoryIdList)&& ListTools.isEmpty( this.docIdList )
				&& ListTools.isEmpty(this.companyList) && ListTools.isEmpty(this.departmentList)
				&& ListTools.isEmpty(this.identityList) && ListTools.isEmpty(this.personList)) {
			return false;
		}
		return true;
	}
	public List<NameIdPair> getAppIdList() {
		return appIdList;
	}
	public void setAppIdList(List<NameIdPair> appIdList) {
		this.appIdList = appIdList;
	}
	public List<NameIdPair> getCatagoryIdList() {
		return catagoryIdList;
	}
	public void setCatagoryIdList(List<NameIdPair> catagoryIdList) {
		this.catagoryIdList = catagoryIdList;
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
	public List<NameIdPair> getDocIdList() {
		return docIdList;
	}
	public void setDocIdList(List<NameIdPair> docIdList) {
		this.docIdList = docIdList;
	}

}