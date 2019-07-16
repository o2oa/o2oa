package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_cms_publish_document.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view.CMSWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.WoIdentityListItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goThenKill
import org.jetbrains.anko.dip
import java.util.*
import kotlin.collections.ArrayList

class CMSPublishDocumentActivity : BaseMVPActivity<CMSPublishDocumentContract.View, CMSPublishDocumentContract.Presenter>(),
        CMSPublishDocumentContract.View {


    companion object {
        const val  CATEGORY_KEY = "START_CREATE_DOCUMENT_CATEGORY_KEY"

        fun start(category: CMSCategoryInfoJson): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(CATEGORY_KEY, category)
            return bundle
        }

    }

    override var mPresenter: CMSPublishDocumentContract.Presenter = CMSPublishDocumentPresenter()


    override fun layoutResId(): Int = R.layout.activity_cms_publish_document


    private var category: CMSCategoryInfoJson? = null
    private val identityList = ArrayList<WoIdentityListItem>()
    private var identity = ""
    override fun afterSetContentView(savedInstanceState: Bundle?) {
        category = intent.extras?.getSerializable(CATEGORY_KEY) as? CMSCategoryInfoJson
        if (category == null) {
            XToast.toastShort(this, "参数不正确！")
            finish()
        }
        setupToolBar("新建文档 - ${category?.categoryName}", true)
//        tv_cms_publish_header.text = "新建文档 - ${category?.categoryName}"
        mPresenter.findCurrentPersonIdentity()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.menu_cms_create, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_cms_create) {
            createDocument()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun currentPersonIdentities(list: List<WoIdentityListItem>) {
        radio_group_cms_publish_identity.removeAllViews()
        identityList.clear()
        identityList.addAll(list)
        if (identityList.size>0) {
            identityList.mapIndexed { index, it ->
                val radio = layoutInflater.inflate(R.layout.snippet_radio_button, null) as RadioButton
                radio.text = if (TextUtils.isEmpty(it.unitName)) it.name else it.name+"/"+it.unitName
                if (index==0) {
                    radio.isChecked = true
                    identity = it.distinguishedName
                }
                radio.id = 100 + index//这里必须添加id 否则后面获取选中Radio的时候 group.getCheckedRadioButtonId() 拿不到id 会有空指针异常
                val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(0, dip(10f), 0, 0)
                radio_group_cms_publish_identity.addView(radio, layoutParams)
            }
        }
        radio_group_cms_publish_identity.setOnCheckedChangeListener { _, checkedId ->
            val index = checkedId - 100
            identity = identityList[index].distinguishedName
        }
    }

    override fun newDocumentId(id: String) {
        if (!TextUtils.isEmpty(id)) {
            val title = edit_cms_publish_title.text.toString()
            goThenKill<CMSWebViewActivity>(CMSWebViewActivity.startBundleData(id, title))
        }else {
            XToast.toastShort(this, "保存失败, 没有返回id！")
        }
    }

    override fun newDocumentFail() {
        XToast.toastShort(this, "保存失败！")
    }

    private fun createDocument() {
        val title = edit_cms_publish_title.text.toString()
        if(TextUtils.isEmpty(title)) {
            XToast.toastShort(this, "标题不能为空！")
            return
        }
        if(TextUtils.isEmpty(identity)) {
            XToast.toastShort(this, "身份不能为空！")
            return
        }
        val document = CMSDocumentInfoJson()
//        document.id = UUID.randomUUID().toString()
        document.title = title
        document.appId = category!!.appId
        document.categoryId = category!!.id
        document.categoryAlias = category!!.categoryAlias
        document.categoryName = category!!.categoryName
        document.creatorIdentity = identity
        document.docStatus = "draft"
        document.isNewDocument = true
        XLog.info(document.toString())
        mPresenter.newDocument(document)

    }

}
