package com.x.cms.core.entity;

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
 * 文档可见权限记录表
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.DocumentPermission.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentPermission extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentPermission.table;

	/**
	 * 获取配置ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置配置ID
	 */
	public void setId( String id ) {
		this.id = id;
	}	
	/**
	 * 获取配置信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置配置信息创建时间
	 */
	public void setCreateTime( Date createTime ) {
		this.createTime = createTime;
	}
	/**
	 * 获取配置信息更新时间
	 */
	public void setUpdateTime( Date updateTime ) {
		this.updateTime = updateTime;
	}
	/**
	 * 设置配置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * 获取配置信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * 设置配置信息记录排序号
	 */
	public void setSequence( String sequence ) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime")
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;
	
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
		if ( null == this.sequence ) {
			this.sequence = StringUtils.join( DateTools.compact( this.getCreateTime() ), this.getId() );
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
	
	@EntityFieldDescribe( "文档ID" )
	@Column( name="xdocumentId", length = JpaObject.length_id  )
	@CheckPersist( allowEmpty = true )
	private String documentId;
	
	@EntityFieldDescribe( "文档标题" )
	@Column( name="xtitle", length = JpaObject.length_255B  )
	@CheckPersist( allowEmpty = true )
	private String title = "无标题";
	
	@EntityFieldDescribe("应用ID")
	@Column(name="xappId", length = JpaObject.length_id )
	@Index(name = TABLE + "_appId")
	@CheckPersist(allowEmpty = true)
	private String appId;
	
	@EntityFieldDescribe("应用名称")
	@Column(name="xappName", length = JpaObject.length_255B )
	@CheckPersist(allowEmpty = true)
	private String appName;

	@EntityFieldDescribe("分类ID")
	@Column(name="xcategoryId", length = JpaObject.length_id  )
	@Index(name = TABLE + "_categoryId")
	@CheckPersist( allowEmpty = true )
	private String categoryId;

	@EntityFieldDescribe( "分类名称" )
	@Column( name="xcategoryName", length = JpaObject.length_255B  )
	@CheckPersist( allowEmpty = true )
	private String categoryName;
	
	@EntityFieldDescribe( "分类别名" )
	@Column( name="xcategoryAlias", length = JpaObject.length_255B  )
	@CheckPersist( allowEmpty = true )
	private String categoryAlias;
	
	@EntityFieldDescribe( "创建日期" )
	@Column( name="xdocCreateDate" )
	@Index(name = TABLE + "_docCreateDate")
	@CheckPersist( allowEmpty = true )
	private Date docCreateDate;
	
	@EntityFieldDescribe( "发布日期" )
	@Column( name="xpublishDate" )
	@Index(name = TABLE + "_publishDate")
	@CheckPersist( allowEmpty = true )
	private Date publishDate;
	
	@EntityFieldDescribe( "文档状态：草稿|审核中|已发布|已归档" )
	@Column( name="xdocumentStatus", length = JpaObject.length_16B  )
	@Index(name = TABLE + "_documentStatus")
	@CheckPersist( allowEmpty = true )
	private String documentStatus = "草稿";
	
	@EntityFieldDescribe( "发布者姓名" )
	@Column( name="xpublisher", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String publisher;
	
	@EntityFieldDescribe( "权限类别：阅读|管理" )
	@Column( name="xpermission", length = JpaObject.length_16B )
	@Index(name = TABLE + "_permission")
	@CheckPersist( allowEmpty = true )
	private String permission = "所有人";
	
	@EntityFieldDescribe( "使用者类别：所有人|组织|人员|群组|角色" )
	@Column( name="xpermissionObjectType", length = JpaObject.length_16B )
	@Index(name = TABLE + "_permissionObjectType")
	@CheckPersist( allowEmpty = true )
	private String permissionObjectType = "所有人";
	
	@EntityFieldDescribe( "使用者编码：所有人|组织编码|人员UID|群组编码|角色编码" )
	@Column( name="xpermissionObjectCode", length = JpaObject.length_255B )
	@Index(name = TABLE + "_permissionObjectCode")
	@CheckPersist( allowEmpty = true )
	private String permissionObjectCode = "所有人";
	
	@EntityFieldDescribe( "使用者名称：所有人|组织名称|人员名称|群组名称|角色名称" )
	@Column( name="xpermissionObjectName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String permissionObjectName = "所有人";
	
	@EntityFieldDescribe( "更新标识" )
	@Column( name="xupdateFlag", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String updateFlag = null;
	
	
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
	public String getTitle() {
		return title;
	}
	public Date getPublishDate() {
		return publishDate;
	}
	public String getDocumentStatus() {
		return documentStatus;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getDocumentId() {
		return documentId;
	}
	public String getPermissionObjectType() {
		return permissionObjectType;
	}
	public String getPermissionObjectCode() {
		return permissionObjectCode;
	}
	public String getPermissionObjectName() {
		return permissionObjectName;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public void setPermissionObjectType(String permissionObjectType) {
		this.permissionObjectType = permissionObjectType;
	}
	public void setPermissionObjectCode(String permissionObjectCode) {
		this.permissionObjectCode = permissionObjectCode;
	}
	public void setPermissionObjectName(String permissionObjectName) {
		this.permissionObjectName = permissionObjectName;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public String getUpdateFlag() {
		return updateFlag;
	}
	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}
	public Date getDocCreateDate() {
		return docCreateDate;
	}
	public void setDocCreateDate(Date docCreateDate) {
		this.docCreateDate = docCreateDate;
	}
	public String getCategoryAlias() {
		return categoryAlias;
	}
	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}
	
}