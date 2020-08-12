package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2ViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.CalendarOB
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarPostData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarPickerOption
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.FrontendResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 11/07/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class CreateCalendarViewModel(app: Application) : BaseO2ViewModel(app) {


    val calendarId: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val calendarTitle: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val calendarColor: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val comment: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isPublic: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isOrgCalendar: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val calendarType:  MutableLiveData<String> by lazy { MutableLiveData<String>() }
    //id 触发查询Calendar对象
    val oldCalendar: LiveData<CalendarPostData> = Transformations.switchMap(calendarId) { id ->
        val calendarData = MutableLiveData<CalendarPostData>()
        loadFromNet(id, calendarData)
        calendarData
    }

    //所属组织、人员
    val target:  MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val targetName: MediatorLiveData<String> by lazy { MediatorLiveData<String>() }
    //管理者
    val manageablePersonList:  MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val manageablePersonName: MediatorLiveData<String> by lazy { MediatorLiveData<String>() }
    //可见范围
    val viewablePersonList:  MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val viewableUnitList:  MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val viewableGroupList:  MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val viewableName: MediatorLiveData<String> by lazy { MediatorLiveData<String>() }
    //可新建日程的范围
    val publishablePersonList:  MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val publishableUnitList:  MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val publishableGroupList:  MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val publishableName: MediatorLiveData<String> by lazy { MediatorLiveData<String>() }


    val deleteBtnVisibleable: MediatorLiveData<Boolean> = MediatorLiveData()

    private val isLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    //网络操作反馈结果
    private val netResponse: MutableLiveData<FrontendResponse> by lazy { MutableLiveData<FrontendResponse>() }

    val calendarTypeKey: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    init {
        isPublic.value = false
        isOrgCalendar.value = false
        deleteBtnVisibleable.addSource(calendarId) { id->
            deleteBtnVisibleable.value = !TextUtils.isEmpty(id)
        }
        targetName.addSource(target) { value ->
            targetName.value = if (value?.contains("@") == true) {
                value.split("@").first()
            }else {
                ""
            }
        }
        manageablePersonName.addSource(manageablePersonList) { value ->
            if (value != null && value.isNotEmpty()) {
                manageablePersonName.value = value.joinToString {
                    if (it.contains("@")) {
                        it.split("@").first()
                    } else {
                        it
                    }
                }
            }else {
                manageablePersonName.value = ""
            }
        }
        viewableName.addSource(viewablePersonList) { viewableNameSetup() }
        viewableName.addSource(viewableUnitList) { viewableNameSetup() }
        viewableName.addSource(viewableGroupList) { viewableNameSetup() }
        publishableName.addSource(publishablePersonList) {
            publishableNameSetup()
        }
        publishableName.addSource(publishableUnitList) {
            publishableNameSetup()
        }
        publishableName.addSource(publishableGroupList) {
            publishableNameSetup()
        }
    }
    private fun viewableNameSetup() {
        val personList = viewablePersonList.value
        val unitList = viewableUnitList.value
        val groupList = viewableGroupList.value
        var name = ""
        if (personList != null && personList.isNotEmpty()) {
            name += personList.joinToString {
                if (it.contains("@")) {
                    it.split("@").first()
                } else {
                    it
                }
            } + " "
        }
        if (unitList != null && unitList.isNotEmpty()) {
            name += unitList.joinToString {
                if (it.contains("@")) {
                    it.split("@").first()
                } else {
                    it
                }
            } + " "
        }
        if (groupList != null && groupList.isNotEmpty()) {
            name += groupList.joinToString {
                if (it.contains("@")) {
                    it.split("@").first()
                } else {
                    it
                }
            } + " "
        }
        viewableName.value = name
    }
    private fun publishableNameSetup() {
        val personList = publishablePersonList.value
        val unitList = publishableUnitList.value
        val groupList = publishableGroupList.value
        var name = ""
        if (personList != null && personList.isNotEmpty()) {
            name += personList.joinToString {
                if (it.contains("@")) {
                    it.split("@").first()
                } else {
                    it
                }
            } + " "
        }
        if (unitList != null && unitList.isNotEmpty()) {
            name += unitList.joinToString {
                if (it.contains("@")) {
                    it.split("@").first()
                } else {
                    it
                }
            } + " "
        }
        if (groupList != null && groupList.isNotEmpty()) {
            name += groupList.joinToString {
                if (it.contains("@")) {
                    it.split("@").first()
                } else {
                    it
                }
            } + " "
        }
        publishableName.value = name
    }


    fun saveCalendar() {
        val post = CalendarPostData()
        post.name = calendarTitle.value ?:""
        post.type = calendarTypeKey.value ?:""
        post.color = calendarColor.value ?:""
        post.isPublic = isPublic.value ?:false
        post.status = "OPEN"
        post.description = comment.value ?:""
        if (TextUtils.isEmpty(target.value)) {
            post.target = O2SDKManager.instance().distinguishedName
        }else {
            post.target = target.value!!
        }
        post.manageablePersonList = manageablePersonList.value ?: ArrayList()
        post.viewablePersonList = viewablePersonList.value ?: ArrayList()
        post.viewableUnitList = viewableUnitList.value ?: ArrayList()
        post.viewableGroupList = viewableGroupList.value ?: ArrayList()
        post.publishablePersonList = publishablePersonList.value ?: ArrayList()
        post.publishableUnitList = publishableUnitList.value ?: ArrayList()
        post.publishableGroupList = publishableGroupList.value ?: ArrayList()

        XLog.info("$post")
        isLoading.value = true
        getCalendarAssembleService()?.saveCalendar(post)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        isLoading.value = false
                        val res = FrontendResponse(true, "保存成功！")
                        netResponse.value = res
                    }
                    onError { e, isNetworkError ->
                        XLog.error("保存日历异常，isnet:$isNetworkError", e)
                        isLoading.value = false
                        val res = FrontendResponse(false, "保存失败！")
                        netResponse.value = res
                    }
                }
    }

    fun updateCalendar() {
        if (TextUtils.isEmpty(calendarId.value)) {
            val res = FrontendResponse(false, "更新异常，没有Id！")
            netResponse.value = res
            return
        }
        val post = CalendarPostData()
        post.id = calendarId.value!!
        post.name = calendarTitle.value ?:""
        post.type = calendarTypeKey.value ?:""
        post.color = calendarColor.value ?:""
        post.isPublic = isPublic.value ?:false
        post.status = "OPEN"
        post.description = comment.value ?:""

        if (TextUtils.isEmpty(target.value)) {
            post.target = O2SDKManager.instance().distinguishedName
        }else {
            post.target = target.value!!
        }
        post.manageablePersonList = manageablePersonList.value ?: ArrayList()
        post.viewablePersonList = viewablePersonList.value ?: ArrayList()
        post.viewableUnitList = viewableUnitList.value ?: ArrayList()
        post.viewableGroupList = viewableGroupList.value ?: ArrayList()
        post.publishablePersonList = publishablePersonList.value ?: ArrayList()
        post.publishableUnitList = publishableUnitList.value ?: ArrayList()
        post.publishableGroupList = publishableGroupList.value ?: ArrayList()
        XLog.info("update calendar: $post")
        isLoading.value = true
        getCalendarAssembleService()?.saveCalendar(post)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        isLoading.value = false
                        val res = FrontendResponse(true, "更新成功！")
                        netResponse.value = res
                    }
                    onError { e, isNetworkError ->
                        XLog.error("保存日历异常，isnet:$isNetworkError", e)
                        isLoading.value = false
                        val res = FrontendResponse(false, "更新失败！")
                        netResponse.value = res
                    }
                }
    }

    fun deleteCalendar() {
        if (TextUtils.isEmpty(calendarId.value)) {
            val res = FrontendResponse(false, "删除异常，没有Id！")
            netResponse.value = res
            return
        }
        getCalendarAssembleService()?.deleteCalendar(calendarId.value!!)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        isLoading.value = false
                        val res = FrontendResponse(true, "删除成功！")
                        netResponse.value = res
                    }
                    onError { e, isNetworkError ->
                        XLog.error("删除日历异常，isnet:$isNetworkError", e)
                        isLoading.value = false
                        val res = FrontendResponse(false, "删除失败！")
                        netResponse.value = res
                    }
                }
    }


    fun calendarColorLive(): LiveData<String> = calendarColor

    fun isLoadingLive(): LiveData<Boolean> = isLoading

    fun netResponseLive(): LiveData<FrontendResponse> = netResponse


    fun setCalendarType(type: CalendarPickerOption) {
        calendarTypeKey.value = type.value
        calendarType.value = type.name
        val isOrg = type.value == "UNIT"
        isOrgCalendar.value = isOrg
        if (isOrg) {
            target.value = ""
            manageablePersonList.value = ArrayList()
            viewablePersonList.value = ArrayList()
            viewableUnitList.value = ArrayList()
            viewableGroupList.value = ArrayList()
            publishablePersonList.value = ArrayList()
            publishableUnitList.value = ArrayList()
            publishableGroupList.value = ArrayList()
        }
    }

    fun setBackCalendarInfo(info: CalendarPostData) {
        var type = CalendarOB.calendarTypes[info.type]
        if (type == null) {
            type = CalendarOB.calendarTypes["PERSON"]!!
        }
        setCalendarType(CalendarPickerOption(type, info.type))

        calendarTitle.value = info.name
        calendarColor.value = info.color
        isPublic.value = info.isPublic
        comment.value = info.description
        target.value = info.target
        manageablePersonList.value = info.manageablePersonList
        viewablePersonList.value = info.viewablePersonList
        viewableUnitList.value = info.viewableUnitList
        viewableGroupList.value = info.viewableGroupList
        publishablePersonList.value = info.publishablePersonList
        publishableUnitList.value = info.publishableUnitList
        publishableGroupList.value = info.publishableGroupList
    }


    private fun loadFromNet(id: String?, calendarPostData: MutableLiveData<CalendarPostData>) {
        if (!TextUtils.isEmpty(id)) {
            val service = getCalendarAssembleService()
            service?.getCalendar(id!!)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.o2Subscribe {
                onNext { res ->
                    val calendar = res.data
                    calendarPostData.value = calendar
                }
                onError { e, isNetworkError ->
                    XLog.error("查询日历出错， isnetworkError:$isNetworkError", e)
                    calendarPostData.value = null
                }
            }
        }
    }
}