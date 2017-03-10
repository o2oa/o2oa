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
 * 论坛权限角色绑定关系表
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.BBSPermissionRole.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class BBSPermissionRole extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSPermissionRole.table;
	
	public BBSPermissionRole() {
		super();
	}
	public BBSPermissionRole(String forumId, String forumName, String sectionId, String sectionName,
			String mainSectionId, String mainSectionName, String permissionType, String permissionName,
			String permissionCode, String roleId, String roleName, String roleCode, String description,
			Integer orderNumber ) {
		super();
		this.forumId = forumId;
		this.forumName = forumName;
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		this.mainSectionId = mainSectionId;
		this.mainSectionName = mainSectionName;
		this.permissionType = permissionType;
		this.permissionName = permissionName;
		this.permissionCode = permissionCode;
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleCode = roleCode;
		this.description = description;
		this.orderNumber = orderNumber;
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
	
	@EntityFieldDescribe( "权限类别：论坛权限|版块权限|其他权限" )
	@Column(name="xpermissionType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String permissionType = "其他权限";
	
	@EntityFieldDescribe( "权限名称" )
	@Column(name="xpermissionName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String permissionName = "";
	
	@EntityFieldDescribe( "权限代码" )
	@Column(name="xpermissionCode", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String permissionCode = "";
	
	@EntityFieldDescribe( "角色ID" )
	@Column(name="xroleId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String roleId = "";
	
	@EntityFieldDescribe( "角色名称" )
	@Column(name="xroleName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String roleName = "";
	
	@EntityFieldDescribe( "角色代码" )
	@Column(name="xroleCode", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String roleCode = "";
	
	@EntityFieldDescribe( "说明" )
	@Column(name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String description = "";
	
	@EntityFieldDescribe( "排序号" )
	@Column(name="xorderNumber" )
	private Integer orderNumber = 1;
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getForumId() {
		return forumId;
	}
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}
	public String getForumName() {
		return forumName;
	}
	public void setForumName(String forumName) {
		this.forumName = forumName;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
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
	public String getPermissionType() {
		return permissionType;
	}
	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public String getPermissionCode() {
		return permissionCode;
	}
	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}	
}