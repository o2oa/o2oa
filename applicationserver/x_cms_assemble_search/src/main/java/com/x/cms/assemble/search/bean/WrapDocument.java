package com.x.cms.assemble.search.bean;  
  
import java.util.Date;
import java.util.List;

import io.searchbox.annotations.JestId;  
  
public class WrapDocument implements Cloneable {  
    
	@JestId  
    private String id;

	private Date createTime;

	private Date updateTime;

	private String sequence;	

	private String summary;

	private String title;

	private String documentType = "信息";

	private String appId;

	private String appName;

	private String categoryId;

	private String categoryName;

	private String categoryAlias;

	private String form;

	private String formName;

	private String readFormId;

	private String readFormName;

	private String creatorPerson;

	private String creatorIdentity;

	private String creatorUnitName;

	private String creatorTopUnitName;

	private String docStatus = "draft";

	private Long viewCount = 0L;

	private Date publishTime;

	private Boolean hasIndexPic = false;

	private List<String> readPersonList;

	private List<String> readUnitList;

	private List<String> readGroupList;

	private List<String> authorPersonList;

	private List<String> authorUnitList;

	private List<String> authorGroupList;

	private List<String> managerList;

	private List<String> pictureList;
	
	private WrapAppInfo appInfo;
	
	private WrapCategoryInfo categoryInfo;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getSummary() {
		return summary;
	}

	public String getTitle() {
		return title;
	}

	public String getDocumentType() {
		return documentType;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppName() {
		return appName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public String getForm() {
		return form;
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

	public String getDocStatus() {
		return docStatus;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public Boolean getHasIndexPic() {
		return hasIndexPic;
	}

	public List<String> getReadPersonList() {
		return readPersonList;
	}

	public List<String> getReadUnitList() {
		return readUnitList;
	}

	public List<String> getReadGroupList() {
		return readGroupList;
	}

	public List<String> getAuthorPersonList() {
		return authorPersonList;
	}

	public List<String> getAuthorUnitList() {
		return authorUnitList;
	}

	public List<String> getAuthorGroupList() {
		return authorGroupList;
	}

	public List<String> getManagerList() {
		return managerList;
	}

	public List<String> getPictureList() {
		return pictureList;
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

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	public void setForm(String form) {
		this.form = form;
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

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}
	
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public void setHasIndexPic(Boolean hasIndexPic) {
		this.hasIndexPic = hasIndexPic;
	}

	public void setReadPersonList(List<String> readPersonList) {
		this.readPersonList = readPersonList;
	}

	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
	}

	public void setReadGroupList(List<String> readGroupList) {
		this.readGroupList = readGroupList;
	}

	public void setAuthorPersonList(List<String> authorPersonList) {
		this.authorPersonList = authorPersonList;
	}

	public void setAuthorUnitList(List<String> authorUnitList) {
		this.authorUnitList = authorUnitList;
	}

	public void setAuthorGroupList(List<String> authorGroupList) {
		this.authorGroupList = authorGroupList;
	}

	public void setManagerList(List<String> managerList) {
		this.managerList = managerList;
	}

	public void setPictureList(List<String> pictureList) {
		this.pictureList = pictureList;
	}

	public WrapAppInfo getAppInfo() {
		return appInfo;
	}

	public WrapCategoryInfo getCategoryInfo() {
		return categoryInfo;
	}

	public void setAppInfo(WrapAppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public void setCategoryInfo(WrapCategoryInfo categoryInfo) {
		this.categoryInfo = categoryInfo;
	}
}  