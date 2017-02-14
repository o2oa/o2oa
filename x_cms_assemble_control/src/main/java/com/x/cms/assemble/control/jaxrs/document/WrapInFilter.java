package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.Document;

@Wrap(Document.class)
public class WrapInFilter extends GsonPropertyObject {

	private List<NameValueCountPair> appIdList; //一个APPID

	private List<NameValueCountPair> catagoryIdList;//一个CATAGORYID

	private List<NameValueCountPair> creatorList;//一个创建人

	private List<NameValueCountPair> statusList;//一个DOCSTATUS

	private List<NameValueCountPair> titleList;	//一个标题
	
	private List<NameValueCountPair> formList;//一个FORM
	
	private List<NameValueCountPair> createDateList;	//创建年份月份，可以传入一个或者两个

	private String order = "DESC";

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

	public List<NameValueCountPair> getFormList() {
		return formList;
	}

	public void setFormList(List<NameValueCountPair> formList) {
		this.formList = formList;
	}

	public List<NameValueCountPair> getCreateDateList() {
		return createDateList;
	}

	public void setCreateDateList(List<NameValueCountPair> createDateList) {
		this.createDateList = createDateList;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

}
