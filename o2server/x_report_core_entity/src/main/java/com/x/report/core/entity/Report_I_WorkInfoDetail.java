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
@Table(name = PersistenceProperties.Report_I_WorkInfoDetail.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_I_WorkInfoDetail.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_I_WorkInfoDetail extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_I_WorkInfoDetail.table;

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
	@Index(name = TABLE + "_reportId")
	@Column(name = "xreportId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String reportId;

	@FieldDescribe("工作信息ID")
	@Index(name = TABLE + "_keyWorkId")
	@Column(name = "xkeyWorkId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String keyWorkId;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作内容说明")
	@Column(name = "xdescribe", length = JpaObject.length_1M)
	private String describe = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作完成情况总结")
	@Column(name = "xworkProgSummary", length = JpaObject.length_1M)
	private String workProgSummary = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("后续工作计划汇总")
	@Column(name = "xworkPlanSummary", length = JpaObject.length_1M)
	private String workPlanSummary = "";

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getKeyWorkId() {
		return keyWorkId;
	}

	public void setKeyWorkId(String keyWorkId) {
		this.keyWorkId = keyWorkId;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getWorkProgSummary() {
		return workProgSummary;
	}

	public String getWorkPlanSummary() {
		return workPlanSummary;
	}

	public void setWorkProgSummary(String workProgSummary) {
		this.workProgSummary = workProgSummary;
	}

	public void setWorkPlanSummary(String workPlanSummary) {
		this.workPlanSummary = workPlanSummary;
	}
}