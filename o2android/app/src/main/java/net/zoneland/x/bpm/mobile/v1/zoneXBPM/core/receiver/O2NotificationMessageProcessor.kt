package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.receiver

import android.content.Context
import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.MessageTypeEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message.ReadMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message.TaskMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.NotificationUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancy on 2017/6/7.
 */


class O2NotificationMessageProcessor {

    fun processMessage(context: Context, type: String, originJson: String) {
        val caseType = MessageTypeEnum.caseType(type)
        if (caseType < 0) {
            XLog.error("错误的消息类型,type: $type")
            return
        }
        when (caseType) {
            0 -> attendanceMessage(context, originJson)
            1 -> yunpanFileMessage(context, originJson)
            2 -> meetingMessage(context, originJson)
            3 -> okrCenterMessage(context, originJson)
            4 -> okrWorkMessage(context, originJson)
            5 -> okrReportMessage(context, originJson)
            6 -> readMessage(context, originJson)
            7 -> reviewMessage(context, originJson)
            8 -> taskMessage(context, originJson)
            else -> XLog.error("错误的消息类型， type:$type ")
        }
    }

    private fun okrCenterMessage(context: Context, originJson: String) {


    }

    private fun okrWorkMessage(context: Context, originJson: String) {


    }

    private fun okrReportMessage(context: Context, originJson: String) {


    }

    private fun readMessage(context: Context, originJson: String) {
        XLog.debug("待阅消息处理。。。。。$originJson")
        val readMessage =  O2SDKManager.instance().gson.fromJson(originJson, ReadMessage::class.java)
        if (readMessage == null || TextUtils.isEmpty(readMessage.read)) {
            XLog.error("待阅为空，无法查询待阅信息，通知不成功！")
            return
        }
        RetrofitClient.instance()
                .processAssembleSurfaceServiceAPI()
                .getRead(readMessage.read)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext { response ->
                        val read = response.data
                        val content = "[${read.processName}] ${read.title}"
                        NotificationUtil.readNotification(context, content,
                                MessageTypeEnum.getTitle(readMessage.type) + O2.NOTIFICATION_STRING,
                                read.title,
                                read.id,
                                read.work)
                    }
                    onError { e, _ ->
                        XLog.error("get read error", e)
                    }
                }
    }

    private fun reviewMessage(context: Context, originJson: String) {


    }

    private fun taskMessage(context: Context, originJson: String) {
        XLog.debug("待办消息处理。。。。。$originJson")
        val taskMessage = O2SDKManager.instance().gson.fromJson(originJson, TaskMessage::class.java)
        if (TextUtils.isEmpty(taskMessage.task)) {
            XLog.error("待办ID为空，无法查询待办信息，通知不成功！")
            return
        }
        RetrofitClient.instance()
                .processAssembleSurfaceServiceAPI()
                .getTask(taskMessage.task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext { response ->
                        val message = "[${response.data.processName}] ${response.data.title}"
                        NotificationUtil.taskNotification(context, message,
                                MessageTypeEnum.getTitle(taskMessage.type) + O2.NOTIFICATION_STRING, response.data.title, taskMessage.work)
                    }
                    onError { e, isNetworkError ->
                        XLog.error("待办消息异常", e)
                    }
                }

    }

    private fun meetingMessage(context: Context, originJson: String) {
        XLog.debug("会议消息处理。。。。。")

    }

    private fun yunpanFileMessage(context: Context, originJson: String) {
        XLog.debug("云盘消息处理。。。。。")
    }

    private fun attendanceMessage(context: Context, originJson: String) {
        XLog.debug("考勤消息处理。。。。。")
    }
}