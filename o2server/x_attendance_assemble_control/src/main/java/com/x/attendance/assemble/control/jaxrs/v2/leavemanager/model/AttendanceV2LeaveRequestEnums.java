package com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model;

public class AttendanceV2LeaveRequestEnums {
    
    public enum LeaveRequestStatusEnum {
        APPLYING("APPLYING"),
        REJECTED("REJECTED"),
        CANCELLED("CANCELLED");

        private String value;
 
        LeaveRequestStatusEnum(String value) {
            this.value = value;
        }   
        public String getValue() {
            return value;
        }
    }
}
