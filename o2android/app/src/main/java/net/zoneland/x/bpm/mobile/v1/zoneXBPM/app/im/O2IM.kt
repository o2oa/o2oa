package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R


object O2IM {

    const val IM_Message_Receiver_Action = "net.o2oa.android.im.message"
    const val IM_Message_Receiver_name = "IM_Message_Receiver_name"

    const val conversation_type_single = "single"
    const val conversation_type_group = "group"

    val im_emoji_hashMap = hashMapOf<String, Int>(
            "[01]" to R.mipmap.im_emotion_01,
            "[02]" to R.mipmap.im_emotion_02,
            "[03]" to R.mipmap.im_emotion_03,
            "[04]" to R.mipmap.im_emotion_04,
            "[05]" to R.mipmap.im_emotion_05,
            "[06]" to R.mipmap.im_emotion_06,
            "[07]" to R.mipmap.im_emotion_07,
            "[08]" to R.mipmap.im_emotion_08,
            "[09]" to R.mipmap.im_emotion_09,
            "[10]" to R.mipmap.im_emotion_10,
            "[11]" to R.mipmap.im_emotion_11,
            "[12]" to R.mipmap.im_emotion_12,
            "[13]" to R.mipmap.im_emotion_13,
            "[14]" to R.mipmap.im_emotion_14,
            "[15]" to R.mipmap.im_emotion_15,
            "[16]" to R.mipmap.im_emotion_16,
            "[17]" to R.mipmap.im_emotion_17,
            "[18]" to R.mipmap.im_emotion_18,
            "[19]" to R.mipmap.im_emotion_19,
            "[20]" to R.mipmap.im_emotion_20,
            "[21]" to R.mipmap.im_emotion_21,
            "[22]" to R.mipmap.im_emotion_22,
            "[23]" to R.mipmap.im_emotion_23,
            "[24]" to R.mipmap.im_emotion_24,
            "[25]" to R.mipmap.im_emotion_25,
            "[26]" to R.mipmap.im_emotion_26,
            "[27]" to R.mipmap.im_emotion_27,
            "[28]" to R.mipmap.im_emotion_28,
            "[29]" to R.mipmap.im_emotion_29,
            "[30]" to R.mipmap.im_emotion_30,
            "[31]" to R.mipmap.im_emotion_31,
            "[32]" to R.mipmap.im_emotion_32,
            "[33]" to R.mipmap.im_emotion_33,
            "[34]" to R.mipmap.im_emotion_34,
            "[35]" to R.mipmap.im_emotion_35,
            "[36]" to R.mipmap.im_emotion_36,
            "[37]" to R.mipmap.im_emotion_37,
            "[38]" to R.mipmap.im_emotion_38,
            "[39]" to R.mipmap.im_emotion_39,
            "[40]" to R.mipmap.im_emotion_40,
            "[41]" to R.mipmap.im_emotion_41,
            "[42]" to R.mipmap.im_emotion_42,
            "[43]" to R.mipmap.im_emotion_43,
            "[44]" to R.mipmap.im_emotion_44,
            "[45]" to R.mipmap.im_emotion_45,
            "[46]" to R.mipmap.im_emotion_46,
            "[47]" to R.mipmap.im_emotion_47,
            "[48]" to R.mipmap.im_emotion_48,
            "[49]" to R.mipmap.im_emotion_49,
            "[50]" to R.mipmap.im_emotion_50,
            "[51]" to R.mipmap.im_emotion_51,
            "[52]" to R.mipmap.im_emotion_52,
            "[53]" to R.mipmap.im_emotion_53,
            "[54]" to R.mipmap.im_emotion_54,
            "[55]" to R.mipmap.im_emotion_55,
            "[56]" to R.mipmap.im_emotion_56,
            "[57]" to R.mipmap.im_emotion_57,
            "[58]" to R.mipmap.im_emotion_58,
            "[59]" to R.mipmap.im_emotion_59,
            "[60]" to R.mipmap.im_emotion_60,
            "[61]" to R.mipmap.im_emotion_61,
            "[62]" to R.mipmap.im_emotion_62,
            "[63]" to R.mipmap.im_emotion_63,
            "[64]" to R.mipmap.im_emotion_64,
            "[65]" to R.mipmap.im_emotion_65,
            "[66]" to R.mipmap.im_emotion_66,
            "[67]" to R.mipmap.im_emotion_67,
            "[68]" to R.mipmap.im_emotion_68,
            "[69]" to R.mipmap.im_emotion_69,
            "[70]" to R.mipmap.im_emotion_70,
            "[71]" to R.mipmap.im_emotion_71,
            "[72]" to R.mipmap.im_emotion_72,
            "[73]" to R.mipmap.im_emotion_73,
            "[74]" to R.mipmap.im_emotion_74,
            "[75]" to R.mipmap.im_emotion_75,
            "[76]" to R.mipmap.im_emotion_76,
            "[77]" to R.mipmap.im_emotion_77,
            "[78]" to R.mipmap.im_emotion_78,
            "[79]" to R.mipmap.im_emotion_79,
            "[80]" to R.mipmap.im_emotion_80,
            "[81]" to R.mipmap.im_emotion_81,
            "[82]" to R.mipmap.im_emotion_82,
            "[83]" to R.mipmap.im_emotion_83,
            "[84]" to R.mipmap.im_emotion_84,
            "[85]" to R.mipmap.im_emotion_85,
            "[86]" to R.mipmap.im_emotion_86,
            "[87]" to R.mipmap.im_emotion_87
    )

