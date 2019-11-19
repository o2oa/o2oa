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

	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	public static final String workTitle_FIELDNAME = "workTitle";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workTitle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String workTitle = "";

	public static final String statisticYear_FIELDNAME = "statisticYear";
	@FieldDescribe("统计年份")
	@Column(name = ColumnNamePrefix + statisticYear_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + statisticYear_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer statisticYear = 2018;

	public static final String statisticTimeFlag_FIELDNAME = "statisticTimeFlag";
	@FieldDescribe("统计时间标识.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + statisticTimeFlag_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + statisticTimeFlag_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String statisticTimeFlag = null;

	public static final String reportStatistic_FIELDNAME = "reportStatistic";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("中心工作汇报统计内容")
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + reportStatistic_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportStatistic = "";

	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerId = "";

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerTitle = "";

	public static final String parentId_FIELDNAME = "parentId";
	@FieldDescribe("上级工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentId = null;

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

	public static final String overallProgress_FIELDNAME = "overallProgress";
	@FieldDescribe("工作进度")
	@Column(name = ColumnNamePrefix + overallProgress_FIELDNAME)
	private Integer overallProgress = 0;

	public static final String reportCycle_FIELDNAME = "reportCycle";
	@FieldDescribe("汇报周期:不需要汇报|每月汇报|每周汇报")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + reportCycle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportCycle = null;

	public static final String reportDayInCycle_FIELDNAME = "reportDayInCycle";
	@FieldDescribe("周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00")
	@Column(name = ColumnNamePrefix + reportDayInCycle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer reportDayInCycle = 0;

	public static final String workDateTimeType_FIELDNAME = "workDateTimeType";
	@FieldDescribe("工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workDateTimeType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workDateTimeType = "长期工作";

	public static final String workProcessStatus_FIELDNAME = "workProcessStatus";
	@FieldDescribe("工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workProcessStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workProcessStatus = "草稿";

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("工作处理状态：正常|已归档")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	public static final String deployDateStr_FIELDNAME = "deployDateStr";
	@FieldDescribe("工作部署日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + deployDateStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployDateStr = "";

	public static final String completeDateLimitStr_FIELDNAME = "completeDateLimitStr";
	@FieldDescribe("工作完成日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + completeDateLimitStr_FIELDNAME)
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