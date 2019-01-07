package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_index_portal.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.CalendarMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application.CMSApplicationActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view.CMSWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.main.MeetingMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.ReadCompletedListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.ReadListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.TaskCompletedListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.TaskListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.TaskWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSApplicationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.zxing.activity.CaptureActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport

/**
 * Created by fancyLou on 21/03/2018.
 * Copyright © 2018 O2. All rights reserved.
 */



class IndexPortalFragment: BaseMVPViewPagerFragment<IndexPortalContract.View, IndexPortalContract.Presenter>(), IndexPortalContract.View {
    override var mPresenter: IndexPortalContract.Presenter = IndexPortalPresenter()

    override fun layoutResId(): Int = R.layout.fragment_index_portal

    companion object {
        val PORTAL_ID_KEY = "PORTAL_ID_KEY"
        fun instance(portalId: String): IndexPortalFragment {
            val instance = IndexPortalFragment()
            val args = Bundle()
            args.putString(PORTAL_ID_KEY, portalId)
            instance.arguments = args
            return instance
        }
    }


    private var portalId: String = ""
    private var portalUrl: String = ""
    override fun initUI() {
        portalId = arguments?.getString(PORTAL_ID_KEY) ?: ""
        if (TextUtils.isEmpty(portalId)) {
            XToast.toastShort(activity, "缺少参数门户ID！！")
            web_view_portal_content.loadData("缺少参数门户ID！！", "text/plain", "UTF-8")
        }else {
            portalUrl = APIAddressHelper.instance().getPortalWebViewUrl(portalId)
            XLog.debug("portal url : $portalUrl")
            web_view_portal_content.addJavascriptInterface(this, "o2") //注册js对象
            web_view_portal_content.webViewSetCookie(activity, portalUrl)
            web_view_portal_content.webViewClient = WebViewClient()
            loadWebview()
        }

    }

    fun loadWebview() {
        web_view_portal_content.loadUrl(portalUrl)
    }

    /**
     * 是否能返回
     */
    fun previousPage(): Boolean {
        return if (web_view_portal_content?.canGoBack() == true) {
            web_view_portal_content.goBack()
            true
        } else {
            false
        }
    }

    override fun lazyLoad() {

    }

    override fun loadCmsCategoryListByAppId(categoryList: List<CMSCategoryInfoJson>) {
        hideLoadingDialog()
        if (categoryList.isNotEmpty()) {
            val app = CMSApplicationInfoJson()
            app.appName = categoryList.first().appName
            app.wrapOutCategoryList = categoryList
            activity.go<CMSApplicationActivity>(CMSApplicationActivity.startBundleData(app))
        }else {
            XLog.error("该应用无法打开 没有分类数据。。。。。")
        }
    }

    /**
     * js 调用  window.o2.loadUrl(url)
     */
    @JavascriptInterface
    fun loadUrl(url: String) {
        web_view_portal_content.loadUrl(url)
    }


    @JavascriptInterface
    fun openO2Work(work: String, workCompleted: String, title: String) {
        XLog.debug("open work : $work, $workCompleted, $title")
        activity.go<TaskWebViewActivity>(TaskWebViewActivity.start(work, workCompleted, title))
    }

    @JavascriptInterface
    fun openO2CmsApplication(appId: String) {
        XLog.debug("openO2CmsApplication : $appId ")
        showLoadingDialog()
        mPresenter.loadCmsCategoryListByAppId(appId)
    }
    @JavascriptInterface
    fun openO2CmsDocument(docId: String, docTitle: String) {
        XLog.debug("openO2CmsDocument : $docId, $docTitle ")
        activity.go<CMSWebViewActivity>(CMSWebViewActivity.startBundleData(docId, docTitle))
    }
    @JavascriptInterface
    fun openO2Meeting(result: String) {
        XLog.debug("openO2Meeting rrrrrrrrrrr")
        activity.go<MeetingMainActivity>()
    }
    @JavascriptInterface
    fun openO2Calendar(result: String) {
        XLog.debug("openO2Calendarvvvvvvvvvvvvvvv")
        activity.go<CalendarMainActivity>()
    }
    @JavascriptInterface
    fun openScan(result: String) {
        XLog.debug("open scan ........")
        activity.runOnUiThread{
            PermissionRequester(activity)
                    .request(Manifest.permission.CAMERA)
                    .o2Subscribe {
                        onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                            XLog.info("granted:$granted , shouldShowRequest:$shouldShowRequestPermissionRationale, denied:$deniedPermissions")
                            if (!granted) {
                                O2DialogSupport.openAlertDialog(activity, "需要摄像头权限才能进行扫一扫功能！")
                            } else {
                                activity.go<CaptureActivity>()
                            }
                        }
                    }
        }

    }

    @JavascriptInterface
    fun openO2WorkSpace(type: String) {
        XLog.info("open work space $type")
        when(type.toLowerCase()) {
            "task" -> activity.go<TaskListActivity>()
            "taskcompleted" -> activity.go<TaskCompletedListActivity>()
            "read" -> activity.go<ReadListActivity>()
            "readcompleted" -> activity.go<ReadCompletedListActivity>()
            else -> activity.go<TaskListActivity>()
        }
    }

}