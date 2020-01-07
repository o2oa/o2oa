package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * Created by FancyLou on 2016/2/24.
 */
public class ProcessWorkData {

    private  int currentTaskIndex;
    private String createTime;
    private String updateTime;
    private String id;
    private boolean completed;
    private String fromActivity;
    private String fromActivityType;
    private String fromActivityName;
    private String fromActivityToken;
    private String fromTime;
    private boolean connected;
    private boolean splitting;
    private List<String> splitTokenList;

    private List<TaskData> taskList;
    private List<TaskCompleteData> taskCompletedList;

    public int getCurrentTaskIndex() {
        return currentTaskIndex;
    }

    public void setCurrentTaskIndex(int currentTaskIndex) {
        this.currentTaskIndex = currentTaskIndex;
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

    public List<TaskData> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskData> taskList) {
        this.taskList = taskList;
    }

    public List<TaskCompleteData> getTaskCompletedList() {
        return taskCompletedList;
    }

    public void setTaskCompletedList(List<TaskCompleteData> taskCompletedList) {
        this.taskCompletedList = taskCompletedList;
    }
}
