package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_bbs_main.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.section.BBSSectionActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class BBSMainActivity : BaseMVPActivity<BBSMainContract.View, BBSMainContract.Presenter>(), BBSMainContract.View {
    override var mPresenter: BBSMainContract.Presenter = BBSMainPresenter()


    override fun layoutResId(): Int = R.layout.activity_bbs_main

    val titles:Array<String> by lazy { resources.getStringArray(R.array.bbs_main_page_list) }
    val fragments = ArrayList<Fragment>()
    val pagerAdapter : FragmentPagerAdapter by lazy {
        object : FragmentPagerAdapter(supportFragmentManager){
            override fun getItem(position: Int): Fragment = fragments[position]

            override fun getCount(): Int = fragments.size

            override fun getPageTitle(position: Int): CharSequence = titles[position]
        }
    }
    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar("", true, true)
        fragments.add(BBSMainCollectionFragment())
        fragments.add(BBSMainSectionFragment())
        view_pager_bbs_main.adapter = pagerAdapter
        toolbar_snippet_tab_layout.tabMode = TabLayout.MODE_FIXED
        toolbar_snippet_tab_layout.setupWithViewPager(view_pager_bbs_main)
        tv_bottom_bbs_main_cancel_collect_button.setOnClickListener { (fragments[0] as BBSMainCollectionFragment).cancelCollection() }
        mPresenter.whetherThereHasCollections()
    }

    override fun onBackPressed() {
        if (view_pager_bbs_main.currentItem == 0) {
            val first = (fragments[0] as BBSMainCollectionFragment)
            if (first.canCheck){
                first.hideCheckTool()
                return
            }
        }
        super.onBackPressed()

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_scale_in, R.anim.activity_scale_out)
    }

    override fun whetherThereHasAnyCollections(flag: Boolean) {
        if (flag) {
            view_pager_bbs_main.currentItem = 0
        }else {
            view_pager_bbs_main.currentItem = 1
        }
    }

    fun showCancelButton() {
        tv_bottom_bbs_main_cancel_collect_button.visible()
    }

    fun hideCancelButton() {
        tv_bottom_bbs_main_cancel_collect_button.gone()
    }

    fun enterBBSSection(sectionId:String, sectionName:String) {
        XLog.debug("点击论坛板块：$sectionName, id:$sectionId")
        go<BBSSectionActivity>(BBSSectionActivity.startBundleData(sectionId, sectionName))
    }

}
