package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_task_list.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.TaskListPagerAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.PictureLoaderService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskApplicationData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.addOnPageChangeListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class TaskListActivity : BaseMVPActivity<TaskListContract.View, TaskListContract.Presenter>(), TaskListContract.View {
    override var mPresenter: TaskListContract.Presenter = TaskListPresenter()


    override fun layoutResId(): Int = R.layout.activity_task_list
    var pictureLoaderService: PictureLoaderService? = null
    var currentTitle = ""
    val applications = ArrayList<TaskApplicationData>()
    val pagerAdapter: TaskListPagerAdapter by lazy { TaskListPagerAdapter(supportFragmentManager, applications) }
    val menuAdapter: CommonAdapter<TaskApplicationData> by lazy {
        object : CommonAdapter<TaskApplicationData>(this, applications, R.layout.item_tab_application_menu) {
            override fun convert(holder: ViewHolder?, t: TaskApplicationData?) {
                val textview = holder?.getView<TextView>(R.id.tv_item_tab_application_menu_name)
                textview?.text = t?.name ?: ""
                if (!TextUtils.isEmpty(currentTitle) && (currentTitle == t?.name)) {
                    textview?.setTextColor(FancySkinManager.instance().getColor(this@TaskListActivity, R.color.z_color_primary))
                } else {
                    textview?.setTextColor(FancySkinManager.instance().getColor(this@TaskListActivity, R.color.z_color_text_primary))
                }

            }
        }
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        val all = TaskApplicationData()
        all.name = "全部"
        all.value = "-1"
        all.count = 0
        applications.add(all)
        setupToolBar(getString(R.string.tab_todo_task), true)

        lv_drawer_application_list.adapter = menuAdapter
        lv_drawer_application_list.setOnItemClickListener { parent, view, position, id ->
            task_list_view_pager_id.currentItem = position
            drawer_layout.closeDrawer(GravityCompat.END)
        }

        image_task_list_all_application_button.setOnClickListener { drawer_layout.openDrawer(GravityCompat.END) }

        showLoadingDialog()
        mPresenter.findTaskApplicationList()
    }

    override fun onResume() {
        super.onResume()
        pictureLoaderService = PictureLoaderService(this)
    }

    override fun onPause() {
        super.onPause()
        pictureLoaderService?.close()
    }

    override fun findTaskApplicationList(list: List<TaskApplicationData>) {
        hideLoadingDialog()
        applications.addAll(list)
        task_list_view_pager_id.visible()
        task_list_view_pager_id.adapter = pagerAdapter
        task_list_view_pager_id.addOnPageChangeListener {
            onPageSelected {
                menuAdapter.notifyDataSetChanged()
            }
        }

        tab_task_list_application.tabMode = TabLayout.MODE_SCROLLABLE
        tab_task_list_application.setupWithViewPager(task_list_view_pager_id)
        tab_task_list_application.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                tabStyle(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tabStyle(tab, false)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabStyle(tab)
                task_list_view_pager_id.currentItem = tab?.position ?: 0
            }
        })
        tab_task_list_application.getTabAt(0)?.select()
        menuAdapter.notifyDataSetChanged()
    }


    override fun findTaskApplicationListFail() {
        XToast.toastShort(this, "查询流程应用失败!")
        hideLoadingDialog()
        finish()
    }

    fun loadApplicationIcon(convertView: View?, appId: String?) {
        pictureLoaderService?.loadProcessAppIcon(convertView, appId)
    }

    private fun tabStyle(tab: TabLayout.Tab?, isSelected: Boolean = true) {
        val colorSpan = when (isSelected) {
            true -> ForegroundColorSpan(FancySkinManager.instance().getColor(this@TaskListActivity, R.color.z_color_primary))
            false -> ForegroundColorSpan(FancySkinManager.instance().getColor(this@TaskListActivity, R.color.z_color_text_primary))
        }
        val title = tab?.text.toString()
        currentTitle = title
        val spannableString = SpannableString(title)
        spannableString.setSpan(colorSpan, 0, title.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        tab?.text = spannableString
    }
}
