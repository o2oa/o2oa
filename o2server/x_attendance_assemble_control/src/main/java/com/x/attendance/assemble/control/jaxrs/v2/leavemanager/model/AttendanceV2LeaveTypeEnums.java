package com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model;

public class AttendanceV2LeaveTypeEnums {

    public enum QuotaTypeEnum {
        QUOTA("QUOTA"),
        UNLIMITED("UNLIMITED");

        private String value;

        QuotaTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum UnitTypeEnum {
        DAY("DAY"),
        HOUR("HOUR");

        private String value;

        UnitTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
