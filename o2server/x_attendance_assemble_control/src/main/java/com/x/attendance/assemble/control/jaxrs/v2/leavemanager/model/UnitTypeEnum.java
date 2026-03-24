package com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model;

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
