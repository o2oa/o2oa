package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.WorkVO;

/**
 * Created by fancyLou on 02/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */
public class WorkCompleted extends WorkVO {


    private String completedTime;
    private String completedTimeMonth;
    private String serial;
    private String form;
    private String work;


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

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }
}
