package com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model;

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
