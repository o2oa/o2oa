package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.publish


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_bbs_publish_subject.*
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.BBSUploadImageBO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SectionInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectPublishFormJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.hideSoftInput
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.BBSSubjectTypePopupWindow
import org.jetbrains.anko.dip
import java.util.*
import kotlin.collections.ArrayList


class BBSPublishSubjectActivity : BaseMVPActivity<BBSPublishSubjectContract.View, BBSPublishSubjectContract.Presenter>(),
        BBSPublishSubjectContract.View {
    override var mPresenter: BBSPublishSubjectContract.Presenter = BBSPublishSubjectPresenter()

    override fun layoutResId(): Int = R.layout.activity_bbs_publish_subject

    companion object {
        val BBS_SECTION_ID = "BBS_SECTION_ID"
        val BBS_TAKE_FROM_PICTURES_CODE = 0

        fun startBundleData(sectionId: String): Bundle {
            val bundle = Bundle()
            bundle.putString(BBS_SECTION_ID, sectionId)
            return bundle
        }
    }

    var sectionId = ""
    private val typeList = ArrayList<String>()
    private val newSubjectId: String by lazy { UUID.randomUUID().toString() }
    private lateinit var popupWindow: BBSSubjectTypePopupWindow
    private val uploadedImageMap = HashMap<String, BBSUploadImageBO>()//已上传的图片
    private val uploadingImageMap = HashMap<String, BBSUploadImageBO>()//正在上传的图片

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        sectionId = intent.extras?.getString(BBS_SECTION_ID) ?: ""
        if (TextUtils.isEmpty(sectionId)) {
            XToast.toastShort(this, "没有传入版块ID，无法发帖！")
            finish()
            return
        }
        setupToolBar(getString(R.string.bbs_publish), true)

        layout_bbs_publish_subject_type_choose_button.setOnClickListener {
            XLog.debug("选择主题类型")
            hideSoftInput()
            popupWindow.animationStyle = R.style.dir_popupwindow_anim
            popupWindow.showAtLocation(image_bbs_publish_subject_attachment_add_button, Gravity.BOTTOM, 0, 0)
            ZoneUtil.lightOff(this@BBSPublishSubjectActivity)
        }

        layout_bbs_publish_subject_attachment_list.viewTreeObserver.addOnGlobalLayoutListener {
            if(layout_bbs_publish_subject_attachment_list.childCount != 0) {
                image_bbs_publish_subject_attachment_add_button.setImageResource(R.mipmap.icon_choose_image_red)
            } else {
                image_bbs_publish_subject_attachment_add_button.setImageResource(R.mipmap.icon_choose_image_gray)
            }
        }

        image_bbs_publish_subject_attachment_add_button.setOnClickListener {
            XLog.debug("选择图片附件")
            PicturePicker()
                    .withActivity(this)
                    .chooseType(PicturePicker.CHOOSE_TYPE_SINGLE)
                    .requestCode(BBS_TAKE_FROM_PICTURES_CODE)
                    .start()
        }

        //查询版块对象
        mPresenter.querySectionById(sectionId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bbs_publish_subject, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_bbs_publish_subject -> {
                XLog.debug("发表。。。。。。。。attachment:" + uploadedImageMap.size)
                publishSubject()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun sectionInfo(info: SectionInfoJson) {
        tv_bbs_publish_subject_section.text = info.sectionName
        info.subjectType.split("|").filter { !TextUtils.isEmpty(it.trim()) }.map { typeList.add(it) }
        popupWindow = BBSSubjectTypePopupWindow(this, typeList)
        popupWindow.setOnDismissListener { ZoneUtil.lightOn(this@BBSPublishSubjectActivity) }
        popupWindow.setListener { type ->
            XLog.debug("choose type:" + type)
            tv_bbs_publish_subject_type.text = type
            popupWindow.dismiss()
        }

    }

    override fun querySectionFail() {
        XToast.toastShort(this, "版块信息查询失败！")
        finish()
    }

    override fun publishFail() {
        XToast.toastShort(this, "发布帖子失败！")
        hideLoadingDialog()
    }

    override fun publishSuccess(id: String) {
        XToast.toastShort(this, "发表成功！")
        finish()
    }

    override fun uploadFail(tag: String) {
        XToast.toastShort(this, "上传图片到服务器失败！")
        val view = layout_bbs_publish_subject_attachment_list.findViewWithTag<RelativeLayout>(tag)
        if (view != null) {
            removeImageFromImageViewList(view)
        }
        uploadingImageMap.remove(tag)
    }

    override fun uploadSuccess(fileId: String, tag: String) {
        val view = layout_bbs_publish_subject_attachment_list.findViewWithTag<RelativeLayout>(tag)
        if (view != null) {
            val loading = view.findViewById<LinearLayout>(R.id.linear_bbs_subject_image_upload_loading)
            loading.gone()
            val deleteIcon = view.findViewById<ImageView>(R.id.image_bbs_subject_image_upload_delete_button)
            deleteIcon.visible()
            val attachmentBaseView = view.findViewById<RelativeLayout>(R.id.relative_bbs_subject_image_upload_grid_top)
            attachmentBaseView.setOnClickListener { _ ->
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

    private fun publishSubject() {
        val type = tv_bbs_publish_subject_type.text.toString()
        if (TextUtils.isEmpty(type) || getString(R.string.bbs_publish_subject_type) == type) {
            XToast.toastShort(this, "请选择主题分类")
            return
        }
        val title = edit_bbs_publish_subject_title.text.toString()
        if (TextUtils.isEmpty(title)) {
            XToast.toastShort(this, "请填写主题")
            return
        }
        val summary = edit_bbs_publish_subject_summary.text.toString()
        if (!uploadingImageMap.isEmpty()) {
            XToast.toastShort(this, "图片正在上传中。。。")
            return
        }
        var content = edit_bbs_publish_subject_content.text.toString()
        if (TextUtils.isEmpty(content) && uploadedImageMap.isEmpty()) {
            XToast.toastShort(this, "请填写主题内容")
            return
        }
        XLog.debug("content:" + content)
        content = formatToHtml(content)
        XLog.debug("content html:" + content)
        content += addAttachmentToContent()
        XLog.debug("content html and image:$content")

        val form = SubjectPublishFormJson(newSubjectId,
                type,
                "信息",
                title,
                summary,
                content,
                sectionId,
                AndroidUtils.getDeviceBrand() + AndroidUtils.getDeviceModelNumber(),
                O2.DEVICE_TYPE,
                attachmentList())
        showLoadingDialog()
        mPresenter.publishSubject(form)
    }

    private fun attachmentList(): ArrayList<String> {
        val array = ArrayList<String>()
        if (!uploadedImageMap.isEmpty()) {
            uploadedImageMap.map { array.add(it.value.fileId) }
        }
        return array
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
            addToUploadingImageMap(result)
            addToImageViewList(result)
            mPresenter.uploadImage(result, newSubjectId)
        }
    }

    private fun addToImageViewList(filePath: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_bbs_suject_image_upload_list, layout_bbs_publish_subject_attachment_list, false)
        val imageView = view.findViewById<ImageView>(R.id.image_bbs_subject_image_upload_content)
        val width = dip(48f)
        val bitmap = BitmapUtil.getFitSampleBitmap(filePath, width, width)
        imageView.setImageBitmap(bitmap)
        view.tag = filePath
        layout_bbs_publish_subject_attachment_list.addView(view)
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

    private fun removeImageFromImageViewList(view: View?) {
        layout_bbs_publish_subject_attachment_list.removeView(view)
    }

}
