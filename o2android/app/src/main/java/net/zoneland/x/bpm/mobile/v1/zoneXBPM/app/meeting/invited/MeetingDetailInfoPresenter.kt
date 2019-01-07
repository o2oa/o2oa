package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.invited

import android.widget.TextView
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingFileInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
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
        doAsync {
            try {
                val file = File(path)
                if (!file.exists()) {
                    val call = RetrofitClient.instance().meetingAssembleControlApi()
                            .downloadMeetingFile(meetingFileInfoJson.id)
                    SDCardHelper.generateNewFile(path)
                    val responseBody = call.execute()
                    val headerDisposition = responseBody.headers().get("Content-Disposition")
                    XLog.debug("header disposition: $headerDisposition")
                    val dataInput = DataInputStream(responseBody.body()?.byteStream())
                    val fileOut = DataOutputStream(FileOutputStream(file))
                    val buffer = ByteArray(4096)
                    var count = 0
                    do {
                        count = dataInput.read(buffer)
                        if (count > 0) {
                            fileOut.write(buffer, 0, count)
                        }
                    } while (count > 0)
                    fileOut.close()
                    dataInput.close()
                }
                uiThread {
                    mView?.downloadAttachmentSuccess(file)
                }
            } catch (e: Exception) {
                XLog.error("下载附件失败！", e)
                if (File(path).exists()) {
                    File(path).delete()
                }
            }

            /*val call = RetrofitClient.instance(it.getContext()).meetingAssembleControlApi()
                    .downloadMeetingFile(meetingFileInfoJson.id)
                try {
                    SDCardHelper.generateNewFile(path)
                    val responseBody = call.execute()
                    val headerDisposition = responseBody.headers().get("Content-Disposition")
                    XLog.debug("header disposition: $headerDisposition")
                    val dataInput = DataInputStream(responseBody.body().byteStream())
                    val fileOut = DataOutputStream(FileOutputStream(file))
                    val buffer = ByteArray(4096)
                    var count = 0
                    do {
                        count = dataInput.read(buffer)
                        if (count > 0) {
                            fileOut.write(buffer, 0, count)
                        }
                    } while (count > 0)
                    fileOut.close()
                    dataInput.close()
                } catch (e: Exception) {
                    XLog.error("下载附件失败！", e)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            Observable.create(Observable.OnSubscribe<File> { t ->
                val thisFile = File(path)
                if (file.exists()) {
                    t?.onNext(thisFile)
                } else {
                    t?.onError(Exception("附件下载异常，找不到文件！"))
                }
                t?.onCompleted()
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ file -> it.downloadAttachmentSuccess(file) }, { e ->
                        XToast.toastShort(it.getContext(), "下载附件失败，${e.message}")
                        XLog.error("",e)
                        it.downloadAttachmentSuccess(null)
                    })*/
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