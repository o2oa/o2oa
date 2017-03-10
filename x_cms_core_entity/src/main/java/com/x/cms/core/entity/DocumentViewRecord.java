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
 * 内容管理日志信息表
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.DocumentViewRecord.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentViewRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentViewRecord.table;

	/**
	 * 获取日志ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置日志ID
	 */
	public void setId( String id ) {
		this.id = id;
	}	
	/**
	 * 获取日志信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置日志信息创建时间
	 */
	public void setCreateTime( Date createTime ) {
		this.createTime = createTime;
	}
	/**
	 * 获取日志信息更新时间
	 */
	public void setUpdateTime( Date updateTime ) {
		this.updateTime = updateTime;
	}
	/**
	 * 设置日志信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * 获取日志信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * 设置日志信息记录排序号
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
	
	@EntityFieldDescribe( "应用ID" )
	@Column( name="xappId", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String appId;
	
	@EntityFieldDescribe( "应用名称" )
	@Column( name="xappName", length = JpaObject.length_96B )
	@CheckPersist( allowEmpty = true )
	private String appName;
	
	@EntityFieldDescribe( "分类ID" )
	@Column( name="xcategoryId", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String categoryId;
	
	@EntityFieldDescribe( "分类名称" )
	@Column( name="xcategoryName", length = JpaObject.length_96B )
	@CheckPersist( allowEmpty = true )
	private String categoryName;
	
	@EntityFieldDescribe( "文档ID" )
	@Column( name="xdocumentId", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String documentId;
	
	@EntityFieldDescribe( "文档标题" )
	@Column( name="xtitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String title;
	
	@EntityFieldDescribe( "访问者姓名" )
	@Column( name="xviewerName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false )
	private String viewerName;
	
	@EntityFieldDescribe( "访问者所属部门" )
	@Column( name="xviewerOrganization", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String viewerOrganization;
	
	@EntityFieldDescribe( "访问者所属公司" )
	@Column( name="xviewerCompany", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String viewerCompany;

	public String getAppId() {
		return appId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public String getDocumentId() {
		return documentId;
	}
	public String getViewerName() {
		return viewerName;
	}
	public String getViewerOrganization() {
		return viewerOrganization;
	}
	public String getViewerCompany() {
		return viewerCompany;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public void setViewerName(String viewerName) {
		this.viewerName = viewerName;
	}
	public void setViewerOrganization(String viewerOrganization) {
		this.viewerOrganization = viewerOrganization;
	}
	public void setViewerCompany(String viewerCompany) {
		this.viewerCompany = viewerCompany;
	}
	public String getAppName() {
		return appName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public String getTitle() {
		return title;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}