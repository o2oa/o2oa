package com.x.cms.assemble.control.jaxrs.log;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.Log;

@Wrap(Log.class)
public class WrapInFilter extends GsonPropertyObject {

	private List<String> appIdList;

	private List<String> categoryIdList;

	private List<String> creatorList;

	private List<String> statusList;

	private List<String> titleList;

	private String key;	

	public List<String> getAppIdList() {
		return appIdList;
	}

	public void setAppIdList(List<String> appIdList) {
		this.appIdList = appIdList;
	}

	public List<String> getCategoryIdList() {
		return categoryIdList;
	}

	public void setCategoryIdList(List<String> categoryIdList) {
		this.categoryIdList = categoryIdList;
	}

	public List<String> getCreatorList() {
		return creatorList;
	}

	public void setCreatorList(List<String> creatorList) {
		this.creatorList = creatorList;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	public List<String> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
