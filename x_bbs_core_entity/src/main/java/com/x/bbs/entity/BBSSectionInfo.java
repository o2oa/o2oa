package com.x.bbs.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 版块信息表
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.BBSSectionInfo.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class BBSSectionInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSSectionInfo.table;

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime" )
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime" )
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;
	
	/**
	 * 获取记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置记录ID
	 */
	public void setId(String id) {
		this.id = id;
	}	
	/**
	 * 获取信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * 设置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * 获取信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * 设置信息记录排序号
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	
	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() throws Exception { 
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		//序列号信息的组成，与排序有关
		if (null == this.sequence) {
			this.sequence = StringUtils.join( DateTools.compact(this.getCreateTime()), this.getId() );
		}
		this.onPersist();
	}
	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception{
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	
	@EntityFieldDescribe( "版块名称" )
	@Column(name="xsectionName", length = JpaObject.length_128B )
	private String sectionName = "";

	@EntityFieldDescribe( "论坛ID" )
	@Column(name="xforumId", length = JpaObject.length_id )
	private String forumId = "";
	
	@EntityFieldDescribe( "论坛名称" )
	@Column(name="xforumName", length = JpaObject.length_64B )
	private String forumName = "";
	
	@EntityFieldDescribe( "主版块ID" )
	@Column(name="xmainSectionId", length = JpaObject.length_id )
	private String mainSectionId = "";
	
	@EntityFieldDescribe( "主版块名称" )
	@Column(name="xmainSectionName", length = JpaObject.length_128B )
	private String mainSectionName = "";
	
	@EntityFieldDescribe( "版块级别：主版块|子版块" )
	@Column(name="xsectionLevel", length = JpaObject.length_16B )
	private String sectionLevel = "主版块";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "版块简介" )
	@Column(name="xsectionDescription", length = JpaObject.length_2K )
	private String sectionDescription = "";
	
	@EntityFieldDescribe("图标icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "xicon", length = JpaObject.length_32K)
	private String icon;
	
	@Lob
	@EntityFieldDescribe( "版块公告" )
	@Column(name="xsectionNotice", length = JpaObject.length_16K )
	private String sectionNotice = "";

	@EntityFieldDescribe( "版块可见：所有人（默认）|根据权限" )
	@Column(name="xsectionVisiable", length = JpaObject.length_16B )
	private String sectionVisiable = "所有人";
	
	@EntityFieldDescribe( "版块发贴权限：所有人（默认）|根据权限" )
	@Column(name="xsubjectPublishAble", length = JpaObject.length_16B )
	private String subjectPublishAble = "所有人";
	
	@EntityFieldDescribe( "版块回复权限：所有人（默认）|根据权限" )
	@Column(name="xreplyPublishAble", length = JpaObject.length_16B )
	private String replyPublishAble = "所有人";
	
	@EntityFieldDescribe( "版主姓名：可多值，默认为创建者" )
	@Column(name="xmoderatorNames", length = JpaObject.length_255B )
	private String moderatorNames = "";
	
	@EntityFieldDescribe( "版块类别：图片新闻，普通新闻，公告，经典（默认）" )
	@Column(name="xsectionType", length = JpaObject.length_16B )
	private String sectionType = "经典";
	
	@EntityFieldDescribe( "论坛版块默认的主题分类名称,以|分隔,如讨论|新闻, 如果未填写, 则以分区配置为主" )
	@Column(name="xsubjectType", length = JpaObject.length_255B )
	private String subjectType = "讨论|新闻";
	
	@EntityFieldDescribe( "论坛版块支持的主题类别名称,以|分隔,信息|问题|投票, 如果未填写,则以分区配置为主" )
	@Column(name="xtypeCatagory", length = JpaObject.length_255B )
	private String typeCatagory = "信息|问题|投票";
	
	@EntityFieldDescribe( "允许推荐到首页：true|false" )
	@Column(name="xindexRecommendable" )
	private Boolean indexRecommendable = true;
	
	@EntityFieldDescribe( "主题需要审核：true|false" )
	@Column(name="xsubjectNeedAudit" )
	@CheckPersist( allowEmpty = true )
	private Boolean subjectNeedAudit = false;
	
	@EntityFieldDescribe( "回复需要审核：true|false" )
	@Column(name="xreplyNeedAudit" )
	@CheckPersist( allowEmpty = true )
	private Boolean replyNeedAudit = false;
	
	@EntityFieldDescribe( "允许创建子版块：true|false" )
	@Column(name="xsubSectionCreateAble" )
	@CheckPersist( allowEmpty = true )
	private Boolean subSectionCreateAble = true;
	
	@EntityFieldDescribe( "主题数量" )
	@Column(name="xsubjectTotal" )
	private Long subjectTotal = 0L;
	
	@EntityFieldDescribe( "回复数量" )
	@Column(name="xreplyTotal" )
	private Long replyTotal = 0L;
	
	@EntityFieldDescribe( "今日主题数量" )
	@Column(name="xsubjectTotalToday" )
	private Long subjectTotalToday = 0L;
	
	@EntityFieldDescribe( "今日回复数量" )
	@Column(name="xreplyTotalToday" )
	private Long replyTotalToday = 0L;
	
	@EntityFieldDescribe( "创建人姓名" )
	@Column(name="xcreatorName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String creatorName = "";
	
	@EntityFieldDescribe( "版块状态：启用|停用" )
	@Column(name="xsectionStatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String sectionStatus = "启用";
	
	@EntityFieldDescribe( "排序号" )
	@Column(name="xorderNumber" )
	private Integer orderNumber = 1;

	public String getForumName() {
		return forumName;
	}
	public void setForumName(String forumName) {
		this.forumName = forumName;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getForumId() {
		return forumId;
	}
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}
	public String getMainSectionId() {
		return mainSectionId;
	}
	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}
	public String getMainSectionName() {
		return mainSectionName;
	}
	public void setMainSectionName(String mainSectionName) {
		this.mainSectionName = mainSectionName;
	}
	public String getSectionLevel() {
		return sectionLevel;
	}
	public void setSectionLevel(String sectionLevel) {
		this.sectionLevel = sectionLevel;
	}
	public String getSectionDescription() {
		return sectionDescription;
	}
	public void setSectionDescription(String sectionDescription) {
		this.sectionDescription = sectionDescription;
	}
	public String getSectionNotice() {
		return sectionNotice;
	}
	public void setSectionNotice(String sectionNotice) {
		this.sectionNotice = sectionNotice;
	}
	public String getSectionVisiable() {
		return sectionVisiable;
	}
	public void setSectionVisiable(String sectionVisiable) {
		this.sectionVisiable = sectionVisiable;
	}
	public String getSubjectPublishAble() {
		return subjectPublishAble;
	}
	public void setSubjectPublishAble(String subjectPublishAble) {
		this.subjectPublishAble = subjectPublishAble;
	}
	public String getReplyPublishAble() {
		return replyPublishAble;
	}
	public void setReplyPublishAble(String replyPublishAble) {
		this.replyPublishAble = replyPublishAble;
	}
	public String getModeratorNames() {
		return moderatorNames;
	}
	public void setModeratorNames(String moderatorNames) {
		this.moderatorNames = moderatorNames;
	}
	public String getSectionType() {
		return sectionType;
	}
	public void setSectionType(String sectionType) {
		this.sectionType = sectionType;
	}
	public Boolean getIndexRecommendable() {
		return indexRecommendable;
	}
	public void setIndexRecommendable(Boolean indexRecommendable) {
		this.indexRecommendable = indexRecommendable;
	}
	public Boolean getSubjectNeedAudit() {
		return subjectNeedAudit;
	}
	public void setSubjectNeedAudit(Boolean subjectNeedAudit) {
		this.subjectNeedAudit = subjectNeedAudit;
	}
	public Boolean getReplyNeedAudit() {
		return replyNeedAudit;
	}
	public void setReplyNeedAudit(Boolean replyNeedAudit) {
		this.replyNeedAudit = replyNeedAudit;
	}
	public Boolean getSubSectionCreateAble() {
		return subSectionCreateAble;
	}
	public void setSubSectionCreateAble(Boolean subSectionCreateAble) {
		this.subSectionCreateAble = subSectionCreateAble;
	}
	public Long getSubjectTotal() {
		return subjectTotal;
	}
	public void setSubjectTotal(Long subjectTotal) {
		this.subjectTotal = subjectTotal;
	}
	public Long getReplyTotal() {
		return replyTotal;
	}
	public void setReplyTotal(Long replyTotal) {
		this.replyTotal = replyTotal;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getSectionStatus() {
		return sectionStatus;
	}
	public void setSectionStatus(String sectionStatus) {
		this.sectionStatus = sectionStatus;
	}
	public Long getSubjectTotalToday() {
		return subjectTotalToday;
	}
	public void setSubjectTotalToday(Long subjectTotalToday) {
		this.subjectTotalToday = subjectTotalToday;
	}
	public Long getReplyTotalToday() {
		return replyTotalToday;
	}
	public void setReplyTotalToday(Long replyTotalToday) {
		this.replyTotalToday = replyTotalToday;
	}
	public String getSubjectType() {
		return subjectType;
	}
	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	public String getTypeCatagory() {
		return typeCatagory;
	}
	public void setTypeCatagory(String typeCatagory) {
		this.typeCatagory = typeCatagory;
	}
	
}