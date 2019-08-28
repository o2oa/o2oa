package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.google.gson.reflect.TypeToken
import com.tencent.smtt.sdk.QbSdk
import kotlinx.android.synthetic.main.activity_work_web_view.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.tbs.FileReaderActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.WorkNewActionItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.WorkControl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.WorkOpinionData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.O2UploadImageData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.BottomSheetMenu
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.WebChromeClientWithProgressAndValueCallback
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.dip
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

    private  val WORK_WEB_VIEW_UPLOAD_REQUEST_CODE = 1001
    private  val WORK_WEB_VIEW_REPLACE_REQUEST_CODE = 1002
    private  val TAKE_FROM_PICTURES_CODE = 1003
    private  val TAKE_FROM_CAMERA_CODE = 1004

    private var title = ""
    private  var workId = ""
    private  var workCompletedId = ""
    private  var isWorkCompleted = false
    private  var url = ""

    private var control: WorkControl? = null
    private var read: ReadData? = null
    private var site = ""
    private var attachmentId = ""
    private var formData: String? = ""//表单json数据
    private var formOpinion: String? = ""// 在表单内的意见信息
    private val routeNameList = ArrayList<String>()

    private val downloadDocument: DownloadDocument by lazy { DownloadDocument(this) }
    private val cameraImageUri: Uri by lazy { FileUtil.getUriFromFile(this, File(FileExtensionHelper.getCameraCacheFilePath())) }
    private val webChromeClient: WebChromeClientWithProgressAndValueCallback by lazy { WebChromeClientWithProgressAndValueCallback.with(this) }
    var imageUploadData: O2UploadImageData? = null
    private val jsNotification: JSInterfaceO2mNotification by lazy { JSInterfaceO2mNotification.with(this) }
    private val jsUtil: JSInterfaceO2mUtil by lazy { JSInterfaceO2mUtil.with(this) }
    private val jsBiz: JSInterfaceO2mBiz by lazy { JSInterfaceO2mBiz.with(this) }




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

        web_view.addJavascriptInterface(this, "o2android")
        jsNotification.setupWebView(web_view)
        jsUtil.setupWebView(web_view)
        jsBiz.setupWebView(web_view)
        web_view.addJavascriptInterface(jsNotification, JSInterfaceO2mNotification.JSInterfaceName)
        web_view.addJavascriptInterface(jsUtil, JSInterfaceO2mUtil.JSInterfaceName)
        web_view.addJavascriptInterface(jsBiz, JSInterfaceO2mBiz.JSInterfaceName)
        web_view.webChromeClient = webChromeClient
        web_view.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                XLog.error("ssl error, $error")
                handler?.proceed()
            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                XLog.debug("shouldOverrideUrlLoading:$url")
                if (ZoneUtil.checkUrlIsInner(url)) {
                    view?.loadUrl(url)
                } else {
                    AndroidUtils.runDefaultBrowser(this@TaskWebViewActivity, url)
                }
                return true
            }

        }


        web_view.webViewSetCookie(this, url)
        web_view.loadUrl(url)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // 网页内 js 选择照片的能力
            if (webChromeClient.onActivityResult(requestCode, resultCode, data)) {
                return
            }
            when (requestCode) {
                WORK_WEB_VIEW_UPLOAD_REQUEST_CODE -> {
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    if (!TextUtils.isEmpty(result)) {
                        XLog.debug("uri path:$result")
                        showLoadingDialog()
                        mPresenter.uploadAttachment(result!!, site, workId)
                    } else {
                        XLog.error("FilePicker 没有返回值！")
                    }
                }
                WORK_WEB_VIEW_REPLACE_REQUEST_CODE -> {
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    if (!TextUtils.isEmpty(result)) {
                        XLog.debug("uri path:$result")
                        showLoadingDialog()
                        mPresenter.replaceAttachment(result!!, site, attachmentId, workId)
                    } else {
                        XLog.error("FilePicker 没有返回值！")
                    }
                }
                TAKE_FROM_PICTURES_CODE -> {
                    //选择照片
                    data?.let {
                        val result = it.extras.getString(PicturePicker.FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY, "")
                        if (!TextUtils.isEmpty(result)) {
                            XLog.debug("照片 path:$result")
                            uploadImage2FileStorageStart(result)
                        }
                    }
                }
                TAKE_FROM_CAMERA_CODE -> {
                    //拍照
                    XLog.debug("拍照//// ")
                    uploadImage2FileStorageStart(FileExtensionHelper.getCameraCacheFilePath())
                }
            }
        }
    }

    //MARK: - click operation button event

    fun formDeleteBtnClick(view: View?) {
        O2DialogSupport.openConfirmDialog(this@TaskWebViewActivity, getString(R.string.delete_work_confirm_message), listener =  {
            showLoadingDialog()
            mPresenter.delete(workId)
        })
    }
    fun formSaveBtnClick(view: View?) {
        XLog.debug("click save button")
        web_view.clearFocus()
        evaluateJavascriptGetFormData()
    }
    fun formGoNextBtnClick(view: View?) {
        XLog.debug("click submit button")
        web_view.clearFocus()
        formData()
        getFormOpinion()
        submitData()
    }
    fun formSetReadBtnClick(view: View?) {
        O2DialogSupport.openConfirmDialog(this@TaskWebViewActivity, getString(R.string.read_complete_confirm_message), listener =  {
            showLoadingDialog()
            mPresenter.setReadComplete(read)
        })
    }
    fun formRetractBtnClick(view: View?) {
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
        XLog.debug("表单加载完成回调：$result")// 20190520 result改成了操作按钮列表 如果是result是true就是老系统，用原来的方式。。。。。。。。得兼容老方式诶
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

            if (result == "true") { // 老版本的操作
                // 获取control 生成操作按钮
                web_view.evaluateJavascript("layout.appForm.businessData.control") { value ->
                    XLog.debug("control: $value")
                    try {
                        control = O2SDKManager.instance().gson.fromJson(value, WorkControl::class.java)
                    } catch (e: Exception) {
                    }
                    initOptionBar()
                }
            }else {// 2019-05-21 增加新版操作按钮
                // 解析result 操作按钮列表
                if (!TextUtils.isEmpty(result)) {
                    try {
                        val type = object : TypeToken<List<WorkNewActionItem>>() {}.type
                        val list: List<WorkNewActionItem> = O2SDKManager.instance().gson.fromJson(result, type)
                        initOptionBarNew(list)
                    }catch (e: Exception){
                        XLog.error("解析操作按钮结果列表出错", e)
                    }
                }else {
                    XLog.error("操作按钮结果为空")
                }

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
        XLog.debug("download attachmentId:$attachmentId")
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

    /**
     * 弹出窗 js调试用
     */
    @JavascriptInterface
    fun openO2Alert(message: String?) {
        if (message != null) {
            XLog.debug("弹出窗。。message:$message")
            runOnUiThread {
                O2DialogSupport.openAlertDialog(this, message)
            }
        }
    }

    /**
     * 图片控件
     */
    @JavascriptInterface
    fun uploadImage2FileStorage(json: String?) {
        imageUploadData = null
        XLog.debug("打开图片上传控件， $json")
        runOnUiThread {
            if (json != null) {
                imageUploadData = O2SDKManager.instance().gson.fromJson(json, O2UploadImageData::class.java)
                showPictureChooseMenu()

            }else {
                XToast.toastShort(this, "没有传入对象")
            }
        }
    }


    //MARK: - view implements

    override fun finishLoading() {
        hideLoadingDialog()
    }

    override fun saveSuccess() {
        hideLoadingDialog()
        XToast.toastShort(this, "保存成功！")
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
        hideLoadingDialog()
        XToast.toastShort(this, "撤回失败！")
    }

    override fun deleteSuccess() {
        hideLoadingDialog()
        XToast.toastShort(this, "删除成功！")
        finish()
    }

    override fun deleteFail() {
        hideLoadingDialog()
        XToast.toastShort(this, "删除失败！")
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
//        if (file.exists()) AndroidUtils.openFileWithDefaultApp(this, file)
        if (file.exists()){
            if (FileExtensionHelper.isImageFromFileExtension(file.extension)) {
                go<LocalImageViewActivity>(LocalImageViewActivity.startBundle(file.absolutePath))
            }else {
                go<FileReaderActivity>(FileReaderActivity.startBundle(file.absolutePath))
//                QbSdk.openFileReader(this, file.absolutePath, HashMap<String, String>()) { p0 -> XLog.info("打开文件返回。。。。。$p0") }
            }
        }
    }

    override fun invalidateArgs() {
        XToast.toastShort(this, "缺少传入参数！")
    }

    override fun downloadFail(message: String) {
        finishLoading()
        XToast.toastShort(this, message)
    }

    override fun upload2FileStorageFail(message: String) {
        hideLoadingDialog()
        XToast.toastShort(this, message)
    }

    override fun upload2FileStorageSuccess(id: String) {
        hideLoadingDialog()
        if (imageUploadData != null) {
            imageUploadData!!.fileId = id
            val callback = imageUploadData!!.callback
            val json = O2SDKManager.instance().gson.toJson(imageUploadData)
            val js = "$callback('$json')"
            XLog.debug("执行js:$js")
            web_view.evaluateJavascript(js){
                value -> XLog.debug("replacedAttachment， onReceiveValue value=$value")
            }
        }else {
            XLog.error("图片控件对象不存在。。。。。。。。")
        }
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
                fl_bottom_operation_bar.visible()
            }
        }else {
            XLog.error("control为空。。。。。。")
        }
    }


    /**
     * 20190521
     * 生成操作按钮 新版
     */
    private fun initOptionBarNew(list: List<WorkNewActionItem>) {
        if(!list.isEmpty()) {
            val len = list.count()
            when(len) {
                1 -> {
                    val menuItem = list[0]
                    tv_work_form_bottom_first_action.text = menuItem.text
                    tv_work_form_bottom_first_action.visible()
                    tv_work_form_bottom_first_action.setOnClickListener {
                        bottomButtonAction(menuItem)
                    }
                }
                2 -> {
                    val menuItem = list[0]
                    tv_work_form_bottom_first_action.text = menuItem.text
                    tv_work_form_bottom_first_action.visible()
                    tv_work_form_bottom_first_action.setOnClickListener {
                        bottomButtonAction(menuItem)
                    }
                    val menuItem2 = list[1]
                    tv_work_form_bottom_second_action.text = menuItem2.text
                    tv_work_form_bottom_second_action.visible()
                    tv_work_form_bottom_second_action.setOnClickListener {
                        bottomButtonAction(menuItem2)
                    }
                }
                else -> {
                    val menuItem = list[0]
                    tv_work_form_bottom_first_action.text = menuItem.text
                    tv_work_form_bottom_first_action.visible()
                    tv_work_form_bottom_first_action.setOnClickListener {
                        bottomButtonAction(menuItem)
                    }
                    val menuItem2 = list[1]
                    tv_work_form_bottom_second_action.text = menuItem2.text
                    tv_work_form_bottom_second_action.visible()
                    tv_work_form_bottom_second_action.setOnClickListener {
                        bottomButtonAction(menuItem2)
                    }
                    img_work_form_bottom_more_action.visible()
                    img_work_form_bottom_more_action.setOnClickListener {
                        if (rl_bottom_operation_bar_mask.visibility == View.VISIBLE) {
                            rl_bottom_operation_bar_mask.gone()
                        }else {
                            rl_bottom_operation_bar_mask.visible()
                        }
                    }
                    rl_bottom_operation_bar_mask.setOnClickListener {
                        XLog.debug("点击了背景。。。。。")
                        rl_bottom_operation_bar_mask.gone()
                    }
                    //装载更多按钮
                    ll_bottom_operation_bar_new_more.removeAllViews()
                    for ((index, item) in list.withIndex()) {
                       if (index > 1) {
                           val button = newBottomMoreButton(item)
                           ll_bottom_operation_bar_new_more.addView(button)
                           button.setOnClickListener {
                               bottomButtonAction(item)
                           }
                       }
                    }
                }

            }
            fl_bottom_operation_bar.visible()
            ll_bottom_operation_bar_new.visible()
        }
    }

    /**
     * 底部操作按钮执行操作
     */
    private fun bottomButtonAction(menuItem: WorkNewActionItem) {
        XLog.debug("点击了按钮${menuItem.text}")
        XLog.debug("动作：${menuItem.action} , control:${menuItem.control}")

        if (!TextUtils.isEmpty(menuItem.actionScript)) {
            val jsExc = "layout.app.appForm._runCustomAction(${menuItem.actionScript})"
            XLog.debug(jsExc)
            web_view.evaluateJavascript(jsExc) { value ->
                XLog.debug("onReceiveValue value=$value")
            }
        }else {
            when(menuItem.control) {
                "allowDelete" -> {
                    formDeleteBtnClick(null)
                }
                "allowSave" -> {
                    formSaveBtnClick(null)
                }
                "allowProcessing" -> {
                    formGoNextBtnClick(null)
                }
                "allowReadProcessing" ->{
                    formSetReadBtnClick(null)
                }
                "allowRetract" -> {
                    formRetractBtnClick(null)
                }
                else -> {
                    val jsExc ="layout.app.appForm[\"${menuItem.action}\"]()"
                    XLog.debug(jsExc)
                    web_view.evaluateJavascript(jsExc) { value ->
                        XLog.debug("onReceiveValue value=$value")
                    }
                }
            }
        }
        rl_bottom_operation_bar_mask.gone()
    }

    /**
     * 更多按钮生成
     */
    private fun newBottomMoreButton(menuItem: WorkNewActionItem): TextView {
        val button = TextView(this)
        val layoutparam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip(42))
        layoutparam.bottomMargin = dip(5)
        button.layoutParams = layoutparam
        button.gravity = Gravity.CENTER
        button.text = menuItem.text
        button.setTextColor(ContextCompat.getColor(this, R.color.z_color_primary))
        button.setBackgroundColor(Color.WHITE)
        button.setTextSize(COMPLEX_UNIT_SP, 16f)
        return button
    }

    private fun submitData() {
        web_view.evaluateJavascript("layout.appForm.formValidation(\"\", \"\")") { value ->
            XLog.debug("formValidation，value:$value")
            if (value == "true") {
                web_view.evaluateJavascript("layout.appForm.businessData.task") { task ->
                    XLog.debug("submitData, onReceiveValue value=$task")
                    try {
                        XLog.debug("submitData，TaskData:$task")
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

    /**
     * 校验表单
     * 选择路由和填写意见后，提交工作前
     */
    fun validateFormForSubmitDialog(route: String, opinion: String, callback:(Boolean)->Unit) {
        web_view.evaluateJavascript("layout.appForm.formValidation(\"$route\", \"$opinion\")") { value ->
            if (value == "true") {
                callback(true)
            }else {
                callback(false)
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
            if (value == null) {
                runOnUiThread {
                    XToast.toastShort(getContext(), "没有获取到表单数据！")
                }
                return@evaluateJavascript
            }
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
            XLog.debug("validateFormBeforeSubmit,value:$value")
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



    private fun showPictureChooseMenu() {
        BottomSheetMenu(this)
                .setTitle("上传照片")
                .setItem("从相册选择", resources.getColor(R.color.z_color_text_primary)) {
                    takeFromPictures()
                }
                .setItem("拍照", resources.getColor(R.color.z_color_text_primary)) {
                    takeFromCamera()
                }
                .setCancelButton("取消", resources.getColor(R.color.z_color_text_hint)) {
                    XLog.debug("取消。。。。。")
                }
                .show()
    }

    private fun takeFromPictures() {
        PicturePicker()
                .withActivity(this)
                .chooseType(PicturePicker.CHOOSE_TYPE_SINGLE)
                .requestCode(TAKE_FROM_PICTURES_CODE)
                .start()
    }

    private fun takeFromCamera() {
        PermissionRequester(this).request(Manifest.permission.CAMERA)
                .o2Subscribe {
                    onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                        XLog.info("granted:$granted , shouldShowRequest:$shouldShowRequestPermissionRationale, denied:$deniedPermissions")
                        if (!granted) {
                            O2DialogSupport.openAlertDialog(this@TaskWebViewActivity, "非常抱歉，相机权限没有开启，无法使用相机！")
                        } else {
                            openCamera()
                        }
                    }
                }
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //return-data false 不是直接返回拍照后的照片Bitmap 因为照片太大会传输失败
        intent.putExtra("return-data", false)
        //改用Uri 传递
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        startActivityForResult(intent, TAKE_FROM_CAMERA_CODE)
    }



    private fun openFancyFilePicker(requestCode: Int) {
        FilePicker().withActivity(this).requestCode(requestCode)
                .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
                .start()
    }



    private fun uploadImage2FileStorageStart(filePath: String) {
        showLoadingDialog()
        if (imageUploadData != null) {
            mPresenter.upload2FileStorage(filePath, imageUploadData!!.referencetype, imageUploadData!!.reference)
        }else {
            finishLoading()
            XToast.toastShort(this, "上传文件参数为空！！！")
        }
    }


}
