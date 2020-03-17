package com.x.cms.assemble.control.jaxrs.document;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.Document;

public class WrapOutDocumentList {
	
	@FieldDescribe( "sequence." )
	private String sequence;
	
	@FieldDescribe( "文档ID." )
	private String id = Document.createId();

	@FieldDescribe( "创建时间" )
	private Date createTime;
	
	@FieldDescribe("文档发布时间")
	private Date publishTime;

	@FieldDescribe( "最近修改时间" )
	private Date updateTime;
	
	@FieldDescribe("文档摘要")
	private String summary;
	
	@FieldDescribe("文档标题")
	private String title;
	
	@FieldDescribe("文件导入的批次号：一般是分类ID+时间缀")
	private String importBatchName;
	
	@FieldDescribe("说明备注，可以填写说明信息，如导入信息检验失败原因等")
	private String description = null;
	
	@FieldDescribe( "分类唯一标识" )
	private String categoryAlias;
	
	@FieldDescribe( "分类名称" )
	private String categoryName;
	
	@FieldDescribe( "栏目ID" )
	private String appId;
	
	@FieldDescribe( "分类ID" )
	private String categoryId;
	
	@FieldDescribe("创建人，可能为空，如果由系统创建。")
	private String creatorPerson;

	@FieldDescribe("创建人组织名称，可能为空，如果由系统创建。")
	private String creatorUnitName;

	@FieldDescribe("创建人顶层组织名称，可能为空，如果由系统创建。")
	private String creatorTopUnitName;

	@FieldDescribe("文档状态: published | draft")
	private String docStatus = "draft";
	
	@FieldDescribe("文档被查看次数")
	private Long viewCount = 0L;
	
	@FieldDescribe("文档被评论次数")
	private Long commentCount = 0L;
	
	@FieldDescribe("文档被点赞次数")
	private Long commendCount = 0L;		
	
	@FieldDescribe("是否含有首页图片")
	private Boolean hasIndexPic = false;

	@FieldDescribe("首页图片列表")
	private List<String> pictureList;		
	
	@FieldDescribe("文档所有数据信息.")
	private Map<?, ?> data;
	
	@FieldDescribe( "是否置顶." )
	private String isTop;
	
	/**
	 * 只作显示用
	 */
	private String creatorPersonShort = null;
	
	private String creatorUnitNameShort = null;
	
	private String creatorTopUnitNameShort = null;

	
	public String getImportBatchName() {
		return importBatchName;
	}

	public String getDescription() {
		return description;
	}

	public void setImportBatchName(String importBatchName) {
		this.importBatchName = importBatchName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatorPersonShort() {
		return creatorPersonShort;
	}

	public String getCreatorUnitNameShort() {
		return creatorUnitNameShort;
	}

	public String getCreatorTopUnitNameShort() {
		return creatorTopUnitNameShort;
	}

	public void setCreatorPersonShort(String creatorPersonShort) {
		this.creatorPersonShort = creatorPersonShort;
	}

	public void setCreatorUnitNameShort(String creatorUnitNameShort) {
		this.creatorUnitNameShort = creatorUnitNameShort;
	}

	public void setCreatorTopUnitNameShort(String creatorTopUnitNameShort) {
		this.creatorTopUnitNameShort = creatorTopUnitNameShort;
	}

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

	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	public String getCreatorTopUnitName() {
		return creatorTopUnitName;
	}

	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	public void setCreatorTopUnitName(String creatorTopUnitName) {
		this.creatorTopUnitName = creatorTopUnitName;
	}

	public String getAppId() {
		return appId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Map<?, ?> getData() {
		return data;
	}

	public void setData(Map<?, ?> data) {
		this.data = data;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public Long getCommendCount() {
		return commendCount;
	}

	public void setCommendCount(Long commendCount) {
		this.commendCount = commendCount;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getIsTop() {
		return isTop;
	}

	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}
	
}
