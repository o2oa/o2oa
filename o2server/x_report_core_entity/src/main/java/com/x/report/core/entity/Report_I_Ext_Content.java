package com.x.report.core.entity;

import java.util.Date;

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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_I_Ext_Content.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_I_Ext_Content.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_I_Ext_Content extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_I_Ext_Content.table;

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
	@FieldDescribe("概要文件信息ID")
	@Index(name = TABLE + "_profileId")
	@Column(name = "xprofileId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String profileId;

	@FieldDescribe("汇报信息ID")
	@Index(name = TABLE + "_reportId")
	@Column(name = "xreportId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String reportId;

	@FieldDescribe("信息级别：员工|汇总")
	@Column(name = "xinfoLevel", length = JpaObject.length_16B)
	private String infoLevel = "员工";

	@FieldDescribe("填写员工")
	@Column(name = "xtargetPerson", length = AbstractPersistenceProperties.organization_name_length)
	private String targetPerson;

	@FieldDescribe("排序號")
	@Column(name = "xorderNumber")
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber;

	public String getProfileId() {
		return profileId;
	}

	public String getReportId() {
		return reportId;
	}

	public String getTargetPerson() {
		return targetPerson;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setTargetPerson(String targetPerson) {
		this.targetPerson = targetPerson;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getInfoLevel() {
		return infoLevel;
	}

	public void setInfoLevel(String infoLevel) {
		this.infoLevel = infoLevel;
	}

}