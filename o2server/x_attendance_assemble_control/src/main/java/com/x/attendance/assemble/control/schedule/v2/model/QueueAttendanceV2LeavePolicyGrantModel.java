package com.x.attendance.assemble.control.schedule.v2.model;

import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;

public class QueueAttendanceV2LeavePolicyGrantModel {

    private AttendanceV2LeavePolicy policy;

    private Boolean isImmediately;

    public AttendanceV2LeavePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(AttendanceV2LeavePolicy policy) {
        this.policy = policy;
    }

    public Boolean getIsImmediately() {
        return isImmediately;
    }

    public void setIsImmediately(Boolean isImmediately) {
        this.isImmediately = isImmediately;
    }
}