    fun emojiResId(key: String) :Int {
        return im_emoji_hashMap[key] ?: R.mipmap.im_emotion_01
    }

    enum class AudioPlayState {
        playing,
        idle
    }

    //instant message type

    /**
     * 流程类型
     */
    const val TYPE_APPLICATION_CREATE = "application_create"

    const val TYPE_APPLICATION_UPDATE = "application_update"

    const val TYPE_APPLICATION_DELETE = "application_delete"

    const val TYPE_PROCESS_CREATE = "process_create"

    const val TYPE_PROCESS_UPDATE = "process_update"

    const val TYPE_PROCESS_DELETE = "process_delete"

    /* 有新的工作通过消息节点 */
    const val TYPE_ACTIVITY_MESSAGE = "activity_message"

    const val TYPE_WORK_TO_WORKCOMPLETED = "work_to_workCompleted"

    const val TYPE_WORK_CREATE = "work_create"

    const val TYPE_WORK_DELETE = "work_delete"

    const val TYPE_WORKCOMPLETED_CREATE = "workCompleted_create"

    const val TYPE_WORKCOMPLETED_DELETE = "workCompleted_delete"

    const val TYPE_TASK_TO_TASKCOMPLETED = "task_to_taskCompleted"

    const val TYPE_TASK_CREATE = "task_create"

    const val TYPE_TASK_DELETE = "task_delete"

    const val TYPE_TASK_URGE = "task_urge"

    const val TYPE_TASK_EXPIRE = "task_expire"

    const val TYPE_TASK_PRESS = "task_press"

    const val TYPE_TASKCOMPLETED_CREATE = "taskCompleted_create"

    const val TYPE_TASKCOMPLETED_DELETE = "taskCompleted_delete"

    const val TYPE_READ_TO_READCOMPLETED = "read_to_readCompleted"

    const val TYPE_READ_CREATE = "read_create"

    const val TYPE_READ_DELETE = "read_delete"

    const val TYPE_READCOMPLETED_CREATE = "readCompleted_create"

    const val TYPE_READCOMPLETED_DELETE = "readCompleted_delete"

    const val TYPE_REVIEW_CREATE = "review_create"

    const val TYPE_REVIEW_DELETE = "review_delete"

    const val TYPE_ATTACHMENT_CREATE = "attachment_create"

    const val TYPE_ATTACHMENT_DELETE = "attachment_delete"

    const val TYPE_MEETING_INVITE = "meeting_invite"

    const val TYPE_MEETING_DELETE = "meeting_delete"

    const val TYPE_MEETING_ACCEPT = "meeting_accept"

    const val TYPE_MEETING_REJECT = "meeting_reject"

    const val TYPE_ATTACHMENT_SHARE = "attachment_share"

    const val TYPE_ATTACHMENT_SHARECANCEL = "attachment_shareCancel"

    const val TYPE_ATTACHMENT_EDITOR = "attachment_editor"

    const val TYPE_ATTACHMENT_EDITORCANCEL = "attachment_editorCancel"

    const val TYPE_ATTACHMENT_EDITORMODIFY = "attachment_editorModify"

    const val TYPE_CALENDAR_ALARM = "calendar_alarm"

    const val TYPE_CUSTOM_CREATE = "custom_create"

    const val TYPE_TEAMWORK_TASKCREATE = "teamwork_taskCreate"

    const val TYPE_TEAMWORK_TASKUPDATE = "teamwork_taskUpdate"

    const val TYPE_TEAMWORK_TASKDELETE = "teamwork_taskDelelte"

    const val TYPE_TEAMWORK_TASKOVERTIME = "teamwork_taskOvertime"

    const val TYPE_TEAMWORK_CHAT = "teamwork_taskChat"

    const val TYPE_CMS_PUBLISH = "cms_publish"

    const val TYPE_BBS_SUBJECTCREATE = "bbs_subjectCreate"

    const val TYPE_BBS_REPLYCREATE = "bbs_replyCreate"

    const val TYPE_MIND_FILESEND = "mind_fileSend"

    const val TYPE_MIND_FILESHARE = "mind_fileShare"

    const val TYPE_IM_CREATE = "im_create"

}