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
@Table(name = PersistenceProperties.OkrStatisticReportStatus.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrStatisticReportStatus.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrStatisticReportStatus extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrStatisticReportStatus.table;

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

	@FieldDescribe("工作ID")
	@Column(name = "xworkId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	@FieldDescribe("工作标题")
	@Column(name = "xworkTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String workTitle = "";

	@FieldDescribe("统计年份")
	@Column(name = "xstatisticYear")
	@CheckPersist(allowEmpty = true)
	private Integer statisticYear = 2016;

	@FieldDescribe("统计时间标识.")
	@Column(name = "xstatisticTimeFlag", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String statisticTimeFlag = null;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("中心工作汇报统计内容")
	@Column(name = "xreportStatistic", length = JpaObject.length_10M)
	@CheckPersist(allowEmpty = true)
	private String reportStatistic = "";

	@FieldDescribe("中心工作ID")
	@Column(name = "xcenterId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String centerId = "";

	@FieldDescribe("中心标题")
	@Column(name = "xcenterTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String centerTitle = "";

	@FieldDescribe("上级工作ID")
	@Column(name = "xparentId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String parentId = null;

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

	@FieldDescribe("工作类别")
	@Column(name = "xworkType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	@FieldDescribe("工作级别")
	@Column(name = "xworkLevel", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String workLevel = "";

	@FieldDescribe("工作进度")
	@Column(name = "xoverallProgress")
	private Integer overallProgress = 0;

	@FieldDescribe("汇报周期:不需要汇报|每月汇报|每周汇报")
	@Column(name = "xreportCycle", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String reportCycle = null;

	@FieldDescribe("周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00")
	@Column(name = "xreportDayInCycle")
	@CheckPersist(allowEmpty = true)
	private Integer reportDayInCycle = 0;

	@FieldDescribe("工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）")
	@Column(name = "xworkDateTimeType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String workDateTimeType = "长期工作";

	@FieldDescribe("工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消")
	@Column(name = "xworkProcessStatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String workProcessStatus = "草稿";

	@FieldDescribe("工作处理状态：正常|已归档")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	@FieldDescribe("工作部署日期-字符串，显示用：yyyy-mm-dd")
	@Column(name = "xdeployDateStr", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String deployDateStr = "";

	@FieldDescribe("工作完成日期-字符串，显示用：yyyy-mm-dd")
	@Column(name = "xcompleteDateLimitStr", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String completeDateLimitStr = "";

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getCenterTitle() {
		return centerTitle;
	}

	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getResponsibilityEmployeeName() {
		return responsibilityEmployeeName;
	}

	public void setResponsibilityEmployeeName(String responsibilityEmployeeName) {
		this.responsibilityEmployeeName = responsibilityEmployeeName;
	}

	public String getResponsibilityIdentity() {
		return responsibilityIdentity;
	}

	public void setResponsibilityIdentity(String responsibilityIdentity) {
		this.responsibilityIdentity = responsibilityIdentity;
	}

	public String getResponsibilityUnitName() {
		return responsibilityUnitName;
	}

	public void setResponsibilityUnitName(String responsibilityUnitName) {
		this.responsibilityUnitName = responsibilityUnitName;
	}

	public String getResponsibilityTopUnitName() {
		return responsibilityTopUnitName;
	}

	public void setResponsibilityTopUnitName(String responsibilityTopUnitName) {
		this.responsibilityTopUnitName = responsibilityTopUnitName;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getWorkLevel() {
		return workLevel;
	}

	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
	}

	public Integer getOverallProgress() {
		return overallProgress;
	}

	public void setOverallProgress(Integer overallProgress) {
		this.overallProgress = overallProgress;
	}

	public String getWorkProcessStatus() {
		return workProcessStatus;
	}

	public void setWorkProcessStatus(String workProcessStatus) {
		this.workProcessStatus = workProcessStatus;
	}

	public String getReportStatistic() {
		return reportStatistic;
	}

	public void setReportStatistic(String reportStatistic) {
		this.reportStatistic = reportStatistic;
	}

	public String getReportCycle() {
		return reportCycle;
	}

	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}

	public Integer getReportDayInCycle() {
		return reportDayInCycle;
	}

	public void setReportDayInCycle(Integer reportDayInCycle) {
		this.reportDayInCycle = reportDayInCycle;
	}

	public String getWorkDateTimeType() {
		return workDateTimeType;
	}

	public void setWorkDateTimeType(String workDateTimeType) {
		this.workDateTimeType = workDateTimeType;
	}

	public Integer getStatisticYear() {
		return statisticYear;
	}

	public void setStatisticYear(Integer statisticYear) {
		this.statisticYear = statisticYear;
	}

	public String getDeployDateStr() {
		return deployDateStr;
	}

	public void setDeployDateStr(String deployDateStr) {
		this.deployDateStr = deployDateStr;
	}

	public String getCompleteDateLimitStr() {
		return completeDateLimitStr;
	}

	public void setCompleteDateLimitStr(String completeDateLimitStr) {
		this.completeDateLimitStr = completeDateLimitStr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getStatisticTimeFlag() {
		return statisticTimeFlag;
	}

	public void setStatisticTimeFlag(String statisticTimeFlag) {
		this.statisticTimeFlag = statisticTimeFlag;
	}

}