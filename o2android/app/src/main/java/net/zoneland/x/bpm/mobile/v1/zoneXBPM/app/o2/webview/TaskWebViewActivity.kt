package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_work_web_view.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.WorkControl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.WorkOpinionData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ZoneUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import java.io.File


class TaskWebViewActivity : BaseMVPActivity<TaskWebViewContract.View, TaskWebViewContract.Presenter>(), TaskWebViewContract.View {
    override var mPresenter: TaskWebViewContract.Presenter = TaskWebViewPresenter()


    override fun layoutResId(): Int = R.layout.activity_work_web_view

    companion object {
        val WORK_WEB_VIEW_TITLE = "xbpm.work.web.view.title"
        val WORK_WEB_VIEW_WORK = "xbpm.work.web.view.work"
        val WORK_WEB_VIEW_WORK_COMPLETED = "xbpm.work.web.view.work.completed"

        fun start(work: String?, workCompleted: String?, title: String?):  Bundle {
            val bundle = Bundle()
            bundle.putString(WORK_WEB_VIEW_TITLE, title)
            bundle.putString(WORK_WEB_VIEW_WORK, work)
            bundle.putString(WORK_WEB_VIEW_WORK_COMPLETED, workCompleted)
            return bundle
        }
    }

    val WORK_WEB_VIEW_UPLOAD_REQUEST_CODE = 1001
    val WORK_WEB_VIEW_REPLACE_REQUEST_CODE = 1002

    var title = ""
    var workId = ""
    var workCompletedId = ""
    var isWorkCompleted = false
    var url = ""

    var control: WorkControl? = null
    var read: ReadData? = null
    var site = ""
    var attachmentId = ""
    var formData: String? = ""//表单json数据
    var formOpinion: String? = ""// 在表单内的意见信息
    val routeNameList = ArrayList<String>()

    val downloadDocument: DownloadDocument by lazy { DownloadDocument(this) }



