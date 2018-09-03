package com.x.report.core.entity;

import java.util.Date;

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

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_S_SettingLobValue.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_S_SettingLobValue.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_S_SettingLobValue extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_S_SettingLobValue.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@FieldDescribe("创建时间,自动生成.")
	@Column(name = ColumnNamePrefix + createTime_FIELDNAME)
	private Date createTime;

	@FieldDescribe("修改时间,自动生成.")
	@Column(name = ColumnNamePrefix + updateTime_FIELDNAME)
	private Date updateTime;

	@FieldDescribe("列表序号,由创建时间以及ID组成.在保存时自动生成.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sequence_FIELDNAME)
	private String sequence;

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

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("配置值")
	@Column(name = "xlobValue", length = JpaObject.length_1M)
	private String lobValue = "";

	public String getLobValue() {
		return lobValue;
	}

	public void setLobValue(String lobValue) {
		this.lobValue = lobValue;
	}
}