package com.x.cms.core.entity.query;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.bean.NameIdPair;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class WhereEntry extends GsonPropertyObject {

	public WhereEntry() {
		this.appInfoList = new ArrayList<NameIdPair>();
		this.categoryList = new ArrayList<NameIdPair>();
		this.unitList = new ArrayList<NameIdPair>();
		this.identityList = new ArrayList<NameIdPair>();
		this.personList = new ArrayList<NameIdPair>();
	}

	private List<NameIdPair> appInfoList;
	private List<NameIdPair> categoryList;
	private List<NameIdPair> unitList;
	private List<NameIdPair> personList;
	private List<NameIdPair> identityList;

	public Boolean available() {
		if (ListTools.isEmpty(this.appInfoList) && ListTools.isEmpty(this.categoryList)
				&& ListTools.isEmpty(this.unitList) && ListTools.isEmpty(this.identityList)
				&& ListTools.isEmpty(this.personList)) {
			return false;
		}
		return true;
	}
	public List<NameIdPair> getAppInfoList() {
		return appInfoList;
	}

	public List<NameIdPair> getCategoryList() {
		return categoryList;
	}

	public void setAppInfoList(List<NameIdPair> appInfoList) {
		this.appInfoList = appInfoList;
	}

	public void setCategoryList(List<NameIdPair> categoryList) {
		this.categoryList = categoryList;
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

	public List<NameIdPair> getUnitList() {
		return unitList;
	}

	public void setUnitList(List<NameIdPair> unitList) {
		this.unitList = unitList;
	}

}