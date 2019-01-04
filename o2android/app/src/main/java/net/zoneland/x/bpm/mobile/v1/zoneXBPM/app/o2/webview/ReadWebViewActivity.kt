package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview


import android.os.Bundle
import android.text.TextUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_read_web_view.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ZoneUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import java.io.File


class ReadWebViewActivity : BaseMVPActivity<ReadWebViewContract.View, ReadWebViewContract.Presenter>(), ReadWebViewContract.View {
    override var mPresenter: ReadWebViewContract.Presenter = ReadWebViewPresenter()


    override fun layoutResId(): Int = R.layout.activity_read_web_view

    companion object {
        val READ_WEB_VIEW_TITLE = "xbpm.read.web.view.title"
        val READ_WEB_VIEW_ID = "xbpm.read.web.view.id"
        val READ_WEB_VIEW_WORK = "xbpm.read.web.view.work"
        /**
         * 启动使用的Bundle
         */
        fun startDataBundle(title: String, id: String, work: String): Bundle {
            val bundle = Bundle()
            bundle.putString(READ_WEB_VIEW_TITLE, title)
            bundle.putString(READ_WEB_VIEW_ID, id)
            bundle.putString(READ_WEB_VIEW_WORK, work)
            return bundle
        }
    }

    val bottomAnimation: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.translate_up) }
    var id = ""//read id
    var workId = "" //work id
    var title = "" //read title
    var isWorkCompleted = false
    var workCompletedId = ""
    var url = ""
    var read: ReadData? = null

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        id = intent.extras?.getString(READ_WEB_VIEW_ID) ?: ""
        workId = intent.extras?.getString(READ_WEB_VIEW_WORK) ?: ""
        title = intent.extras?.getString(READ_WEB_VIEW_TITLE) ?: ""
        setupToolBar(title, true)

        tv_bottom_operate_single_button.text = getString(R.string.work_form_set_read_button)
        tv_bottom_operate_single_button.setOnClickListener {
            O2DialogSupport.openConfirmDialog(this@ReadWebViewActivity, getString(R.string.read_complete_confirm_message), {
                showLoadingDialog()
                mPresenter.setReadComplete(read)
            })
        }

        web_view.addJavascriptInterface(this, "o2")
        web_view.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                XLog.debug("shouldOverrideUrlLoading:" + url)
                if (ZoneUtil.checkUrlIsInner(url)) {
                    view?.loadUrl(url)
                } else {
                    AndroidUtils.runDefaultBrowser(this@ReadWebViewActivity, url)
                }
                return true
            }
        })

        showLoadingDialog()
        mPresenter.loadReadInfo(id)
    }

    /**
     * 下载附件
     *
     * @param attachmentId
     */
    @JavascriptInterface
    fun downloadAttachment(attachmentId: String) {
        XLog.debug("download attachmentId:$attachmentId")
        if (TextUtils.isEmpty(attachmentId)) {
            XLog.error("调用失败，附件id没有传入！")
            return
        }
        runOnUiThread{showLoadingDialog()}
        if (isWorkCompleted) {
            mPresenter.downloadWorkCompletedAttachment(attachmentId, workCompletedId)
        } else {
            mPresenter.downloadAttachment(attachmentId, workId)
        }
    }

    override fun downloadAttachment(file: File) {
        hideLoadingDialog()
        if (file != null && file.exists()) AndroidUtils.openFileWithDefaultApp(this, file)
    }

    override fun loadReadInfo(info: ReadInfoData) {
        hideLoadingDialog()
        read = info.read
        if (read != null) {
            tv_bottom_operate_single_button.startAnimation(bottomAnimation)
            tv_bottom_operate_single_button.visible()
            workCompletedId = read?.workCompleted ?: ""
            isWorkCompleted = read?.isCompleted ?: false
        }

        if (isWorkCompleted) {
            url = APIAddressHelper.instance().getWorkCompletedUrl()
            url = String.format(url, workCompletedId)
        } else {
            url = APIAddressHelper.instance().getWorkUrlPre()
            url = String.format(url, workId)
        }

        url += "&time=" + System.currentTimeMillis()
        XLog.debug("url=" + url)
        web_view.webViewSetCookie(this, url)
        web_view.loadUrl(url)

    }


    override fun finishLoading() {
        hideLoadingDialog()
    }

    override fun invalidateArgs() {
        XToast.toastShort(this, "缺少传入参数！")
    }

    override fun downloadFail(message: String) {
        XToast.toastShort(this, message)
        finishLoading()
    }

    override fun setReadCompletedSuccess() {
        hideLoadingDialog()
        XToast.toastShort(this, "设置成功！")
        finish()
    }


}
