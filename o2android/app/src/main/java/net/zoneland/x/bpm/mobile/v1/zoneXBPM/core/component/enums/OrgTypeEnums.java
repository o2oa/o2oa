package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

/**
 * Created by FancyLou on 2015/10/16.
 */
public enum OrgTypeEnums {

    COMPANY("company", "公司"),
    DEPARTMENT("department", "部门");

    private final String key;
    private final String name;
    OrgTypeEnums(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
