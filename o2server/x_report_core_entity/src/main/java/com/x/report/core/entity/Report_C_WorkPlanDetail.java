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

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_C_WorkPlanDetail.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_C_WorkPlanDetail.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_C_WorkPlanDetail extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_C_WorkPlanDetail.table;

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
	@FieldDescribe("汇报信息ID")
	@Index(name = TABLE + "_xreportId")
	@Column(name = "xreportId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String reportId;

	@FieldDescribe("工作信息ID")
	@Index(name = TABLE + "_xworkInfoId")
	@Column(name = "xworkInfoId", length = JpaObject.length_id)
	private String workInfoId;

	@FieldDescribe("重点工作ID")
	@Index(name = TABLE + "_xkeyWorkId")
	@Column(name = "xkeyWorkId", length = JpaObject.length_id)
	private String keyWorkId;

	@Lob
	@FieldDescribe("工作标题")
	@Column(name = "xworkTitle", length = JpaObject.length_1M)
	private String workTitle;

	@FieldDescribe("计划ID")
	@Index(name = TABLE + "_xplanId")
	@Column(name = "xplanId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String planId;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作概述")
	@Column(name = "xworkContent", length = JpaObject.length_1M)
	private String workContent = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("计划工作内容概述")
	@Column(name = "xplanContent", length = JpaObject.length_1M)
	private String planContent = "";

	public String getReportId() {
		return reportId;
	}

	public String getPlanId() {
		return planId;
	}

	public String getPlanContent() {
		return planContent;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public void setPlanContent(String planContent) {
		this.planContent = planContent;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public String getWorkContent() {
		return workContent;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public void setWorkContent(String workContent) {
		this.workContent = workContent;
	}

	public String getWorkInfoId() {
		return workInfoId;
	}

	public String getKeyWorkId() {
		return keyWorkId;
	}

	public void setWorkInfoId(String workInfoId) {
		this.workInfoId = workInfoId;
	}

	public void setKeyWorkId(String keyWorkId) {
		this.keyWorkId = keyWorkId;
	}

}