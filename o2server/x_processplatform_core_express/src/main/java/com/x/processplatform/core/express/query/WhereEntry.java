package com.x.processplatform.core.express.query;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.bean.NameIdPair;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class WhereEntry extends GsonPropertyObject {

	public WhereEntry() {
		this.applicationList = new ArrayList<NameIdPair>();
		this.processList = new ArrayList<NameIdPair>();
		this.unitList = new ArrayList<NameIdPair>();
		this.identityList = new ArrayList<NameIdPair>();
		this.personList = new ArrayList<NameIdPair>();
	}

	private List<NameIdPair> applicationList;
	private List<NameIdPair> processList;
	private List<NameIdPair> unitList;
	private List<NameIdPair> personList;
	private List<NameIdPair> identityList;

	public Boolean available() {
		if (ListTools.isEmpty(this.applicationList) && ListTools.isEmpty(this.processList)
				&& ListTools.isEmpty(this.unitList) && ListTools.isEmpty(this.identityList)
				&& ListTools.isEmpty(this.personList)) {
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