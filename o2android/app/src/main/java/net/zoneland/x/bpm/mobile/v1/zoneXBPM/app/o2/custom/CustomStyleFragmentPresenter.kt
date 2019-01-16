package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.custom

import android.os.Handler
import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.CustomStyleData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.portal.PortalData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AppItemOnlineVo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.Base64ImageUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.edit
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 16/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class CustomStyleFragmentPresenter : BasePresenterImpl<CustomStyleFragmentContract.View>(), CustomStyleFragmentContract.Presenter {

    val service: RealmDataService by lazy { RealmDataService() }

    override fun installCustomStyle(handler: Handler?) {
        var url = O2SDKManager.instance().prefs().getString(O2.PRE_CENTER_URL_KEY, "")
        getApiService(mView?.getContext(), url)?.getCustomStyle()
                ?.subscribeOn(Schedulers.io())
                ?.flatMap { response ->
                    val data = response.data
                    if (data != null) {
                        val images = data.images
                        val portalList = data.portalList
                        val nativeAppList = data.nativeAppList
                        storageIndexPageInfo(data)
                        sendMessage(handler, 25)
                        storageImages(images)
                        sendMessage(handler, 50)
                        storagePortalList(portalList)
                        sendMessage(handler, 75)
                        storageNativeList(nativeAppList)
                        sendMessage(handler, 99)
                    }

                    Observable.just(true)
                }?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        mView?.installFinish()
                    }
                    onError { e, isNetworkError ->
                        XLog.error("install Style fail isnetWorkError: $isNetworkError", e)
                        //更新异常，清空hash 下次重新更新
                        O2SDKManager.instance().prefs().edit {
                            putString(O2CustomStyle.CUSTOM_STYLE_UPDATE_HASH_KEY, "")
                        }
                        mView?.installFinish()
                    }
                }
    }

    private fun sendMessage(handler: Handler?, process: Int) {
        val messageIndex = handler?.obtainMessage()
        messageIndex?.arg1 = process
        handler?.sendMessage(messageIndex)
    }

    private fun storageNativeList(nativeAppList: List<AppItemOnlineVo>) {
        service.deleteALlNativeApp().subscribeOn(Schedulers.immediate()).subscribe {
            service.saveNativeList(nativeAppList).subscribe()
        }
    }

    private fun storagePortalList(portalList: List<PortalData>) {
        service.deleteAllPortal().subscribeOn(Schedulers.immediate()).subscribe {
            service.savePortalList(portalList).subscribe()
        }
    }

    private fun storageImages(images: List<CustomStyleData.ImageValue>) {
        images.map { image ->
            val base64 = image.value
            val path = when (image.name) {
                O2CustomStyle.IMAGE_KEY_LAUNCH_LOGO -> {
                    O2CustomStyle.launchLogoImagePath(mView?.getContext())
                }
                O2CustomStyle.IMAGE_KEY_INDEX_BOTTOM_MENU_LOGO_FOCUS -> {
                    O2CustomStyle.indexMenuLogoFocusImagePath(mView?.getContext())
                }
                O2CustomStyle.IMAGE_KEY_INDEX_BOTTOM_MENU_LOGO_BLUR -> {
                    O2CustomStyle.indexMenuLogoBlurImagePath(mView?.getContext())
                }
                O2CustomStyle.IMAGE_KEY_LOGIN_AVATAR -> {
                    O2CustomStyle.loginAvatarImagePath(mView?.getContext())
                }
                O2CustomStyle.IMAGE_KEY_PEOPLE_AVATAR_DEFAULT -> {
                    O2CustomStyle.peopleAvatarImagePath(mView?.getContext())
                }
                O2CustomStyle.IMAGE_KEY_PROCESS_DEFAULT -> {
                    O2CustomStyle.processDefaultImagePath(mView?.getContext())
                }
                O2CustomStyle.IMAGE_KEY_SETUP_ABOUT_LOGO -> {
                    O2CustomStyle.setupAboutImagePath(mView?.getContext())
                }
                else -> ""
            }
            if (!TextUtils.isEmpty(path)) {
                val result = Base64ImageUtil.generateImage(path, base64)
                XLog.info("generate image result: $result, path: $path")
            }
        }
    }

    private fun storageIndexPageInfo(data: CustomStyleData?) {
        O2SDKManager.instance().prefs().edit {
            putString(O2CustomStyle.INDEX_TYPE_PREF_KEY, data?.indexType
                    ?: O2CustomStyle.INDEX_TYPE_DEFAULT)
            putString(O2CustomStyle.INDEX_ID_PREF_KEY, data?.indexPortal ?: "")
        }
    }
}