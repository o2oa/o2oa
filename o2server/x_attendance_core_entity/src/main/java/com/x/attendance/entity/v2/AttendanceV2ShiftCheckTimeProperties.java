package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by fancyLou on 2023/1/30.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2ShiftCheckTimeProperties extends JsonProperties {


    private static final long serialVersionUID = 6450503259865573434L;
    @FieldDescribe("班次上下班打卡时间")
    private List<AttendanceV2ShiftCheckTime> timeList;


    public List<AttendanceV2ShiftCheckTime> getTimeList() {
        if (timeList == null || timeList.isEmpty()) {
            return new ArrayList<>();
        }
        return timeList.stream().sorted(Comparator.comparing(AttendanceV2ShiftCheckTime::getOnDutyTime)).collect(Collectors.toList());
    }

    public void setTimeList(List<AttendanceV2ShiftCheckTime> timeList) {
        this.timeList = timeList;
    }
}
