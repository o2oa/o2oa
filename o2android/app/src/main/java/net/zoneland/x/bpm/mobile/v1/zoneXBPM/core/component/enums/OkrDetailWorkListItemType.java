package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

/**
 * 具体工作
 * Created by FancyLou on 2016/10/10.
 */

public enum OkrDetailWorkListItemType {

    main(0), //主工作
    header(1), //包含子工作的主工作
    sub(2);//子工作


    private final int value;

    OkrDetailWorkListItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
