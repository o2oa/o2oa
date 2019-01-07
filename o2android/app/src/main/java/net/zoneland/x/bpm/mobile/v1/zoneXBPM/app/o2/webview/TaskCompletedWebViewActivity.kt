package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview


import android.os.Bundle
import android.text.TextUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_task_complete_web_view.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ZoneUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import java.io.File


class TaskCompletedWebViewActivity : BaseMVPActivity<TaskCompletedWebViewContract.View, TaskCompletedWebViewContract.Presenter>(), TaskCompletedWebViewContract.View {
    override var mPresenter: TaskCompletedWebViewContract.Presenter = TaskCompletedWebViewPresenter()


    override fun layoutResId(): Int = R.layout.activity_task_complete_web_view

    companion object {
        val TASK_COMPLETE_WEB_VIEW_TITLE = "xbpm.task.complete.web.view.title"
        val TASK_COMPLETE_WEB_VIEW_WORK = "xbpm.task.complete.web.view.work"
        val TASK_COMPLETE_WEB_VIEW_CANRETRACT = "xbpm.task.complete.can.retract"
        val TASK_COMPLETE_WEB_VIEW_WORK_COMPLETED = "xbpm.task.complete.web.view.work.completed"


        fun startWork(title: String, work: String, canRetract: Boolean = false): Bundle {
            val bundle = Bundle()
            bundle.putString(TASK_COMPLETE_WEB_VIEW_TITLE, title)
            bundle.putString(TASK_COMPLETE_WEB_VIEW_WORK, work)
            bundle.putBoolean(TASK_COMPLETE_WEB_VIEW_CANRETRACT, canRetract)
            return bundle
        }

        fun startWorkCompleted(title: String, workCompleted: String, canRetract: Boolean = false): Bundle {
            val bundle = Bundle()
            bundle.putString(TASK_COMPLETE_WEB_VIEW_TITLE, title)
            bundle.putString(TASK_COMPLETE_WEB_VIEW_WORK_COMPLETED, workCompleted)
            bundle.putBoolean(TASK_COMPLETE_WEB_VIEW_CANRETRACT, canRetract)
            return bundle
        }
    }

    val bottomAnimation: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.translate_up) }


    var title = "" //task title
    var workId = "" //work id
    var workCompletedId = ""
    var isWorkCompleted = false
    var showRecallButton = false
    var url = ""


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        workId = intent.extras?.getString(TASK_COMPLETE_WEB_VIEW_WORK) ?: ""
        workCompletedId = intent.extras?.getString(TASK_COMPLETE_WEB_VIEW_WORK_COMPLETED) ?: ""
        title = intent.extras?.getString(TASK_COMPLETE_WEB_VIEW_TITLE) ?: ""
        showRecallButton = intent.extras?.getBoolean(TASK_COMPLETE_WEB_VIEW_CANRETRACT, false) ?: false

        isCompleted()

        setupToolBar(title, true)

        tv_bottom_operate_single_button.text = getString(R.string.work_form_retract_button)
        tv_bottom_operate_single_button.setOnClickListener {
            XLog.debug("click 撤回按钮")
            O2DialogSupport.openConfirmDialog(this@TaskCompletedWebViewActivity, getString(R.string.retract_confirm_message), {
                showLoadingDialog()
                mPresenter.retractWork(workId)
            })
        }
        web_view.addJavascriptInterface(this, "o2")
        web_view.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                XLog.debug("shouldOverrideUrlLoading:" + url)
                if (ZoneUtil.checkUrlIsInner(url)) {
                    view?.loadUrl(url)
                } else {
                    AndroidUtils.runDefaultBrowser(this@TaskCompletedWebViewActivity, url)
                }
                return true
            }
        })


        //开始显示撤回按钮 动画启动
        if (showRecallButton) {
            tv_bottom_operate_single_button.startAnimation(bottomAnimation)
            tv_bottom_operate_single_button.visible()
        }
    }

    override fun onResume() {
        super.onResume()
        loadWebView()
    }

    override fun onPause() {
        super.onPause()
        try {
            web_view?.loadUrl("about:blank")
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        try {
            web_view?.loadUrl("about:blank")
            web_view?.destroy()
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

    private fun isCompleted() {
        if (!TextUtils.isEmpty(workId)) {
            isWorkCompleted = false
        } else if (!TextUtils.isEmpty(workCompletedId)) {
            isWorkCompleted = true
        } else {
            XToast.toastShort(this, "参数传入不正确！")
            finish()
        }
    }

    /**
     * 下载附件
     * 注意： js调用本地方法是子线程
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
        runOnUiThread {
            showLoadingDialog()
        }
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

    override fun retractSuccess() {
        hideLoadingDialog()
        XToast.toastShort(this, "撤回成功！")
        //goThenKill<TaskWebViewActivity>(TaskWebViewActivity.startDataBundle(title, workId))
        finish()
    }

    override fun retractFail() {
        XToast.toastShort(this, "撤回失败！")
        hideLoadingDialog()
    }

    override fun invalidateArgs() {
        XToast.toastShort(this, "缺少传入参数！")
        hideLoadingDialog()
    }

    override fun downloadFail(message: String) {
        XToast.toastShort(this, message)
        hideLoadingDialog()
    }


    private fun loadWebView() {
        if (isWorkCompleted) {
            url = APIAddressHelper.instance().getWorkCompletedUrl()
            url = String.format(url, workCompletedId)
        } else {
            url = APIAddressHelper.instance().getWorkUrlPre()
            url = String.format(url, workId)
        }

        url += "&time=" + System.currentTimeMillis()
        XLog.debug("url=$url")
        web_view.webViewSetCookie(this, url)
        web_view.loadUrl(url)
    }
}
