package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_calendar_main.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2Activity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm.CalendarViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.GroupRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarEventInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoPickViewData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.addDrawerListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import org.jetbrains.anko.dip
import java.util.*


class CalendarMainActivity : BaseO2Activity() {

    override fun layoutResId(): Int = R.layout.activity_calendar_main

    private val monthView by lazy { MonthCalendarViewModelFragment() }
    private val weekView by lazy { WeekCalendarViewFragment() }
    private val dayView by lazy { DayCalendarViewFragment() }

    val viewModel: CalendarViewModel by lazy { ViewModelProviders.of(this).get(CalendarViewModel::class.java) }
    val calendarIds = ArrayList<String>()
    val groups = ArrayList<Group<String, CalendarInfoPickViewData>>()
    val adapter: GroupRecyclerViewAdapter<String, CalendarInfoPickViewData> by lazy {
        object : GroupRecyclerViewAdapter<String, CalendarInfoPickViewData>(groups,
                R.layout.item_fragment_calendar_dialog_list_header, R.layout.item_fragment_calendar_dialog_list_body) {
            override fun onBindHeaderViewHolder(holder: CommonRecyclerViewHolder, header: String, position: Int) {
                holder.setText(R.id.tv_item_fragment_calendar_dialog_list_header_title, header)
            }

            override fun onBindChildViewHolder(holder: CommonRecyclerViewHolder, child: CalendarInfoPickViewData, position: Int) {
                val divider = holder.getView<View>(R.id.view_item_fragment_calendar_dialog_list_body_divider)
                val colorView = holder.getView<CardView>(R.id.cv_item_fragment_calendar_dialog_list_body_color)
                val checkBox = holder.getView<CheckBox>(R.id.cb_item_fragment_calendar_dialog_list_body_choose)
                val editBtn = holder.getView<ImageView>(R.id.image_tem_fragment_calendar_dialog_list_body_edit)
                editBtn.gone()
                divider.visible()
                groups.forEach { group ->
                    if (group.children.isNotEmpty() && child.id == group.children.last().id) {
                        divider.gone()
                    }
                }
                checkBox.isChecked = calendarIds.any { child.id == it }
                holder.setText(R.id.tv_item_fragment_calendar_dialog_list_body_title, child.name)
                @SuppressLint("Range")
                val color = try {
                    Color.parseColor(child.color)
                } catch (e: Exception) {
                    XLog.error("transform color error ", e)
                    Color.RED
                }
                colorView?.setCardBackgroundColor(color)

                checkBox.setOnClickListener {
                    toggleCheck(checkBox.isChecked, child.id)
                }
                //@date 2018-8-8 暂时不修改组织日历
                if (child.manageable && child.type == "PERSON") {
                    editBtn.visible()
                    editBtn.setOnClickListener {
                        XLog.info("点击编辑日历。。。。。。${child.name}.........")
                        go<CreateCalendarActivity>(CreateCalendarActivity.startEdit(child.id))
                    }
                }
                holder.convertView.setOnClickListener {
                    val checked = checkBox.isChecked
                    checkBox.isChecked = !checked
                    toggleCheck(!checked, child.id)
                }
            }
        }
    }

