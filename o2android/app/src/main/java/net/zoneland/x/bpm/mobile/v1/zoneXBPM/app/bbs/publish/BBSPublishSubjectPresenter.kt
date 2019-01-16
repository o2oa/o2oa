package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.publish

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SectionInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectPublishFormJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ZoneUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class BBSPublishSubjectPresenter : BasePresenterImpl<BBSPublishSubjectContract.View>(), BBSPublishSubjectContract.Presenter {

    override fun querySectionById(sectionId: String) {
        getBBSAssembleControlService(mView?.getContext())?.let { service ->
            service.querySectionById(sectionId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<SectionInfoJson> { info -> mView?.sectionInfo(info) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.querySectionFail()
                            })
        }
    }

    override fun publishSubject(form: SubjectPublishFormJson) {
        val json = O2SDKManager.instance().gson.toJson(form)
        XLog.debug("publish json:$json")
        val body = RequestBody.create(MediaType.parse("text/json"), json)
        getBBSAssembleControlService(mView?.getContext())?.let { service ->
            service.publishSubject(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { id -> mView?.publishSuccess(id.id) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.publishFail()
                            })
        }
    }

    override fun uploadImage(filePath: String, subjectId: String) {
        getBBSAssembleControlService(mView?.getContext())?.let { service ->
            Observable.create(object : Observable.OnSubscribe<File> {
                override fun call(t: Subscriber<in File>?) {
                    try {
                        val path = ZoneUtil.compressBBSImage(filePath)
                        val file = File(path)
                        t?.onNext(file)
                    } catch (e: Exception) {
                        t?.onError(e)
                    }
                    t?.onCompleted()
                }
            }).subscribeOn(Schedulers.io())
                    .flatMap { file ->
                        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
                        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
                        val siteBody = RequestBody.create(MediaType.parse("text/plain"), subjectId)
                        service.uploadBBSSubjectAttachment(body, siteBody, subjectId)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { id ->
                        mView?.uploadSuccess(id.id, filePath)
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                        mView?.uploadFail(filePath)
                    })
        }
    }
}
