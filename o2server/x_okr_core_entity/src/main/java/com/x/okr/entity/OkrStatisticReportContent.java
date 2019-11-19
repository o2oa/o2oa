package com.x.okr.entity;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 中心工作汇报情况统计信息实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrStatisticReportContent.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrStatisticReportContent.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrStatisticReportContent extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrStatisticReportContent.table;

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
	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerId = "";

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerTitle = "";

	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	public static final String parentId_FIELDNAME = "parentId";
	@FieldDescribe("上级工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentId = null;

	public static final String workTitle_FIELDNAME = "workTitle";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workTitle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String workTitle = "";

	public static final String reportId_FIELDNAME = "reportId";
	@FieldDescribe("工作汇报ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + reportId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportId = "";

	public static final String workType_FIELDNAME = "workType";
	@FieldDescribe("工作类别")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + workType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	public static final String workLevel_FIELDNAME = "workLevel";
	@FieldDescribe("工作级别")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + workLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workLevel = "";

	public static final String cycleType_FIELDNAME = "cycleType";
	@FieldDescribe("统计周期：每周统计|每月统计")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + cycleType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cycleType = "";

	public static final String statisticTime_FIELDNAME = "statisticTime";
	@FieldDescribe("统计时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + statisticTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date statisticTime = null;

	public static final String statisticTimeFlag_FIELDNAME = "statisticTimeFlag";
	@FieldDescribe("统计时间标识.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + statisticTimeFlag_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String statisticTimeFlag = null;

	public static final String statisticYear_FIELDNAME = "statisticYear";
	@FieldDescribe("统计年份")
	@Column(name = ColumnNamePrefix + statisticYear_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer statisticYear = null;

	public static final String statisticMonth_FIELDNAME = "statisticMonth";
	@FieldDescribe("统计月份")
	@Column(name = ColumnNamePrefix + statisticMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer statisticMonth = null;

	public static final String statisticWeek_FIELDNAME = "statisticWeek";
	@FieldDescribe("统计周数")
	@Column(name = ColumnNamePrefix + statisticWeek_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer statisticWeek = null;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	public static final String responsibilityEmployeeName_FIELDNAME = "responsibilityEmployeeName";
	@FieldDescribe("主责人姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ responsibilityEmployeeName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String responsibilityEmployeeName = "";

	public static final String responsibilityIdentity_FIELDNAME = "responsibilityIdentity";
	@FieldDescribe("主责人身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ responsibilityIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String responsibilityIdentity = "";

	public static final String responsibilityUnitName_FIELDNAME = "responsibilityUnitName";
	@FieldDescribe("主责人所属组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ responsibilityUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String responsibilityUnitName = "";

	public static final String responsibilityTopUnitName_FIELDNAME = "responsibilityTopUnitName";
	@FieldDescribe("主责人所属顶层组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ responsibilityTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String responsibilityTopUnitName = "";

	public static final String isCompleted_FIELDNAME = "isCompleted";
	@FieldDescribe("工作是否已完成")
	@Column(name = ColumnNamePrefix + isCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isCompleted = false;

	public static final String isOverTime_FIELDNAME = "isOverTime";
	@FieldDescribe("工作是否已超期")
	@Column(name = ColumnNamePrefix + isOverTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isOverTime = false;

	public static final String workProcessStatus_FIELDNAME = "workProcessStatus";
	@FieldDescribe("工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workProcessStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workProcessStatus = "草稿";

	public static final String reportStatus_FIELDNAME = "reportStatus";
	@FieldDescribe("工作汇报状态")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + reportStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportStatus = "未提交汇报";

	public static final String reportDayInCycle_FIELDNAME = "reportDayInCycle";
	@FieldDescribe("周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00")
	@Column(name = ColumnNamePrefix + reportDayInCycle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer reportDayInCycle = 0;

	public static final String progressDescription_FIELDNAME = "progressDescription";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("截止当前完成情况")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + progressDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String progressDescription = "";

	public static final String workPlan_FIELDNAME = "workPlan";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("后续工作计划")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + workPlan_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workPlan = "";

	public static final String memo_FIELDNAME = "memo";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作汇报备注信息")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + memo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memo = "";

	public static final String workPointAndRequirements_FIELDNAME = "workPointAndRequirements";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作要点及需求")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + workPointAndRequirements_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workPointAndRequirements = "";

	public static final String adminSuperviseInfo_FIELDNAME = "adminSuperviseInfo";
	@Lob
	@FieldDescribe("管理员督办信息")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + adminSuperviseInfo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String adminSuperviseInfo = "";

	public static final String opinion_FIELDNAME = "opinion";
	@Lob
	@FieldDescribe("领导处理意见")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + opinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String opinion = "";

	public String getCenterId() {
		return centerId;
	}

	public String getCenterTitle() {
		return centerTitle;
	}

	public String getWorkId() {
		return workId;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public String getReportId() {
		return reportId;
	}

	public String getWorkType() {
		return workType;
	}

	public String getWorkLevel() {
		return workLevel;
	}

	public String getCycleType() {
		return cycleType;
	}

	public Date getStatisticTime() {
		return statisticTime;
	}

	public String getStatus() {
		return status;
	}

	public String getResponsibilityEmployeeName() {
		return responsibilityEmployeeName;
	}

	public String getResponsibilityIdentity() {
		return responsibilityIdentity;
	}

	public String getResponsibilityUnitName() {
		return responsibilityUnitName;
	}

	public String getResponsibilityTopUnitName() {
		return responsibilityTopUnitName;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public Boolean getIsOverTime() {
		return isOverTime;
	}

	public String getWorkProcessStatus() {
		return workProcessStatus;
	}

	public Integer getReportDayInCycle() {
		return reportDayInCycle;
	}

	public String getProgressDescription() {
		return progressDescription;
	}

	public String getWorkPlan() {
		return workPlan;
	}

	public String getMemo() {
		return memo;
	}

	public String getWorkPointAndRequirements() {
		return workPointAndRequirements;
	}

	public String getAdminSuperviseInfo() {
		return adminSuperviseInfo;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
	}

	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}

	public void setStatisticTime(Date statisticTime) {
		this.statisticTime = statisticTime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setResponsibilityEmployeeName(String responsibilityEmployeeName) {
		this.responsibilityEmployeeName = responsibilityEmployeeName;
	}

	public void setResponsibilityIdentity(String responsibilityIdentity) {
		this.responsibilityIdentity = responsibilityIdentity;
	}

	public void setResponsibilityUnitName(String responsibilityUnitName) {
		this.responsibilityUnitName = responsibilityUnitName;
	}

	public void setResponsibilityTopUnitName(String responsibilityTopUnitName) {
		this.responsibilityTopUnitName = responsibilityTopUnitName;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public void setIsOverTime(Boolean isOverTime) {
		this.isOverTime = isOverTime;
	}

	public void setWorkProcessStatus(String workProcessStatus) {
		this.workProcessStatus = workProcessStatus;
	}

	public void setReportDayInCycle(Integer reportDayInCycle) {
		this.reportDayInCycle = reportDayInCycle;
	}

	public void setProgressDescription(String progressDescription) {
		this.progressDescription = progressDescription;
	}

	public void setWorkPlan(String workPlan) {
		this.workPlan = workPlan;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setWorkPointAndRequirements(String workPointAndRequirements) {
		this.workPointAndRequirements = workPointAndRequirements;
	}

	public void setAdminSuperviseInfo(String adminSuperviseInfo) {
		this.adminSuperviseInfo = adminSuperviseInfo;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public Integer getStatisticYear() {
		return statisticYear;
	}

	public Integer getStatisticMonth() {
		return statisticMonth;
	}

	public Integer getStatisticWeek() {
		return statisticWeek;
	}

	public void setStatisticYear(Integer statisticYear) {
		this.statisticYear = statisticYear;
	}

	public void setStatisticMonth(Integer statisticMonth) {
		this.statisticMonth = statisticMonth;
	}

	public void setStatisticWeek(Integer statisticWeek) {
		this.statisticWeek = statisticWeek;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getStatisticTimeFlag() {
		return statisticTimeFlag;
	}

	public void setStatisticTimeFlag(String statisticTimeFlag) {
		this.statisticTimeFlag = statisticTimeFlag;
	}

}