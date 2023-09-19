package com.x.attendance.entity.v2;

import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * 上下班时间对象
 * Created by fancyLou on 2023/1/30.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2ShiftCheckTime  extends GsonPropertyObject {

    private static final long serialVersionUID = -6411723100579845550L;
    private String onDutyTime; // 上班打卡时间 如 09:00
    private String onDutyTimeBeforeLimit; // onDutyTime前打卡限制
    private String onDutyTimeAfterLimit; // onDutyTime后打卡限制
    private String offDutyTime; // 下班打卡时间 如 18:00
    private String offDutyTimeBeforeLimit; // offDutyTime前打卡限制
    private String offDutyTimeAfterLimit; // offDutyTime后打卡限制
    private Boolean offDutyNextDay = false; // 下班打卡是否是次日 下班打卡可跨天


    
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

	public Boolean getOffDutyNextDay() {
		return offDutyNextDay;
	}

	public void setOffDutyNextDay(Boolean offDutyNextDay) {
		this.offDutyNextDay = offDutyNextDay;
	}

	 
}