    private var mShowBar = false

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.calendar_name))
        toolbar?.setNavigationIcon(R.mipmap.menu_left)
        toolbar?.setNavigationOnClickListener { drawer_calendar_main.openDrawer(Gravity.START, true) }

        viewModel.getGroups().observe(this, Observer { list ->
            if (list != null) {
                calendarIds.clear()
                list.forEach { group ->
                    group.children.forEach { calendar ->
                        calendarIds.add(calendar.id)
                    }
                }
                groups.clear()
                groups.addAll(list)
                adapter.notifyDataSetChanged()
                setCalendarIdsToFragment()
            }
        })


        val transaction = supportFragmentManager.beginTransaction()
        transaction?.replace(R.id.frame_calendar_content, monthView)
        transaction?.commit()

        rl_calendar_main_month_btn.setOnClickListener {
            changeIndicator(0)
            hideBar()
        }
        rl_calendar_main_week_btn.setOnClickListener {
            changeIndicator(1)
            hideBar()
        }
        rl_calendar_main_day_btn.setOnClickListener {
            changeIndicator(2)
            hideBar()
        }
        rv_calendar_main_calendar_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_calendar_main_calendar_list.adapter = adapter
        drawer_calendar_main.addDrawerListener {
            onDrawerClosed {
                setCalendarIdsToFragment()
            }
        }
        cl_calendar_main_create_btn.setOnClickListener {
            createCalendar()
            drawer_calendar_main.closeDrawer(Gravity.START)
        }
        cl_calendar_main_setting_btn.setOnClickListener {
            calendarStore()
            drawer_calendar_main.closeDrawer(Gravity.START)
        }
        fab_calendar_main_add_event.setOnClickListener {
            createCalendarEvent()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCalendarGroups()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calendar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (mShowBar) {
            menu?.findItem(R.id.menu_calendar_tool)?.setIcon(R.mipmap.menu_calendar_tool_up)
        } else {
            menu?.findItem(R.id.menu_calendar_tool)?.setIcon(R.mipmap.menu_calendar_tool_down)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_calendar_tool -> {
                if (mShowBar) {
                    hideBar()
                } else {
                    showBar()
                }
            }
            R.id.menu_calendar_today -> {
                today()
            }


        }
        return super.onOptionsItemSelected(item)
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_scale_in, R.anim.activity_scale_out)
    }


    fun updateActivityTitle(title: String) {
        updateToolbarTitle(title)
    }

    fun getCalendarIds(): List<String> = calendarIds

    /**
     * 修改日程事件
     */
    fun editEvent(event: CalendarEventInfoData) {
        val arr = ArrayList<CalendarInfoPickViewData>()
        groups[0].children.forEach { arr.add(it) }
        go<CreateEventActivity>(CreateEventActivity.startEdit(event, arr))
    }

    /**
     * 跳转到今天
     */
    private fun today() {
        if (monthView.isVisible) monthView.jump2Today()
        if (weekView.isVisible) weekView.jump2Today()
        if (dayView.isVisible) dayView.jump2Today()
    }

    /**
     * 创建日程事件
     */
    private fun createCalendarEvent() {
        if (groups.isNotEmpty() && groups[0].children.isNotEmpty()) {
            val arr = ArrayList<CalendarInfoPickViewData>()
            groups[0].children.forEach { arr.add(it) }
            go<CreateEventActivity>(CreateEventActivity.startCreate(arr))
        } else {
            XToast.toastShort(this, "日历数据为空！！！！")
        }
    }

    private fun createCalendar() {
        go<CreateCalendarActivity>()
    }

    private fun calendarStore() {
        go<CalendarStoreActivity>()
    }

    private fun toggleCheck(checked: Boolean, id: String) {
        if (checked) {
            calendarIds.add(id)
        } else {
            calendarIds.remove(id)
        }
        XLog.info("toggleCheck, calendarIds:${calendarIds.size}")
    }

    private fun setCalendarIdsToFragment() {
        if (monthView.isVisible) monthView.setCalendarFilter(calendarIds)
        if (weekView.isVisible) weekView.setCalendarFilter(calendarIds)
        if (dayView.isVisible) dayView.setCalendarFilter(calendarIds)
    }

    private fun hideBar() {
        ll_calendar_main_top_bar.gone()
        mShowBar = false
        invalidateOptionsMenu()
    }

    private fun showBar() {
        ll_calendar_main_top_bar.visible()
        val start = 0 - dip(36).toFloat()
        ll_calendar_main_top_bar.translationY = start
        frame_calendar_content.translationY = start
        val animationBar = ObjectAnimator.ofFloat(ll_calendar_main_top_bar, "translationY", start, 0.toFloat())
        animationBar.duration = 400
        val animationContent = ObjectAnimator.ofFloat(frame_calendar_content, "translationY", start, 0.toFloat())
        animationContent.duration = 400
        val set = AnimatorSet()
        set.play(animationBar).with(animationContent)
        set.start()
        mShowBar = true
        invalidateOptionsMenu()
    }

    private fun changeIndicator(index: Int) {
        when (index) {
            0 -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction?.replace(R.id.frame_calendar_content, monthView)
                transaction?.commit()
                tv_calendar_main_month_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_primary))
                view_calendar_main_month_indicator.visible()
                tv_calendar_main_week_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_text_primary))
                view_calendar_main_week_indicator.gone()
                tv_calendar_main_day_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_text_primary))
                view_calendar_main_day_indicator.gone()
            }
            1 -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction?.replace(R.id.frame_calendar_content, weekView)
                transaction?.commit()
                tv_calendar_main_month_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_text_primary))
                view_calendar_main_month_indicator.gone()
                tv_calendar_main_week_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_primary))
                view_calendar_main_week_indicator.visible()
                tv_calendar_main_day_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_text_primary))
                view_calendar_main_day_indicator.gone()
            }
            2 -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction?.replace(R.id.frame_calendar_content, dayView)
                transaction?.commit()
                tv_calendar_main_month_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_text_primary))
                view_calendar_main_month_indicator.gone()
                tv_calendar_main_week_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_text_primary))
                view_calendar_main_week_indicator.gone()
                tv_calendar_main_day_label.setTextColor(ContextCompat.getColor(this, R.color.z_color_primary))
                view_calendar_main_day_indicator.visible()
            }
        }
    }

}
