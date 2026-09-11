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


    public enum ExpireTypeEnum {
        THIS_YEAR("THIS_YEAR"),
        NEXT_YEAR("NEXT_YEAR"),
        AFTER_GRANT("AFTER_GRANT");

        private String value;

        ExpireTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }


        public static boolean isValidateKey(String key) {
            return ExpireTypeEnum.THIS_YEAR.value.equals(key)
                   || ExpireTypeEnum.NEXT_YEAR.value.equals(key)
                   || ExpireTypeEnum.AFTER_GRANT.value.equals(key);
        }
    }
}
