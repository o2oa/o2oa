package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

/**
 * 已办对象
 *
 * Created by FancyLou on 2015/11/24.
 */
public class TaskCompleteData {

    private String createTime;
    private String updateTime;
    private String id;
    private String job;//任务
    private String title;//标题
    private String startTime;//开始时间
    private String startTimeMonth;//开始时间月份
    private String completedTime;//完成时间
    private String completedTimeMonth;//完成时间月份
    private String work;//工作ID
    private String workCompleted;//工作结束后生成的id
    private boolean completed;//
    private String application;//应用
    private String applicationName;//应用名称
    private String process;//流程id
    private String processName;//流程名称
    private String person;//当前处理人
    private String identity;//当前处理人Identity
    private String unit;//当前处理人所在部门.

    private String activity;//活动id
    private String activityName;//活动名称
    private String activityType;//活动类型 net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ActivityTypeEnums
    private String activityToken;//活动token
    private String creatorPerson;
    private String creatorIdentity;
    private String creatorUnit;
    private String routeName;//路由名称
    private String opinion;//处理意见
    private String task;//任务id
    private boolean expired;//是否超期
    private String processingType;


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTimeMonth() {
        return startTimeMonth;
    }

    public void setStartTimeMonth(String startTimeMonth) {
        this.startTimeMonth = startTimeMonth;
    }

    public String getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(String completedTime) {
        this.completedTime = completedTime;
    }

    public String getCompletedTimeMonth() {
        return completedTimeMonth;
    }

    public void setCompletedTimeMonth(String completedTimeMonth) {
        this.completedTimeMonth = completedTimeMonth;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getWorkCompleted() {
        return workCompleted;
    }

    public void setWorkCompleted(String workCompleted) {
        this.workCompleted = workCompleted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityToken() {
        return activityToken;
    }

    public void setActivityToken(String activityToken) {
        this.activityToken = activityToken;
    }

    public String getCreatorPerson() {
        return creatorPerson;
    }

    public void setCreatorPerson(String creatorPerson) {
        this.creatorPerson = creatorPerson;
    }

    public String getCreatorIdentity() {
        return creatorIdentity;
    }

    public void setCreatorIdentity(String creatorIdentity) {
        this.creatorIdentity = creatorIdentity;
    }

    public String getCreatorUnit() {
        return creatorUnit;
    }

    public void setCreatorUnit(String creatorUnit) {
        this.creatorUnit = creatorUnit;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getProcessingType() {
        return processingType;
    }

    public void setProcessingType(String processingType) {
        this.processingType = processingType;
    }
}
