package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.main

import android.text.TextUtils
import android.widget.TextView
import com.google.gson.Gson
import net.muliba.accounting.app.ExceptionHandler
import net.muliba.fancyfilepickerlibrary.ext.concat
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.group.O2Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonList
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.process.ProcessDataJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ProcessStartBo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ProcessWorkData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class MeetingMainFragmentPresenter : BasePresenterImpl<MeetingMainFragmentContract.View>(), MeetingMainFragmentContract.Presenter {

    override fun findMyMeetingByDay(year: String, month: String, day: String, isViewer: Boolean) {
        if (TextUtils.isEmpty(year) || TextUtils.isEmpty(month) || TextUtils.isEmpty(day)) {
            mView?.onException("传入参数不正确，无法查询当日会议数据！")
            return
        }
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            val response = if (isViewer) {
                service.findMyMeetingByDayAll(year, month, day)
            } else {
                service.findMyMeetingByDay(year, month, day)
            }
            response.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { list -> mView?.findMyMeetingByDay(list) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                mView?.onException(e.message ?: "查询当日会议数据失败")
                            })
        }
    }

    override fun asyncLoadRoomName(roomTv: TextView, id: String, room: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.getRoomById(room).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { info ->
                        val tag = roomTv.tag as String
                        if (id == tag) {
                            roomTv.text = info.name
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                    })

        }
    }

    override fun findMyMeetingByMonth(monthDate: String, isWeek: Boolean, isViewer: Boolean) {
        if (TextUtils.isEmpty(monthDate)) {
            mView?.onException("传入参数不正确，无法查询月份会议数据！")
            return
        }
        if (isWeek) {
            //计算当前周是否跨月
            val date = DateHelper.convertStringToDate("yyyy-MM-dd", monthDate)
            val firstDay = DateHelper.getFirstDayOfWeek(date)
            val lastDay = DateHelper.getLastDayOfWeek(date)
            val firstMonth = DateHelper.getDateTime("yyyy-MM", firstDay)
            val lastMonth = DateHelper.getDateTime("yyyy-MM", lastDay)
            if (firstMonth == lastMonth) {
                val year = monthDate.substring(0, 4)
                val month = monthDate.substring(5, 7)
                findMonth(year, month, isViewer)
            } else {
                val year = firstMonth.substring(0, 4)
                val month = firstMonth.substring(5, 7)
                val yearSec = lastMonth.substring(0, 4)
                val monthSec = lastMonth.substring(5, 7)
                findMonth2(year, month, yearSec, monthSec, isViewer)
            }
        }else {
            val year = monthDate.substring(0, 4)
            val month = monthDate.substring(5, 7)
            findMonth(year, month, isViewer)
        }
    }

    private fun findMonth(year: String, month: String, isViewer: Boolean) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            val response = if (isViewer) {
                service.findMyMeetingByMonthAll(year, month)
            } else {
                service.findMyMeetingByMonth(year, month)
            }
            response.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { list -> mView?.findMyMeetingByMonth(list) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                mView?.onException(e.message ?: "查询月份会议数据失败")
                            })
        }
    }

    private fun findMonth2(year: String, month: String, secYear: String, secMonth: String, isViewer: Boolean) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            val list = ArrayList<MeetingInfoJson>()
            if (isViewer) {
                service.findMyMeetingByMonthAll(year, month)
                        .subscribeOn(Schedulers.io())
                        .flatMap { response ->
                            if (response.data != null && response.data.isNotEmpty()) {
                                list.addAll(response.data)
                            }

                            service.findMyMeetingByMonthAll(secYear, secMonth)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler { result ->
                            list.addAll(result)
                            mView?.findMyMeetingByMonth(list)
                        },
                                ExceptionHandler(mView?.getContext()) { e ->
                                    mView?.onException(e.message ?: "查询月份会议数据失败")
                                })
            } else {
                service.findMyMeetingByMonth(year, month)
                        .subscribeOn(Schedulers.io())
                        .flatMap { response ->
                            if (response.data != null && response.data.isNotEmpty()) {
                                list.addAll(response.data)
                            }
                            service.findMyMeetingByMonth(secYear, secMonth)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler { result ->
                            list.addAll(result)
                            mView?.findMyMeetingByMonth(list)
                        },
                                ExceptionHandler(mView?.getContext()) { e ->
                                    mView?.onException(e.message ?: "查询月份会议数据失败")
                                })
            }

        }
    }

    override fun asyncLoadPersonName(personTv: TextView, tag: String, person: String) {
        getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
            service.person(person)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { info ->
                        if (personTv.tag == tag) {
                            if (tag.contains("%%%"))
                                personTv.text = info.name
                            else
                                personTv.text = personTv.text.toString().concat(info.name).concat(" ")
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                    })
        }
    }

    override fun getMeetingConfig() {
        val service = getOrganizationAssembleCustomService(mView?.getContext())
        if (service != null){
            service.getMeetingConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ResponseHandler { info ->
                    mView?.setMeetingConfig(info)
                }, ExceptionHandler(mView?.getContext()) { e ->
                    XLog.error("", e)
                    mView?.setMeetingConfig("")
                })
        }else {
            XLog.info("老公共服务器模块已经去掉了！！！")
            val personService = getAssemblePersonalApi(mView?.getContext())
            if (personService != null) {
                personService.getMeetingConfig()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler { info ->
                            mView?.setMeetingConfig(info)
                        }, ExceptionHandler(mView?.getContext()) { e ->
                            XLog.error("", e)
                            mView?.setMeetingConfig("")
                        })
            }else {
                XLog.error("公共服务模块不存在")
                mView?.setMeetingConfig("")
            }
        }

    }

    override fun loadCurrentPersonIdentityWithProcess(processId: String) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.availableIdentityWithProcess(processId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { list ->
                        XLog.debug("identities: $list")
                        mView?.loadCurrentPersonIdentity(list)
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                        mView?.onException(e.message + "")
                    })
        }
    }

    override fun checkViewer(isDay: Boolean, configJson: String) {
        mView?.let {
            if (O2SDKManager.instance().isMeetingAdministrator()) {
                it.checkViewerBack(isDay, true)
                return
            }
            val gson = Gson()
            var config: ProcessDataJson? = null
            try {
                config = gson.fromJson<ProcessDataJson>(configJson, ProcessDataJson::class.java)
            } catch (e: Exception) {
            }
            val viewerList = config?.meetingViewer
            if (viewerList == null || viewerList.isEmpty()) {
                it.checkViewerBack(isDay, false)
            } else {
                if (viewerList.any { it == O2SDKManager.instance().distinguishedName }) { //配置个人的情况
                    it.checkViewerBack(isDay, true)
                    return
                }
                //查询用户所在组织列表
                val personList = PersonList(arrayListOf(O2SDKManager.instance().distinguishedName))
                val json = gson.toJson(personList)
                XLog.info(json)
                val body = RequestBody.create(MediaType.parse("text/json"), json)
                getAssembleExpressApi(it.getContext())?.listUnitPersonSup(body)
                        ?.subscribeOn(Schedulers.io())
                        ?.flatMap { response ->
                            var result = false
                            val groupList = ArrayList<O2Group>()
                            viewerList.forEach { viewer ->
                                if (response.data.unitList.any { it == viewer }) {
                                    result = true
                                    return@forEach
                                }
                            }
                            if (result) {
                                Observable.create {
                                    val r = ApiResponse<List<O2Group>>()
                                    r.data = groupList
                                    it.onNext(r)
                                    it.onCompleted()
                                }
                            } else {
                                getOrganizationAssembleControlApi(it.getContext())?.groupListWithPerson(O2SDKManager.instance().distinguishedName)
                            }
                        }
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.o2Subscribe {
                            onNext { response ->
                                var result = false
                                val grouplist = response.data
                                if (grouplist.isNotEmpty()) {
                                    viewerList.forEach { viewer ->
                                        if (grouplist.any { it.distinguishedName == viewer }) {
                                            result = true
                                            return@forEach
                                        }
                                    }
                                }
                                it.checkViewerBack(isDay, result)
                            }
                            onError { e, _ ->
                                XLog.error("", e)
                                it.checkViewerBack(isDay, false)
                            }
                        }
            }
        }
    }

    override fun startProcess(title: String, identifyId: String, processId: String) {
        if (TextUtils.isEmpty(identifyId) || TextUtils.isEmpty(processId)) {
            mView?.startProcessFail("传入参数为空，无法启动流程，identity:$identifyId,processId:$processId")
            return
        }
        val body = ProcessStartBo()
        body.title = title
        body.identity = identifyId
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.startProcess(processId, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<ProcessWorkData>> { list ->
                        try {
                            mView?.startProcessSuccess(list[0].taskList[0].work)
                        } catch (e: Exception) {
                            XLog.error("", e)
                            mView?.startProcessFail("返回数据异常！${e.message}")
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        mView?.startProcessFail(e.message ?: "")
                    })
        }
    }
}
