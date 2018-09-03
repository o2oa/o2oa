package com.x.okr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 领导秘书配置信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrConfigSecretary.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrConfigSecretary.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrConfigSecretary extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrConfigSecretary.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */

	@FieldDescribe("秘书姓名")
	@Column(name = "xsecretaryName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String secretaryName = "";

	@FieldDescribe("秘书身份名称")
	@Column(name = "xsecretaryIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String secretaryIdentity = "";

	@FieldDescribe("秘书所属组织")
	@Column(name = "xsecretaryUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String secretaryUnitName = "";

	@FieldDescribe("秘书所属顶层组织")
	@Column(name = "xsecretaryTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String secretaryTopUnitName = "";

	@FieldDescribe("领导姓名")
	@Column(name = "xleaderName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String leaderName = "";

	@FieldDescribe("领导身份名称")
	@Column(name = "xleaderIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String leaderIdentity = "";

	@FieldDescribe("领导所属组织")
	@Column(name = "xleaderUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String leaderUnitName = "";

	@FieldDescribe("领导所属顶层组织")
	@Column(name = "xleaderTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String leaderTopUnitName = "";

	@FieldDescribe("备注说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	/**
	 * 获取秘书姓名
	 * 
	 * @return
	 */
	public String getSecretaryName() {
		return secretaryName;
	}

	/**
	 * 设置秘书姓名
	 * 
	 * @param secretaryName
	 */
	public void setSecretaryName(String secretaryName) {
		this.secretaryName = secretaryName;
	}

	/**
	 * 获取秘书所属组织名称
	 * 
	 * @return
	 */
	public String getSecretaryUnitName() {
		return secretaryUnitName;
	}

	/**
	 * 设置秘书所属组织名称
	 * 
	 * @param secretaryUnitName
	 */
	public void setSecretaryUnitName(String secretaryUnitName) {
		this.secretaryUnitName = secretaryUnitName;
	}

	/**
	 * 获取秘书所属顶层组织名称
	 * 
	 * @return
	 */
	public String getSecretaryTopUnitName() {
		return secretaryTopUnitName;
	}

	/**
	 * 设置秘书所属顶层组织名称
	 * 
	 * @param secretaryTopUnitName
	 */
	public void setSecretaryTopUnitName(String secretaryTopUnitName) {
		this.secretaryTopUnitName = secretaryTopUnitName;
	}

	/**
	 * 获取领导姓名
	 * 
	 * @return
	 */
	public String getLeaderName() {
		return leaderName;
	}

	/**
	 * 设置领导姓名
	 * 
	 * @param leaderName
	 */
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	/**
	 * 获取领导所属组织名称
	 * 
	 * @return
	 */
	public String getLeaderUnitName() {
		return leaderUnitName;
	}

	/**
	 * 设置领导所属组织名称
	 * 
	 * @param leaderUnitName
	 */
	public void setLeaderUnitName(String leaderUnitName) {
		this.leaderUnitName = leaderUnitName;
	}

	/**
	 * 获取领导所属顶层组织名称
	 * 
	 * @return
	 */
	public String getLeaderTopUnitName() {
		return leaderTopUnitName;
	}

	/**
	 * 设置领导所属顶层组织名称
	 * 
	 * @param leaderTopUnitName
	 */
	public void setLeaderTopUnitName(String leaderTopUnitName) {
		this.leaderTopUnitName = leaderTopUnitName;
	}

	/**
	 * 获取备注说明信息
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置备注说明信息
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取领导的身份
	 * 
	 * @return
	 */
	public String getLeaderIdentity() {
		return leaderIdentity;
	}

	/**
	 * 设置领导的身份
	 * 
	 * @param leaderIdentity
	 */
	public void setLeaderIdentity(String leaderIdentity) {
		this.leaderIdentity = leaderIdentity;
	}

	public String getSecretaryIdentity() {
		return secretaryIdentity;
	}

	public void setSecretaryIdentity(String secretaryIdentity) {
		this.secretaryIdentity = secretaryIdentity;
	}

}