package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.okr

import java.io.Serializable

/**
 * Created by fancy on 2017/4/5.
 */

/**
 * okr 秘书
 */
data class Secretary(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var secretaryName: String = "",
        var secretaryIdentity: String = "",
        var secretaryOrganizationName: String = "",
        var secretaryCompanyName: String = "",
        var leaderName: String = "",
        var leaderIdentity: String = "",
        var leaderOrganizationName: String = "",
        var leaderCompanyName: String = "",
        var description: String = ""
)

/**
 * okr 登录请求对象
 */
data class LoginRequest(
        var loginIdentity: String = ""
)

/**
 * okr 登录返回对象
 */
data class LoginResponse(
        var operationUserName: String = "",
        var operationUserOrganizationName: String = "",
        var operationUserCompanyName: String = "",
        var loginIdentityName: String = "",
        var loginUserName: String = "",
        var loginUserOrganizationName: String = "",
        var loginUserCompanyName: String = "",
        var okrSystemAdmin: Boolean = false
)

/**
 * okr 汇总统计信息
 */
data class SummaryStatistics(
        var responWorkTotal: Int = 0,
        var responProcessingWorkCount: Int = 0,
        var responCompletedWorkCount: Int = 0,
        var draftWorkCount: Int = 0,
        var overtimeResponWorkCount: Int = 0,
        var overtimeCooperWorkCount: Int = 0,
        var overtimeDeployWorkCount: Int = 0,
        var overtimenessResponWorkCount: Int = 0,
        var overtimenessCooperWorkCount: Int = 0,
        var overtimenessDeployWorkCount: Int = 0,
        var percent: Double = 0.0
)

/**
 * okr 待办任务
 */
data class TodoTask(
        var id: String = "",
        var title: String = "",
        var processType: String = "",
        var centerId: String = "",
        var centerTitle: String = "",
        var workId: String = "",
        var workTitle: String = "",
        var workType: String = "",
        var dynamicObjectType: String = "",
        var dynamicObjectId: String = "",
        var dynamicObjectTitle: String = "",
        var arriveDateTimeStr: String = "",
        var arriveDateTime: String = "",
        var activityName: String = "",
        var status: String = ""
)

/**
 * okr 工作动态
 */
data class WorkDynamic(
        var id: String = "",
        var dynamicType: String = "",
        var dynamicObjectType: String = "",
        var dynamicObjectId: String = "",
        var dynamicObjectTitle: String = "",
        var centerId: String = "",
        var centerTitle: String = "",
        var workId: String = "",
        var workTitle: String = "",
        var dateTimeStr: String = "",
        var dateTime: String = "",
        var operatorName: String = "",
        var targetName: String = "",
        var targetIdentity: String = "",
        var content: String = "",
        var description: String = "",
        var status: String = ""
)

/**
 * okr 配置信息请求对象
 */
data class SystemConfigRequest(
        var configCode: String = ""
)

/**
 * okr 配置信息反馈对象
 */
data class SystemConfigResponse(
        var id: String = "",
        var configName: String = "",
        var configCode: String = "",
        var configValue: String = "",
        var orderNumber: Int = 0,
        var description: String = ""
)

/**
 * 中心工作 分类
 */
data class CenterWorkType(
        var id: String = "",
        var workTypeName: String = "",
        var orderNumber: Int = 0,
        var description: String = ""
)

/**
 * 中心工作创建和修改的post对象
 */
data class CenterWorkRequestBody(
        var id: String = "",
        var title: String = "",
        var reportAuditLeaderIdentity: String = "",
        var defaultWorkType: String = "",
        var defaultCompleteDateLimitStr: String = "",
        var description: String = ""
)

/**
 * 中心工作对象
 */
