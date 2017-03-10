package com.x.bbs.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
 * 系统操作日志信息表
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.BBSOperationRecord.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class BBSOperationRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSOperationRecord.table;

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
	@CheckPersist( allowEmpty = true )
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
	
	@EntityFieldDescribe( "主题名称：标题" )
	@Column(name="xtitle", length = JpaObject.length_128B )
	@CheckPersist( allowEmpty = true )
	private String title = "";
	
	@EntityFieldDescribe( "操作对象ID" )
	@Column(name="xobjectId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String objectId = "";
	
	@EntityFieldDescribe( "操作对象名称" )
	@Column(name="xobjectName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String objectName = "";
	
	@EntityFieldDescribe( "操作对象类别：论坛、版块、主题、回复" )
	@Column(name="xobjectType", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String objectType = "";
	
	@EntityFieldDescribe( "操作方式：登入，登出，新增，修改，删除，查看" )
	@Column(name="xoptType", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String optType = "未知";
	
	@EntityFieldDescribe( "操作人姓名" )
	@Column(name="xoperatorName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String operatorName = "";
	
	@EntityFieldDescribe( "主机名称" )
	@Column(name="xhostname", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String hostname = "";
	
	@EntityFieldDescribe( "主机IP地址" )
	@Column(name="xhostIp", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String hostIp = "";

	/**
	 * @EntityFieldDescribe(value="论坛名称")
@Column(name="xforumName", length=64)
@CheckPersist(allowEmpty=false)
	 * @return
	 */
	public String getForumName() {
		return forumName;
	}
	/**
	 * @EntityFieldDescribe(value="论坛名称")
@Column(name="xforumName", length=64)
@CheckPersist(allowEmpty=false)
	 * @param forumName
	 */
	public void setForumName(String forumName) {
		this.forumName = forumName;
	}
	/**
	 * @EntityFieldDescribe(value="版块名称")
@Column(name="xsectionName", length=128)
@CheckPersist(allowEmpty=false)
	 * @return
	 */
	public String getSectionName() {
		return sectionName;
	}
	/**
	 * @EntityFieldDescribe(value="版块名称")
@Column(name="xsectionName", length=128)
@CheckPersist(allowEmpty=false)
	 * @param sectionName
	 */
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	/**
	 * @EntityFieldDescribe(value="版块ID")
@Column(name="xsectionId", length=64)
@CheckPersist(allowEmpty=false)
	 * @return
	 */
	public String getForumId() {
		return forumId;
	}
	/**
	 * @EntityFieldDescribe(value="版块ID")
@Column(name="xsectionId", length=64)
@CheckPersist(allowEmpty=false)
	 * @param forumId
	 */
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}
	/**
	 * @EntityFieldDescribe(value="版块ID")
@Column(name="xsectionId", length=64)
@CheckPersist(allowEmpty=false)
	 * @return
	 */
	public String getSectionId() {
		return sectionId;
	}
	/**
	 * @EntityFieldDescribe(value="版块ID")
@Column(name="xsectionId", length=64)
@CheckPersist(allowEmpty=false)
	 * @param sectionId
	 */
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	/**
	 * @EntityFieldDescribe(value="主题名称：标题")
@Column(name="xtitle", length=128)
@CheckPersist(allowEmpty=false)
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @EntityFieldDescribe(value="主题名称：标题")
@Column(name="xtitle", length=128)
@CheckPersist(allowEmpty=false)
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @EntityFieldDescribe(value="主版块ID")
@Column(name="xmainSectionId", length=64)
@CheckPersist(allowEmpty=false)
	 * @return
	 */
	public String getMainSectionId() {
		return mainSectionId;
	}
	/**
	 * @EntityFieldDescribe(value="主版块ID")
@Column(name="xmainSectionId", length=64)
@CheckPersist(allowEmpty=false)
	 * @param mainSectionId
	 */
	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}
	/**
	 * @EntityFieldDescribe(value="主版块名称")
@Column(name="xmainSectionName", length=128)
@CheckPersist(allowEmpty=false)
	 * @return
	 */
	public String getMainSectionName() {
		return mainSectionName;
	}
	/**
	 * @EntityFieldDescribe(value="主版块名称")
@Column(name="xmainSectionName", length=128)
@CheckPersist(allowEmpty=false)
	 * @param mainSectionName
	 */
	public void setMainSectionName(String mainSectionName) {
		this.mainSectionName = mainSectionName;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getOptType() {
		return optType;
	}
	public void setOptType(String optType) {
		this.optType = optType;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
}