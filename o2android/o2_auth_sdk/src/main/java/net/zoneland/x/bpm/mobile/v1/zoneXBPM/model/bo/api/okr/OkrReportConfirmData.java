package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.okr;

import java.util.List;

/**
 * Created by FancyLou on 2016/9/29.
 */

public class OkrReportConfirmData {

    private String id;
    private String createTime;
    private String updateTime;
    private String sequence;

    private List<OkrReportProcessLogData> processLogs;

    private boolean isReporter;
    private boolean isWorkAdmin;
    private boolean isReadLeader;
    private boolean isCreator;
    private String adminSuperviseInfo;
    private String workPointAndRequirements;
    private String progressDescription;
    private String workPlan;
    private String memo;
    private String submitTime;
    private String workTitle;
    private String centerTitle;
    private String centerId;
    private String workId;
    private String workType;
    private String title;
    private String shortTitle;
    private String activityName;
    private int reportCount;
    private String reporterName;
    private String reporterIdentity;
    private String reporterOrganizationName;
    private String reporterCompanyName;
    private String creatorName;
    private String creatorIdentity;
    private String creatorOrganizationName;
    private String creatorCompanyName;
    private boolean isWorkCompleted;
    private double progressPercent;
    private String processStatus;
    private String status;
    private String processType;
    private int currentProcessLevel;
    private boolean needAdminAudit;
    private boolean needLeaderRead;
    private String reportWorkflowType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public List<OkrReportProcessLogData> getProcessLogs() {
        return processLogs;
    }

    public void setProcessLogs(List<OkrReportProcessLogData> processLogs) {
        this.processLogs = processLogs;
    }

    public boolean isReporter() {
        return isReporter;
    }

    public void setReporter(boolean reporter) {
        isReporter = reporter;
    }

    public boolean isWorkAdmin() {
        return isWorkAdmin;
    }

    public void setWorkAdmin(boolean workAdmin) {
        isWorkAdmin = workAdmin;
    }

    public boolean isReadLeader() {
        return isReadLeader;
    }

    public void setReadLeader(boolean readLeader) {
        isReadLeader = readLeader;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public String getAdminSuperviseInfo() {
        return adminSuperviseInfo;
    }

    public void setAdminSuperviseInfo(String adminSuperviseInfo) {
        this.adminSuperviseInfo = adminSuperviseInfo;
    }

    public String getWorkPointAndRequirements() {
        return workPointAndRequirements;
    }

    public void setWorkPointAndRequirements(String workPointAndRequirements) {
        this.workPointAndRequirements = workPointAndRequirements;
    }

    public String getProgressDescription() {
        return progressDescription;
    }

    public void setProgressDescription(String progressDescription) {
        this.progressDescription = progressDescription;
    }

    public String getWorkPlan() {
        return workPlan;
    }

    public void setWorkPlan(String workPlan) {
        this.workPlan = workPlan;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public String getCenterTitle() {
        return centerTitle;
    }

    public void setCenterTitle(String centerTitle) {
        this.centerTitle = centerTitle;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterIdentity() {
        return reporterIdentity;
    }

    public void setReporterIdentity(String reporterIdentity) {
        this.reporterIdentity = reporterIdentity;
    }

    public String getReporterOrganizationName() {
        return reporterOrganizationName;
    }

    public void setReporterOrganizationName(String reporterOrganizationName) {
        this.reporterOrganizationName = reporterOrganizationName;
    }

    public String getReporterCompanyName() {
        return reporterCompanyName;
    }

    public void setReporterCompanyName(String reporterCompanyName) {
        this.reporterCompanyName = reporterCompanyName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorIdentity() {
        return creatorIdentity;
    }

    public void setCreatorIdentity(String creatorIdentity) {
        this.creatorIdentity = creatorIdentity;
    }

    public String getCreatorOrganizationName() {
        return creatorOrganizationName;
    }

    public void setCreatorOrganizationName(String creatorOrganizationName) {
        this.creatorOrganizationName = creatorOrganizationName;
    }

    public String getCreatorCompanyName() {
        return creatorCompanyName;
    }

    public void setCreatorCompanyName(String creatorCompanyName) {
        this.creatorCompanyName = creatorCompanyName;
    }

    public boolean isWorkCompleted() {
        return isWorkCompleted;
    }

    public void setWorkCompleted(boolean workCompleted) {
        isWorkCompleted = workCompleted;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public int getCurrentProcessLevel() {
        return currentProcessLevel;
    }

    public void setCurrentProcessLevel(int currentProcessLevel) {
        this.currentProcessLevel = currentProcessLevel;
    }

    public boolean isNeedAdminAudit() {
        return needAdminAudit;
    }

    public void setNeedAdminAudit(boolean needAdminAudit) {
        this.needAdminAudit = needAdminAudit;
    }

    public boolean isNeedLeaderRead() {
        return needLeaderRead;
    }

    public void setNeedLeaderRead(boolean needLeaderRead) {
        this.needLeaderRead = needLeaderRead;
    }

    public String getReportWorkflowType() {
        return reportWorkflowType;
    }

    public void setReportWorkflowType(String reportWorkflowType) {
        this.reportWorkflowType = reportWorkflowType;
    }
}
