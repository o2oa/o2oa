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

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_P_Profile.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_P_Profile.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_P_Profile extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_P_Profile.table;

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
	@FieldDescribe("汇报周期类别：MONTH|WEEK|DAILY")
	@Index(name = TABLE + "_reportType")
	@Column(name = "xreportType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String reportType;

	@FieldDescribe("汇报发起年份")
	@Index(name = TABLE + "_reportYear")
	@Column(name = "xreportYear", length = JpaObject.length_8B)
	@CheckPersist(allowEmpty = false)
	private String reportYear;

	@FieldDescribe("汇报发起月份")
	@Index(name = TABLE + "_reportMonth")
	@Column(name = "xreportMonth", length = JpaObject.length_8B)
	private String reportMonth;

	@FieldDescribe("汇报发起周数")
	@Index(name = TABLE + "_reportWeek")
	@Column(name = "xreportWeek", length = JpaObject.length_8B)
	private String reportWeek;

	@FieldDescribe("汇报日期字符串")
	@Index(name = TABLE + "_reportDateString")
	@Column(name = "xreportDateString", length = JpaObject.length_32B)
	private String reportDateString;

	@FieldDescribe("汇报日期")
	@Column(name = "xreportDate")
	private Date reportDate;

	@FieldDescribe("涉及的应用模块")
	@Column(name = "xmodules", length = JpaObject.length_64B)
	private String modules = "NONE";

	@FieldDescribe("涉及的应用模块数量")
	@Column(name = "xmoduleCount")
	private Integer moduleCount = 0;

	@FieldDescribe("参与工作汇报个人数量")
	@Column(name = "xworkPersonCount")
	private Integer workPersonCount = 0;

	@FieldDescribe("参与工作汇报组织数量")
	@Column(name = "xworkUnitCount")
	private Integer workUnitCount = 0;

	@FieldDescribe("生成汇报文档数量")
	@Column(name = "xcreateDocumentCount")
	private Integer createDocumentCount = 0;

	@FieldDescribe("发起汇报流程数量")
	@Column(name = "xstartWorkflowCount")
	private Integer startWorkflowCount = 0;

	@FieldDescribe("是否全部成功发起")
	@Index(name = TABLE + "_xcreateSuccess")
	@Column(name = "xcreateSuccess")
	private Boolean createSuccess = false;

	@FieldDescribe("创建日期字符串")
	@Index(name = TABLE + "_createDateString")
	@Column(name = "xcreateDateString", length = JpaObject.length_32B)
	private String createDateString;

	public String getReportYear() {
		return reportYear;
	}

	public String getReportMonth() {
		return reportMonth;
	}

	public String getReportWeek() {
		return reportWeek;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportYear(String reportYear) {
		this.reportYear = reportYear;
	}

	public void setReportMonth(String reportMonth) {
		this.reportMonth = reportMonth;
	}

	public void setReportWeek(String reportWeek) {
		this.reportWeek = reportWeek;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Integer getWorkPersonCount() {
		return workPersonCount;
	}

	public Integer getWorkUnitCount() {
		return workUnitCount;
	}

	public void setWorkPersonCount(Integer workPersonCount) {
		this.workPersonCount = workPersonCount;
	}

	public void setWorkUnitCount(Integer workUnitCount) {
		this.workUnitCount = workUnitCount;
	}

	public String getReportDateString() {
		return reportDateString;
	}

	public void setReportDateString(String reportDateString) {
		this.reportDateString = reportDateString;
	}

	public String getModules() {
		return modules;
	}

	public Integer getModuleCount() {
		return moduleCount;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}

	public void setModuleCount(Integer moduleCount) {
		this.moduleCount = moduleCount;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public Boolean getCreateSuccess() {
		return createSuccess;
	}

	public void setCreateSuccess(Boolean createSuccess) {
		this.createSuccess = createSuccess;
	}

	public Integer getCreateDocumentCount() {
		return createDocumentCount;
	}

	public Integer getStartWorkflowCount() {
		return startWorkflowCount;
	}

	public void setCreateDocumentCount(Integer createDocumentCount) {
		this.createDocumentCount = createDocumentCount;
	}

	public void setStartWorkflowCount(Integer startWorkflowCount) {
		this.startWorkflowCount = startWorkflowCount;
	}

	public String getCreateDateString() {
		return createDateString;
	}

	public void setCreateDateString(String createDateString) {
		this.createDateString = createDateString;
	}

}