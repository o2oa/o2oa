package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

/**
 * 已阅
 * Created by FancyLou on 2015/11/24.
 */
public class ReadCompleteData  {
    private long rank;
    private String createTime;
    private String updateTime;
    private String sequence;
    private String id;
    private String job;//任务
    private String work;//工作ID
    private String workCompleted;//工作结束ID
    private boolean completed;//
    private String read;//待阅id
    private String title;//标题
    private String application;//应用
    private String applicationName;//应用名称
    private String process;//流程id
    private String processName;//流程名称
    private String person;//当前处理人
    private String identity;//当前处理人Identity
    private String department;//当前处理人所在部门.
    private String company;//当前处理人公司.
    private String activity;//活动id
    private String activityName;//活动名称
    private String activityType;//活动类型 net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ActivityTypeEnums
    private String activityToken;//活动token
    private String startTime;//
    private String startTimeMonth;//
    private String completedTime;
    private String completedTimeMonth;//


    public String getWorkCompleted() {
        return workCompleted;
    }

    public void setWorkCompleted(String workCompleted) {
        this.workCompleted = workCompleted;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
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

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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
}
