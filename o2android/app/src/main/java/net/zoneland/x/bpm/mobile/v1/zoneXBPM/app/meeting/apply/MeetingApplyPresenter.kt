package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.apply

import android.widget.TextView
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.Serializable


class MeetingApplyPresenter : BasePresenterImpl<MeetingApplyContract.View>(), MeetingApplyContract.Presenter {

    override fun asyncLoadPersonName(tv: TextView, id: String) {
        getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
            service.person(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { person ->
                        XLog.debug("person id:${person.id}, name:${person.name}")
                        val tag = tv.tag as String
                        if (person.id == tag) {
                            tv.text = person.name
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("查询个人信息失败, id:$id", e)
                    })
        }
    }

    override fun saveMeeting(info: MeetingInfoJson, meetingFile: String) {
        val json = O2SDKManager.instance().gson.toJson(info)
        XLog.debug("meeting:" + json)
        val body = RequestBody.create(MediaType.parse("text/json"), json)
        var meetingId = ""
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.saveMeeting(body)
                    .subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        meetingId = response.data.id
                        val file = File(meetingFile)
                        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
                        val fileBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
                        service.saveMeetingFile(fileBody, meetingId)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        val fileId = response.data.id
                        XLog.debug("save meeting success id：$meetingId")
                        XLog.debug("save meeting file id：$fileId")
                        mView?.saveMeetingSuccess(meetingId, fileId)
                    }, { e ->
                        XLog.error("", e)
                        mView?.doMeetingFail("申请会议失败，${e.message}")
                    })
        }
    }

    override fun saveMeetingFile(fileId: String, meetingId: String) {
            val file = File(fileId)
            val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
            getMeetingAssembleControlService(mView?.getContext())?.let { service->
                service.saveMeetingFile(body, meetingId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        XLog.debug("save meeting success id：")
                        mView?.saveMeetingFileSuccess(response.data.id)
                    }, { e ->
                        XLog.error("", e)
                        mView?.doMeetingFail("上传会议材料失败，${e.message}")
                    })
        }
    }

    override fun saveMeetingNoFile(info: MeetingInfoJson) {
            val json = O2SDKManager.instance().gson.toJson(info)
            XLog.debug("meeting:" + json)
            val body = RequestBody.create(MediaType.parse("text/json"), json)
            getMeetingAssembleControlService(mView?.getContext())?.let { service->
                service.saveMeeting(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ ->
                        XLog.debug("save meeting success id：")
                        mView?.updateMeetingSuccess()
                    }, { e ->
                        XLog.error("", e)
                        mView?.doMeetingFail("申请会议失败，${e.message}")
                    })
        }
    }

    override fun updateMeetingInfo(meeting: MeetingInfoJson, meetingId: String) {
            val json = O2SDKManager.instance().gson.toJson(meeting)
            XLog.debug("meeting:" + json)
            val body = RequestBody.create(MediaType.parse("text/json"), json)
            getMeetingAssembleControlService(mView?.getContext())?.let { service->
                service.updateMeeting(body, meetingId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { id ->
                        XLog.debug("update meeting success id：$id")
                        mView?.updateMeetingSuccess()
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        mView?.doMeetingFail("修改会议失败，${e.message}")
                    })
        }
    }

    override fun deleteMeetingFile(fileId: String, position: Int) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.deleteMeetingFile(fileId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { _ ->
                        mView?.deleteMeetingFile(fileId, position)
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        mView?.doMeetingFail("修改会议失败，${e.message}")
                    })
        }
    }

    class MeetingFile(
            var meetingId: String = "",
            var idData: ApiResponse<IdData> = ApiResponse()
    ) : Serializable
}
