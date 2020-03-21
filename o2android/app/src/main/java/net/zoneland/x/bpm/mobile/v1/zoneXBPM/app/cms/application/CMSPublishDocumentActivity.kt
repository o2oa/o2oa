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
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.TaskWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.WoIdentityListItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goThenKill
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import org.jetbrains.anko.dip
import kotlin.collections.ArrayList

class CMSPublishDocumentActivity : BaseMVPActivity<CMSPublishDocumentContract.View, CMSPublishDocumentContract.Presenter>(),
        CMSPublishDocumentContract.View {


    companion object {
        const val  CATEGORY_KEY = "START_CREATE_DOCUMENT_CATEGORY_KEY"
        const val  IGNORE_TITLE_KEY = "START_CREATE_IGNORE_TITLE_KEY"

        fun start(category: CMSCategoryInfoJson, ignoreTitle: Boolean): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(CATEGORY_KEY, category)
            bundle.putBoolean(IGNORE_TITLE_KEY, ignoreTitle)
            return bundle
        }

    }

    override var mPresenter: CMSPublishDocumentContract.Presenter = CMSPublishDocumentPresenter()


    override fun layoutResId(): Int = R.layout.activity_cms_publish_document


    private var category: CMSCategoryInfoJson? = null
    private val identityList = ArrayList<WoIdentityListItem>()
    private var identity = ""
    private var showCreateMenu = false
    private var ignoreTitle = false

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        category = intent.extras?.getSerializable(CATEGORY_KEY) as? CMSCategoryInfoJson
        ignoreTitle = intent.extras?.getBoolean(IGNORE_TITLE_KEY) ?: false
        if (category == null) {
            XToast.toastShort(this, "参数不正确！")
            finish()
        }
        setupToolBar("新建文档 - ${category?.categoryName}", true)
        if (!ignoreTitle) {
            scroll_cms_publish_content.visible()
        }else {
            showLoadingDialog()
        }
        mPresenter.findCurrentPersonIdentity()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (showCreateMenu) {
            menuInflater.inflate(R.menu.menu_cms_create, menu)
        }
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
        if (ignoreTitle && list.size == 1) {
            identityList.clear()
            identityList.addAll(list)
            createDocument()
        }else {
            showCreateMenu = true
            invalidateOptionsMenu()
            hideLoadingDialog()
            scroll_cms_publish_content.visible()
            radio_group_cms_publish_identity.removeAllViews()
            identityList.clear()
            identityList.addAll(list)
            if (identityList.size > 0) {
                identityList.mapIndexed { index, it ->
                    val radio = layoutInflater.inflate(R.layout.snippet_radio_button, null) as RadioButton
                    radio.text = if (TextUtils.isEmpty(it.unitName)) it.name else it.name + "/" + it.unitName
                    if (index == 0) {
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
            if (!ignoreTitle) {
                ll_cms_publish_title.visible()
            }else {
                ll_cms_publish_title.gone()
            }

        }
    }

    override fun newDocumentId(id: String) {
        hideLoadingDialog()
        if (!TextUtils.isEmpty(id)) {
            val title = edit_cms_publish_title.text.toString()
            goThenKill<CMSWebViewActivity>(CMSWebViewActivity.startBundleData(id, title))
        }else {
            XToast.toastShort(this, "保存失败, 没有返回id！")
        }
    }

    override fun newDocumentFail(msg: String) {
        hideLoadingDialog()
        XToast.toastShort(this, msg)
        if (ignoreTitle) {
            finish()
        }
    }

    override fun startProcessSuccess(workId: String, title: String) {
        hideLoadingDialog()
        val bundle = Bundle()
        bundle.putString(TaskWebViewActivity.WORK_WEB_VIEW_WORK, workId)
        bundle.putString(TaskWebViewActivity.WORK_WEB_VIEW_TITLE, title)
        goThenKill<TaskWebViewActivity>(bundle)
    }

    override fun startProcessFail(message: String) {
        XToast.toastShort(this, "启动流程失败, $message")
        hideLoadingDialog()
        if (ignoreTitle) {
            finish()
        }
    }


    private fun createDocument() {
        val title = edit_cms_publish_title.text.toString()
//        if(TextUtils.isEmpty(title)) {
//            XToast.toastShort(this, "标题不能为空！")
//            return
//        }
        if(TextUtils.isEmpty(identity)) {
            if (identityList.isEmpty()) {
                XToast.toastShort(this, "身份不能为空！")
                return
            }else {
                identity = identityList[0].distinguishedName
            }
        }
        showLoadingDialog()
        if (category?.workflowFlag != null && "" != category?.workflowFlag) {
            mPresenter.startProcess(title, identity, category!!)
        }else {
            val document = CMSDocumentInfoJson()
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

}
