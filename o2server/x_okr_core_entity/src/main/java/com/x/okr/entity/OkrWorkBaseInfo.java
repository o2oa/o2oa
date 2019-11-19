package com.x.okr.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

/**
 * 工作基础信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkBaseInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkBaseInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkBaseInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkBaseInfo.table;

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
	public static final String title_FIELDNAME = "title";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = "";

	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerId = "";

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerTitle = "";

	public static final String parentWorkId_FIELDNAME = "parentWorkId";
	@FieldDescribe("上级工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentWorkId_FIELDNAME)
	@Index(name = TABLE + "_parentWorkId")
	@CheckPersist(allowEmpty = true)
	private String parentWorkId = "";

	public static final String parentWorkTitle_FIELDNAME = "parentWorkTitle";
	@FieldDescribe("上级工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + parentWorkTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentWorkTitle = "";

	public static final String workDateTimeType_FIELDNAME = "workDateTimeType";
	@FieldDescribe("工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workDateTimeType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workDateTimeType = "长期工作";

	public static final String deployYear_FIELDNAME = "deployYear";
	@FieldDescribe("工作部署年份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + deployYear_FIELDNAME)
	@Index(name = TABLE + "_deployYear")
	@CheckPersist(allowEmpty = true)
	private String deployYear = "";

	public static final String deployMonth_FIELDNAME = "deployMonth";
	@FieldDescribe("工作部署月份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + deployMonth_FIELDNAME)
	@Index(name = TABLE + "_deployMonth")
	@CheckPersist(allowEmpty = true)
	private String deployMonth = "";

	public static final String deployerName_FIELDNAME = "deployerName";
	@FieldDescribe("部署者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ deployerName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployerName = "";

	public static final String deployerIdentity_FIELDNAME = "deployerIdentity";
	@FieldDescribe("部署者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ deployerIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployerIdentity = "";

	public static final String deployerUnitName_FIELDNAME = "deployerUnitName";
	@FieldDescribe("部署者所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ deployerUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployerUnitName = "";

	public static final String deployerTopUnitName_FIELDNAME = "deployerTopUnitName";
	@FieldDescribe("部署者所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ deployerTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployerTopUnitName = "";

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("创建者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorName = "";

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity = "";

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("创建者所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName = "";

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("创建者所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName = "";

	public static final String deployDateStr_FIELDNAME = "deployDateStr";
	@FieldDescribe("工作部署日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + deployDateStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployDateStr = "";

	public static final String confirmDateStr_FIELDNAME = "confirmDateStr";
	@FieldDescribe("工作确认日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + confirmDateStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String confirmDateStr = "";

	public static final String completeDateLimit_FIELDNAME = "completeDateLimit";

	@FieldDescribe("工作完成日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + completeDateLimit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date completeDateLimit = null;

	public static final String archiveDate_FIELDNAME = "archiveDate";
	@FieldDescribe("工作归档时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + archiveDate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date archiveDate = null;

	public static final String completeDateLimitStr_FIELDNAME = "completeDateLimitStr";
	@FieldDescribe("工作完成日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + completeDateLimitStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String completeDateLimitStr = "";

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
	@FieldDescribe("主责人所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ responsibilityUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String responsibilityUnitName = "";

	public static final String responsibilityTopUnitName_FIELDNAME = "responsibilityTopUnitName";
	@FieldDescribe("主责人所属顶层组织")
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

	public static final String workProcessStatus_FIELDNAME = "workProcessStatus";
	@FieldDescribe("工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workProcessStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workProcessStatus = "草稿";

	public static final String isOverTime_FIELDNAME = "isOverTime";
	@FieldDescribe("工作是否已超期")
	@Column(name = ColumnNamePrefix + isOverTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isOverTime = false;

	public static final String isCompleted_FIELDNAME = "isCompleted";
	@FieldDescribe("工作是否已完成")
	@Column(name = ColumnNamePrefix + isCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isCompleted = false;

	public static final String completeTime_FIELDNAME = "completeTime";
	@FieldDescribe("工作完成时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + completeTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date completeTime = null;

	public static final String lastReportTime_FIELDNAME = "lastReportTime";
	@FieldDescribe("上一次汇报时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + lastReportTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date lastReportTime = null;

	public static final String nextReportTime_FIELDNAME = "nextReportTime";
	@FieldDescribe("下一次汇报时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + nextReportTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date nextReportTime = null;

	public static final String reportCount_FIELDNAME = "reportCount";
	@FieldDescribe("已汇报次数")
	@Column(name = ColumnNamePrefix + reportCount_FIELDNAME)
	private Integer reportCount = 0;

	public static final String reportTimeQue_FIELDNAME = "reportTimeQue";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("汇报时间队列")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + reportTimeQue_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportTimeQue = null;

	public static final String reportCycle_FIELDNAME = "reportCycle";
	@FieldDescribe("汇报周期:不需要汇报|每月汇报|每周汇报")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + reportCycle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportCycle = null;

	public static final String isNeedReport_FIELDNAME = "isNeedReport";
	@FieldDescribe("是否需要定期汇报")
	@Column(name = ColumnNamePrefix + isNeedReport_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isNeedReport = true;

	public static final String reportDayInCycle_FIELDNAME = "reportDayInCycle";
	@FieldDescribe("周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00")
	@Column(name = ColumnNamePrefix + reportDayInCycle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer reportDayInCycle = 0;

	public static final String workAuditLevel_FIELDNAME = "workAuditLevel";
	@FieldDescribe("工作部署级别")
	@Column(name = ColumnNamePrefix + workAuditLevel_FIELDNAME)
	private Integer workAuditLevel = 1;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	public static final String reportNeedAdminAudit_FIELDNAME = "reportNeedAdminAudit";
	@FieldDescribe("工作汇报是否需要管理补充信息")
	@Column(name = ColumnNamePrefix + reportNeedAdminAudit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean reportNeedAdminAudit = false;

	public static final String reportAdminName_FIELDNAME = "reportAdminName";
	@FieldDescribe("工作管理员姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reportAdminName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportAdminName = null;

	public static final String reportAdminIdentity_FIELDNAME = "reportAdminIdentity";
	@FieldDescribe("工作管理员身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reportAdminIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportAdminIdentity = null;

	public static final String shortWorkDetail_FIELDNAME = "shortWorkDetail";
	@FieldDescribe("工作详细描述, 事项分解")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shortWorkDetail_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String shortWorkDetail = "";

	public static final String shortDutyDescription_FIELDNAME = "shortDutyDescription";
	@FieldDescribe("职责描述")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shortDutyDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String shortDutyDescription = "";

	public static final String shortProgressAction_FIELDNAME = "shortProgressAction";
	@FieldDescribe("具体行动举措")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shortProgressAction_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String shortProgressAction = "";

	public static final String shortLandmarkDescription_FIELDNAME = "shortLandmarkDescription";
	@FieldDescribe("里程碑标志说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shortLandmarkDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String shortLandmarkDescription = "";

	public static final String shortResultDescription_FIELDNAME = "shortResultDescription";
	@FieldDescribe("交付成果说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shortResultDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String shortResultDescription = "";

	public static final String shortMajorIssuesDescription_FIELDNAME = "shortMajorIssuesDescription";
	@FieldDescribe("重点事项说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shortMajorIssuesDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String shortMajorIssuesDescription = "";

	public static final String shortProgressPlan_FIELDNAME = "shortProgressPlan";
	@FieldDescribe("进展计划时限说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shortProgressPlan_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String shortProgressPlan = "";

	public static final String progressAnalyseTime_FIELDNAME = "progressAnalyseTime";
	@FieldDescribe("工作进展分析时间")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + progressAnalyseTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String progressAnalyseTime = "";

	public static final String currentAppraiseTitle_FIELDNAME = "currentAppraiseTitle";
	@FieldDescribe("当前考核流程标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + currentAppraiseTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String currentAppraiseTitle = "无标题";

	public static final String currentAppraiseInfoId_FIELDNAME = "currentAppraiseInfoId";
	@FieldDescribe("当前OKR考核流程信息ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + currentAppraiseInfoId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String currentAppraiseInfoId = "";

	public static final String currentAppraiseWorkId_FIELDNAME = "currentAppraiseWorkId";
	@FieldDescribe("当前考核流程WorkID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + currentAppraiseWorkId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String currentAppraiseWorkId = "";

	public static final String currentAppraiseJobId_FIELDNAME = "currentAppraiseJobId";
	@FieldDescribe("当前考核流程JobID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + currentAppraiseJobId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String currentAppraiseJobId = "";

	public static final String currentActivityName_FIELDNAME = "currentActivityName";
	@FieldDescribe("当前考核流程环节名称")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + currentActivityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String currentActivityName = "";

	public static final String currentAppraiseStatus_FIELDNAME = "currentAppraiseStatus";
	@FieldDescribe("当前考核审核状态")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + currentAppraiseStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String currentAppraiseStatus = "";

	public static final String appraiseTimes_FIELDNAME = "appraiseTimes";
	@FieldDescribe("考核次数")
	@Column(name = ColumnNamePrefix + appraiseTimes_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer appraiseTimes = 0;

	public static final String cooperateEmployeeNameList_FIELDNAME = "cooperateEmployeeNameList";
	@FieldDescribe("协助人姓名，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ cooperateEmployeeNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ cooperateEmployeeNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ cooperateEmployeeNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + cooperateEmployeeNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> cooperateEmployeeNameList;

	public static final String cooperateIdentityList_FIELDNAME = "cooperateIdentityList";
	@FieldDescribe("协助人身份，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ cooperateIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ cooperateIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ cooperateIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + cooperateIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> cooperateIdentityList;

	public static final String cooperateUnitNameList_FIELDNAME = "cooperateUnitNameList";
	@FieldDescribe("协助人所属组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ cooperateUnitNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ cooperateUnitNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ cooperateUnitNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + cooperateUnitNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> cooperateUnitNameList;

	public static final String cooperateTopUnitNameList_FIELDNAME = "cooperateTopUnitNameList";
	@FieldDescribe("协助人所属顶层组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ cooperateTopUnitNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ cooperateTopUnitNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ cooperateTopUnitNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + cooperateTopUnitNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> cooperateTopUnitNameList;

	public static final String readLeaderIdentityList_FIELDNAME = "readLeaderIdentityList";
	@FieldDescribe("阅知领导身份，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readLeaderIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ readLeaderIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readLeaderIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readLeaderIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readLeaderIdentityList;

	public static final String readLeaderNameList_FIELDNAME = "readLeaderNameList";
	@FieldDescribe("阅知领导，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readLeaderNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ readLeaderNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readLeaderNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readLeaderNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readLeaderNameList;

	public static final String readLeaderUnitNameList_FIELDNAME = "readLeaderUnitNameList";
	@FieldDescribe("阅知领导所属组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readLeaderUnitNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ readLeaderUnitNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readLeaderUnitNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readLeaderUnitNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readLeaderUnitNameList;

	public static final String readLeaderTopUnitNameList_FIELDNAME = "readLeaderTopUnitNameList";
	@FieldDescribe("阅知领导所属顶层组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readLeaderTopUnitNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ readLeaderTopUnitNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readLeaderTopUnitNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readLeaderTopUnitNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readLeaderTopUnitNameList;

	public static final String attachmentList_FIELDNAME = "attachmentList";
	@FieldDescribe("附件列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + attachmentList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + attachmentList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ attachmentList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + attachmentList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;

	public static final String appraiseInfoList_FIELDNAME = "appraiseInfoList";
	@FieldDescribe("考核流程信息ID列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ appraiseInfoList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + appraiseInfoList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ appraiseInfoList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + appraiseInfoList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> appraiseInfoList;

	/**
	 * 获取工作标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置工作标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取所属中心工作ID
	 * 
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * 设置所属中心工作ID
	 * 
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * 获取中心工作标题
	 * 
	 * @return
	 */
	public String getCenterTitle() {
		return centerTitle;
	}

	/**
	 * 设置中心工作标题
	 * 
	 * @param centerTitle
	 */
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}

	/**
	 * 获取上级工作ID
	 * 
	 * @return
	 */
	public String getParentWorkId() {
		return parentWorkId;
	}

	/**
	 * 设置上级工作ID
	 * 
	 * @param parentWorkId
	 */
	public void setParentWorkId(String parentWorkId) {
		this.parentWorkId = parentWorkId;
	}

	/**
	 * 获取上级工作标题
	 * 
	 * @return
	 */
	public String getParentWorkTitle() {
		return parentWorkTitle;
	}

	/**
	 * 设置上级工作标题
	 * 
	 * @param parentWorkTitle
	 */
	public void setParentWorkTitle(String parentWorkTitle) {
		this.parentWorkTitle = parentWorkTitle;
	}

	/**
	 * 获取部署者姓名
	 * 
	 * @return
	 */
	public String getDeployerName() {
		return deployerName;
	}

	/**
	 * 设置部署者姓名
	 * 
	 * @param deployerName
	 */
	public void setDeployerName(String deployerName) {
		this.deployerName = deployerName;
	}

	/**
	 * 获取部署者所属组织名称
	 * 
	 * @return
	 */
	public String getDeployerUnitName() {
		return deployerUnitName;
	}

	/**
	 * 设置部署者所属组织名称
	 * 
	 * @param deployerUnitName
	 */
	public void setDeployerUnitName(String deployerUnitName) {
		this.deployerUnitName = deployerUnitName;
	}

	/**
	 * 获取部署者所属顶层组织名称
	 * 
	 * @return
	 */
	public String getDeployerTopUnitName() {
		return deployerTopUnitName;
	}

	/**
	 * 设置部署者所属顶层组织名称
	 * 
	 * @param deployerTopUnitName
	 */
	public void setDeployerTopUnitName(String deployerTopUnitName) {
		this.deployerTopUnitName = deployerTopUnitName;
	}

	/**
	 * 获取创建者姓名
	 * 
	 * @return
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * 设置创建者姓名
	 * 
	 * @param creatorName
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * 获取创建者所属组织名称
	 * 
	 * @return
	 */
	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	/**
	 * 设置创建者所属组织名称
	 * 
	 * @param creatorUnitName
	 */
	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	/**
	 * 获取创建者所属顶层组织名称
	 * 
	 * @return
	 */
	public String getCreatorTopUnitName() {
		return creatorTopUnitName;
	}

	/**
	 * 设置创建者所属顶层组织名称
	 * 
	 * @param creatorTopUnitName
	 */
	public void setCreatorTopUnitName(String creatorTopUnitName) {
		this.creatorTopUnitName = creatorTopUnitName;
	}

	/**
	 * 获取部署日期
	 * 
	 * @return
	 */
	public String getDeployDateStr() {
		return deployDateStr;
	}

	/**
	 * 设置部署日期
	 * 
	 * @param deployDateStr
	 */
	public void setDeployDateStr(String deployDateStr) {
		this.deployDateStr = deployDateStr;
	}

	/**
	 * 获取工作确认日期
	 * 
	 * @return
	 */
	public String getConfirmDateStr() {
		return confirmDateStr;
	}

	/**
	 * 设置工作确认日期
	 * 
	 * @param confirmDateStr
	 */
	public void setConfirmDateStr(String confirmDateStr) {
		this.confirmDateStr = confirmDateStr;
	}

	/**
	 * 获取工作完成时限
	 * 
	 * @return
	 */
	public Date getCompleteDateLimit() {
		return completeDateLimit;
	}

	/**
	 * 设置工作完成时限
	 * 
	 * @param completeDateLimit
	 */
	public void setCompleteDateLimit(Date completeDateLimit) {
		this.completeDateLimit = completeDateLimit;
	}

	/**
	 * 获取工作完成时限（字符串）
	 * 
	 * @return
	 */
	public String getCompleteDateLimitStr() {
		return completeDateLimitStr;
	}

	/**
	 * 设置工作完成时限（字符串）
	 * 
	 * @param completeDateLimitStr
	 */
	public void setCompleteDateLimitStr(String completeDateLimitStr) {
		this.completeDateLimitStr = completeDateLimitStr;
	}

	/**
	 * 获取主责人姓名
	 * 
	 * @return
	 */
	public String getResponsibilityEmployeeName() {
		return responsibilityEmployeeName;
	}

	/**
	 * 设置主责人姓名
	 * 
	 * @param responsibilityEmployeeName
	 */
	public void setResponsibilityEmployeeName(String responsibilityEmployeeName) {
		this.responsibilityEmployeeName = responsibilityEmployeeName;
	}

	/**
	 * 获取主责人所属组织名称
	 * 
	 * @return
	 */
	public String getResponsibilityUnitName() {
		return responsibilityUnitName;
	}

	/**
	 * 设置主责人所属组织名称
	 * 
	 * @param responsibilityUnitName
	 */
	public void setResponsibilityUnitName(String responsibilityUnitName) {
		this.responsibilityUnitName = responsibilityUnitName;
	}

	/**
	 * 获取主责人所属顶层组织名称
	 * 
	 * @return
	 */
	public String getResponsibilityTopUnitName() {
		return responsibilityTopUnitName;
	}

	/**
	 * 设置主责人所属顶层组织名称
	 * 
	 * @param responsibilityTopUnitName
	 */
	public void setResponsibilityTopUnitName(String responsibilityTopUnitName) {
		this.responsibilityTopUnitName = responsibilityTopUnitName;
	}

	public Integer getOverallProgress() {
		return overallProgress;
	}

	public void setOverallProgress(Integer overallProgress) {
		this.overallProgress = overallProgress;
	}

	/**
	 * 获取工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消
	 * 
	 * @return
	 */
	public String getWorkProcessStatus() {
		return workProcessStatus;
	}

	/**
	 * 设置工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消
	 * 
	 * @param workProcessStatus
	 */
	public void setWorkProcessStatus(String workProcessStatus) {
		this.workProcessStatus = workProcessStatus;
	}

	/**
	 * 获取上一次汇报时间
	 * 
	 * @return
	 */
	public Date getLastReportTime() {
		return lastReportTime;
	}

	/**
	 * 设置上一次汇报时间
	 * 
	 * @param lastReportTime
	 */
	public void setLastReportTime(Date lastReportTime) {
		this.lastReportTime = lastReportTime;
	}

	/**
	 * 获取下一次汇报时间
	 * 
	 * @return
	 */
	public Date getNextReportTime() {
		return nextReportTime;
	}

	/**
	 * 设置下一次汇报时间
	 * 
	 * @param nextReportTime
	 */
	public void setNextReportTime(Date nextReportTime) {
		this.nextReportTime = nextReportTime;
	}

	/**
	 * 获取工作汇报次数
	 * 
	 * @return
	 */
	public Integer getReportCount() {
		return reportCount;
	}

	/**
	 * 设置工作汇报次数
	 * 
	 * @param reportCount
	 */
	public void setReportCount(Integer reportCount) {
		this.reportCount = reportCount;
	}

	/**
	 * 获取工作汇报时间队列，从工作部署日期到工作完成日期，根据汇报周期要求，分析出汇报时间队列以JSON方式存储
	 * 
	 * @return
	 */
	public String getReportTimeQue() {
		return reportTimeQue;
	}

	/**
	 * 设置工作汇报时间队列，从工作部署日期到工作完成日期，根据汇报周期要求，分析出汇报时间队列以JSON方式存储
	 * 
	 * @param reportTimeQue
	 */
	public void setReportTimeQue(String reportTimeQue) {
		this.reportTimeQue = reportTimeQue;
	}

	/**
	 * 获取汇报周期设定：每月汇报|每周汇报
	 * 
	 * @return
	 */
	public String getReportCycle() {
		return reportCycle;
	}

	/**
	 * 设置汇报周期：每月汇报|每周汇报
	 * 
	 * @param reportCycle
	 */
	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}

	/**
	 * 获取周期汇报日期设定：每月的几号，每周的星期几，启动时间由系统配置设定，比如：10:00
	 * 
	 * @return
	 */
	public Integer getReportDayInCycle() {
		return reportDayInCycle;
	}

	/**
	 * 设置周期汇报日期：每月的几号，每周的星期几，启动时间由系统配置设定，比如：10:00
	 * 
	 * @param reportDayInCycle
	 */
	public void setReportDayInCycle(Integer reportDayInCycle) {
		this.reportDayInCycle = reportDayInCycle;
	}

	/**
	 * 获取工作类别：工作类别由工作类别配置表决定
	 * 
	 * @return
	 */
	public String getWorkType() {
		return workType;
	}

	/**
	 * 设置工作类别：工作类别由工作类别配置表决定
	 * 
	 * @param workType
	 */
	public void setWorkType(String workType) {
		this.workType = workType;
	}

	/**
	 * 获取工作级别：工作级别由工作级别配置表决定
	 * 
	 * @return
	 */
	public String getWorkLevel() {
		return workLevel;
	}

	/**
	 * 设置工作级别：工作级别由工作级别配置表决定
	 * 
	 * @param workLevel
	 */
	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
	}

	/**
	 * 获取工作部署年份
	 * 
	 * @return
	 */
	public String getDeployYear() {
		return deployYear;
	}

	/**
	 * 设置工作部署年份
	 * 
	 * @param deployYear
	 */
	public void setDeployYear(String deployYear) {
		this.deployYear = deployYear;
	}

	/**
	 * 获取工作部署月份
	 * 
	 * @return
	 */
	public String getDeployMonth() {
		return deployMonth;
	}

	/**
	 * 设置工作部署月份
	 * 
	 * @param deployMonth
	 */
	public void setDeployMonth(String deployMonth) {
		this.deployMonth = deployMonth;
	}

	/**
	 * 获取工作是否已超期
	 * 
	 * @return
	 */
	public Boolean getIsOverTime() {
		return isOverTime;
	}

	/**
	 * 设置工作是否已超期
	 * 
	 * @param isOverTime
	 */
	public void setIsOverTime(Boolean isOverTime) {
		this.isOverTime = isOverTime;
	}

	/**
	 * 获取工作是否已完成
	 * 
	 * @return
	 */
	public Boolean getIsCompleted() {
		return isCompleted;
	}

	/**
	 * 设置工作是否已完成
	 * 
	 * @param isCompleted
	 */
	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	/**
	 * 获取工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
	 * 
	 * @return
	 */
	public String getWorkDateTimeType() {
		return workDateTimeType;
	}

	/**
	 * 设置工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
	 * 
	 * @param workDateTimeType
	 */
	public void setWorkDateTimeType(String workDateTimeType) {
		this.workDateTimeType = workDateTimeType;
	}

	/**
	 * 获取工作审核层级
	 * 
	 * @return
	 */
	public Integer getWorkAuditLevel() {
		return workAuditLevel;
	}

	/**
	 * 工作审核层级
	 * 
	 * @param workAuditLevel
	 */
	public void setWorkAuditLevel(Integer workAuditLevel) {
		this.workAuditLevel = workAuditLevel;
	}

	// ==================================================================================================
	// ===================== 其他方法
	// =============================================================
	// ==================================================================================================
	/**
	 * 增加一个工作审核层级
	 */
	public void addWorkAuditLevel() {
		this.workAuditLevel++;
	}

	/**
	 * 减少一个工作审核层级
	 */
	public void minusWorkAuditLevel() {
		this.workAuditLevel--;
	}

	/**
	 * 增加一次已汇报次数
	 */
	public void addWorkReportCount() {
		this.reportCount++;
	}

	/**
	 * 减少一次已汇报次数
	 */
	public void minusaWorkReportCount() {
		this.reportCount--;
	}

	/**
	 * 获取是否需要汇报
	 * 
	 * @return
	 */
	public Boolean getIsNeedReport() {
		return isNeedReport;
	}

	/**
	 * 设置是否需要汇报
	 * 
	 * @param needReport
	 */
	public void setIsNeedReport(Boolean isNeedReport) {
		this.isNeedReport = isNeedReport;
	}

	/**
	 * 获取信息状态：正常|已删除
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置信息状态：正常|已删除
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 获取工作汇报是否需要管理员审核
	 * 
	 * @return
	 */
	public Boolean getReportNeedAdminAudit() {
		return reportNeedAdminAudit;
	}

	/**
	 * 设置工作汇报是否需要管理员审核
	 * 
	 * @param reportNeedAdminAudit
	 */
	public void setReportNeedAdminAudit(Boolean reportNeedAdminAudit) {
		this.reportNeedAdminAudit = reportNeedAdminAudit;
	}

	/**
	 * 获取工作管理员姓名
	 * 
	 * @return
	 */
	public String getReportAdminName() {
		return reportAdminName;
	}

	/**
	 * 设置工作管理员姓名
	 * 
	 * @param reportAdminName
	 */
	public void setReportAdminName(String reportAdminName) {
		this.reportAdminName = reportAdminName;
	}

	/**
	 * 获取附件列表
	 * 
	 * @return
	 */
	public List<String> getAttachmentList() {
		return attachmentList;
	}

	/**
	 * 设置附件列表
	 * 
	 * @param attachmentList
	 */
	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getDeployerIdentity() {
		return deployerIdentity;
	}

	public void setDeployerIdentity(String deployerIdentity) {
		this.deployerIdentity = deployerIdentity;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getResponsibilityIdentity() {
		return responsibilityIdentity;
	}

	public void setResponsibilityIdentity(String responsibilityIdentity) {
		this.responsibilityIdentity = responsibilityIdentity;
	}

	public String getReportAdminIdentity() {
		return reportAdminIdentity;
	}

	public void setReportAdminIdentity(String reportAdminIdentity) {
		this.reportAdminIdentity = reportAdminIdentity;
	}

	public String getShortWorkDetail() {
		return shortWorkDetail;
	}

	public void setShortWorkDetail(String shortWorkDetail) {
		this.shortWorkDetail = shortWorkDetail;
	}

	public String getShortDutyDescription() {
		return shortDutyDescription;
	}

	public void setShortDutyDescription(String shortDutyDescription) {
		this.shortDutyDescription = shortDutyDescription;
	}

	public String getShortProgressAction() {
		return shortProgressAction;
	}

	public void setShortProgressAction(String shortProgressAction) {
		this.shortProgressAction = shortProgressAction;
	}

	public String getShortLandmarkDescription() {
		return shortLandmarkDescription;
	}

	public void setShortLandmarkDescription(String shortLandmarkDescription) {
		this.shortLandmarkDescription = shortLandmarkDescription;
	}

	public String getShortResultDescription() {
		return shortResultDescription;
	}

	public void setShortResultDescription(String shortResultDescription) {
		this.shortResultDescription = shortResultDescription;
	}

	public String getShortMajorIssuesDescription() {
		return shortMajorIssuesDescription;
	}

	public void setShortMajorIssuesDescription(String shortMajorIssuesDescription) {
		this.shortMajorIssuesDescription = shortMajorIssuesDescription;
	}

	public String getShortProgressPlan() {
		return shortProgressPlan;
	}

	public void setShortProgressPlan(String shortProgressPlan) {
		this.shortProgressPlan = shortProgressPlan;
	}

	public String getProgressAnalyseTime() {
		return progressAnalyseTime;
	}

	public void setProgressAnalyseTime(String progressAnalyseTime) {
		this.progressAnalyseTime = progressAnalyseTime;
	}

	public Date getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	public Boolean getOverTime() {
		return isOverTime;
	}

	public void setOverTime(Boolean overTime) {
		isOverTime = overTime;
	}

	public Boolean getCompleted() {
		return isCompleted;
	}

	public void setCompleted(Boolean completed) {
		isCompleted = completed;
	}

	public Boolean getNeedReport() {
		return isNeedReport;
	}

	public void setNeedReport(Boolean needReport) {
		isNeedReport = needReport;
	}

	public String getCurrentAppraiseInfoId() {
		return currentAppraiseInfoId;
	}

	public void setCurrentAppraiseInfoId(String currentAppraiseInfoId) {
		this.currentAppraiseInfoId = currentAppraiseInfoId;
	}

	public String getCurrentAppraiseWorkId() {
		return currentAppraiseWorkId;
	}

	public void setCurrentAppraiseWorkId(String currentAppraiseWorkId) {
		this.currentAppraiseWorkId = currentAppraiseWorkId;
	}

	public String getCurrentActivityName() {
		return currentActivityName;
	}

	public void setCurrentActivityName(String currentActivityName) {
		this.currentActivityName = currentActivityName;
	}

	public String getCurrentAppraiseStatus() {
		return currentAppraiseStatus;
	}

	public void setCurrentAppraiseStatus(String currentAppraiseStatus) {
		this.currentAppraiseStatus = currentAppraiseStatus;
	}

	public List<String> getAppraiseInfoList() {
		return appraiseInfoList;
	}

	public void setAppraiseInfoList(List<String> appraiseInfoList) {
		this.appraiseInfoList = appraiseInfoList;
	}

	public Integer getAppraiseTimes() {
		return appraiseTimes;
	}

	public void setAppraiseTimes(Integer appraiseTimes) {
		this.appraiseTimes = appraiseTimes;
	}

	public String getCurrentAppraiseTitle() {
		return currentAppraiseTitle;
	}

	public void setCurrentAppraiseTitle(String currentAppraiseTitle) {
		this.currentAppraiseTitle = currentAppraiseTitle;
	}

	public String getCurrentAppraiseJobId() {
		return currentAppraiseJobId;
	}

	public void setCurrentAppraiseJobId(String currentAppraiseJobId) {
		this.currentAppraiseJobId = currentAppraiseJobId;
	}

	public List<String> getCooperateEmployeeNameList() {
		return cooperateEmployeeNameList == null ? new ArrayList<>() : cooperateEmployeeNameList;
	}

	public List<String> getCooperateIdentityList() {
		return cooperateIdentityList == null ? new ArrayList<>() : cooperateIdentityList;
	}

	public List<String> getCooperateUnitNameList() {
		return cooperateUnitNameList == null ? new ArrayList<>() : cooperateUnitNameList;
	}

	public List<String> getCooperateTopUnitNameList() {
		return cooperateTopUnitNameList == null ? new ArrayList<>() : cooperateTopUnitNameList;
	}

	public List<String> getReadLeaderIdentityList() {
		return readLeaderIdentityList == null ? new ArrayList<>() : readLeaderIdentityList;
	}

	public List<String> getReadLeaderNameList() {
		return readLeaderNameList == null ? new ArrayList<>() : readLeaderNameList;
	}

	public List<String> getReadLeaderUnitNameList() {
		return readLeaderUnitNameList == null ? new ArrayList<>() : readLeaderUnitNameList;
	}

	public List<String> getReadLeaderTopUnitNameList() {
		return readLeaderTopUnitNameList == null ? new ArrayList<>() : readLeaderTopUnitNameList;
	}

	public void setCooperateEmployeeNameList(List<String> cooperateEmployeeNameList) {
		this.cooperateEmployeeNameList = cooperateEmployeeNameList;
	}

	public void setCooperateIdentityList(List<String> cooperateIdentityList) {
		this.cooperateIdentityList = cooperateIdentityList;
	}

	public void setCooperateUnitNameList(List<String> cooperateUnitNameList) {
		this.cooperateUnitNameList = cooperateUnitNameList;
	}

	public void setCooperateTopUnitNameList(List<String> cooperateTopUnitNameList) {
		this.cooperateTopUnitNameList = cooperateTopUnitNameList;
	}

	public void setReadLeaderIdentityList(List<String> readLeaderIdentityList) {
		this.readLeaderIdentityList = readLeaderIdentityList;
	}

	public void setReadLeaderNameList(List<String> readLeaderNameList) {
		this.readLeaderNameList = readLeaderNameList;
	}

	public void setReadLeaderUnitNameList(List<String> readLeaderUnitNameList) {
		this.readLeaderUnitNameList = readLeaderUnitNameList;
	}

	public void setReadLeaderTopUnitNameList(List<String> readLeaderTopUnitNameList) {
		this.readLeaderTopUnitNameList = readLeaderTopUnitNameList;
	}
}