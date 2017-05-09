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
 * 主题信息表
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.BBSReplyInfo.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class BBSReplyInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSReplyInfo.table;

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
		if ( null == this.createTime ) {
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
	@EntityFieldDescribe( "论坛ID" )
	@Column(name="xforumId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String forumId = "";
	
	@EntityFieldDescribe( "论坛名称" )
	@Column(name="xforumName", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String forumName = "";
	
	@EntityFieldDescribe( "版块ID" )
	@Column(name="xsectionId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String sectionId = "";
	
	@EntityFieldDescribe( "版块名称" )
	@Column(name="xsectionName", length = JpaObject.length_128B )
	@CheckPersist( allowEmpty = false )
	private String sectionName = "";
	
	@EntityFieldDescribe( "主版块ID" )
	@Column(name="xmainSectionId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String mainSectionId = "";
	
	@EntityFieldDescribe( "主版块名称" )
	@Column(name="xmainSectionName", length = JpaObject.length_128B )
	@CheckPersist( allowEmpty = true )
	private String mainSectionName = "";
	
	@EntityFieldDescribe( "主题ID" )
	@Column(name="xsubjectId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String subjectId = "";
	
	@EntityFieldDescribe( "回贴标题：如果没有则与主题相同" )
	@Column(name="xtitle", length = JpaObject.length_128B )
	@CheckPersist( allowEmpty = true )
	private String title = "";
	
	@EntityFieldDescribe( "上级回帖ID" )
	@Column(name="xparentId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String parentId = "";
	
	@EntityFieldDescribe( "主版块ID" )
	@Column(name="xpicId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String picId = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "内容" )
	@Column(name="xcontent", length = JpaObject.length_1M )
	@CheckPersist( allowEmpty = true )
	private String content = "";
	
	@EntityFieldDescribe( "创建人姓名" )
	@Column(name="xcreatorName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String creatorName = "";
	
	@EntityFieldDescribe( "回复审核状态：无审核|待审核|审核通过" )
	@Column(name="xreplyAuditStatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String replyAuditStatus = "无审核";
	
	@EntityFieldDescribe( "审核人姓名" )
	@Column(name="xauditorName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String auditorName = "";
	
	@EntityFieldDescribe( "排序号" )
	@Column(name="xorderNumber" )
	private Integer orderNumber = 1;
	
	@EntityFieldDescribe( "设备类别：手机|平板电脑|个人电脑等" )
	@Column(name="xmachineName", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String machineName = "PC";
	
	@EntityFieldDescribe( "系统名称" )
	@Column(name="xsystemType", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String systemType = "Windows";
	
	@EntityFieldDescribe( "IP地址" )
	@Column(name="xhostIp", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String hostIp = "";

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
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getReplyAuditStatus() {
		return replyAuditStatus;
	}
	public void setReplyAuditStatus(String replyAuditStatus) {
		this.replyAuditStatus = replyAuditStatus;
	}
	public String getAuditorName() {
		return auditorName;
	}
	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public String getSystemType() {
		return systemType;
	}
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	public String getPicId() {
		return picId;
	}
	public void setPicId(String picId) {
		this.picId = picId;
	}
	
}