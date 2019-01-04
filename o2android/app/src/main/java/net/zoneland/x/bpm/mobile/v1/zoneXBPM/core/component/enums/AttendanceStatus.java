package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * 考勤状态
 * Created by FancyLou on 2016/5/13.
 */
public enum AttendanceStatus {

    NORMAL("正常", R.color.z_pie_chart_normal),
    HOLIDAY("请假", R.color.z_pie_chart_holiday),
    LATE("迟到", R.color.z_pie_chart_late),
//    LEAVEEARLIY("早退"),
    APPEAL("申诉通过", R.color.z_pie_chart_appeal),
    ABSENT("缺勤", R.color.z_pie_chart_absent),
    ABNORMALDUTY("异常打卡", R.color.z_pie_chart_abnormalduty),
    LACKOFTIME("工时不足", R.color.z_pie_chart_lackoftime);

    private final String label;
    private final int color;

    AttendanceStatus(String label, int color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public int getColor() {
        return color;
    }
}
