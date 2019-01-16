package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.Gravity
import android.view.View
import android.widget.*
import com.bigkoo.pickerview.OptionsPickerView
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.jzxiang.pickerview.listener.OnDateSetListener
import kotlinx.android.synthetic.main.activity_create_event.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm.CreateEventViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.databinding.ActivityCreateEventBinding
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarEventInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoPickViewData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarPickerOption
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ImmersedStatusBarUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.LoadingDialog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.dip

class CreateEventActivity : AppCompatActivity(), OnDateSetListener {


    private val STARTTIME_TAG = "startTime"
    private val ENDTIME_TAG = "endTime"
    private val REPEAT_UNTIL_DATE_TAG = "RepeatUntilDate"
    private val viewModel: CreateEventViewModel by lazy { ViewModelProviders.of(this).get(CreateEventViewModel::class.java) }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private var eventOld:CalendarEventInfoData? = null

    companion object {
        private val CALENDAR_LIST_KEY = "CALENDAR_LIST_KEY"
        private val CALENDAR_EVENT_KEY = "CALENDAR_EVENT_KEY"
        /**
         * 新增
         */
        fun startCreate(calendars: ArrayList<CalendarInfoPickViewData>): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(CALENDAR_LIST_KEY, calendars)
            return bundle
        }

