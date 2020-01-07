package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * Created by FancyLou on 2016/3/8.
 */
public class WorkLog {

    private List<TaskCompleteData> taskCompletedList;
    private List<TaskData> taskList;
    private String createTime;
    private String updateTime;
    private String id;
    private boolean completed;
    private String fromActivity;
    private String fromActivityType;
    private String fromActivityName;
    private String fromActivityToken;
    private String fromTime;
    private String arrivedActivity;
    private String arrivedActivityType;
    private String arrivedActivityName;
    private String arrivedActivityToken;
    private String arrivedTime;
    private String route;
    private String routeName;
    private boolean connected;
    private boolean splitting;
    private List<String> splitTokenList;

    public List<TaskCompleteData> getTaskCompletedList() {
        return taskCompletedList;
    }

    public void setTaskCompletedList(List<TaskCompleteData> taskCompletedList) {
        this.taskCompletedList = taskCompletedList;
    }

    public List<TaskData> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskData> taskList) {
        this.taskList = taskList;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getFromActivity() {
        return fromActivity;
    }

    public void setFromActivity(String fromActivity) {
        this.fromActivity = fromActivity;
    }

    public String getFromActivityType() {
        return fromActivityType;
    }

    public void setFromActivityType(String fromActivityType) {
        this.fromActivityType = fromActivityType;
    }

    public String getFromActivityName() {
        return fromActivityName;
    }

    public void setFromActivityName(String fromActivityName) {
        this.fromActivityName = fromActivityName;
    }

    public String getFromActivityToken() {
        return fromActivityToken;
    }

    public void setFromActivityToken(String fromActivityToken) {
        this.fromActivityToken = fromActivityToken;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getArrivedActivity() {
        return arrivedActivity;
    }

    public void setArrivedActivity(String arrivedActivity) {
        this.arrivedActivity = arrivedActivity;
    }

    public String getArrivedActivityType() {
        return arrivedActivityType;
    }

    public void setArrivedActivityType(String arrivedActivityType) {
        this.arrivedActivityType = arrivedActivityType;
    }

    public String getArrivedActivityName() {
        return arrivedActivityName;
    }

    public void setArrivedActivityName(String arrivedActivityName) {
        this.arrivedActivityName = arrivedActivityName;
    }

    public String getArrivedActivityToken() {
        return arrivedActivityToken;
    }

    public void setArrivedActivityToken(String arrivedActivityToken) {
        this.arrivedActivityToken = arrivedActivityToken;
    }

    public String getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(String arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isSplitting() {
        return splitting;
    }

    public void setSplitting(boolean splitting) {
        this.splitting = splitting;
    }

    public List<String> getSplitTokenList() {
        return splitTokenList;
    }

    public void setSplitTokenList(List<String> splitTokenList) {
        this.splitTokenList = splitTokenList;
    }
}
