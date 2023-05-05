package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

import java.util.List;

/**
 *
 * Created by fancyLou on 2023/1/30.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2ShiftCheckTimeProperties extends JsonProperties {


    @FieldDescribe("班次上下班打卡时间")
    private List<AttendanceV2ShiftCheckTime> timeList;


    public List<AttendanceV2ShiftCheckTime> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<AttendanceV2ShiftCheckTime> timeList) {
        this.timeList = timeList;
    }
}
