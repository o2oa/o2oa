package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.edit

import android.widget.TextView
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class MeetingEditPresenter : BasePresenterImpl<MeetingEditContract.View>(), MeetingEditContract.Presenter {

    override fun asyncLoadPersonName(nameTv: TextView, id: String) {
        getOrganizationAssembleControlApi(mView?.getContext())?.let { service->
                    service.person(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { person ->
                        XLog.debug("person id:${person.id}, name:${person.name}")
                        val tag = nameTv.tag as String
                        if (id == tag) {
                            nameTv.text = person.name
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("查询个人信息失败, id:$id", e)
                    })
        }
    }

    override fun deleteMeeting(id: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.deleteMeeting(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { id ->
                        XLog.debug("delete meeting success id：$id")
                        mView?.deleteMeetingSuccess()
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        mView?.onError("取消会议失败，${e.message}")
                    })
        }
    }

    override fun updateMeetingInfo(meeting: MeetingInfoJson) {
            val json = O2SDKManager.instance().gson.toJson(meeting)
            XLog.debug("meeting:" + json)
            val body = RequestBody.create(MediaType.parse("text/json"), json)
            getMeetingAssembleControlService(mView?.getContext())?.let { service ->
                service.updateMeeting(body, meeting.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { id ->
                        XLog.debug("update meeting success id：$id")
                        mView?.updateMeetingSuccess()
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        mView?.onError("修改会议失败，${e.message}")
                    })
        }
    }

    override fun saveMeetingFile(fileId : String,meetingId : String) {
            val file = File(fileId)
            val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
            getMeetingAssembleControlService(mView?.getContext())?.let { service ->
                service.saveMeetingFile(body, meetingId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        XLog.debug("save meeting success id：")
                        mView?.saveMeetingFileSuccess(file.name,response.data.id)
                    }, { e ->
                        XLog.error("", e)
                        mView?.onError("上传会议材料失败，${e.message}")
                    })
        }
    }

    override fun deleteMeetingFile(fileId: String, position: Int) {
        XLog.debug(fileId)
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.deleteMeetingFile(fileId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { _ ->
                        mView?.deleteMeetingFile(position)
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        mView?.onError("删除会议材料失败，${e.message}")
                    })
        }
    }
}
