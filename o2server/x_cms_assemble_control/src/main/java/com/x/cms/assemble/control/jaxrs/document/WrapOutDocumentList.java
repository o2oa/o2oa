package com.x.cms.assemble.control.jaxrs.document;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.cms.core.entity.Document;

/**
 * @author sword
 */
public class WrapOutDocumentList extends GsonPropertyObject {

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

	@FieldDescribe("文档状态: published | draft | waitPublish")
	private String docStatus = "draft";

	@FieldDescribe("文档被查看次数")
	private Long viewCount = 0L;

	@FieldDescribe("文档被评论次数")
	private Long commentCount = 0L;

	@FieldDescribe("文档被点赞次数")
	private Long commendCount = 0L;

	@FieldDescribe("是否含有首页图片")
	private Boolean hasIndexPic = false;

	@FieldDescribe("首页图片列表，分页展现返回前3个图片")
	private List<String> pictureList;

	@FieldDescribe("文档所有数据信息.")
	private Map<?, ?> data;

	@FieldDescribe( "是否置顶." )
	private Boolean isTop;

	@FieldDescribe( "是否全员可读." )
	private Boolean isAllRead;

	@FieldDescribe("业务数据String值01.")
	private String stringValue01;

	@FieldDescribe("业务数据String值02.")
	private String stringValue02;

	@FieldDescribe("业务数据String值03.")
	private String stringValue03;

	@FieldDescribe("业务数据String值04.")
	private String stringValue04;

	@FieldDescribe("业务数据String值05.")
	private String stringValue05;

	@FieldDescribe("业务数据String值06.")
	private String stringValue06;

	@FieldDescribe("业务数据String值07.")
	private String stringValue07;

	@FieldDescribe("业务数据String值08.")
	private String stringValue08;

	@FieldDescribe("业务数据String值09.")
	private String stringValue09;

	@FieldDescribe("业务数据String值10.")
	private String stringValue10;

	@FieldDescribe("业务数据Long值01.")
	private Long longValue01;

	@FieldDescribe("业务数据Long值02.")
	private Long longValue02;

	@FieldDescribe("业务数据Double值01.")
	private Double doubleValue01;

	@FieldDescribe("业务数据Double值02.")
	private Double doubleValue02;

	@FieldDescribe("业务数据DateTime值01.")
	private Date dateTimeValue01;

	@FieldDescribe("业务数据DateTime值02.")
	private Date dateTimeValue02;

	@FieldDescribe("业务数据DateTime值03.")
	private Date dateTimeValue03;

	@FieldDescribe( "是否已读." )
	private Boolean hasRead = false;

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

	public Boolean getIsTop() {
		return isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	public Boolean getIsAllRead() {
		return isAllRead;
	}

	public void setIsAllRead(Boolean isAllRead) {
		this.isAllRead = isAllRead;
	}

	public String getStringValue01() {
		return stringValue01;
	}

	public void setStringValue01(String stringValue01) {
		this.stringValue01 = stringValue01;
	}

	public String getStringValue02() {
		return stringValue02;
	}

	public void setStringValue02(String stringValue02) {
		this.stringValue02 = stringValue02;
	}

	public String getStringValue03() {
		return stringValue03;
	}

	public void setStringValue03(String stringValue03) {
		this.stringValue03 = stringValue03;
	}

	public String getStringValue04() {
		return stringValue04;
	}

	public void setStringValue04(String stringValue04) {
		this.stringValue04 = stringValue04;
	}

	public Long getLongValue01() {
		return longValue01;
	}

	public void setLongValue01(Long longValue01) {
		this.longValue01 = longValue01;
	}

	public Long getLongValue02() {
		return longValue02;
	}

	public void setLongValue02(Long longValue02) {
		this.longValue02 = longValue02;
	}

	public Double getDoubleValue01() {
		return doubleValue01;
	}

	public void setDoubleValue01(Double doubleValue01) {
		this.doubleValue01 = doubleValue01;
	}

	public Double getDoubleValue02() {
		return doubleValue02;
	}

	public void setDoubleValue02(Double doubleValue02) {
		this.doubleValue02 = doubleValue02;
	}

	public Date getDateTimeValue01() {
		return dateTimeValue01;
	}

	public void setDateTimeValue01(Date dateTimeValue01) {
		this.dateTimeValue01 = dateTimeValue01;
	}

	public Date getDateTimeValue02() {
		return dateTimeValue02;
	}

	public void setDateTimeValue02(Date dateTimeValue02) {
		this.dateTimeValue02 = dateTimeValue02;
	}

	public String getStringValue05() {
		return stringValue05;
	}

	public void setStringValue05(String stringValue05) {
		this.stringValue05 = stringValue05;
	}

	public String getStringValue06() {
		return stringValue06;
	}

	public void setStringValue06(String stringValue06) {
		this.stringValue06 = stringValue06;
	}

	public Date getDateTimeValue03() {
		return dateTimeValue03;
	}

	public void setDateTimeValue03(Date dateTimeValue03) {
		this.dateTimeValue03 = dateTimeValue03;
	}

	public Boolean getHasRead() {
		return hasRead;
	}

	public void setHasRead(Boolean hasRead) {
		this.hasRead = hasRead;
	}

	public String getStringValue07() {
		return stringValue07;
	}

	public void setStringValue07(String stringValue07) {
		this.stringValue07 = stringValue07;
	}

	public String getStringValue08() {
		return stringValue08;
	}

	public void setStringValue08(String stringValue08) {
		this.stringValue08 = stringValue08;
	}

	public String getStringValue09() {
		return stringValue09;
	}

	public void setStringValue09(String stringValue09) {
		this.stringValue09 = stringValue09;
	}

	public String getStringValue10() {
		return stringValue10;
	}

	public void setStringValue10(String stringValue10) {
		this.stringValue10 = stringValue10;
	}
}