data class CenterWorkInfo(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var title: String = "",
        var deployYear: String = "",
        var deployMonth: String = "",
        var deployerName: String = "",
        var deployerIdentity: String = "",
        var deployerOrganizationName: String = "",
        var deployerCompanyName: String = "",
        var auditLeaderName: String = "",
        var auditLeaderIdentity: String = "",
        var auditLeaderOrganizationName: String = "",
        var auditLeaderCompanyName: String = "",
        var creatorName: String = "",
        var creatorIdentity: String = "",
        var creatorOrganizationName: String = "",
        var creatorCompanyName: String = "",
        var processStatus: String = "",
        var defaultCompleteDateLimit: String = "",
        var defaultCompleteDateLimitStr: String = "",
        var defaultWorkType: String = "",
        var defaultWorkLevel: String = "",
        var defaultLeader: String = "",
        var defaultLeaderIdentity: String = "",
        var reportAuditLeaderName: String = "",
        var reportAuditLeaderIdentity: String = "",
        var isNeedAudit: Boolean = false,
        var status: String = "",
        var description: String = "",
        var workTotal: Int = 0,
        var processingWorkCount: Int = 0,
        var completedWorkCount: Int = 0,
        var overtimeWorkCount: Int = 0,
        var draftWorkCount: Int = 0,
        var workProcessIdentity: List<String> = ArrayList(),
        var operation: List<String> = ArrayList()
)

/**
 * 具体工作对象
 */
data class WorkInfo(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var workOutType: String = "",
        var title: String = "",
        var centerId: String = "",
        var centerTitle: String = "",
        var parentWorkId: String = "",
        var parentWorkTitle: String = "",
        var workDateTimeType: String = "",
        var deployYear: String = "",
        var deployMonth: String = "",
        var deployerName: String = "",
        var deployerIdentity: String = "",
        var deployerOrganizationName: String = "",
        var deployerCompanyName: String = "",
        var creatorName: String = "",
        var creatorIdentity: String = "",
        var creatorOrganizationName: String = "",
        var creatorCompanyName: String = "",
        var deployDateStr: String = "",
        var confirmDateStr: String = "",
        var completeDateLimit: String = "",
        var completeDateLimitStr: String = "",
        var responsibilityEmployeeName: String = "",
        var responsibilityIdentity: String = "",
        var responsibilityOrganizationName: String = "",
        var responsibilityCompanyName: String = "",
        var cooperateEmployeeName: String = "",
        var cooperateIdentity: String = "",
        var cooperateOrganizationName: String = "",
        var cooperateCompanyName: String = "",
        var readLeaderIdentity: String = "",
        var readLeaderName: String = "",
        var readLeaderOrganizationName: String = "",
        var readLeaderCompanyName: String = "",
        var workType: String = "",
        var workLevel: String = "",
        var overallProgress: Double = 0.0,
        var workProcessStatus: String = "",
        var nextReportTime: String = "",
        var reportTimeQue: String = "",
        var reportCycle: String = "",
        var reportDayInCycle: Int = 0,
        var reportCount: Int = 0,
        var workAuditLevel: Int = 0,
        var status: String = "",
        var workDetail: String = "",
        var progressAction: String = "",
        var landmarkDescription: String = "",
        var shortWorkDetail: String = "",
        var shortDutyDescription: String = "",
        var shortProgressAction: String = "",
        var shortLandmarkDescription: String = "",
        var shortResultDescription: String = "",
        var shortMajorIssuesDescription: String = "",
        var shortProgressPlan: String = "",
        var progressAnalyseTime: String = "",
        var attachmentList: List<String> = ArrayList(),
        var subWorks: List<WorkInfo> = ArrayList(),
        var workProcessIdentity: List<String> = ArrayList(),
        var operation: List<String> = ArrayList()
)

/**
 * 部署具体工作 post对象
 */
data class WorkDeployRequestBody(
        var workIds: List<String> = ArrayList()
)

/**
 * 具体工作 post对象
 */
data class WorkRequestBody(
        var id: String = "",
        var centerWorkId: String = "",
        var completeDateLimitStr: String = "",
        var reportCycle: String = "",
        var reportDayInCycle: String = "",
        var responsibilityOrganizationName: String = "",
        var responsibilityIdentity: String = "",
        var cooperateOrganizationName: String = "",
        var cooperateIdentity: String = "",
        var readLeaderIdentity: String = "",
        var workDetail: String = "",
        var progressAction: String = "",
        var landmarkDescription: String = "",
        var title: String = "",
        var deployerName: String = "",
        var creatorName: String = "",
        var centerId: String = "",
        var parentWorkId: String = "",
        var workDateTimeType: String = "",
        var workLevel: String = "",
        var overallProgress: Double = 0.0,
        var specificActionInitiatives: String = "",
        var cityCompanyDuty: String = ""
)

