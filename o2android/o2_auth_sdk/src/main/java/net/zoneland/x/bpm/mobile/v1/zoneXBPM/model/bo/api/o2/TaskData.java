package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import android.text.TextUtils;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ToDoFragmentListViewItemVO;

import java.io.Serializable;
import java.util.List;

/**
 * 待办对象
 * Created by FancyLou on 2015/11/24.
 */
public class TaskData implements Serializable {

    private long rank;
    private String createTime;
    private String updateTime;
    private String sequence;
    private String id;
    private String job;//任务
    private String title;//标题
    private String startTime;//开始时间
    private String startTimeMonth;//开始时间月份
    private String work;//工作ID
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
    private String creatorPerson;
    private String creatorIdentity;
    private String creatorDepartment;
    private String creatorCompany;
    private List<String> routeList;//当前活动可选路由
    private List<String> routeNameList;//当前活动可选路由名称
    private String routeName;//
    private String manualMode;
    private String opinion;
    private String mediaOpinion;
    private boolean modified;
    private boolean viewed;
    private boolean allowRapid;


    public ToDoFragmentListViewItemVO copyToTodoListItem() {
        String showTime = "";
        if (!TextUtils.isEmpty(this.startTime) && this.startTime.length() >= 10) {
            showTime = this.startTime.substring(0, 10);
        }
        return new ToDoFragmentListViewItemVO(this.work, O2.INSTANCE.getBUSINESS_TYPE_WORK_CENTER(), this.title==null?"":this.title, "【" + this.processName + "】", showTime);
    }

    public String getMediaOpinion() {
        return mediaOpinion;
    }

    public void setMediaOpinion(String mediaOpinion) {
        this.mediaOpinion = mediaOpinion;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
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

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
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

    public String getCreatorDepartment() {
        return creatorDepartment;
    }

    public void setCreatorDepartment(String creatorDepartment) {
        this.creatorDepartment = creatorDepartment;
    }

    public String getCreatorCompany() {
        return creatorCompany;
    }

    public void setCreatorCompany(String creatorCompany) {
        this.creatorCompany = creatorCompany;
    }

    public List<String> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<String> routeList) {
        this.routeList = routeList;
    }

    public List<String> getRouteNameList() {
        return routeNameList;
    }

    public void setRouteNameList(List<String> routeNameList) {
        this.routeNameList = routeNameList;
    }

    public String getManualMode() {
        return manualMode;
    }

    public void setManualMode(String manualMode) {
        this.manualMode = manualMode;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public boolean isAllowRapid() {
        return allowRapid;
    }

    public void setAllowRapid(boolean allowRapid) {
        this.allowRapid = allowRapid;
    }

    @Override
    public String toString() {
        return "TaskData{" +
                "rank=" + rank +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", sequence='" + sequence + '\'' +
                ", id='" + id + '\'' +
                ", job='" + job + '\'' +
                ", title='" + title + '\'' +
                ", startTime='" + startTime + '\'' +
                ", startTimeMonth='" + startTimeMonth + '\'' +
                ", work='" + work + '\'' +
                ", application='" + application + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", process='" + process + '\'' +
                ", processName='" + processName + '\'' +
                ", person='" + person + '\'' +
                ", identity='" + identity + '\'' +
                ", department='" + department + '\'' +
                ", company='" + company + '\'' +
                ", activity='" + activity + '\'' +
                ", activityName='" + activityName + '\'' +
                ", activityType='" + activityType + '\'' +
                ", activityToken='" + activityToken + '\'' +
                ", creatorPerson='" + creatorPerson + '\'' +
                ", creatorIdentity='" + creatorIdentity + '\'' +
                ", creatorDepartment='" + creatorDepartment + '\'' +
                ", creatorCompany='" + creatorCompany + '\'' +
                ", routeList=" + routeList +
                ", routeNameList=" + routeNameList +
                ", routeName='" + routeName + '\'' +
                ", manualMode='" + manualMode + '\'' +
                ", opinion='" + opinion + '\'' +
                ", modified=" + modified +
                ", viewed=" + viewed +
                ", allowRapid=" + allowRapid +
                '}';
    }
}
