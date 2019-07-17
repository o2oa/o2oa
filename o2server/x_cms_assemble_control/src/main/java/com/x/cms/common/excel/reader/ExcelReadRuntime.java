package com.x.cms.common.excel.reader;

import java.util.Date;
import java.util.List;

import com.x.cms.assemble.control.jaxrs.document.ActionPersistImportDataExcel.Wi;
import com.x.cms.assemble.control.jaxrs.document.ActionPersistImportDataExcel.Wo;
import com.x.cms.assemble.control.queue.DataImportStatus;

public class ExcelReadRuntime {
	
	public Integer startRow = 1;	
	public Wi wi = null;
	public List<String> propertyNames = null;	
	public DocTemplate template = null;
	public String importBatchName = null;
	public Wo wo = null;
	public String operatorName = null;
	public DataImportStatus dataImportStatus = null;
	
	public ExcelReadRuntime( String operatorName, DocTemplate template, List<String> propertyNames, Integer startRow, Wi wi, Wo wo, String importBatchName, DataImportStatus dataImportStatus ) {
		this.operatorName = operatorName;
		this.template = template;
		this.propertyNames = propertyNames;
		this.startRow = startRow;
		this.wi = wi;
		this.wo = wo;
		this.importBatchName = importBatchName;
		this.dataImportStatus = dataImportStatus;
	}
	
	public static class DocTemplate{
		private String importBatchName;
		private String title;
		private String documentType = "数据";
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
		private List<String> readPersonList;
		private List<String> readUnitList;
		private List<String> readGroupList;
		private List<String> authorPersonList;
		private List<String> authorUnitList;
		private List<String> authorGroupList;
		private List<String> managerList;
		private Date publishTime;

		public Date getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(Date publishTime) {
			this.publishTime = publishTime;
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

		public String getImportBatchName() {
			return importBatchName;
		}

		public void setImportBatchName(String importBatchName) {
			this.importBatchName = importBatchName;
		}
	}
}
