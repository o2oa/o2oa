package com.x.report.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_I_Base.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_I_Base.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_I_Base extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_I_Base.table;

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
		if (this.targetPerson != null) {
			this.targetPerson_sequence = this.targetPerson.split("@")[0] + "_" + this.getSequence();
		}
		if (this.targetUnit != null) {
			this.targetUnit_sequence = this.targetUnit.split("@")[0] + "_" + this.getSequence();
		}
	}

	@FieldDescribe("汇报管理员（第一个接收者）")
	@Index(name = TABLE + "_xtargetPerson_sequence")
	@Column(name = "xtargetPerson_sequence", length = JpaObject.length_255B)
	private String targetPerson_sequence;

	@FieldDescribe("汇报部门序列号")
	@Index(name = TABLE + "_xtargetUnit_sequence")
	@Column(name = "xtargetUnit_sequence", length = JpaObject.length_255B)
	private String targetUnit_sequence;
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
	@CheckPersist(allowEmpty = false)
	private String profileId;

	@FieldDescribe("汇报标题")
	@Index(name = TABLE + "_xtitle")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String title;

	@FieldDescribe("汇报对象类别: PERSON | UNIT")
	@Index(name = TABLE + "_xreportObjType")
	@Column(name = "xreportObjType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String reportObjType;

	@FieldDescribe("汇报周期类别：MONTH|WEEK|DAILY")
	@Index(name = TABLE + "_xreportType")
	@Column(name = "xreportType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String reportType;

	@FieldDescribe("部门主管（人员唯一标识）")
	@Index(name = TABLE + "_xunitManager")
	@Column(name = "xunitManager", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String unitManager;

	@FieldDescribe("汇报年份")
	@Index(name = TABLE + "_xyear")
	@Column(name = "xyear", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String year;

	@FieldDescribe("汇报月份")
	@Index(name = TABLE + "_xmonth")
	@Column(name = "xmonth", length = JpaObject.length_16B)
	private String month;

	@FieldDescribe("汇报周数")
	@Column(name = "xweek", length = JpaObject.length_16B)
	private String week;

	@FieldDescribe("计划期数标识：年+月+周数+日期")
	@Column(name = "xflag", length = JpaObject.length_64B)
	private String flag;

	@FieldDescribe("汇报日期")
	@Column(name = "xreportDate")
	private Date reportDate;

	@FieldDescribe("日期字符串")
	@Index(name = TABLE + "_xreportDateString")
	@Column(name = "xreportDateString", length = JpaObject.length_32B)
	private String reportDateString;

	@FieldDescribe("汇报所属组织")
	@Index(name = TABLE + "_targetUnit")
	@Column(name = "xtargetUnit", length = JpaObject.length_255B)
	private String targetUnit = null;

	@FieldDescribe("当前审核环节")
	@Index(name = TABLE + "_xactivityName")
	@Column(name = "xactivityName", length = JpaObject.length_255B)
	private String activityName = null;

	@FieldDescribe("承载汇报的个人")
	@Index(name = TABLE + "_xtargetPerson")
	@Column(name = "xtargetPerson", length = JpaObject.length_255B)
	private String targetPerson = null;

	@FieldDescribe("承载汇报的个人身份")
	@Index(name = TABLE + "_xtargetIdentity")
	@Column(name = "xtargetIdentity", length = JpaObject.length_255B)
	private String targetIdentity = null;

	@FieldDescribe("需要工作汇报的业务人员标识")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE
			+ "_workreportPersonList", joinIndex = @Index(name = TABLE + "_workreportPersonList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xworkreportPersonList")
	@ElementIndex(name = TABLE + "_workreportPersonList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> workreportPersonList;

	@FieldDescribe("汇报流程ID")
	@Index(name = TABLE + "_xprocessId")
	@Column(name = "xprocessId", length = JpaObject.length_255B)
	private String processId = "";

	@FieldDescribe("汇报流程WorkID")
	@Index(name = TABLE + "_xwf_WorkId")
	@Column(name = "xwf_WorkId", length = JpaObject.length_255B)
	private String wf_WorkId = "";

	@FieldDescribe("汇报流程JobID")
	@Index(name = TABLE + "_xwf_JobId")
	@Column(name = "xwf_JobId", length = JpaObject.length_255B)
	private String wf_JobId = "";

	@FieldDescribe("汇报流程名称")
	@Column(name = "xworkflowName", length = JpaObject.length_255B)
	private String workflowName = "";

	@FieldDescribe("汇报信息状态：审核中|已完成")
	@Index(name = TABLE + "_reportStatus")
	@Column(name = "xreportStatus", length = JpaObject.length_32B)
	private String reportStatus = "审核中";

	@FieldDescribe("流程流转状态：待启动|流转中|已完成|错误")
	@Index(name = TABLE + "_xwfProcessStatus")
	@Column(name = "xwfProcessStatus", length = JpaObject.length_32B)
	private String wfProcessStatus = "待启动";

	@FieldDescribe("流程流转消息：如错误信息")
	@Column(name = "xwfProcessDescription", length = JpaObject.length_255B)
	private String wfProcessDescription = "";

	@FieldDescribe("日期字符串")
	@Index(name = TABLE + "_createDateString")
	@Column(name = "xcreateDateString", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String createDateString;

	@FieldDescribe("工作计划是否允许修改")
	@Index(name = TABLE + "_createDateString")
	@Column(name = "xworkPlanModifyable")
	@CheckPersist(allowEmpty = false)
	private Boolean workPlanModifyable = true;

	@FieldDescribe("上个月工作汇报ID")
	@Index(name = TABLE + "_xlastReportId")
	@Column(name = "xlastReportId")
	private String lastReportId = null;

	@FieldDescribe("最后提交时间")
	@Index(name = TABLE + "_xlastSubmitTime")
	@Column(name = "xlastSubmitTime")
	private Date lastSubmitTime = null;

	public String getUnitManager() {
		return unitManager;
	}

	public void setUnitManager(String unitManager) {
		this.unitManager = unitManager;
	}

	public Date getLastSubmitTime() {
		return lastSubmitTime;
	}

	public void setLastSubmitTime(Date lastSubmitTime) {
		this.lastSubmitTime = lastSubmitTime;
	}

	public String getTargetUnit() {
		return targetUnit;
	}

	public void setTargetUnit(String targetUnit) {
		this.targetUnit = targetUnit;
	}

	public String getTitle() {
		return title;
	}

	public String getYear() {
		return year;
	}

	public String getMonth() {
		return month;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getReportType() {
		return reportType;
	}

	public String getWeek() {
		return week;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getReportObjType() {
		return reportObjType;
	}

	public String getProcessId() {
		return processId;
	}

	public void setReportObjType(String reportObjType) {
		this.reportObjType = reportObjType;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getWf_WorkId() {
		return wf_WorkId;
	}

	public void setWf_WorkId(String wf_WorkId) {
		this.wf_WorkId = wf_WorkId;
	}

	public String getWfProcessStatus() {
		return wfProcessStatus;
	}

	public String getWfProcessDescription() {
		return wfProcessDescription;
	}

	public void setWfProcessStatus(String wfProcessStatus) {
		this.wfProcessStatus = wfProcessStatus;
	}

	public void setWfProcessDescription(String wfProcessDescription) {
		this.wfProcessDescription = wfProcessDescription;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public String getReportDateString() {
		return reportDateString;
	}

	public String getCreateDateString() {
		return createDateString;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public void setReportDateString(String reportDateString) {
		this.reportDateString = reportDateString;
	}

	public void setCreateDateString(String createDateString) {
		this.createDateString = createDateString;
	}

	public String getWf_JobId() {
		return wf_JobId;
	}

	public void setWf_JobId(String wf_JobId) {
		this.wf_JobId = wf_JobId;
	}

	public List<String> getWorkreportPersonList() {
		return workreportPersonList;
	}

	public void setWorkreportPersonList(List<String> workreportPersonList) {
		this.workreportPersonList = workreportPersonList;
	}

	public String getTargetPerson() {
		return targetPerson;
	}

	public void setTargetPerson(String targetPerson) {
		this.targetPerson = targetPerson;
	}

	public String getTargetIdentity() {
		return targetIdentity;
	}

	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}

	public String getTargetPerson_sequence() {
		return targetPerson_sequence;
	}

	public String getTargetUnit_sequence() {
		return targetUnit_sequence;
	}

	public void setTargetPerson_sequence(String targetPerson_sequence) {
		this.targetPerson_sequence = targetPerson_sequence;
	}

	public void setTargetUnit_sequence(String targetUnit_sequence) {
		this.targetUnit_sequence = targetUnit_sequence;
	}

	public Boolean getWorkPlanModifyable() {
		return workPlanModifyable;
	}

	public void setWorkPlanModifyable(Boolean workPlanModifyable) {
		this.workPlanModifyable = workPlanModifyable;
	}

	public String getLastReportId() {
		return lastReportId;
	}

	public void setLastReportId(String lastReportId) {
		this.lastReportId = lastReportId;
	}

}