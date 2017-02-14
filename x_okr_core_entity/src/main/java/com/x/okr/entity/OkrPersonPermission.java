package com.x.okr.entity;

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
 * 角色关系信息配置
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrPersonPermission.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrPersonPermission extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrPersonPermission.table;

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
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() {
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
	
	@EntityFieldDescribe( "目标对象类别值，比如人员的身份" )
	@Column(name="xtargetObjectKey", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = false )
	private String targetObjectKey = "";
	
	@EntityFieldDescribe( "目标对象类别：IDENTITY|ORGANIZATION|COMPANY|GROUP" )
	@Column(name="xtargetObjectType", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = false )
	private String targetObjectType = "IDENTITY";
	
	@EntityFieldDescribe( "组织名称" )
	@Column(name="xorganizationName", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true )
	private String organizationName = "";
	
	@EntityFieldDescribe( "公司名称" )
	@Column(name="xcompanyName", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true )
	private String companyName = "";
	
	@EntityFieldDescribe( "对象类型: ROLE | PERMISSION" )
	@Column(name="xobjectType", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String objectType = "ROLE";
	
	@EntityFieldDescribe( "对象名称" )
	@Column(name="xobjectName", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String objectName = "";
	
	@EntityFieldDescribe( "对象代码" )
	@Column(name="xobjectCode", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String objectCode = "";
	
	@EntityFieldDescribe( "角色说明" )
	@Column(name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String description = "";

	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getObjectCode() {
		return objectCode;
	}
	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getTargetObjectKey() {
		return targetObjectKey;
	}
	public void setTargetObjectKey(String targetObjectKey) {
		this.targetObjectKey = targetObjectKey;
	}
	public String getTargetObjectType() {
		return targetObjectType;
	}
	public void setTargetObjectType(String targetObjectType) {
		this.targetObjectType = targetObjectType;
	}
	
}