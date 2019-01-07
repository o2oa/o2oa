package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

/**
 * 通知消息
 * 小分类
 * Created by FancyLou on 2016/8/10.
 */
public enum MessageTypeEnum {

    ATTENDANCEAPPEALACCEPT("attendanceAppealAccept", "考勤申诉通过审核", 0),
    ATTENDANCEAPPEALCANCEL("attendanceAppealCancel", "考勤", 0),
    ATTENDANCEAPPEALINVITE("attendanceAppealInvite", "收到需要审核的考勤", 0),
    ATTENDANCEAPPEALREJECT("attendanceAppealReject", "考勤申诉未通过审核", 0),
    FILEMODIFY("fileModify", "云文件修改", 1),
    FILESHARE("fileShare", "云文件分享", 1),
    MEETINGACCEPT("meetingAccept", "接收会议邀请", 2),
    MEETINGCANCEL("meetingCancel", "会议取消", 2),
    MEETINGINVITE("meetingInvite", "会议邀请", 2),
    MEETINGREJECT("meetingReject", "拒绝会议邀请", 2),
    OKRCENTERWORKDEPLOYACCEPT("okrCenterWorkDeployAccept", "okrCenterWorkDeployAccept", 3),
    OKRWORKDELETEDACCEPT("okrWorkDeletedAccept", "okrWorkDeletedAccept", 4),
    OKRWORKDEPLOYACCEPT("okrWorkDeployAccept", "okrWorkDeployAccept", 4),
    OKRWORKGETACCEPT("okrWorkGetAccept", "okrWorkGetAccept", 4),
    OKRWORKREPORTDELETEDACCEPT("okrWorkReportDeletedAccept", "okrWorkReportDeletedAccept", 5),
    READ("read", "待阅", 6),
    REVIEW("review", "review", 7),
    TASK("task", "待办", 8);


    private final String key;
    private final String name;
    private final int typeCase;


    MessageTypeEnum(String key, String name, int typeCase) {
        this.name = name;
        this.key = key;
        this.typeCase = typeCase;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public int getTypeCase() {
        return typeCase;
    }

    /**
     * 解析分类
     * @param key
     * @return
     */
    public static int caseType(String key) {
        for (MessageTypeEnum en : MessageTypeEnum.values()) {
            if (en.getKey().equals(key)) {
                return en.getTypeCase();
            }
        }
        return -1;
    }

    /**
     * 获取中文名称
     * @param key
     * @return
     */
    public static String getTitle(String key) {
        for (MessageTypeEnum en : MessageTypeEnum.values()) {
            if (en.getKey().equals(key)) {
                return en.getName();
            }
        }
        return "";
    }
}
