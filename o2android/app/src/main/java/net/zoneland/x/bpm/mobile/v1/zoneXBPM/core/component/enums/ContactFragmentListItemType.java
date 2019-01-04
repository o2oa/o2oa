package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

/**
 * Created by FancyLou on 2016/10/27.
 */

public enum ContactFragmentListItemType {

    header(0),//头部
    department(1),//我的部门
    company(2),//我的公司
    group(3),//我的群组
    usually(4);//我的常用联系人

    private final int type;

    ContactFragmentListItemType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
