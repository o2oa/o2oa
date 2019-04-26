package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * Created by FancyLou on 2016/4/27.
 */
public enum ApplicationEnum {

    TASK("task", "待办", R.mipmap.ic_todo_task),
    TASKCOMPLETED("taskcompleted", "已办", R.mipmap.ic_todo_task_completed),
    READ("read", "待阅", R.mipmap.ic_todo_read),
    READCOMPLETED("readcompleted", "已阅", R.mipmap.ic_todo_read_completed),
    MEETING("meeting", "会议管理", R.mipmap.app_meeting),
    YUNPAN("yunpan", "云盘", R.mipmap.app_yunpan),
//    OKR("okr", "工作管理", R.mipmap.app_okr),
    BBS("bbs", "论坛", R.mipmap.app_bbs),
    CMS("cms", "信息中心", R.mipmap.app_cms),
    ATTENDANCE("attendance", "考勤打卡", R.mipmap.app_attendance),
    O2AI("o2ai", "语音助手", R.mipmap.app_o2_ai),
    CALENDAR("calendar", "日程安排", R.mipmap.app_calendar),
    MindMap("mindMap", "脑图", R.mipmap.app_mind_map);



    private final String key;
    private final String appName;
    private final int iconResId;

    ApplicationEnum(String key, String appName, int iconResId){
        this.key = key;
        this.appName = appName;
        this.iconResId = iconResId;
    }

    public static ApplicationEnum getApplicationByKey(String key) {
        for (ApplicationEnum applicationEnum : ApplicationEnum.values()) {
            if (applicationEnum.key.equals(key)) {
                return applicationEnum;
            }
        }
        return null;
    }

    public static Boolean isNativeApplication(String key) {
        for (ApplicationEnum applicationEnum : ApplicationEnum.values()) {
            if (applicationEnum.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public String getKey() {
        return key;
    }

    public String getAppName() {
        return appName;
    }

    public int getIconResId() {
        return iconResId;
    }
}
