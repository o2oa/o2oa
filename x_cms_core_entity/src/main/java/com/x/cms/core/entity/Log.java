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
@Table(name = PersistenceProperties.Log.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public class Log extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Log.table;

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
	public void prePersist() {
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
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() {
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe( "操作类别：新增|修改|删除|查看|查询" )
	@Column( name="xoperationType", length = JpaObject.length_64B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String operationType;

	@EntityFieldDescribe( "操作级别：应用|分类|文档|文件" )
	@Column( name="xoperationLevel", length = JpaObject.length_64B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String operationLevel;
	
	@EntityFieldDescribe( "操作对象：应用ID" )
	@Column( name="xappId", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String appId;
	
	@EntityFieldDescribe( "操作对象：分类ID" )
	@Column( name="xcatagoryId", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String catagoryId;
	
	@EntityFieldDescribe( "操作对象：文档ID" )
	@Column( name="xdocumentId", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String documentId;
	
	@EntityFieldDescribe( "操作对象：文件ID" )
	@Column( name="xfileId", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String fileId;
	
	@EntityFieldDescribe( "操作者UID" )
	@Column( name="xoperatorUid", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String operatorUid;
	
	@EntityFieldDescribe( "操作者姓名" )
	@Column( name="xoperatorName", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String operatorName;
	
	@EntityFieldDescribe( "操作文字描述" )
	@Column( name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String description;

	/**
	 * 获取操作类别
	 * @return
	 */
	public String getOperationType() {
		return operationType;
	}
	/**
	 * 设置操作类别
	 * @param operationType
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}	
	/**
	 * 获取操作者帐号
	 * @return
	 */
	public String getOperatorUid() {
		return operatorUid;
	}
	/**
	 * 设置操作者帐号
	 * @param operatorUid
	 */
	public void setOperatorUid(String operatorUid) {
		this.operatorUid = operatorUid;
	}
	/**
	 * 获取操作者姓名
	 * @return
	 */
	public String getOperatorName() {
		return operatorName;
	}
	/**
	 * 设置操作者姓名
	 * @param operatorName
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	/**
	 * 获取操作日志文字描述
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置操作日志文字描述
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 获取操作的应用ID
	 * @return
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * 设置操作的应用ID
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/**
	 * 获取操作的分类ID
	 * @return
	 */
	public String getCatagoryId() {
		return catagoryId;
	}
	/**
	 * 设置操作的分类ID
	 * @param appId
	 */
	public void setCatagoryId(String catagoryId) {
		this.catagoryId = catagoryId;
	}
	/**
	 * 获取操作的文档ID
	 * @return
	 */
	public String getDocumentId() {
		return documentId;
	}
	/**
	 * 设置操作的文档ID
	 * @param appId
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	/**
	 * 获取操作的文件ID
	 * @return
	 */
	public String getFileId() {
		return fileId;
	}	
	/**
	 * 设置操作的文件ID
	 * @param appId
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	/**
	 * 操作对象级别：应用|分类|文档|文件
	 * @return
	 */
	public String getOperationLevel() {
		return operationLevel;
	}
	/**
	 * 操作对象级别：应用|分类|文档|文件
	 * @return
	 */
	public void setOperationLevel(String operationLevel) {
		this.operationLevel = operationLevel;
	}	
}