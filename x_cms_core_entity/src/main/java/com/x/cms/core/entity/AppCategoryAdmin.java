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
 * 内容管理应用目录分类管理员配置表
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AppCategoryAdmin.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public class AppCategoryAdmin extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AppCategoryAdmin.table;

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
	
	@EntityFieldDescribe( "对象类别：应用|分类" )
	@Column( name="xobjectType",length = JpaObject.length_16B  )
	@CheckPersist( allowEmpty = true )
	private String objectType;
	
	@EntityFieldDescribe( "对象ID" )
	@Column( name="xobjectId",length = JpaObject.length_id  )
	@CheckPersist( allowEmpty = true )
	private String objectId;
	
	@EntityFieldDescribe( "对象名称" )
	@Column( name="xobjectName",length = JpaObject.length_255B  )
	@CheckPersist( allowEmpty = true )
	private String objectName;
	
	@EntityFieldDescribe( "管理员UID" )
	@Column( name="xadminUid",length = JpaObject.length_64B  )
	@CheckPersist( allowEmpty = true )
	private String adminUid;
	
	@EntityFieldDescribe( "管理员姓名" )
	@Column( name="xadminName",length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String adminName;
	
	@EntityFieldDescribe( "管理员级别: ADMIN | DOCUMENTADMIN | PUBLISHER" )
	@Column( name="xadminLevel",length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String adminLevel;
	
	@EntityFieldDescribe( "描述" )
	@Column( name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String description;

	@EntityFieldDescribe( "创建者UID" )
	@Column( name="xcreatorUid", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String creatorUid;
	
	/**
	 * 获取操作对象类别
	 * @return
	 */
	public String getObjectType() {
		return objectType;
	}
	/**
	 * 设置操作对象类别
	 * @param objectType
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	/**
	 * 获取操作对象ID
	 * @return
	 */
	public String getObjectId() {
		return objectId;
	}
	/**
	 * 设置操作对象ID
	 * @param objectId
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	/**
	 * 获取操作者帐号
	 * @return
	 */
	public String getAdminUid() {
		return adminUid;
	}
	/**
	 * 设置操作者帐号
	 * @param operatorUid
	 */
	public void setAdminUid(String adminUid) {
		this.adminUid = adminUid;
	}
	/**
	 * 获取操作者姓名
	 * @return
	 */
	public String getAdminName() {
		return adminName;
	}
	/**
	 * 设置操作者姓名
	 * @param operatorName
	 */
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	/**
	 * 获取文字描述
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置文字描述
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 获取配置创建者帐号
	 * @return
	 */
	public String getCreatorUid() {
		return creatorUid;
	}
	/**
	 * 设置配置创建者帐号
	 * @param creatorUid
	 */
	public void setCreatorUid(String creatorUid) {
		this.creatorUid = creatorUid;
	}
	/**
	 * 获取用户的管理级别：管理员级别: ADMIN | DOCUMENTADMIN | PUBLISHER
	 * @return
	 */
	public String getAdminLevel() {
		return adminLevel;
	}
	/**
	 * 设置用户的管理级别：管理员级别: ADMIN | DOCUMENTADMIN | PUBLISHER
	 * @param adminLevel
	 */
	public void setAdminLevel(String adminLevel) {
		this.adminLevel = adminLevel;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
}