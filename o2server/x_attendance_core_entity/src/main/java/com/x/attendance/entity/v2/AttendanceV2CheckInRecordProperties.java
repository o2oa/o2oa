package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2CheckInRecordProperties extends JsonProperties  {



    @FieldDescribe("外勤打卡关联工作id.")
    private String fieldWorkJobId;
    @FieldDescribe("外勤打卡关联工作状态，1:审批中，2:已审核.")
    private Integer fieldWorkJobStatus;

    public void startFieldWorkJob(String jobId) {
        this.fieldWorkJobId = jobId;
        this.fieldWorkJobStatus = 1;
    }
    public void finishFieldWorkJob() {
        this.fieldWorkJobStatus = 2;
    }
    public void cancelFieldWorkJob() {
        this.fieldWorkJobId = null;
        this.fieldWorkJobStatus = null;
    }

    public String getFieldWorkJobId() {
        return fieldWorkJobId;
    }

    public void setFieldWorkJobId(String fieldWorkJobId) {
        this.fieldWorkJobId = fieldWorkJobId;
    }

    public Integer getFieldWorkJobStatus() {
        return fieldWorkJobStatus;
    }

    public void setFieldWorkJobStatus(Integer fieldWorkJobStatus) {
        this.fieldWorkJobStatus = fieldWorkJobStatus;
    }
}
