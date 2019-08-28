package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_cms_web_view_document.*
import kotlinx.android.synthetic.main.activity_cms_web_view_document.bottom_operate_button_layout
import kotlinx.android.synthetic.main.activity_cms_web_view_document.fl_bottom_operation_bar
import kotlinx.android.synthetic.main.activity_work_web_view.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.tbs.FileReaderActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.CMSWorkControl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.O2UploadImageData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.AttachPopupWindow
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.BottomSheetMenu
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.WebChromeClientWithProgressAndValueCallback
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Future


class CMSWebViewActivity : BaseMVPActivity<CMSWebViewContract.View, CMSWebViewContract.Presenter>(), CMSWebViewContract.View {
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
    private val UPLOAD_REQUEST_CODE = 10086
    private val REPLACE_REQUEST_CODE = 10087
    private val TAKE_FROM_PICTURES_CODE = 10088
    private val TAKE_FROM_CAMERA_CODE = 10089
    private var docId = ""
    private var docTitle = ""
    private var url = ""
    private val webChromeClient: WebChromeClientWithProgressAndValueCallback by lazy { WebChromeClientWithProgressAndValueCallback.with(this) }
    private val jsNotification: JSInterfaceO2mNotification by lazy { JSInterfaceO2mNotification.with(this) }
    private val jsUtil: JSInterfaceO2mUtil by lazy { JSInterfaceO2mUtil.with(this) }
    private val jsBiz: JSInterfaceO2mBiz by lazy { JSInterfaceO2mBiz.with(this) }

    private val downloadDocument: DownloadDocument by lazy { DownloadDocument(this) }
    private val cameraImageUri: Uri by lazy { FileUtil.getUriFromFile(this, File(FileExtensionHelper.getCameraCacheFilePath())) }
    //上传附件
    private var site = ""
    //replace 附件
    private var attachmentId = ""
    // 图片控制器
    private var imageUploadData: O2UploadImageData? = null

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

        //init webview
        web_view_cms_document_content.addJavascriptInterface(this, "o2android")
        jsNotification.setupWebView(web_view_cms_document_content)
        jsUtil.setupWebView(web_view_cms_document_content)
        jsBiz.setupWebView(web_view_cms_document_content)

