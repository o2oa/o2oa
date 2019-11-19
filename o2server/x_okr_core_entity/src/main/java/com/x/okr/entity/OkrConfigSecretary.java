package com.x.okr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

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
	public static final String secretaryName_FIELDNAME = "secretaryName";
	@FieldDescribe("秘书姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ secretaryName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + secretaryName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String secretaryName = "";

	public static final String secretaryIdentity_FIELDNAME = "secretaryIdentity";
	@FieldDescribe("秘书身份名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ secretaryIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + secretaryIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String secretaryIdentity = "";

	public static final String secretaryUnitName_FIELDNAME = "secretaryUnitName";
	@FieldDescribe("秘书所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ secretaryUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String secretaryUnitName = "";

	public static final String secretaryTopUnitName_FIELDNAME = "secretaryTopUnitName";
	@FieldDescribe("秘书所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ secretaryTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String secretaryTopUnitName = "";

	public static final String leaderName_FIELDNAME = "leaderName";
	@FieldDescribe("领导姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ leaderName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String leaderName = "";

	public static final String leaderIdentity_FIELDNAME = "leaderIdentity";
	@FieldDescribe("领导身份名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ leaderIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + leaderIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String leaderIdentity = "";

	public static final String leaderUnitName_FIELDNAME = "leaderUnitName";
	@FieldDescribe("领导所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ leaderUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String leaderUnitName = "";

	public static final String leaderTopUnitName_FIELDNAME = "leaderTopUnitName";
	@FieldDescribe("领导所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ leaderTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String leaderTopUnitName = "";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
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