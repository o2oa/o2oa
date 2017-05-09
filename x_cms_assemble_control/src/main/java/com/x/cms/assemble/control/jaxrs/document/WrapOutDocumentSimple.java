package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.Document;

@Wrap( WrapOutDocumentSimple.class )
public class WrapOutDocumentSimple {
	
	public static List<String> Excludes = new ArrayList<String>();
	
//	@EntityFieldDescribe("附件列表")
//	private List<String> attachmentList;	
	
//	@EntityFieldDescribe("应用ID")
//	private String appId;
//	
//	@EntityFieldDescribe("应用名称")
//	private String appName;
//
//	@EntityFieldDescribe("分类ID")
//	private String categoryId;
//
//	@EntityFieldDescribe( "分类名称" )
//	private String categoryName;
//	
//	@EntityFieldDescribe( "绑定的表单模板ID" )
//	private String form;
//	
//	@EntityFieldDescribe( "绑定的表单模板名称" )
//	private String formName;
//	
//	@EntityFieldDescribe( "绑定的阅读表单模板ID" )
//	private String readFormId;
//	
//	@EntityFieldDescribe( "绑定的阅读表单模板名称" )
//	private String readFormName;	
	
	@EntityFieldDescribe( "数据库主键,自动生成." )
	private String id = Document.createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	private Date createTime;
	
	@EntityFieldDescribe("文档发布时间")
	private Date publishTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	private Date updateTime;
	
	@EntityFieldDescribe("文档摘要")
	private String summary;
	
	@EntityFieldDescribe("文档标题")
	private String title;
	
	@EntityFieldDescribe( "分类唯一标识" )
	private String categoryAlias;
	
	@EntityFieldDescribe( "分类名称" )
	private String categoryName;
	
	@EntityFieldDescribe("创建人，可能为空，如果由系统创建。")
	private String creatorPerson;

	@EntityFieldDescribe("创建人Identity，可能为空，如果由系统创建。")
	private String creatorIdentity;

	@EntityFieldDescribe("创建人部门，可能为空，如果由系统创建。")
	private String creatorDepartment;

	@EntityFieldDescribe("创建人公司，可能为空，如果由系统创建。")
	private String creatorCompany;

	@EntityFieldDescribe("文档状态: published | draft")
	private String docStatus = "draft";
	
	@EntityFieldDescribe("文档被查看次数")
	private Long viewCount = 0L;
	
	@EntityFieldDescribe("是否含有首页图片")
	private Boolean hasIndexPic = false;

	@EntityFieldDescribe("首页图片列表")
	private List<String> pictureList;

	public String getId() {
		return id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getSummary() {
		return summary;
	}

	public String getTitle() {
		return title;
	}

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public String getCreatorDepartment() {
		return creatorDepartment;
	}

	public String getCreatorCompany() {
		return creatorCompany;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public Boolean getHasIndexPic() {
		return hasIndexPic;
	}

	public List<String> getPictureList() {
		return pictureList;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public void setCreatorDepartment(String creatorDepartment) {
		this.creatorDepartment = creatorDepartment;
	}

	public void setCreatorCompany(String creatorCompany) {
		this.creatorCompany = creatorCompany;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public void setHasIndexPic(Boolean hasIndexPic) {
		this.hasIndexPic = hasIndexPic;
	}

	public void setPictureList(List<String> pictureList) {
		this.pictureList = pictureList;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	

}