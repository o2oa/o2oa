package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * task
 *
 * Created by FancyLou on 2016/8/10.
 */
public class TaskMessage extends BaseMessage {

    private String work;//workId
    private String task;//待办id

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
