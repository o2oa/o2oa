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
 * 论坛角色人员信息表
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.BBSUserRole.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSUserRole extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSUserRole.table;

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(name = "xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号, 由创建时间以及ID组成.在保存时自动生成.")
	@Column(name = "xsequence", length = AbstractPersistenceProperties.length_sequence)
	@Index(name = TABLE + "_sequence")
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
	public void prePersist() {
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		// 序列号信息的组成，与排序有关
		if (null == this.sequence) {
			this.sequence = StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId());
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
	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */
	@EntityFieldDescribe("对象所属公司名称")
	@Column(name = "xcompanyName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String companyName = "";

	@EntityFieldDescribe("对象所属部门名称")
	@Column(name = "xorganizationName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String organizationName = "";

	@EntityFieldDescribe("对象直属部门名称")
	@Column(name = "xdepartmentName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String departmentName = "";

	@EntityFieldDescribe("对象名称：姓名，部门,群组名等等")
	@Column(name = "xobjectName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String objectName = "";

	@EntityFieldDescribe("对象类别：人员|组织|群组")
	@Column(name = "xobjectType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String objectType = "";

	@EntityFieldDescribe("对象唯一标识")
	@Column(name = "xuniqueId", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String uniqueId = "";

	@EntityFieldDescribe("角色Id")
	@Column(name = "xroleId", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String roleId = "";

	@EntityFieldDescribe("角色代码")
	@Column(name = "xroleCode", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String roleCode = "";

	@EntityFieldDescribe("角色名称")
	@Column(name = "xroleName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String roleName = "";

	@EntityFieldDescribe("排序号")
	@Column(name = "xorderNumber")
	private Integer orderNumber = 1;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
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

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

}