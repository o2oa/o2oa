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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 论坛信息表
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.BBSForumInfo.table)
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class BBSForumInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSForumInfo.table;

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
	
	@EntityFieldDescribe( "论坛名称" )
	@Column(name="xforumName", length = JpaObject.length_64B )
	private String forumName = "";
	
	@EntityFieldDescribe( "论坛管理员姓名" )
	@Column(name="xforumManagerName", length = JpaObject.length_32B )
	private String forumManagerName = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "论坛公告" )
	@Column(name="xforumNotice", length = JpaObject.length_32K )
	private String forumNotice = "";

	@EntityFieldDescribe( "论坛可见：所有人（默认）|根据权限" )
	@Column(name="xforumVisiable", length = JpaObject.length_16B )
	private String forumVisiable = "所有人";
	
	@EntityFieldDescribe( "版块发贴权限：所有人（默认）|根据权限" )
	@Column(name="xsubjectPublishAble", length = JpaObject.length_16B )
	private String subjectPublishAble = "所有人";
	
	@EntityFieldDescribe( "版块回复权限：所有人（默认）|根据权限" )
	@Column(name="xreplyPublishAble", length = JpaObject.length_16B )
	private String replyPublishAble = "所有人";
	
	@EntityFieldDescribe( "首页列表样式：经典|简单矩形|图片矩形" )
	@Column(name="xindexListStyle", length = JpaObject.length_16B )
	private String indexListStyle = "经典";
	
	@EntityFieldDescribe( "论坛主页面样式：经典|新闻|照片" )
	@Column(name="xforumIndexStyle", length = JpaObject.length_16B )
	private String forumIndexStyle = "经典";
	
	@EntityFieldDescribe( "论坛版块默认的主题分类名称,以|分隔,如讨论|新闻" )
	@Column(name="xsubjectType", length = JpaObject.length_255B )
	private String subjectType = "讨论|新闻";
	
	@EntityFieldDescribe( "论坛版块支持的主题类别名称,以|分隔,默认:信息|问题|投票" )
	@Column(name="xtypeCategory", length = JpaObject.length_255B )
	private String typeCategory = "信息|问题|投票";
	
	@EntityFieldDescribe( "允许推荐到首页：true|false" )
	@Column(name="xindexRecommendable" )
	private Boolean indexRecommendable = true;
	
	@EntityFieldDescribe( "主题需要审核：true|false" )
	@Column(name="xsubjectNeedAudit" )
	private Boolean subjectNeedAudit = false;
	
	@EntityFieldDescribe( "回复需要审核：true|false" )
	@Column(name="xreplyNeedAudit" )
	private Boolean replyNeedAudit = false;
	
	@EntityFieldDescribe( "允许创建版块：true|false" )
	@Column(name="xsectionCreateAble" )
	private Boolean sectionCreateAble = true;
	
	@EntityFieldDescribe( "版块数量" )
	@Column(name="xsectionTotal" )
	private Long sectionTotal = 0L;
	
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
	private String creatorName = "";
	
	@EntityFieldDescribe( "主题顔色" )
	@Column(name="xforumColor", length = JpaObject.length_32B )
	private String forumColor = "";
	
	@EntityFieldDescribe( "论坛状态：启用|停用" )
	@Column(name="xforumStatus", length = JpaObject.length_16B )
	private String forumStatus = "启用";
	
	@EntityFieldDescribe( "排序号" )
	@Column(name="xorderNumber" )
	private Integer orderNumber = 1;

	public String getForumName() {
		return forumName;
	}
	public void setForumName(String forumName) {
		this.forumName = forumName;
	}
	public String getForumManagerName() {
		return forumManagerName;
	}
	public void setForumManagerName(String forumManagerName) {
		this.forumManagerName = forumManagerName;
	}
	public String getForumNotice() {
		return forumNotice;
	}
	public void setForumNotice(String forumNotice) {
		this.forumNotice = forumNotice;
	}
	public String getForumVisiable() {
		return forumVisiable;
	}
	public void setForumVisiable(String forumVisiable) {
		this.forumVisiable = forumVisiable;
	}
	public String getIndexListStyle() {
		return indexListStyle;
	}
	public void setIndexListStyle(String indexListStyle) {
		this.indexListStyle = indexListStyle;
	}
	public String getForumIndexStyle() {
		return forumIndexStyle;
	}
	public void setForumIndexStyle(String forumIndexStyle) {
		this.forumIndexStyle = forumIndexStyle;
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
	public Boolean getSectionCreateAble() {
		return sectionCreateAble;
	}
	public void setSectionCreateAble(Boolean sectionCreateAble) {
		this.sectionCreateAble = sectionCreateAble;
	}
	public Long getSectionTotal() {
		return sectionTotal;
	}
	public void setSectionTotal( Long sectionTotal ) {
		this.sectionTotal = sectionTotal;
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
	public String getForumStatus() {
		return forumStatus;
	}
	public void setForumStatus(String forumStatus) {
		this.forumStatus = forumStatus;
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
	public String getForumColor() {
		return forumColor;
	}
	public void setForumColor(String forumColor) {
		this.forumColor = forumColor;
	}
	public String getSubjectType() {
		return subjectType;
	}
	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	public String getTypeCategory() {
		return typeCategory;
	}
	public void setTypeCategory(String typeCategory) {
		this.typeCategory = typeCategory;
	}
}