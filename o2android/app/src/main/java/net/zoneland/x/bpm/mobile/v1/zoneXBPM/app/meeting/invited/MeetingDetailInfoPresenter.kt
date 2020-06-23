package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.invited

import android.widget.TextView
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.APIDistributeTypeEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingFileInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.O2FileDownloadHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by 73419 on 2017/8/4 0004.
 */
class MeetingDetailInfoPresenter : BasePresenterImpl<MeetingDetailInfoContract.View>(),
        MeetingDetailInfoContract.Presenter {

    override fun downloadMeetingFile(meetingFileInfoJson: MeetingFileInfoJson) {
        val path = FileExtensionHelper.getXBPMMEETINGAttachmentFileByName(meetingFileInfoJson.name)
        val downloadUrl = APIAddressHelper.instance()
                .getCommonDownloadUrl(APIDistributeTypeEnum.x_meeting_assemble_control, "jaxrs/attachment/${meetingFileInfoJson.id}/download/true")
        O2FileDownloadHelper.download(downloadUrl, path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.downloadAttachmentSuccess(File(path))
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                    }
                }

    }

    override fun asyncLoadPersonName(nameTv: TextView, id: String) {
        getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
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

    override fun asyncLoadRoomName(roomTv: TextView, room: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.getRoomById(room).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { info ->
                        roomTv.text = info.name
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                    })

        }
    }
}