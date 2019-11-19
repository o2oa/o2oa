package com.x.okr.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
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
 * 系统问题身份涉及记录详细信息实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrErrorIdentityRecords.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrErrorIdentityRecords.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrErrorIdentityRecords extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrErrorIdentityRecords.table;

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

	public static final String identity_FIELDNAME = "identity";
	@FieldDescribe("身份名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ identity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + identity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String identity = "未知";

	public static final String recordsJson_FIELDNAME = "recordsJson";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("涉及到的所有记录JSON")
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + recordsJson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String recordsJson = "";

	public static final String flag_FIELDNAME = "flag";
	@FieldDescribe("更新标识")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + flag_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + flag_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String flag = "未知";

	public String getIdentity() {
		return identity;
	}

	public String getRecordsJson() {
		return recordsJson;
	}

	public String getFlag() {
		return flag;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public void setRecordsJson(String recordsJson) {
		this.recordsJson = recordsJson;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}