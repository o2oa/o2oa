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
import javax.persistence.UniqueConstraint;

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
	@FieldDescribe("中心工作ID")
	@Column(name = "xcenterId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String centerId = "";

	@FieldDescribe("中心标题")
	@Column(name = "xcenterTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String centerTitle = "";

	@FieldDescribe("工作ID")
	@Column(name = "xworkId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	@FieldDescribe("上级工作ID")
	@Column(name = "xparentId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String parentId = null;

	@FieldDescribe("工作标题")
	@Column(name = "xworkTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String workTitle = "";

	@FieldDescribe("工作汇报ID")
	@Column(name = "xreportId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String reportId = "";

	@FieldDescribe("工作类别")
	@Column(name = "xworkType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	@FieldDescribe("工作级别")
	@Column(name = "xworkLevel", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String workLevel = "";

	@FieldDescribe("统计周期：每周统计|每月统计")
	@Column(name = "xcycleType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String cycleType = "";

	@FieldDescribe("统计时间.")
	@Column(name = "xstatisticTime")
	@CheckPersist(allowEmpty = true)
	private Date statisticTime = null;

	@FieldDescribe("统计时间标识.")
	@Column(name = "xstatisticTimeFlag", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String statisticTimeFlag = null;

	@FieldDescribe("统计年份")
	@Column(name = "xstatisticYear")
	@CheckPersist(allowEmpty = true)
	private Integer statisticYear = null;

	@FieldDescribe("统计月份")
	@Column(name = "xstatisticMonth")
	@CheckPersist(allowEmpty = true)
	private Integer statisticMonth = null;

	@FieldDescribe("统计周数")
	@Column(name = "xstatisticWeek")
	@CheckPersist(allowEmpty = true)
	private Integer statisticWeek = null;

	@FieldDescribe("处理状态：正常|已删除")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	@FieldDescribe("主责人姓名")
	@Column(name = "xresponsibilityEmployeeName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String responsibilityEmployeeName = "";

	@FieldDescribe("主责人身份")
	@Column(name = "xresponsibilityIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String responsibilityIdentity = "";

	@FieldDescribe("主责人所属组织名称")
	@Column(name = "xresponsibilityUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String responsibilityUnitName = "";

	@FieldDescribe("主责人所属顶层组织名称")
	@Column(name = "xresponsibilityTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String responsibilityTopUnitName = "";

	@FieldDescribe("工作是否已完成")
	@Column(name = "xisCompleted")
	@CheckPersist(allowEmpty = true)
	private Boolean isCompleted = false;

	@FieldDescribe("工作是否已超期")
	@Column(name = "xisOverTime")
	@CheckPersist(allowEmpty = true)
	private Boolean isOverTime = false;

	@FieldDescribe("工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消")
	@Column(name = "xworkProcessStatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String workProcessStatus = "草稿";

	@FieldDescribe("工作汇报状态")
	@Column(name = "xreportStatus", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String reportStatus = "未提交汇报";

	@FieldDescribe("周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00")
	@Column(name = "xreportDayInCycle")
	@CheckPersist(allowEmpty = true)
	private Integer reportDayInCycle = 0;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("截止当前完成情况")
	@Column(name = "xprogressDescription", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String progressDescription = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("后续工作计划")
	@Column(name = "xworkPlan", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String workPlan = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作汇报备注信息")
	@Column(name = "xmemo", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String memo = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作要点及需求")
	@Column(name = "xworkPointAndRequirements", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String workPointAndRequirements = "";

	@Lob
	@FieldDescribe("管理员督办信息")
	@Column(name = "xadminSuperviseInfo", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String adminSuperviseInfo = "";

	@Lob
	@FieldDescribe("领导处理意见")
	@Column(name = "xopinion", length = JpaObject.length_2K)
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