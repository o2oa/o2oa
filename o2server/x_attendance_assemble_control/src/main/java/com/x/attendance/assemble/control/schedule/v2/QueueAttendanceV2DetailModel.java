package com.x.attendance.assemble.control.schedule.v2;

import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class QueueAttendanceV2DetailModel extends GsonPropertyObject {


    private static final long serialVersionUID = -961721648296688272L;


    public QueueAttendanceV2DetailModel(String person, String date) {
        this.person = person;
        this.date = date;
    }

    private String person; // 人员DN

    private String date; // 要处理的日期 如：2021-01-01


    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
