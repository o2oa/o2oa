package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2ConfigProperties  extends JsonProperties {

    private static final long serialVersionUID = -8203944978565083706L;
    

    @FieldDescribe("假期管理默认数据是否已经初始化.")
    private Boolean leaveTypeInitialized = false;


    public Boolean getLeaveTypeInitialized() {
        return leaveTypeInitialized;
    }


    public void setLeaveTypeInitialized(Boolean leaveTypeInitialized) {
        this.leaveTypeInitialized = leaveTypeInitialized;
    }

    
}
