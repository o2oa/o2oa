package com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model;

public class AttendanceV2LeaveTransactionEnums {
    
    public enum BizTypeEnum {
        GRANT("GRANT"), // 发放
        USE("USE"), // 使用
        CANCEL("CANCEL"), // 取消
        EXPIRE("EXPIRE"), // 过期
        CARRYFORWARD("CARRYFORWARD"), // 结转
        ADJUST("ADJUST"); // 调整

        private String value;
 
        BizTypeEnum(String value) {
            this.value = value;
        }   
        public String getValue() {
            return value;
        }
    }
}
