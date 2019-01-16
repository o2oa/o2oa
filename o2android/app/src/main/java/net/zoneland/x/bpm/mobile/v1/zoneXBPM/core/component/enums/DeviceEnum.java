package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * 会议室设备
 * Created by FancyLou on 2016/3/31.
 */
public enum DeviceEnum {

    PROJECTOR("projector", "投影仪", R.mipmap.icon__projector),
    BOARD("board", "白板", R.mipmap.icon__board),
    TVSET("tvset", "电视", R.mipmap.icon__tv),
    TV("tv", "视频会议", R.mipmap.icon__video),
    CAMERA("camera", "摄像头", R.mipmap.icon__camera),
    WIFI("wifi", "WIFI", R.mipmap.icon__wifi),
    PHONE("phone", "电话会议", R.mipmap.icon__phone);

    private final String key;
    private final String name;
    private final int res;

    DeviceEnum(String key, String name, int res) {
        this.key = key;
        this.name = name;
        this.res = res;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getRes() {
        return res;
    }

    public static int getResourceByKey(String key) {
        for (DeviceEnum en :
                DeviceEnum.values()) {
            if (en.getKey().equals(key)) {
                return en.getRes();
            }
        }
        return R.mipmap.icon_room_board;
    }

    public static String getDeviceNameByKey(String key) {
        for (DeviceEnum en :
                DeviceEnum.values()) {
            if (en.getKey().equals(key)) {
                return en.getName();
            }
        }
        return "";
    }
}
