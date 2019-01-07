package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_calendar_store.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2BindActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm.CalendarStoreViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.databinding.ActivityCalendarStoreBinding
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarPostData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible

class CalendarStoreActivity : BaseO2BindActivity() {

    val viewmodel by lazy { ViewModelProviders.of(this).get(CalendarStoreViewModel::class.java) }
    val publicList = ArrayList<CalendarPostData>()
    val adapter: CommonRecycleViewAdapter<CalendarPostData> by lazy {
        object : CommonRecycleViewAdapter<CalendarPostData>(this, publicList, R.layout.item_calendar_store_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: CalendarPostData?) {
                if (t != null) {
                    val colorView = holder?.getView<CardView>(R.id.cv_item_calendar_store_color)
                    @SuppressLint("Range")
                    val color = try {
                        Color.parseColor(t.color)
                    } catch (e: Exception) {
                        XLog.error("transform color error ", e)
                        Color.RED
                    }
                    colorView?.setCardBackgroundColor(color)
                    val attentionBtn = holder?.getView<TextView>(R.id.tv_item_calendar_store_attention)
                    if (t.followed) {
                        attentionBtn?.text = getString(R.string.calendar_already_follow)
                    }else {
                        attentionBtn?.text = getString(R.string.calendar_follow)
                    }
                    attentionBtn?.setTextColor(FancySkinManager.instance().getColor(this@CalendarStoreActivity, R.color.z_color_text_hint))
                    attentionBtn?.setOnClickListener { followCalendar(t.id, t.followed) }

                    val divider = holder?.getView<View>(R.id.view_item_calendar_store_divider)
                    if (publicList.last().id == t.id) {
                        divider?.gone()
                    }else {
                        divider?.visible()
                    }

                    var creator = t.createor
                    if (creator.contains("@")) {
                        creator = creator.split("@").first()
                    }
                    holder?.setText(R.id.tv_item_calendar_store_title, t.name)
                            ?.setText(R.id.tv_item_calendar_store_creator, "创建人：$creator")
                }
            }
        }
    }

    private fun followCalendar(id: String, isFollow: Boolean) {
        publicList.filter { it.id == id }.map {
            it.followed = !isFollow
        }
        adapter.notifyDataSetChanged()
        viewmodel.followCalendar(id, isFollow)
    }

    override fun bindView(savedInstanceState: Bundle?) {
        val bind = DataBindingUtil.setContentView<ActivityCalendarStoreBinding>(this, R.layout.activity_calendar_store)
        bind.viewmodel = viewmodel
        bind.setLifecycleOwner(this)

        viewmodel.publicCalendarList().observe(this, Observer { list ->
            if (list!=null && list.isNotEmpty()) {
                publicList.clear()
                publicList.addAll(list)
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.calendar_store), true)
        rv_calendar_store_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_calendar_store_list.adapter = adapter


        viewmodel.loadPublicList()
    }


}