        /**
         * 修改
         */
        fun startEdit(event: CalendarEventInfoData, calendars: ArrayList<CalendarInfoPickViewData>): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(CALENDAR_LIST_KEY, calendars)
            bundle.putParcelable(CALENDAR_EVENT_KEY, event)
            return bundle
        }
    }

    private var calendars: ArrayList<CalendarInfoPickViewData> = ArrayList()
    private val remindList = ArrayList<CalendarPickerOption>()
    private val repeatList = ArrayList<CalendarPickerOption>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = DataBindingUtil.setContentView<ActivityCreateEventBinding>(this, R.layout.activity_create_event)
        bind.viewmodel = viewModel
        bind.setLifecycleOwner(this)

        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)

        initView()

        initData()


    }

    override fun onDateSet(timePickerView: TimePickerDialog?, millseconds: Long) {
        val tag = timePickerView?.tag
        if (tag != null) {
            when (tag) {
                STARTTIME_TAG -> {
                    viewModel.setStartTimeAndFormat(millseconds)
                }
                ENDTIME_TAG -> {
                    viewModel.setEndTimeAndFormat(millseconds)
                }
                REPEAT_UNTIL_DATE_TAG -> {
                    viewModel.setUntilDateAndFormat(millseconds)
                }
            }
        }
    }

    fun clickSaveBtn(view: View) {
        if (TextUtils.isEmpty(viewModel.eventTitle.value)) {
            XToast.toastShort(this, "事件标题不能为空！")
            return
        }
        if (!viewModel.isTimeCorrect()) {
            XToast.toastShort(this, "结束时间不能小于开始时间！")
            return
        }
        if (TextUtils.isEmpty(viewModel.eventId.value)) {
            viewModel.saveEvent()
        } else {
            if (TextUtils.isEmpty(viewModel.recurrenceRule.value)) {
                viewModel.updateEvent(0)
            } else {
                //show dialog
                O2DialogSupport.openCustomViewDialog(this, getString(R.string.calendar_update_type_title),
                        R.layout.dialog_calendar_event_update_type) { dialog ->
                    val group = dialog.findViewById<RadioGroup>(R.id.group_dialog_calendar_event_update_type)
                    when (group.checkedRadioButtonId) {
                        R.id.radio_dialog_calendar_event_update_type_single -> {
                            viewModel.updateEvent(0)
                        }
                        R.id.radio_dialog_calendar_event_update_type_after -> {
                            viewModel.updateEvent(1)
                        }
                        R.id.radio_dialog_calendar_event_update_type_all -> {
                            viewModel.updateEvent(2)
                        }
                    }
                }
            }
        }
    }

    fun clickDeleteBtn(view: View) {
        if (TextUtils.isEmpty(viewModel.recurrenceRule.value)) {
            viewModel.deleteEvent(0)
        } else {
            //show dialog
            val dialog = O2DialogSupport.openCustomViewDialog(this, getString(R.string.calendar_delete_type_title),
                    R.layout.dialog_calendar_event_update_type) { dialog ->
                val group = dialog.findViewById<RadioGroup>(R.id.group_dialog_calendar_event_update_type)
                when (group.checkedRadioButtonId) {
                    R.id.radio_dialog_calendar_event_update_type_single -> {
                        viewModel.deleteEvent(0)
                    }
                    R.id.radio_dialog_calendar_event_update_type_after -> {
                        viewModel.deleteEvent(1)
                    }
                    R.id.radio_dialog_calendar_event_update_type_all -> {
                        viewModel.deleteEvent(2)
                    }
                }
            }
            val radioSingle = dialog.findViewById<RadioButton>(R.id.radio_dialog_calendar_event_update_type_single)
            radioSingle.text = getString(R.string.calendar_delete_type_single)
            val radioAfter = dialog.findViewById<RadioButton>(R.id.radio_dialog_calendar_event_update_type_after)
            radioAfter.text = getString(R.string.calendar_delete_type_after)
            val radioAll = dialog.findViewById<RadioButton>(R.id.radio_dialog_calendar_event_update_type_all)
            radioAll.text = getString(R.string.calendar_delete_type_all)
        }
    }

    fun clickClose(view: View) {
        finish()
    }

    /**
     * click事件
     * 选择时间 ， 开始时间和结束时间
     */
    fun chooseDay(view: View) {
        var tag = STARTTIME_TAG
        var title = "开始时间选择"
        var currentTime = System.currentTimeMillis()
        val type = if (viewModel.isAllDayEvent.value == true) Type.YEAR_MONTH_DAY else Type.MONTH_DAY_HOUR_MIN
        when (view.id) {
            R.id.tv_create_calendar_event_start_time -> {
                tag = STARTTIME_TAG
                title = "开始时间选择"
                val start = viewModel.startTime.value
                if (!TextUtils.isEmpty(start)) {
                    val date = if (viewModel.isAllDayEvent.value == true) {
                        DateHelper.convertStringToDate(CalendarOB.ALL_DAY_DATE_FORMAT, start)
                    } else {
                        DateHelper.convertStringToDate(CalendarOB.NOT_ALL_DAY_DATE_FORMAT, start)
                    }
                    if (date != null) {
                        currentTime = date.time
                    }
                }
            }
            R.id.tv_create_calendar_event_end_time -> {
                tag = ENDTIME_TAG
                title = "结束时间选择"
                val end = viewModel.endTime.value
                if (!TextUtils.isEmpty(end)) {
                    val date = if (viewModel.isAllDayEvent.value == true) {
                        DateHelper.convertStringToDate(CalendarOB.ALL_DAY_DATE_FORMAT, end)
                    } else {
                        DateHelper.convertStringToDate(CalendarOB.NOT_ALL_DAY_DATE_FORMAT, end)
                    }
                    if (date != null) {
                        currentTime = date.time
                    }
                }
            }
        }
        val dialog = TimePickerDialog.Builder()
                .setCallBack(this)
                .setTitleStringId(title)
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(currentTime)
                .setThemeColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
                .setType(type)
                .setWheelItemTextSize(12)
                .build()

        dialog.show(supportFragmentManager, tag)
    }

    /**
     * click事件
     * 选择日历
     */
    fun chooseCalendar(view: View) {
        val picker = OptionsPickerView.Builder(this, OptionsPickerView.OnOptionsSelectListener { option1, _, _, _ ->
            XLog.info("select calendar: $option1")
            viewModel.setCalendar(calendars[option1])
        }).setTitleText(getString(R.string.calendar_menu_calendar_choose))
                .isDialog(true)
                .build()
        picker.setPicker(calendars)
        var selectIndex = calendars.indexOfFirst { it.id == viewModel.calendarId.value }
        if (selectIndex < 0) {
            selectIndex = 0
        }
        picker.setSelectOptions(selectIndex)
        picker.showDialog()
    }

    /**
     * click事件
     * 选择提醒时间
     */
    fun chooseRemind(view: View) {
        val picker = OptionsPickerView.Builder(this, OptionsPickerView.OnOptionsSelectListener { option1, _, _, _ ->
            XLog.info("select remind: $option1")
            viewModel.setSelectedRemind(remindList[option1])
        }).setTitleText(getString(R.string.calendar_remind_choose))
                .isDialog(true)
                .build()
        var selectIndex = remindList.indexOfFirst { it.value == viewModel.remindValue.value }
        if (selectIndex < 0) {
            selectIndex = 0
        }
        picker.setPicker(remindList)
        picker.setSelectOptions(selectIndex)
        picker.showDialog()
    }

    /**
     * click事件
     * 选择重复方式
     */
    fun chooseRepeat(view: View) {
        val picker = OptionsPickerView.Builder(this, OptionsPickerView.OnOptionsSelectListener { option1, _, _, _ ->
            XLog.info("select repeat: $option1")
            viewModel.setSelectedRepeat(repeatList[option1])
        }).setTitleText(getString(R.string.calendar_repeat_choose))
                .isDialog(true)
                .build()
        picker.setPicker(repeatList)
        var selectIndex = repeatList.indexOfFirst { it.value == viewModel.repeatValue.value }
        if (selectIndex < 0) {
            selectIndex = 0
        }
        picker.setSelectOptions(selectIndex)
        picker.showDialog()
    }

    /**
     * click事件
     * 选择截至日期
     */
    fun chooseRepeatUntilDate(view: View) {
        val title = "选择截至日期"
        val until = viewModel.untilDate.value
        var currentTime = System.currentTimeMillis()
        if (until != null && until != CalendarOB.NONE) {
            val day = DateHelper.convertStringToDate(CalendarOB.ALL_DAY_DATE_FORMAT, until)
            if (day != null) {
                currentTime = day.time
            }
        }
        val dialog = TimePickerDialog.Builder()
                .setCallBack(this)
                .setTitleStringId(title)
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(currentTime)
                .setThemeColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextSize(12)
                .build()

        dialog.show(supportFragmentManager, REPEAT_UNTIL_DATE_TAG)
    }

    /**
     * 点击clear按钮 清除截至日期
     */
    fun clearUntilDate(view: View) {
        viewModel.untilDate.value = CalendarOB.NONE
    }

    /**
     * click事件
     * 每周重复的时候选择周
     */
    fun chooseWeekDay(view: View) {
        if (view is CardView) {
            val tag = view.tag
            tag as String
            val selectList = viewModel.repeatWeekList.value
            if (selectList?.any { it == tag } == true) {
                if (selectList.size > 1) {
                    viewModel.repeatWeekList.value = selectList.filter { it != tag }
                }
            } else {
                val newlist = arrayListOf(tag)
                selectList?.forEach {
                    newlist.add(it)
                }
                viewModel.repeatWeekList.value = newlist.toList()
            }
        }
    }

    private fun refreshWeekCardView(card: CardView, cardChecked: Boolean) {
        if (!cardChecked) {
            card.setCardBackgroundColor(Color.WHITE)
            val cardTv = card.getChildAt(0)
            if (cardTv is TextView) {
                cardTv.setTextColor(FancySkinManager.instance().getColor(this@CreateEventActivity, R.color.z_color_text_primary))
            }
        } else {
            card.setCardBackgroundColor(FancySkinManager.instance().getColor(this@CreateEventActivity, R.color.z_color_primary))
            val cardTv = card.getChildAt(0)
            if (cardTv is TextView) {
                cardTv.setTextColor(Color.WHITE)
            }
        }

    }


    private fun initView() {
        viewModel.netResponse.observe(this, Observer { res ->
            if (res != null) {
                XLog.info("res:$res")
                XToast.toastShort(this, res.message)
                if (res.result) {
                    finish()
                }
            }
        })
        viewModel.isLoading.observe(this, Observer { loading ->
            if (loading == true) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        })
        viewModel.eventColorLive().observe(this, Observer { color ->
            if (color != null) {
                XLog.info("observer eventcolor: $color")
                val size = ll_create_calendar_event_color_layout.childCount
                for (i in 0 until size) {
                    val card = ll_create_calendar_event_color_layout.getChildAt(i)
                    if (card is CardView) {
                        card.removeAllViews()
                        val tag = card.tag
                        if (tag != null) {
                            if (color == tag as String) {
                                //add image
                                val checkImage = ImageView(this@CreateEventActivity)
                                checkImage.setImageResource(R.mipmap.icon_calendar_check)
                                val lp = FrameLayout.LayoutParams(dip(16), dip(16))
                                lp.gravity = Gravity.CENTER
                                card.addView(checkImage, lp)
                            }
                        }
                    }
                }
            }
        })
        viewModel.repeatWeekListLive().observe(this, Observer { list ->
            XLog.info("observer week list : $list")
            val size = ll_create_calendar_event_repeat_week_days.childCount
            for (i in 0 until size) {
                val card = ll_create_calendar_event_repeat_week_days.getChildAt(i)
                if (card is CardView) {
                    val tag = card.tag
                    tag as String
                    refreshWeekCardView(card, list?.any { it == tag } == true)
                }
            }
        })
        viewModel.eventIdLive().observe(this, Observer { id->
            XLog.info("observer id , $id")
            if (eventOld!=null) {
                viewModel.restoreEventInfo(eventOld!!, calendars)
            }
        })
        /**
         * event color choose view
         */
        ll_create_calendar_event_color_layout.removeAllViews()
        CalendarOB.deepColor.forEach { (_, value) ->
            val card = CardView(this@CreateEventActivity)
            card.setCardBackgroundColor(Color.parseColor(value))
            card.radius = dip(11).toFloat()
            card.tag = value
            val lp = LinearLayout.LayoutParams(dip(22), dip(22))
            val margin = dip(8)
            lp.setMargins(margin, margin, margin, margin)
            ll_create_calendar_event_color_layout.addView(card, lp)
            card.setOnClickListener {
                val tag = it?.tag
                if (tag != null) {
                    XLog.info("select color $tag")
                    viewModel.eventColor.value = (tag as String)
                }
            }
        }
        /**
         * week day choose view
         */
        ll_create_calendar_event_repeat_week_days.removeAllViews()
        CalendarOB.weekDays.forEach { (key, name) ->
            val card = CardView(this@CreateEventActivity)
            card.setCardBackgroundColor(Color.WHITE)
            card.radius = dip(4).toFloat()
            card.tag = key
            val lp = LinearLayout.LayoutParams(dip(36), dip(24))
            val margin = dip(5)
            lp.setMargins(margin, margin, margin, margin)
            ll_create_calendar_event_repeat_week_days.addView(card, lp)
            val tv = TextView(this@CreateEventActivity)
            tv.text = name
            tv.setTextSize(COMPLEX_UNIT_SP, 10f)
            tv.setTextColor(FancySkinManager.instance().getColor(this@CreateEventActivity, R.color.z_color_text_primary))
            val tvlp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            tvlp.gravity = Gravity.CENTER
            card.addView(tv, tvlp)
            card.setOnClickListener { view ->
                chooseWeekDay(view)
            }
        }
    }

    private fun initData() {
        /**
         * calendar data
         */
        val list = intent.getSerializableExtra(CALENDAR_LIST_KEY)
        if (list != null) {
            calendars = list as ArrayList<CalendarInfoPickViewData>
        }
        if (calendars.isNotEmpty()) {
            viewModel.setCalendar(calendars[0])
        }
        /**
         * remind data
         */
        CalendarOB.remindOptions.forEach { (key, name) ->
            remindList.add(CalendarPickerOption(name, key))
        }
        if (remindList.isNotEmpty()) {
            viewModel.setSelectedRemind(remindList[0])
        }
        /**
         * repeat data
         */
        CalendarOB.repeatOptions.forEach { (key, name) ->
            repeatList.add(CalendarPickerOption(name, key))
        }
        if (repeatList.isNotEmpty()) {
            viewModel.setSelectedRepeat(repeatList[0])
        }
        /**
         * 修改的情况
         */
        val event = intent.getParcelableExtra<CalendarEventInfoData>(CALENDAR_EVENT_KEY)
        if (event != null) {
            eventOld = event
            viewModel.setEventUpdateObserver(event.id, event.recurrenceRule)
            XLog.info("finish setEventUpdateObserver...................")
        }
    }

}
