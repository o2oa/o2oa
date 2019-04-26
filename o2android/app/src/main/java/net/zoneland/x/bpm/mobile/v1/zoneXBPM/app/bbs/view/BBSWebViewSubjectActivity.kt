package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.view


import android.app.Activity
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_bbs_web_view_subject.*
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.BBSUploadImageBO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.ReplyFormJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectReplyInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.BBSWebViewAttachmentVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.hideSoftInput
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.AttachPopupWindow
import org.jetbrains.anko.dip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.Future


class BBSWebViewSubjectActivity : BaseMVPActivity<BBSWebViewSubjectContract.View,
        BBSWebViewSubjectContract.Presenter>(), BBSWebViewSubjectContract.View, AttachPopupWindow.AttachListener {

    companion object {
        val BBS_VIEW_SUBJECT_ID_KEY = "BBS_VIEW_SUBJECT_ID_KEY"
        val BBS_VIEW_SUBJECT_TITLE_KEY = "BBS_VIEW_SUBJECT_TITLE_KEY"
        val BBS_TAKE_FROM_PICTURES_CODE = 0
        val BBS_VIEW_REQUEST_CODE_FROM_REPLY = 103
        fun startBundleData(subjectId:String, subjectTitle:String): Bundle {
            val bundle = Bundle()
            bundle.putString(BBS_VIEW_SUBJECT_ID_KEY, subjectId)
            bundle.putString(BBS_VIEW_SUBJECT_TITLE_KEY, subjectTitle)
            return  bundle
        }
    }

    private var subjectId = ""
    private var subjectTitle = ""
    private var parentId = ""
    //private var keyHeight = 0
    private var newReplyId = UUID.randomUUID().toString()
    private val taskMap = HashMap<String, Future<Unit>>()
    private val attachList = ArrayList<AttachmentItemVO>()
    private val uploadedImageMap = HashMap<String, BBSUploadImageBO>()//已上传的图片
    private val uploadingImageMap = HashMap<String, BBSUploadImageBO>()//正在上传的图片
    private val popupWindow: AttachPopupWindow by lazy { AttachPopupWindow(this, attachList) }

    override var mPresenter: BBSWebViewSubjectContract.Presenter = BBSWebViewSubjectPresenter()
    override fun layoutResId(): Int = R.layout.activity_bbs_web_view_subject

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        subjectId = intent.extras?.getString(BBS_VIEW_SUBJECT_ID_KEY)?:""
        subjectTitle = intent.extras?.getString(BBS_VIEW_SUBJECT_TITLE_KEY)?:""
        //prepare attachment storage dir
        val folder = File(FileExtensionHelper.getXBPMBBSAttachFolder())
        if (!folder.exists()) {
            folder.mkdirs()
        }
        setupToolBar(subjectTitle, true)
        /*val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        //阀值设置为屏幕高度的1/3
        keyHeight = metric.heightPixels/3*/
        //init webview
        web_view_bbs_web_view_subject_content.addJavascriptInterface(this, "o2bbs")
        web_view_bbs_web_view_subject_content.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                XLog.error("ssl error, $error")
                handler?.proceed()
            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                XLog.debug("shouldOverrideUrlLoading:$url")
                if (ZoneUtil.checkUrlIsInner(url)) {
                    view?.loadUrl(url)
                } else {
                    AndroidUtils.runDefaultBrowser(this@BBSWebViewSubjectActivity, url)
                }
                return true
            }
        }
        //发送监听
        button_bbs_subject_reply.setOnClickListener {
            publishReply()
        }
        //附件监听
        button_bbs_subject_attach.setOnClickListener {
            hideSoftInput()
            popupWindow.animationStyle = R.style.dir_popupwindow_anim
            popupWindow.showAtLocation(activity_bbs_web_view_subject, Gravity.BOTTOM, 0, 0 )
            ZoneUtil.lightOff(this@BBSWebViewSubjectActivity)
        }
        //图片附件
        image_bbs_reply_subject_attachment_add_button.setOnClickListener {
            PicturePicker()
                    .withActivity(this)
                    .chooseType(PicturePicker.CHOOSE_TYPE_SINGLE)
                    .requestCode(BBS_TAKE_FROM_PICTURES_CODE)
                    .start()
        }
        //监听软键盘
        //activity_bbs_web_view_subject.addOnLayoutChangeListener(this)
        layout_bbs_reply_subject_attachment_list.viewTreeObserver.addOnGlobalLayoutListener {
            if(layout_bbs_reply_subject_attachment_list.childCount != 0) {
                image_bbs_reply_subject_attachment_add_button.setImageResource(R.mipmap.icon_choose_image_red)
            } else {
                image_bbs_reply_subject_attachment_add_button.setImageResource(R.mipmap.icon_choose_image_gray)
            }
        }
        //open webview
        val url = APIAddressHelper.instance().getBBSWebViewUrl(subjectId, 1) + "&time="+System.currentTimeMillis()
        XLog.debug(url)
        web_view_bbs_web_view_subject_content.webViewSetCookie(this, url)
        web_view_bbs_web_view_subject_content.loadUrl(url)
        //加载评论和附件
        mPresenter.loadReplyPermissionAndAttachList(subjectId)
    }

    override fun loadReplyPermissionAndAttachList(attachment: BBSWebViewAttachmentVO) {
        XLog.debug("bbs webView attachment : $attachment")
        val canReply  =  attachment.canReply
        val hasAttach = attachment.hasAttach
//        if (!canReply && !hasAttach) {
//            layout_bbs_subject_operation_bar.gone()
//            button_bbs_subject_attach.gone()
//        } else if (canReply && !hasAttach) {
//            button_bbs_subject_attach.setOnClickListener {
//                XToast.toastShort(this,"没有附件")
//            }
//        } else
        if (!canReply){
            edit_bbs_reply_subject_content.isClickable = false
            button_bbs_subject_reply.isClickable = false
            button_bbs_subject_reply.text = "禁止评论"
        }

        if (hasAttach) {
            button_bbs_subject_attach.visible()
            attachList.clear()
            attachList.addAll(attachment.attachList)
            popupWindow.listener = this
            popupWindow.setOnDismissListener { ZoneUtil.lightOn(this@BBSWebViewSubjectActivity) }
        }else {
            button_bbs_subject_attach.gone()
        }
    }

    private fun publishReply() {
        if (!uploadingImageMap.isEmpty()) {
            XToast.toastShort(this, "附件上传中。。。。。")
            return
        }

        var content = edit_bbs_reply_subject_content.text.toString()
        if (TextUtils.isEmpty(content) && uploadedImageMap.isEmpty()) {
            XToast.toastShort(this, "请填写回复内容")
            return
        }
        XLog.debug("content:$content")
        content = formatToHtml(content)
        XLog.debug("content html:$content")
        content += addAttachmentToContent()
        XLog.debug("content html and image:$content")
        val form = ReplyFormJson(
                newReplyId,
                content,
                subjectId,
                parentId,
                AndroidUtils.getDeviceBrand() + AndroidUtils.getDeviceModelNumber(),
                O2.DEVICE_TYPE
        )
        showLoadingDialog()
        mPresenter.postReply(form)
    }

    private fun formatToHtml(content: String): String {
        var ret = ""
        if (!TextUtils.isEmpty(content)) {
            val lineSeparator = System.getProperty("line.separator", "\n")
            content.split(lineSeparator).map {
                ret += "<p>$it</p>"
            }
        }
        return ret
    }

    private fun addAttachmentToContent(): String {
        var content = ""
        if (!uploadedImageMap.isEmpty()) {
            uploadedImageMap.map {
                val attachmentURL = APIAddressHelper.instance().getBBSAttachmentURL(it.value.fileId)
                val path = "<p><img src=\"$attachmentURL\"  style=\"display: block; margin: auto; width:${it.value.showWidth}px; max-width:100%;\" /></p>"
                content += path
            }
        }
        return content
    }

    /**
     * 回复
     * @param parentId
     */
    @JavascriptInterface
    fun reply(parentId:String) {
        XLog.debug("回复 parent id:"+parentId)
        this.parentId = parentId
        runOnUiThread { showLoadingDialog() }
        mPresenter.getReplyParentInfo(parentId)
    }

    override fun getReplyParentFail() {
        hideLoadingDialog()
        XToast.toastShort(this, "获取回复内容失败！")
    }

    override fun getReplyParentSuccess(info: SubjectReplyInfoJson) {
        hideLoadingDialog()
        val name = if (TextUtils.isEmpty(info.creatorNameShort)){info.creatorName}else{info.creatorNameShort}
        edit_bbs_reply_subject_content.hint = "回复$name："
        edit_bbs_reply_subject_content.isFocusable = true
        edit_bbs_reply_subject_content.isFocusableInTouchMode = true
        edit_bbs_reply_subject_content.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun getAttachStatus(id: String?): AttachPopupWindow.AttachStatus {
        if (!TextUtils.isEmpty(id)) {
            if (taskMap.containsKey(id!!)) {
                return  AttachPopupWindow.AttachStatus.DOWNLOADING
            }
            val file = File(getAttachFileLocalPath(id))
            return if (file.exists()) {
                AttachPopupWindow.AttachStatus.DOWNLOADCOMPLETED
            }else {
                AttachPopupWindow.AttachStatus.ONCLOUD
            }
        }else{
            return AttachPopupWindow.AttachStatus.ONCLOUD
        }
    }

    override fun openCompletedFile(id: String?) {
        if (!TextUtils.isEmpty(id)) {
            val file = File(getAttachFileLocalPath(id!!))
            if (file.exists()) AndroidUtils.openFileWithDefaultApp(this, file)
        }
    }

    override fun startDownLoadFile(id: String?) {
        if (!TextUtils.isEmpty(id)){
            taskMap[id!!] = doAsync {
                val filePath = getAttachFileLocalPath(id)
                val file = File(filePath)
                var downloadSuccess = false
                try {
                    if (!file.exists()) {
                        val call = RetrofitClient.instance().bbsAssembleControlServiceApi()
                                .downloadAttach(id)
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
                } catch(e: Exception) {
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
                        XToast.toastShort(this@BBSWebViewSubjectActivity, "下载附件失败！")
                    }
                }
            }
        }
    }

    private fun getAttachFileLocalPath(id:String) : String{
        var path  = ""
        attachList.asSequence().filter { it.id == id }.map { path = FileExtensionHelper.getXBPMBBSAttachFolder()+File.separator+it.fileName }.toList()
        return path
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                BBS_TAKE_FROM_PICTURES_CODE -> {
                    val result = data?.extras?.getString(PicturePicker.FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY, "")
                    if (!TextUtils.isEmpty(result)) {
                        readyUploadImages(result!!)
                    }
                }
            }
        }
    }

    private fun readyUploadImages(result: String) {
        if (!uploadingImageMap.containsKey(result) && !uploadedImageMap.containsKey(result)) {
            XLog.debug("choose file:$result")
            addToUploadingImageMap(result)
            addToImageViewList(result)
            mPresenter.uploadImage(result, newReplyId)
        }
    }

    private fun addToUploadingImageMap(filePath: String) {
        val options = BitmapUtil.getImageOptions(filePath)
        val width = options.outWidth
        val height = options.outHeight
        val bean = BBSUploadImageBO()
        bean.width = width
        bean.height = height
        if (width > O2.BBS_IMAGE_MAX_WIDTH) {
            val lv: Double = (width / O2.BBS_IMAGE_MAX_WIDTH).toDouble()
            val showHeight = (height / lv).toInt()
            bean.showWidth = O2.BBS_IMAGE_MAX_WIDTH
            bean.showHeight = showHeight
        } else {
            bean.showHeight = height
            bean.showWidth = width
        }
        uploadingImageMap.put(filePath, bean)
    }

    private fun addToImageViewList(filePath: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_bbs_suject_image_upload_list, layout_bbs_reply_subject_attachment_list, false)
        val imageView = view.findViewById<ImageView>(R.id.image_bbs_subject_image_upload_content)
        val width = dip(48f)
        val bitmap = BitmapUtil.getFitSampleBitmap(filePath, width, width)
        imageView.setImageBitmap(bitmap)
        view.tag = filePath
        layout_bbs_reply_subject_attachment_list.addView(view)
    }

    override fun uploadSuccess(fileId: String, tag: String) {
        val view = layout_bbs_reply_subject_attachment_list.findViewWithTag<RelativeLayout>(tag)
        if (view != null) {
            val loading = view.findViewById<LinearLayout>(R.id.linear_bbs_subject_image_upload_loading)
            loading.gone()
            val deleteIcon = view.findViewById<ImageView>(R.id.image_bbs_subject_image_upload_delete_button)
            deleteIcon.visible()
            val attachmentBaseView = view.findViewById<RelativeLayout>(R.id.relative_bbs_subject_image_upload_grid_top)
            attachmentBaseView.setOnClickListener { v ->
                removeImageFromImageViewList(v)
                uploadedImageMap.remove(tag)
            }
            val imageBean = uploadingImageMap[tag]
            if (imageBean != null) {
                imageBean.fileId = fileId
                uploadedImageMap[tag] = imageBean
            }
        }
        uploadingImageMap.remove(tag)
    }

    override fun uploadFail(tag: String) {
        XToast.toastShort(this, "上传图片到服务器失败！")
        val view = layout_bbs_reply_subject_attachment_list.findViewWithTag<RelativeLayout>(tag)
        if (view != null) {
            removeImageFromImageViewList(view)
        }
        uploadingImageMap.remove(tag)
    }

    private fun removeImageFromImageViewList(view: View?) {
        layout_bbs_reply_subject_attachment_list.removeView(view)
    }

    override fun publishReplySuccess(id: String) {
        hideLoadingDialog()
        if (!TextUtils.isEmpty(id)) {
            edit_bbs_reply_subject_content.text.clear()
            parentId = ""
            uploadedImageMap.map {
                val view = layout_bbs_reply_subject_attachment_list.findViewWithTag<RelativeLayout>(it.key)
                if (view != null) {
                    removeImageFromImageViewList(view)
                }
            }
            newReplyId = UUID.randomUUID().toString()
            web_view_bbs_web_view_subject_content.evaluateJavascript("window.layout.showReply(\"$id\")") { _ ->
                //XLog.debug("showReply， onReceiveValue value=")
            }
            edit_bbs_reply_subject_content.hint = ""
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }
    }

    override fun publishReplyFail() {
        hideLoadingDialog()
        XToast.toastShort(this, "发表回复失败！")
    }

}
