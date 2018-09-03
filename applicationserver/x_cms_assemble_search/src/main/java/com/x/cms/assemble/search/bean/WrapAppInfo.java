package com.x.cms.assemble.search.bean;  
  
import java.util.Date;
import java.util.List;

import io.searchbox.annotations.JestId;  
  
public class WrapAppInfo implements Cloneable {  
    
	@JestId  
    private String id;

	private Date createTime;

	private Date updateTime;

	private String sequence;	

	private String appName;

	private String appAlias;

	private String documentType = "信息";

	private String appInfoSeq;

	private String description;

	private String iconColor;

	private String appMemo;

	private String creatorPerson;

	private String creatorIdentity;

	private String creatorUnitName;

	private String creatorTopUnitName;

	private Boolean allPeopleView = true;

	private Boolean allPeoplePublish = true;
	
	private List<String> viewablePersonList;

	private List<String> viewableUnitList;

	private List<String> viewableGroupList;

	private List<String> publishablePersonList;

	private List<String> publishableUnitList;

	private List<String> publishableGroupList;
	
	private List<String> manageablePersonList;
	
	private List<String> manageableUnitList;
	
	private List<String> manageableGroupList;

	public String getId() {
		return id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getSequence() {
		return sequence;
	}

	public String getAppName() {
		return appName;
	}

	public String getAppAlias() {
		return appAlias;
	}

	public String getDocumentType() {
		return documentType;
	}

	public String getAppInfoSeq() {
		return appInfoSeq;
	}

	public String getDescription() {
		return description;
	}

	public String getIconColor() {
		return iconColor;
	}

	public String getAppMemo() {
		return appMemo;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	public String getCreatorTopUnitName() {
		return creatorTopUnitName;
	}

	public Boolean getAllPeopleView() {
		return allPeopleView;
	}

	public Boolean getAllPeoplePublish() {
		return allPeoplePublish;
	}

	public List<String> getViewablePersonList() {
		return viewablePersonList;
	}

	public List<String> getViewableUnitList() {
		return viewableUnitList;
	}

	public List<String> getViewableGroupList() {
		return viewableGroupList;
	}

	public List<String> getPublishablePersonList() {
		return publishablePersonList;
	}

	public List<String> getPublishableUnitList() {
		return publishableUnitList;
	}

	public List<String> getPublishableGroupList() {
		return publishableGroupList;
	}

	public List<String> getManageablePersonList() {
		return manageablePersonList;
	}

	public List<String> getManageableUnitList() {
		return manageableUnitList;
	}

	public List<String> getManageableGroupList() {
		return manageableGroupList;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setAppInfoSeq(String appInfoSeq) {
		this.appInfoSeq = appInfoSeq;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIconColor(String iconColor) {
		this.iconColor = iconColor;
	}

	public void setAppMemo(String appMemo) {
		this.appMemo = appMemo;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	public void setCreatorTopUnitName(String creatorTopUnitName) {
		this.creatorTopUnitName = creatorTopUnitName;
	}

	public void setAllPeopleView(Boolean allPeopleView) {
		this.allPeopleView = allPeopleView;
	}

	public void setAllPeoplePublish(Boolean allPeoplePublish) {
		this.allPeoplePublish = allPeoplePublish;
	}

	public void setViewablePersonList(List<String> viewablePersonList) {
		this.viewablePersonList = viewablePersonList;
	}

	public void setViewableUnitList(List<String> viewableUnitList) {
		this.viewableUnitList = viewableUnitList;
	}

	public void setViewableGroupList(List<String> viewableGroupList) {
		this.viewableGroupList = viewableGroupList;
	}

	public void setPublishablePersonList(List<String> publishablePersonList) {
		this.publishablePersonList = publishablePersonList;
	}

	public void setPublishableUnitList(List<String> publishableUnitList) {
		this.publishableUnitList = publishableUnitList;
	}

	public void setPublishableGroupList(List<String> publishableGroupList) {
		this.publishableGroupList = publishableGroupList;
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}

	public void setManageableUnitList(List<String> manageableUnitList) {
		this.manageableUnitList = manageableUnitList;
	}

	public void setManageableGroupList(List<String> manageableGroupList) {
		this.manageableGroupList = manageableGroupList;
	}	
}  