    override fun afterSetContentView(savedInstanceState: Bundle?) {
        title = intent.extras?.getString(WORK_WEB_VIEW_TITLE) ?: ""
        workId = intent.extras?.getString(WORK_WEB_VIEW_WORK) ?: ""
        workCompletedId = intent.extras?.getString(WORK_WEB_VIEW_WORK_COMPLETED) ?: ""

        isWorkCompleted = !TextUtils.isEmpty(workCompletedId)

        if (isWorkCompleted) {
            url = APIAddressHelper.instance().getWorkCompletedUrl()
            url = String.format(url, workCompletedId)
        } else {
            url = APIAddressHelper.instance().getWorkUrlPre()
            url = String.format(url, workId)
        }
        url += "&time=" + System.currentTimeMillis()

        XLog.debug("title:$title ,  url:$url")
        setupToolBar(title, true)

        web_view.addJavascriptInterface(this, "o2")
        web_view.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                XLog.debug("shouldOverrideUrlLoading:" + url)
                if (ZoneUtil.checkUrlIsInner(url)) {
                    view?.loadUrl(url)
                } else {
                    AndroidUtils.runDefaultBrowser(this@TaskWebViewActivity, url)
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

            }
        })


        web_view.webViewSetCookie(this, url)
        web_view.loadUrl(url)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                WORK_WEB_VIEW_UPLOAD_REQUEST_CODE -> {
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    if (!TextUtils.isEmpty(result)) {
                        XLog.debug("uri path:" + result)
                        showLoadingDialog()
                        mPresenter.uploadAttachment(result!!, site, workId)
                    } else {
                        XLog.error("FilePicker 没有返回值！")
                    }
                }
                WORK_WEB_VIEW_REPLACE_REQUEST_CODE -> {
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    if (!TextUtils.isEmpty(result)) {
                        XLog.debug("uri path:" + result)
                        showLoadingDialog()
                        mPresenter.replaceAttachment(result!!, site, attachmentId, workId)
                    } else {
                        XLog.error("FilePicker 没有返回值！")
                    }
                }
            }
        }
    }

    //MARK: - click operation button event

    fun formDeleteBtnClick(view: View) {
        O2DialogSupport.openConfirmDialog(this@TaskWebViewActivity, getString(R.string.delete_work_confirm_message), listener =  {
            showLoadingDialog()
            mPresenter.delete(workId)
        })
    }
    fun formSaveBtnClick(view: View) {
        XLog.debug("click save button")
        web_view.clearFocus()
        evaluateJavascriptGetFormData()
    }
    fun formGoNextBtnClick(view: View) {
        XLog.debug("click submit button")
        web_view.clearFocus()
        formData()
        getFormOpinion()
        submitData()
    }
    fun formSetReadBtnClick(view: View) {
        O2DialogSupport.openConfirmDialog(this@TaskWebViewActivity, getString(R.string.read_complete_confirm_message), listener =  {
            showLoadingDialog()
            mPresenter.setReadComplete(read)
        })
    }
    fun formRetractBtnClick(view: View) {
        O2DialogSupport.openConfirmDialog(this@TaskWebViewActivity, getString(R.string.retract_confirm_message), listener = {
            showLoadingDialog()
            mPresenter.retractWork(workId)
        })
    }

    // MARK: - finish submit callback webview javascript
    /**
     * @param site 如果是手写签批的 返回site值
     */
    fun finishSubmit(site: String?) {
        if (!TextUtils.isEmpty(site)) {
            XLog.info("finish submit ...$site")
        }
        finish()
    }


    // MARK: - javascriptInterface

    /**
     * 表单加载完成后回调
     */
    @JavascriptInterface
    fun appFormLoaded(result: String) {// 获取control 动态生成操作按钮
        runOnUiThread {
            if (TextUtils.isEmpty(title)) {
                web_view.evaluateJavascript("layout.appForm.businessData.work.title") { value ->
                    XLog.debug("title: $title")
                    try {
                        title = O2SDKManager.instance().gson.fromJson(value, String::class.java)
                        updateToolbarTitle(title)
                    } catch (e: Exception) {
                    }
                }
            }
            // 获取control 生成操作按钮
            web_view.evaluateJavascript("layout.appForm.businessData.control") { value ->
                XLog.debug("control: $value")
                try {
                    control = O2SDKManager.instance().gson.fromJson(value, WorkControl::class.java)
                } catch (e: Exception) {
                }
                initOptionBar()
            }
            web_view.evaluateJavascript("layout.appForm.businessData.read") { value ->
                XLog.debug("read: $value")
                try {
                    read = O2SDKManager.instance().gson.fromJson(value, ReadData::class.java)
                } catch (e: Exception) {
                }
            }
        }
    }



    /**
     * 上传附件
     *
     * @param site
     */
    @JavascriptInterface
    fun uploadAttachment(site: String) {
        XLog.debug("upload site:$site")
        if (TextUtils.isEmpty(site)) {
            XLog.error("没有传入site")
            return
        }
        this.site = site
        openFancyFilePicker(WORK_WEB_VIEW_UPLOAD_REQUEST_CODE)
    }

    /**
     * 替换附件
     *
     * @param attachmentId
     * @param site
     */
    @JavascriptInterface
    fun replaceAttachment(attachmentId: String, site: String) {
        XLog.debug("replace site:$site, attachmentId:$attachmentId")
        if (TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(site)) {
            XLog.error("没有传入attachmentId 或 site")
            return
        }
        this.site = site
        this.attachmentId = attachmentId
        openFancyFilePicker(WORK_WEB_VIEW_REPLACE_REQUEST_CODE)
    }

    /**
     * 下载附件
     *
     * @param attachmentId
     */
    @JavascriptInterface
    fun downloadAttachment(attachmentId: String) {
        XLog.debug("download attachmentId:" + attachmentId)
        if (TextUtils.isEmpty(attachmentId)) {
            XLog.error("调用失败，附件id没有传入！")
            return
        }
        runOnUiThread {
            showLoadingDialog()
        }

        mPresenter.downloadAttachment(attachmentId, workId)
    }

    /**
     * 打开文档 公文打开 office pdf 等
     */
    @JavascriptInterface
    fun openDocument(url: String) {
        XLog.debug("打开文档。。。。。文档地址：$url")
        runOnUiThread {
            showLoadingDialog()
        }
        downloadDocument.downloadDocumentAndOpenIt(url) {
            hideLoadingDialog()
        }
    }


    //MARK: - view implements

    override fun finishLoading() {
        hideLoadingDialog()
    }

    override fun submitSuccess() {
        hideLoadingDialog()
        finish()
    }

    override fun setReadCompletedSuccess() {
        hideLoadingDialog()
        finish()
    }

    override fun retractSuccess() {
        hideLoadingDialog()
        XToast.toastShort(this, "撤回成功！")
        finish()
    }

    override fun retractFail() {
        XToast.toastShort(this, "撤回失败！")
        hideLoadingDialog()
    }

    override fun deleteSuccess() {
        hideLoadingDialog()
        XToast.toastShort(this, "删除成功！")
        finish()
    }

    override fun deleteFail() {
        XToast.toastShort(this, "删除失败！")
        hideLoadingDialog()
    }

    override fun uploadAttachmentSuccess(attachmentId: String, site: String) {
        XLog.debug("uploadAttachmentResponse attachmentId:$attachmentId, site:$site")
        hideLoadingDialog()
        web_view.evaluateJavascript("layout.appForm.uploadedAttachment(\"$site\", \"$attachmentId\")"){
            value -> XLog.debug("uploadedAttachment， onReceiveValue value=$value")
        }
    }

    override fun replaceAttachmentSuccess(attachmentId: String, site: String) {
        XLog.debug("replaceAttachmentResponse attachmentId:$attachmentId, site:$site")
        hideLoadingDialog()
        web_view.evaluateJavascript("layout.appForm.replacedAttachment(\"$site\", \"$attachmentId\")"){
            value -> XLog.debug("replacedAttachment， onReceiveValue value=$value")
        }
    }

    override fun downloadAttachmentSuccess(file: File) {
        hideLoadingDialog()
        if (file != null && file.exists()) AndroidUtils.openFileWithDefaultApp(this, file)
    }

    override fun invalidateArgs() {
        XToast.toastShort(this, "缺少传入参数！")
    }

    override fun downloadFail(message: String) {
        XToast.toastShort(this, message)
        finishLoading()
    }



    //MARK: - private function

    /**
     * 生成操作按钮
     */
    private fun initOptionBar() {
        XLog.debug("initOptionBar......安装操作按钮")
        if (control != null) {
            var count = 0
            if (control?.allowDelete == true) {
                count ++
                tv_work_form_delete_btn.visible()
            }
            if (control?.allowSave == true) {
                count ++
                tv_work_form_save_btn.visible()
            }
            if (control?.allowProcessing == true) {
                    count ++
                    tv_work_form_go_next_btn.visible()
                }
            if (control?.allowReadProcessing == true) {
                    count ++
                    tv_work_form_set_read_btn.visible()
                }
            if (control?.allowRetract == true) {
                    count ++
                    tv_work_form_retract_btn.visible()
            }
            if (count > 0 ) {
                bottom_operate_button_layout.visible()
            }
        }else {
            XLog.error("control为空。。。。。。")
        }
    }

    private fun submitData() {
        web_view.evaluateJavascript("layout.appForm.formValidation(\"\", \"\")") { value ->
            XLog.debug("formValidation，value:$value")
            if (value == "true") {
                web_view.evaluateJavascript("layout.appForm.businessData.task") { task ->
                    XLog.debug("submitData, onReceiveValue value=$task")
                    try {
                        XLog.debug("submitData，TaskData:$task")
//                        val data = O2App.instance.gson.fromJson(task, TaskData::class.java)
//                        XLog.debug("submitData，createTime:" + data.createTime)
//                        routeNameList.clear()
//                        data.routeNameList?.let {
//                            routeNameList.addAll(it)
//                        }
//                        openChooseRouterDialog(data)
                        if (TextUtils.isEmpty(task)) {
                            XToast.toastShort(this@TaskWebViewActivity, "任务数据获取不到！")
                        }else {
                            openTaskWorkSubmitDialog(task)
                        }
                    } catch (e: Exception) {
                        XLog.error("", e)
                        XToast.toastShort(this@TaskWebViewActivity, "解析数据异常！")
                    }
                }
            } else {
                XToast.toastShort(this@TaskWebViewActivity, "请检查表单填写是否正确！")
            }
        }
    }

    private fun openTaskWorkSubmitDialog(taskData: String) {
        TaskWorkSubmitDialogFragment.startWorkDialog(workId, taskData, formData, formOpinion)
                .show(supportFragmentManager, TaskWorkSubmitDialogFragment.TAG)
    }


    private fun formData() {
        web_view.evaluateJavascript("layout.appForm.getData()") { value ->
            XLog.debug("evaluateJavascriptGetFormData， onReceiveValue form value=$value")
            formData = value
        }
    }

    private fun getFormOpinion() {
        web_view.evaluateJavascript("layout.appForm.getOpinion()") { value ->
            XLog.debug("evaluateJavascript get from Opinion， onReceiveValue form value=$value")
            if (!TextUtils.isEmpty(value)) {
                formOpinion = if (value == "\"\"") {
                    ""
                }else {
                    var result = ""
                    try {
                        val woData = O2SDKManager.instance().gson.fromJson<WorkOpinionData>(value, WorkOpinionData::class.java)
                        result = woData.opinion ?: ""
                    } catch (e: Exception) {
                    }
                    result
                }

            }
        }
    }

    private fun evaluateJavascriptGetFormData() {
        web_view.evaluateJavascript("layout.appForm.getData()") { value ->
            XLog.debug("evaluateJavascriptGetFormData， onReceiveValue save value=$value")
            formData = value
            showLoadingDialog()
            mPresenter.save(workId, value)
        }
    }

    /**
     * 继续流转 路由选择弹出窗
     *
     * @param data
     */
    private fun openChooseRouterDialog(data: TaskData) {
        val dialog = O2DialogSupport.openCustomViewDialog(this,
                getString(R.string.work_form_submit),
                getString(R.string.work_form_submit_button),
                getString(R.string.work_form_cancel_button),
                R.layout.dialog_approve_router_choose,
                { dialog ->
                    val text = dialog.findViewById<EditText>(R.id.edit_approve_router_opinion)
                    val opinion = text.text.toString()
                    XLog.debug("Positive，opinion: $opinion")
                    val group = dialog.findViewById<RadioGroup>(R.id.radio_group_approve_router_choose)
                    XLog.debug("Positvie，checked id:" + group.checkedRadioButtonId)
                    val radio = dialog.findViewById<RadioButton>(group.checkedRadioButtonId)
                    val routeName = radio.text.toString()
                    XLog.debug("Positive，radio, $routeName")
                    validateFormBeforeSubmit(routeName, opinion, data)
                    dialog.dismiss()
                },
                { _ ->
                    hideLoadingDialog()
                })
        val opinionText = dialog.findViewById<EditText>(R.id.edit_approve_router_opinion)
        opinionText.setText(formOpinion)
        val group = dialog.findViewById<RadioGroup>(R.id.radio_group_approve_router_choose)
        routeNameList.mapIndexed { index, s ->
            val tempButton = RadioButton(this@TaskWebViewActivity)
            tempButton.text = s
            tempButton.isChecked = routeNameList.size==1
            tempButton.id = index+ 100
            group.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun validateFormBeforeSubmit(routeName: String, opinion: String, data: TaskData) {
        web_view.evaluateJavascript("layout.appForm.formValidation(\"$routeName\", \"$opinion\")") { value ->
            XLog.debug("validateFormBeforeSubmit,value:" + value)
            if ("true" == value) {
                data.opinion = opinion
                data.routeName = routeName
                showLoadingDialog()
                mPresenter.submit(data, workId, formData)
            }else {
                XToast.toastShort(this@TaskWebViewActivity, "请检查表单填写是否正确！")
            }
        }
    }

    private fun openFancyFilePicker(requestCode: Int) {
        FilePicker().withActivity(this).requestCode(requestCode)
                .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
                .start()
    }




}
