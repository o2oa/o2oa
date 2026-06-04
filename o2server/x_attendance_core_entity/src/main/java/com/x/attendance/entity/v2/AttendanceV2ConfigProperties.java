package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2ConfigProperties  extends JsonProperties {

    private static final long serialVersionUID = -8203944978565083706L;
    

    @FieldDescribe("假期管理默认数据是否已经初始化.")
    private Boolean leaveTypeInitialized = false;
    @FieldDescribe("下班后提醒分钟数.")
    private Integer checkInAlertOffDutyAfterMinutes = 10;
    @FieldDescribe("上班前提醒分钟数.")
    private Integer checkInAlertOnDutyBeforeMinutes = 10;


    public Integer getCheckInAlertOffDutyAfterMinutes() {
        return checkInAlertOffDutyAfterMinutes;
    }

    public void setCheckInAlertOffDutyAfterMinutes(Integer checkInAlertOffDutyAfterMinutes) {
        this.checkInAlertOffDutyAfterMinutes = checkInAlertOffDutyAfterMinutes;
    }

    public Integer getCheckInAlertOnDutyBeforeMinutes() {
        return checkInAlertOnDutyBeforeMinutes;
    }

    public void setCheckInAlertOnDutyBeforeMinutes(Integer checkInAlertOnDutyBeforeMinutes) {
        this.checkInAlertOnDutyBeforeMinutes = checkInAlertOnDutyBeforeMinutes;
    }

    public Boolean getLeaveTypeInitialized() {
        return leaveTypeInitialized;
    }


    public void setLeaveTypeInitialized(Boolean leaveTypeInitialized) {
        this.leaveTypeInitialized = leaveTypeInitialized;
    }

    
}
