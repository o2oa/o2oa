package net.zoneland.x.bpm.mobile.v1.zoneXBPM

import android.content.Context
import android.content.Intent

/**
 * Created by fancy on 2017/6/5.
 */

object O2 {


    val O2_COLLECT_URL = "http://collect.o2oa.net:20080/o2_collect_assemble/"
//    val O2_DOWNLOAD_URL = "https://sample.o2oa.net/app/download.html"
    val O2_DOWNLOAD_URL = "https://app.o2oa.net/download/download.html"

    const val O2_Process_start_mode_draft = "draft"

    /**
     * 项目文件存储路径
     * 根目录
     */
    val BASE_FILE_PATH = "ZONE_XBPM"

    val BASE_APP_ALIAS = "XBPM"

    val AVATAR_TMP_FOLDER = "avatar_temp"

    val BASE_TMP_FOLDER = "temp"

    val BASE_IM_RECI_FOLDER = "im"

    val SKIN_FILE_FOLDER = "skin"

    //流程附件目录
    val BASE_WORK_ATTACH_FOLDER = "process"

    val BASE_BBS_ATTACH_FOLDER = "bbs"

    val BASE_MEETING_ATTACH_FOLDER = "meeting"

    val BASE_CMS_ATTACH_FOLDER = "cms"

    val BASE_LOG_FOLDER = "log"

    val HTTP_CACHE_FOLDER = "ok_http_cache"

    val FIRST_PAGE_TAG = "(0)"

    /**
     * 图片后缀
     */
    val IMAGE_SUFFIX_JPG = ".jpg"
    val IMAGE_SUFFIX_PNG = ".png"

    /**
     * 默认分页 每页显示数据
     */
    val DEFAULT_PAGE_NUMBER = 15


    /**
     * 论坛内容中的图片展现宽度不超过720
     */
    val BBS_IMAGE_MAX_WIDTH = 720

    /**
     * 清除临时文件的任务id
     */
    val O2_CLEAR_TEMP_FILE_JOB_ID = 1024
    val O2_COLLECT_LOG_JOB_ID = 1025


    /**
     * 消息id
     */
    val NOTIFYID = 19840523


    val NOTIFICATION_STRING = "通知"

    /**
     * choosePicture activity requestCode
     */
    val CHOOSE_PICTURE_REQUEST_CODE = 1110


    val SETTING_MESSAGE_NOTICE_KEY = "SETTING_MESSAGE_NOTICE_KEY"//消息是否提醒 默认true
    val SETTING_MESSAGE_NOTICE_SOUND_KEY = "SETTING_MESSAGE_NOTICE_SOUND_KEY"//消息提醒声音是否开启 默认true
    val SETTING_MESSAGE_NOTICE_VIBRATE_KEY = "SETTING_MESSAGE_NOTICE_VIBRATE_KEY"//消息提醒震动是否开启 默认true


    /**
     * 热图
     */
    val SETTING_HOT_PICTURE_DEFAULT_SHOW_NUMBER = 5//首页广告热图默认显示数量
    val DEFAULT_HOT_PICTURE_ID = "xbpm_hot_picture"//默认广告热图的id


    val DEVICE_TYPE = "android"

    val TOKEN_TYPE_ANONYMOUS = "anonymous"

    /**
     * 考勤审核人确定方式 如果是这个值 申诉的时候就需要选择身份
     */
    val ATTENDANCE_SETTING_AUDITOR_TYPE_NEED_CHOOSE_IDENTITY = "所属部门职务"
    /**
     * 申诉审批状态启用开关 启用
     */
    val ATTENDANCE_SETTING_APPEAL_ABLE_TRUE = "true"


    //////////////////////////////////SharedPreferences KEY /////////////////////////////////////////////////

    val PREFERENCE_FILE = "API_DISTRIBUTE_FILE"
    val SECURITY_PREFERENCE_FILE = "API_DIST_FILE_SECURITY"
    val SECURITY_IS_UPDATE = "SECURITY_IS_UPDATE"
    val PRE_ASSEMBLESJSON_KEY = "ASSEMBLESJSON_KEY"
    val PRE_WEBSERVERJSON_KEY = "WEBSERVERJSON_KEY"
    val PRE_CENTER_HOST_KEY = "PRE_CENTER_HOST_KEY"//中心服务器地址
    val PRE_CENTER_HTTP_PROTOCOL_KEY = "PRE_CENTER_HTTP_PROTOCOL_KEY"//中心服务器 HTTP协议
    val PRE_CENTER_CONTEXT_KEY = "PRE_CENTER_CONTEXT_KEY"//中心服务器上下文
    val PRE_CENTER_PORT_KEY = "PRE_CENTER_PORT_KEY"//中心服务器端口
    val PRE_CENTER_URL_KEY = "PRE_CENTER_URL_KEY"
    val PRE_BIND_UNIT_ID_KEY = "PRE_BIND_UNIT_ID_KEY"//绑定的公司的id unitId
    val PRE_BIND_UNIT_KEY = "PRE_BIND_UNIT_KEY"//绑定的公司 unitName
    val PRE_BIND_PHONE_KEY = "PRE_BIND_PHONE_KEY"//绑定手机号码
    val PRE_BIND_PHONE_TOKEN_KEY = "PRE_BIND_PHONE_TOKEN_JPUSH_KEY"//绑定手机唯一编码 @date 2018-04-19 修改成极光推送

    val PRE_DEVICE_DPI_KEY = "PRE_DEVICE_DPI_KEY"//手机分辨率

    val PRE_LAUNCH_INTRODUCTION_KEY = "PRE_LAUNCH_INTRODUCTION_KEY"//启动页，首次安装介绍页面

    val PREFERENCE_CONSTANCE_FILE = "PREFERENCE_CONSTANCE_FILE"
    val PRE_UPDATE_REMIND_DAY = "PRE_UPDATE_REMIND_DAY"
    val PRE_GUIDE_KEY = "guide_key"
    val PRE_DEMO_ALERT_REMIND_DAY = "PRE_DEMO_ALERT_REMIND_DAY"

    val PRE_IS_FIRST = "IS_FIRST_LOGIN"

    val PRE_ATTENDANCE_VERSION_KEY = "PRE_ATTENDANCE_VERSION_KEY" //考勤版本兼容问题的key  1表示新版本 其他表示老版本 切换打卡页面使用

    val BUSINESS_TYPE_MESSAGE_CENTER = 0//信息中心
    val BUSINESS_TYPE_WORK_CENTER = 1//工作中心


    val SKIN_CHANGE_BROADCAST_ACTION_KEY = "net.zoneland.o2.skin.change"//改变皮肤广播的Action
    val SKIN_CHANGE_BROAD_CAST_KEY = "skin_change_broad_cast_key"//改变皮肤发送广播的key



    val O2_INDEX_OR_PORTAL = "O2_INDEX_OR_PORTAL"

    val O2_CLIENT = "oa"


}