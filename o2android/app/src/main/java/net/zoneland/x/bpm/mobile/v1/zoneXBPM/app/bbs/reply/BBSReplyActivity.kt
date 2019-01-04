package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.reply


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_bbs_reply_subject.*
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.BBSUploadImageBO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.ReplyFormJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectReplyInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import org.jetbrains.anko.dip
import java.util.*
import kotlin.collections.HashMap


class BBSReplyActivity : BaseMVPActivity<BBSReplyContract.View, BBSReplyContract.Presenter>(), BBSReplyContract.View {
    override var mPresenter: BBSReplyContract.Presenter = BBSReplyPresenter()

    override fun layoutResId(): Int = R.layout.activity_bbs_reply_subject

    companion object {
        val BBS_REPLY_ID_FEEDBACK = "BBS_REPLY_ID_FEEDBACK"
        val BBS_TAKE_FROM_PICTURES_CODE = 0
        val BBS_REPLY_SUBJECT_ID = "BBS_REPLY_SUBJECT_ID"
        val BBS_REPLY_SUBJECT_PARENT_ID = "BBS_REPLY_SUBJECT_PARENT_ID"

        fun startBundleData(subjectId: String, parentId: String = ""): Bundle {
            val bundle = Bundle()
            bundle.putString(BBS_REPLY_SUBJECT_ID, subjectId)
            bundle.putString(BBS_REPLY_SUBJECT_PARENT_ID, parentId)
            return bundle
        }
    }

    var subjectId: String = ""
    var parentId: String = ""
    val newReplyId: String by lazy { UUID.randomUUID().toString() }
    val uploadedImageMap = HashMap<String, BBSUploadImageBO>()//已上传的图片
    val uploadingImageMap = HashMap<String, BBSUploadImageBO>()//正在上传的图片

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        subjectId = intent.extras?.getString(BBS_REPLY_SUBJECT_ID) ?: ""
        parentId = intent.extras?.getString(BBS_REPLY_SUBJECT_PARENT_ID) ?: ""

        setupToolBar(getString(R.string.bbs_reply_subject_title), true)

        if (TextUtils.isEmpty(subjectId)) {
            XToast.toastShort(this, "没有帖子ID！")
            finish()
        } else {
            if (!TextUtils.isEmpty(parentId)) {
                mPresenter.getReplyParentInfo(parentId)
            }
        }

        image_bbs_reply_subject_attachment_add_button.setOnClickListener {
            PicturePicker()
                    .withActivity(this)
                    .chooseType(PicturePicker.CHOOSE_TYPE_SINGLE)
                    .requestCode(BBS_TAKE_FROM_PICTURES_CODE)
                    .start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bbs_publish_reply, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_bbs_publish_reply -> {
                XLog.debug("发表。。。。。。。。回复:" + uploadedImageMap.size)
                publishReply()
            }
        }
        return super.onOptionsItemSelected(item)
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

    override fun uploadFail(tag: String) {
        XToast.toastShort(this, "上传图片到服务器失败！")
        val view = layout_bbs_reply_subject_attachment_list.findViewWithTag<RelativeLayout>(tag)
        if (view != null) {
            removeImageFromImageViewList(view)
        }
        uploadingImageMap.remove(tag)
    }


    override fun uploadSuccess(fileId: String, tag: String) {
        val view = layout_bbs_reply_subject_attachment_list.findViewWithTag<RelativeLayout>(tag)
        if (view != null) {
            val loading = view.findViewById<LinearLayout>(R.id.linear_bbs_subject_image_upload_loading)
            loading.gone()
            val deleteIcon = view.findViewById<ImageView>(R.id.image_bbs_subject_image_upload_delete_button)
            deleteIcon.visible()
            val attachmentBaseView = view.findViewById<RelativeLayout>(R.id.relative_bbs_subject_image_upload_grid_top)
            attachmentBaseView.setOnClickListener { view ->
                removeImageFromImageViewList(view)
                uploadedImageMap.remove(tag)
            }
            val imageBean = uploadingImageMap[tag]
            if (imageBean != null) {
                imageBean.fileId = fileId
                uploadedImageMap.put(tag, imageBean)
            }
        }
        uploadingImageMap.remove(tag)
    }

    override fun getReplyParentFail() {
        XToast.toastShort(this, "获取回复内容失败！")
        relative_bbs_reply_subject_parent.gone()
    }

    override fun getReplyParentSuccess(info: SubjectReplyInfoJson) {
        tv_bbs_reply_subject_parent_creator.text = if (TextUtils.isEmpty(info.creatorNameShort)){info.creatorName}else{info.creatorNameShort}
        val time = if (info.createTime != null && info.createTime.length > 16) info.createTime.substring(0, 16) else info.createTime
        tv_bbs_reply_subject_parent_time.text = getString(R.string.bbs_view_subject_publish_time) + " " + time
        var html = info.content
        html = HtmlRegexpUtil.fiterHtmlTag(html, "img")//去掉img标签
        XLog.debug("parent reply html:" + html)
        tv_bbs_reply_subject_parent_content.text = html
        relative_bbs_reply_subject_parent.visible()

    }

    override fun publishReplyFail() {
        hideLoadingDialog()
        XToast.toastShort(this, "发表回复失败！")
    }

    override fun publishReplySuccess(replyId: String) {
        hideLoadingDialog()
        XLog.debug("reply success $replyId")
        val bundle = Bundle()
        bundle.putString(BBS_REPLY_ID_FEEDBACK, replyId)
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        finish()
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
        XLog.debug("content:" + content)
        content = formatToHtml(content)
        XLog.debug("content html:" + content)
        content += addAttachmentToContent()
        XLog.debug("content html and image:" + content)

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

    private fun addAttachmentToContent(): String {
        var content = ""
        if (uploadedImageMap != null && !uploadedImageMap.isEmpty()) {
            uploadedImageMap.map {
                val attachmentURL = APIAddressHelper.instance().getBBSAttachmentURL(it.value.fileId)
                val path = "<p><img src=\"$attachmentURL\"  style=\"display: block; margin: auto; width:${it.value.showWidth}px; max-width:100%;\" /></p>"
                content += path
            }
        }
        return content
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


    private fun removeImageFromImageViewList(view: View?) {
        layout_bbs_reply_subject_attachment_list.removeView(view)
    }
}
