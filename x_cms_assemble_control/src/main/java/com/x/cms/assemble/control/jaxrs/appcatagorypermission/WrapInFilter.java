package com.x.cms.assemble.control.jaxrs.appcatagorypermission;

import java.util.List;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.AppCatagoryPermission;

@Wrap( AppCatagoryPermission.class)
public class WrapInFilter extends GsonPropertyObject {

	private List<NameValueCountPair> appIdList;

	private List<NameValueCountPair> catagoryIdList;

	private List<NameValueCountPair> creatorList;

	private List<NameValueCountPair> statusList;

	private List<NameValueCountPair> titleList;

	private String key;	

	public List<NameValueCountPair> getAppIdList() {
		return appIdList;
	}

	public void setAppIdList(List<NameValueCountPair> appIdList) {
		this.appIdList = appIdList;
	}

	public List<NameValueCountPair> getCatagoryIdList() {
		return catagoryIdList;
	}

	public void setCatagoryIdList(List<NameValueCountPair> catagoryIdList) {
		this.catagoryIdList = catagoryIdList;
	}

	public List<NameValueCountPair> getCreatorList() {
		return creatorList;
	}

	public void setCreatorList(List<NameValueCountPair> creatorList) {
		this.creatorList = creatorList;
	}

	public List<NameValueCountPair> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<NameValueCountPair> statusList) {
		this.statusList = statusList;
	}

	public List<NameValueCountPair> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<NameValueCountPair> titleList) {
		this.titleList = titleList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