/**
 * 具体工作附件对象
 */
data class WorkAttachmentInfo(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var lastUpdateTime: String = "",
        var storage: String = "",
        var name: String = "",
        var fileName: String = "",
        var centerId: String = "",
        var workInfoId: String = "",
        var parentType: String = "",
        var key: String = "",
        var fileHost: String = "",
        var filePath: String = "",
        var storageName: String = "",
        var creatorUid: String = "",
        var extension: String = "",
        var status: String = "",
        var length: Long = 0L
)

/******************************汇报关系对象****************************/
data class Report(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var submitTime: String = "",
        var workTitle: String = "",
        var centerTitle: String = "",
        var centerId: String = "",
        var workId: String = "",
        var workType: String = "",
        var title: String = "",
        var shortTitle: String = "",
        var activityName: String = "",
        var workPlan: String = "",
        var progressDescription: String = "",
        var processStatus: String = "",
        var status: String = "",
        var processType: String = "",
        var reportWorkflowType: String = "",
        var adminSuperviseInfo: String = "",
        var progressPercent: Double = 0.0,
        var description: String = "",
        var workInfo: WorkInfo = WorkInfo()
)

data class ReportInfo(
        var id: String = "",
        var activityName: String = "",
        var status: String = "",
        var processType: String = "",
        var name: String = "",
        var reports: List<Report> = ArrayList()
)

data class ReportCollect(
        var id: String = "",
        var activityName: String = "",
        var status: String = "",
        var processType: String = "",
        var organizationNames: List<String> = ArrayList(),
        var reportInfos: List<ReportInfo> = ArrayList()
) : Serializable

/**
 * 汇总任务对象
 */
data class ReportTaskCollect(
        var id: String = "",
        var activityName: String = "",
        var status: String = "",
        var processType: String = "",
        var count: Int = 0,
        var reportCollect: ReportCollect = ReportCollect()
)


data class ReportProcessInfo(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var workReportId: String = "",
        var reportTitle: String = "",
        var workId: String = "",
        var title: String = "",
        var centerId: String = "",
        var centerTitle: String = "",
        var processorName: String = "",
        var processorIdentity: String = "",
        var processorOrganizationName: String = "",
        var processorCompanyName: String = "",
        var processLevel: Int = 0,
        var activityName: String = "",
        var opinion: String = "",
        var arriveTime: String = "",
        var processTime: String = "",
        var processStatus: String = "",
        var status: String = ""
)

data class ReportConfirm(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var isReporter: Boolean = false,
        var isWorkAdmin: Boolean = false,
        var isReadLeader: Boolean = false,
        var isCreator: Boolean = false,
        var adminSuperviseInfo: String = "",
        var workPointAndRequirements: String = "",
        var progressDescription: String = "",
        var workPlan: String = "",
        var memo: String = "",
        var submitTime: String = "",
        var workTitle: String = "",
        var centerTitle: String = "",
        var centerId: String = "",
        var workId: String = "",
        var workType: String = "",
        var title: String = "",
        var shortTitle: String = "",
        var activityName: String = "",
        var reportCount: Int = 0,
        var reporterName: String = "",
        var reporterIdentity: String = "",
        var reporterOrganizationName: String = "",
        var reporterCompanyName: String = "",
        var creatorName: String = "",
        var creatorIdentity: String = "",
        var creatorOrganizationName: String = "",
        var creatorCompanyName: String = "",
        var isWorkCompleted: Boolean = false,
        var progressPercent: Double = 0.0,
        var processStatus: String = "",
        var status: String = "",
        var processType: String = "",
        var currentProcessLevel: Int = 0,
        var needAdminAudit: Boolean = false,
        var needLeaderRead: Boolean = false,
        var reportWorkflowType: String = "",
        var processLogs: List<ReportProcessInfo> = ArrayList()
)