        web_view_cms_document_content.addJavascriptInterface(jsNotification, JSInterfaceO2mNotification.JSInterfaceName)
        web_view_cms_document_content.addJavascriptInterface(jsUtil, JSInterfaceO2mUtil.JSInterfaceName)
        web_view_cms_document_content.addJavascriptInterface(jsBiz, JSInterfaceO2mBiz.JSInterfaceName)
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(webChromeClient.onActivityResult(requestCode, resultCode, data)){
            return
        }
        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                UPLOAD_REQUEST_CODE ->{
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    if (!TextUtils.isEmpty(result)) {
                        XLog.debug("uri path:$result")
                        showLoadingDialog()
                        //上传附件
                        mPresenter.uploadAttachment(result!!, site, docId)
                    } else {
                        XLog.error("FilePicker 没有返回值！")
                    }
                }
                REPLACE_REQUEST_CODE -> {
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    if (!TextUtils.isEmpty(result)) {
                        XLog.debug("uri path:$result")
                        showLoadingDialog()
                        //替换附件
                        mPresenter.replaceAttachment(result!!, site, attachmentId, docId)
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

    override fun finishLoading() {
        hideLoadingDialog()
    }

    override fun uploadAttachmentSuccess(attachmentId: String, site: String) {
        XLog.debug("uploadAttachmentResponse attachmentId:$attachmentId, site:$site")
        hideLoadingDialog()
        web_view_cms_document_content.evaluateJavascript("layout.appForm.uploadedAttachment(\"$site\", \"$attachmentId\")"){
            value -> XLog.debug("uploadedAttachment， onReceiveValue value=$value")
        }
    }

    override fun replaceAttachmentSuccess(attachmentId: String, site: String) {
        XLog.debug("replaceAttachmentResponse attachmentId:$attachmentId, site:$site")
        hideLoadingDialog()
        web_view_cms_document_content.evaluateJavascript("layout.appForm.replacedAttachment(\"$site\", \"$attachmentId\")"){
            value -> XLog.debug("replacedAttachment， onReceiveValue value=$value")
        }
    }

    override fun downloadAttachmentSuccess(file: File) {
        hideLoadingDialog()
        if (file.exists()){
            if (FileExtensionHelper.isImageFromFileExtension(file.extension)) {
                go<LocalImageViewActivity>(LocalImageViewActivity.startBundle(file.absolutePath))
            }else {
                go<FileReaderActivity>(FileReaderActivity.startBundle(file.absolutePath))
            }
        }
    }

    override fun downloadAttachmentFail(message: String) {
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
            web_view_cms_document_content.evaluateJavascript(js){
                value -> XLog.debug("replacedAttachment， onReceiveValue value=$value")
            }
        }else {
            XLog.error("图片控件对象不存在。。。。。。。。")
        }
    }

    //MARK: 操作按钮
    /**
     * 发布文档
     */
    fun publishDocument(view: View?) {
        web_view_cms_document_content.evaluateJavascript("layout.appForm.publishDocument()") { value ->
            XLog.info("发布文档返回：$value")
        }
    }

    /**
     * 删除文档
     */
    fun deleteDocument(view: View?) {
        O2DialogSupport.openConfirmDialog(this, "你确定要删除当前文档？", listener = {
            web_view_cms_document_content.evaluateJavascript("layout.appForm.deleteDocumentForMobile()") { value ->
                XLog.info("删除文档返回：$value")
            }
        })
    }


    //MARK: javascript interface

    /**
     * 关闭当前窗口
     */
    @JavascriptInterface
    fun closeDocumentWindow(result: String) {
        finish()
    }

    /**
     * 表单加载完成后回调
     */
    @JavascriptInterface
    fun cmsFormLoaded(control: String) {
        //{
        //                        "allowRead": true,
        //                        "allowPublishDocument": isControl && this.document.docStatus === "draft",
        //                        "allowSave": isControl && this.document.docStatus === "published",
        //                        "allowPopularDocument": false,
        //                        "allowEditDocument":  isControl && !this.document.wf_workId,
        //                        "allowDeleteDocument":  isControl && !this.document.wf_workId,
        //                        "allowArchiveDocument" : false,
        //                        "allowRedraftDocument" : false
        //                    };
        XLog.debug("表单加载完成回调：$control")
        if (!TextUtils.isEmpty(control)) {
            try {
                val cmsWorkControl = O2SDKManager.instance().gson.fromJson(control, CMSWorkControl::class.java)
                runOnUiThread {
                    var i = 0
                    if (cmsWorkControl.allowDeleteDocument) {
                        tv_cms_form_delete_btn.visible()
                        i++
                    }
                    if (cmsWorkControl.allowPublishDocument) {
                        tv_cms_form_publish_btn.visible()
                        i++
                    }
                    if (i>0) {
                        fl_bottom_operation_bar.visible()
                        bottom_operate_button_layout.visible()
                    }
                }
            } catch (e: Exception) {
                XLog.error("json parse error", e)
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
        runOnUiThread {
            openFancyFilePicker(UPLOAD_REQUEST_CODE)
        }
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
        runOnUiThread {
            openFancyFilePicker(REPLACE_REQUEST_CODE)
        }
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

        //下载附件
        mPresenter.downloadAttachment(attachmentId, docId)
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
        //打开文档
        downloadDocument.downloadDocumentAndOpenIt(url) {
            hideLoadingDialog()
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


    //MARK: private method



    private fun openFancyFilePicker(requestCode: Int) {
        FilePicker().withActivity(this).requestCode(requestCode)
                .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
                .start()
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
                            O2DialogSupport.openAlertDialog(this@CMSWebViewActivity, "非常抱歉，相机权限没有开启，无法使用相机！")
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
