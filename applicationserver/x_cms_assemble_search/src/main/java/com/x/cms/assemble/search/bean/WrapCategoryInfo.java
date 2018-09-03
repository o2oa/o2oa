package com.x.cms.assemble.search.bean;  
  
import java.util.Date;
import java.util.List;

import io.searchbox.annotations.JestId;  
  
public class WrapCategoryInfo implements Cloneable {  
    
	@JestId  
    private String id;

	private Date createTime;

	private Date updateTime;

	private String sequence;	

	private String categoryName;

	private String appId;

	private String appName;

	private String documentType = "信息";

	private String parentId;

	private String categoryAlias;

	private String workflowType = "禁用审批流";

	private String workflowAppId;

	private String workflowAppName;

	private String workflowName;

	private String workflowFlag;

	private String formId;

	private String formName;

	private String readFormId;

	private String readFormName;

	private String defaultViewId;

	private String defaultViewName;

	private String categorySeq;

	private String description;

	private String categoryIcon;

	private String categoryMemo;

	private String creatorPerson;

	private String creatorIdentity;

	private String creatorUnitName;

	private String creatorTopUnitName;

	private Boolean allPeopleView = true;

	private Boolean allPeoplePublish = true;
	
	private String importViewAppId = null;

	private String importViewId = null;

	private String importViewName = null;

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

	public String getCategoryName() {
		return categoryName;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppName() {
		return appName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public String getParentId() {
		return parentId;
	}

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public String getWorkflowAppId() {
		return workflowAppId;
	}

	public String getWorkflowAppName() {
		return workflowAppName;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public String getWorkflowFlag() {
		return workflowFlag;
	}

	public String getFormId() {
		return formId;
	}

	public String getFormName() {
		return formName;
	}

	public String getReadFormId() {
		return readFormId;
	}

	public String getReadFormName() {
		return readFormName;
	}

	public String getDefaultViewId() {
		return defaultViewId;
	}

	public String getDefaultViewName() {
		return defaultViewName;
	}

	public String getCategorySeq() {
		return categorySeq;
	}

	public String getDescription() {
		return description;
	}

	public String getCategoryIcon() {
		return categoryIcon;
	}

	public String getCategoryMemo() {
		return categoryMemo;
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

	public String getImportViewAppId() {
		return importViewAppId;
	}

	public String getImportViewId() {
		return importViewId;
	}

	public String getImportViewName() {
		return importViewName;
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

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public void setWorkflowAppId(String workflowAppId) {
		this.workflowAppId = workflowAppId;
	}

	public void setWorkflowAppName(String workflowAppName) {
		this.workflowAppName = workflowAppName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public void setWorkflowFlag(String workflowFlag) {
		this.workflowFlag = workflowFlag;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setReadFormId(String readFormId) {
		this.readFormId = readFormId;
	}

	public void setReadFormName(String readFormName) {
		this.readFormName = readFormName;
	}

	public void setDefaultViewId(String defaultViewId) {
		this.defaultViewId = defaultViewId;
	}

	public void setDefaultViewName(String defaultViewName) {
		this.defaultViewName = defaultViewName;
	}

	public void setCategorySeq(String categorySeq) {
		this.categorySeq = categorySeq;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCategoryIcon(String categoryIcon) {
		this.categoryIcon = categoryIcon;
	}

	public void setCategoryMemo(String categoryMemo) {
		this.categoryMemo = categoryMemo;
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

	public void setImportViewAppId(String importViewAppId) {
		this.importViewAppId = importViewAppId;
	}

	public void setImportViewId(String importViewId) {
		this.importViewId = importViewId;
	}

	public void setImportViewName(String importViewName) {
		this.importViewName = importViewName;
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