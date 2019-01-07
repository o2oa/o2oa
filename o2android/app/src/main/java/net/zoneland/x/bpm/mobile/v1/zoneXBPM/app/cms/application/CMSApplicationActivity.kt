package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_cms_application.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CMSApplicationPagerAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSApplicationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.addOnPageChangeListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class CMSApplicationActivity : BaseMVPActivity<CMSApplicationContract.View, CMSApplicationContract.Presenter>(), CMSApplicationContract.View {
    override var mPresenter: CMSApplicationContract.Presenter = CMSApplicationPresenter()

    override fun layoutResId(): Int = R.layout.activity_cms_application

    companion object {
        val CMS_APP_OBJECT = "CMS_APP_OBJECT"

        fun startBundleData(applicationInfo: CMSApplicationInfoJson): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(CMS_APP_OBJECT, applicationInfo)
            return bundle
        }
    }

    var currentCategory = ""
    var application: CMSApplicationInfoJson? = null
    val pagerAdapter: CMSApplicationPagerAdapter by lazy { CMSApplicationPagerAdapter(supportFragmentManager, application?.wrapOutCategoryList ?: ArrayList<CMSCategoryInfoJson>()) }
    val menuList = ArrayList<CMSCategoryInfoJson>()
    val menuAdapter: CommonAdapter<CMSCategoryInfoJson> by lazy {
        object : CommonAdapter<CMSCategoryInfoJson>(this, menuList, R.layout.item_tab_application_menu) {
            override fun convert(holder: ViewHolder?, t: CMSCategoryInfoJson?) {
                val textview = holder?.getView<TextView>(R.id.tv_item_tab_application_menu_name)
                textview?.text = t?.categoryName ?: ""
                if (!TextUtils.isEmpty(currentCategory) && currentCategory.equals(t?.categoryName)) {
                    textview?.setTextColor(FancySkinManager.instance().getColor(this@CMSApplicationActivity, R.color.z_color_primary))
                } else {
                    textview?.setTextColor(FancySkinManager.instance().getColor(this@CMSApplicationActivity, R.color.z_color_text_primary))
                }
            }
        }
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        if (intent.extras?.getSerializable(CMS_APP_OBJECT) == null) {
            XToast.toastShort(this, "没有专栏信息，无法打开专栏！")
            finish()
            return
        }
        application = intent.extras?.getSerializable(CMS_APP_OBJECT) as CMSApplicationInfoJson


        setupToolBar(application?.appName ?: "", true)


        lv_drawer_category_list.adapter = menuAdapter
        lv_drawer_category_list.setOnItemClickListener { _, _, position, _ ->
            view_pager_cms_application.currentItem = position
            drawer_layout.closeDrawer(GravityCompat.END)
        }

        image_cms_application_all_category_button.setOnClickListener { drawer_layout.openDrawer(GravityCompat.END) }

        menuList.clear()
        menuList.addAll(application?.wrapOutCategoryList ?: ArrayList<CMSCategoryInfoJson>())


        view_pager_cms_application.visible()
        view_pager_cms_application.adapter = pagerAdapter
        view_pager_cms_application.addOnPageChangeListener {
            onPageSelected { menuAdapter.notifyDataSetChanged() }
        }
        tab_cms_application_category.tabMode = TabLayout.MODE_SCROLLABLE
        tab_cms_application_category.setupWithViewPager(view_pager_cms_application)
        tab_cms_application_category.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                tabStyle(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tabStyle(tab, false)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabStyle(tab)
                view_pager_cms_application.currentItem = tab?.position ?: 0

            }
        })
        tab_cms_application_category.getTabAt(0)?.select()
        menuAdapter.notifyDataSetChanged()
    }


    private fun tabStyle(tab: TabLayout.Tab?, isSelected: Boolean = true) {
        val colorSpan = when (isSelected) {
            true -> ForegroundColorSpan(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
            false -> ForegroundColorSpan(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary))
        }
        val title = tab?.text.toString()
        currentCategory = title
        val spannableString = SpannableString(title)
        spannableString.setSpan(colorSpan, 0, title.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        tab?.text = spannableString
    }

}
