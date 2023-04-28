package com.x.attendance.entity.v2;

import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * 上下班时间对象
 * Created by fancyLou on 2023/1/30.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2ShiftCheckTime  extends GsonPropertyObject {

    private String onDutyTime; // 上班打卡时间 如 09:00
    private String onDutyTimeBeforeLimit; // onDutyTime前打卡限制 如 00:30 表示可以在8:30-9:00之间打上班的卡
    private String onDutyTimeAfterLimit; // onDutyTime后打卡限制 如 00:30 表示可以在9:00-9:30之间打上班的卡
    private String offDutyTime; // 下班打卡时间 如 18:00
    private String offDutyTimeBeforeLimit; // offDutyTime前打卡限制 如 00:30 表示可以在17:30-18:00之间打下班的卡
    private String offDutyTimeAfterLimit; // offDutyTime后打卡限制 如 00:30 表示可以在18:00-18:30之间打下班的卡


    public String getOnDutyTime() {
        return onDutyTime;
    }

    public void setOnDutyTime(String onDutyTime) {
        this.onDutyTime = onDutyTime;
    }

    public String getOnDutyTimeBeforeLimit() {
        return onDutyTimeBeforeLimit;
    }

    public void setOnDutyTimeBeforeLimit(String onDutyTimeBeforeLimit) {
        this.onDutyTimeBeforeLimit = onDutyTimeBeforeLimit;
    }

    public String getOnDutyTimeAfterLimit() {
        return onDutyTimeAfterLimit;
    }

    public void setOnDutyTimeAfterLimit(String onDutyTimeAfterLimit) {
        this.onDutyTimeAfterLimit = onDutyTimeAfterLimit;
    }

    public String getOffDutyTime() {
        return offDutyTime;
    }

    public void setOffDutyTime(String offDutyTime) {
        this.offDutyTime = offDutyTime;
    }

    public String getOffDutyTimeBeforeLimit() {
        return offDutyTimeBeforeLimit;
    }

    public void setOffDutyTimeBeforeLimit(String offDutyTimeBeforeLimit) {
        this.offDutyTimeBeforeLimit = offDutyTimeBeforeLimit;
    }

    public String getOffDutyTimeAfterLimit() {
        return offDutyTimeAfterLimit;
    }

    public void setOffDutyTimeAfterLimit(String offDutyTimeAfterLimit) {
        this.offDutyTimeAfterLimit = offDutyTimeAfterLimit;
    }
}
