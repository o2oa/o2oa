package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * 已办详细信息
 * Created by FancyLou on 2016/3/8.
 */
public class TaskCompleteInfoData {

    private List<WorkLog> workLogList;
    private TaskCompleteData taskCompleted;

    private List<Work> workList;
    private List<WorkCompleted> workCompletedList;



    public List<WorkLog> getWorkLogList() {
        return workLogList;
    }

    public void setWorkLogList(List<WorkLog> workLogList) {
        this.workLogList = workLogList;
    }

    public TaskCompleteData getTaskCompleted() {
        return taskCompleted;
    }

    public void setTaskCompleted(TaskCompleteData taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    public List<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(List<Work> workList) {
        this.workList = workList;
    }

    public List<WorkCompleted> getWorkCompletedList() {
        return workCompletedList;
    }

    public void setWorkCompletedList(List<WorkCompleted> workCompletedList) {
        this.workCompletedList = workCompletedList;
    }
}
