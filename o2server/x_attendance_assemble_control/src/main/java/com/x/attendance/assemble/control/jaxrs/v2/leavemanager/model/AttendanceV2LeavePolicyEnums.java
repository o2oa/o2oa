package com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model;

public class AttendanceV2LeavePolicyEnums {

    public enum GrantScopeTypeEnum {
        ALL("ALL"),
        DEPARTMENT("DEPARTMENT");

        private String value;

        GrantScopeTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum GrantTypeEnum {
        YEARLY("YEARLY"),
        MONTHLY("MONTHLY"),
        ONE_TIME("ONE_TIME");

        private String value;

        GrantTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ExpireTypeEnum {
        NEVER("NEVER"),
        FIXED("FIXED"),
        RELATIVE("RELATIVE");

        private String value;

        ExpireTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
