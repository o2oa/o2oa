package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

/**
 * Created by FancyLou on 2016/11/28.
 */

public enum HotPictureApplicationEnum {

    BBS("BBS", "论坛"),
    CMS("CMS", "内容管理");

    private final String key;
    private final String name;

    HotPictureApplicationEnum(String key, String name){
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
