package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view


import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_cms_web_view_document.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.JSInterfaceO2mNotification
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.JSInterfaceO2mUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.AttachPopupWindow
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.WebChromeClientWithProgressAndValueCallback
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Future


class CMSWebViewActivity : BaseMVPActivity<CMSWebViewContract.View, CMSWebViewContract.Presenter>(), CMSWebViewContract.View, AttachPopupWindow.AttachListener {
    override var mPresenter: CMSWebViewContract.Presenter = CMSWebViewPresenter()

    override fun layoutResId(): Int  = R.layout.activity_cms_web_view_document

    companion object {
        const val CMS_VIEW_DOCUMENT_ID_KEY = "CMS_VIEW_DOCUMENT_ID_KEY"
        const val CMS_VIEW_DOCUMENT_TITLE_KEY = "CMS_VIEW_DOCUMENT_TITLE_KEY"

        fun startBundleData(docId: String, docTitle:String): Bundle {
            val bundle = Bundle()
            bundle.putString(CMS_VIEW_DOCUMENT_ID_KEY, docId)
            bundle.putString(CMS_VIEW_DOCUMENT_TITLE_KEY, docTitle)
            return bundle
        }
    }
    private var docId = ""
    private var docTitle = ""
    private var url = ""
    private val attachList = ArrayList<AttachmentItemVO>()
    private val popupWindow: AttachPopupWindow by lazy { AttachPopupWindow(this, attachList) }
    private val webChromeClient: WebChromeClientWithProgressAndValueCallback by lazy { WebChromeClientWithProgressAndValueCallback.with(this) }
    private val jsNotification: JSInterfaceO2mNotification by lazy { JSInterfaceO2mNotification.with(this) }
    private val jsUtil: JSInterfaceO2mUtil by lazy { JSInterfaceO2mUtil.with(this) }


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        docId = intent.extras?.getString(CMS_VIEW_DOCUMENT_ID_KEY) ?: ""
        docTitle = intent.extras?.getString(CMS_VIEW_DOCUMENT_TITLE_KEY) ?: ""

        //初始化附件存储目录  权限
        val folder = File(FileExtensionHelper.getXBPMCMSAttachFolder())
        if (!folder.exists()) {
            folder.mkdirs()
        }
        url = APIAddressHelper.instance().getCMSWebViewUrl(docId)
        url += "&time="+System.currentTimeMillis()
        XLog.debug("url=$url")

        setupToolBar(docTitle, true)


        fab_cms_document_attach.setOnClickListener {
            popupWindow.animationStyle = R.style.dir_popupwindow_anim
            popupWindow.showAtLocation(activity_cms_web_view_document, Gravity.BOTTOM, 0, 0)
            ZoneUtil.lightOff(this@CMSWebViewActivity)
        }

        //init webview
        jsNotification.setupWebView(web_view_cms_document_content)
        jsUtil.setupWebView(web_view_cms_document_content)
        web_view_cms_document_content.addJavascriptInterface(jsNotification, JSInterfaceO2mNotification.JSInterfaceName)
        web_view_cms_document_content.addJavascriptInterface(jsUtil, JSInterfaceO2mUtil.JSInterfaceName)
        web_view_cms_document_content.webChromeClient = webChromeClient
        web_view_cms_document_content.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                XLog.error("ssl error, $error")
                handler?.proceed()
            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                XLog.debug("shouldOverrideUrlLoading:$url")
                if (ZoneUtil.checkUrlIsInner(url)) {
                    view?.loadUrl(url)
                } else {
                    AndroidUtils.runDefaultBrowser(this@CMSWebViewActivity, url)
                }
                return true
            }
        }
        web_view_cms_document_content.webViewSetCookie(this, url)
        web_view_cms_document_content.loadUrl(url)

        mPresenter.loadAttachList(docId)
    }

    override fun loadAttachList(list: List<AttachmentItemVO>) {
        if (list.isNotEmpty()) {
            fab_cms_document_attach.visible()
            attachList.clear()
            attachList.addAll(list)
            popupWindow.listener = this
            popupWindow.setOnDismissListener {
                ZoneUtil.lightOn(this@CMSWebViewActivity)
            }
            popupWindow.notifyStatusChanged()
        }else {
            fab_cms_document_attach.gone()
        }
    }

    override fun getAttachStatus(id: String?): AttachPopupWindow.AttachStatus {
        if (!TextUtils.isEmpty(id)) {
            if (taskMap.containsKey(id!!)) {
                return  AttachPopupWindow.AttachStatus.DOWNLOADING
            }
            val file = File(getAttachFileLocalPath(id))
            if (file.exists()) {
                return AttachPopupWindow.AttachStatus.DOWNLOADCOMPLETED
            }else {
                return AttachPopupWindow.AttachStatus.ONCLOUD
            }
        }else{
            return AttachPopupWindow.AttachStatus.ONCLOUD
        }
    }



    override fun openCompletedFile(id: String?) {
        if (!TextUtils.isEmpty(id)) {
            val file = File(getAttachFileLocalPath(id!!))
            if (file != null && file.exists()) AndroidUtils.openFileWithDefaultApp(this, file)
        }
    }

    override fun startDownLoadFile(id: String?) {
        if (!TextUtils.isEmpty(id)){
            taskMap.put(id!!, doAsync {
                val filePath = getAttachFileLocalPath(id)
                val file = File(filePath)
                var downloadSuccess = false
                try {
                    if (!file.exists()) {
                        val call = RetrofitClient.instance().cmsAssembleControlService().downloadAttach(id)
                        val response = call.execute()
                        val input = DataInputStream(response.body()?.byteStream())
                        val output = DataOutputStream(FileOutputStream(file))
                        val buffer = ByteArray(4096)
                        var count = 0
                        do {
                            count = input.read(buffer)
                            if (count > 0) {
                                output.write(buffer, 0, count)
                            }
                        } while (count > 0)
                        output.close()
                        input.close()
                    }
                    downloadSuccess = true
                }catch (e: Exception){
                    XLog.error("下载附件异常", e)
                }
                uiThread {
                    if (taskMap.containsKey(id)){
                        taskMap.remove(id)
                    }
                    if (downloadSuccess) {
                        popupWindow.notifyStatusChanged()
                    }else{
                        if (file.exists()){
                            file.delete()
                        }
                        XToast.toastShort(this@CMSWebViewActivity, "下载附件失败！")
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webChromeClient.onActivityResult(requestCode, resultCode, data)
    }

    private val taskMap = HashMap<String, Future<Unit>>()
    private fun getAttachFileLocalPath(id: String): String {
        var path  = ""
        attachList.asSequence().filter { it.id == id }.map { path = FileExtensionHelper.getXBPMCMSAttachFolder()+File.separator+it.fileName }.toList()
        return path
    }
}
