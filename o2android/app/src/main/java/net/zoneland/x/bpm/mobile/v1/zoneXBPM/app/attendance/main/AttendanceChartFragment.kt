package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_attendance_chart.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.AttendanceStatus
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson


class AttendanceChartFragment : BaseMVPViewPagerFragment<AttendanceChartContract.View, AttendanceChartContract.Presenter>(), AttendanceChartContract.View {

    override var mPresenter: AttendanceChartContract.Presenter = AttendanceChartPresenter()

    override fun layoutResId(): Int = R.layout.fragment_attendance_chart


    val typeFace: Typeface by lazy { Typeface.createFromAsset(activity.assets, "OpenSans-Regular.ttf") }

    override fun initUI() {
        //chart init
        pie_attendance_chart.setUsePercentValues(true)
        pie_attendance_chart.setDescription("")
        pie_attendance_chart.setExtraOffsets(5f, 10f, 5f, 5f)
        pie_attendance_chart.dragDecelerationFrictionCoef = 0.95f
        pie_attendance_chart.setTransparentCircleColor(Color.WHITE)
        pie_attendance_chart.setTransparentCircleAlpha(110)
        pie_attendance_chart.holeRadius = 58f
        pie_attendance_chart.transparentCircleRadius = 61f
        pie_attendance_chart.setDrawCenterText(true)
        pie_attendance_chart.centerText = getString(R.string.attendance_pie_title)
        pie_attendance_chart.setCenterTextSize(14f)
        pie_attendance_chart.rotationAngle = 0f
        pie_attendance_chart.isRotationEnabled = true
        pie_attendance_chart.isHighlightPerTapEnabled = true

    }

    override fun lazyLoad() {
        mPresenter.getAttendanceDetailList(DateHelper.nowByFormate("yyyy"), DateHelper.nowByFormate("MM"), O2SDKManager.instance().distinguishedName)
    }

    override fun attendanceDetailList(list: List<AttendanceDetailInfoJson>) {
        val map = HashMap<AttendanceStatus, Int>()
        list.map {
            when {
                it.isGetSelfHolidays -> {
                    var i = map[AttendanceStatus.HOLIDAY] ?: 0
                    i++
                    map.put(AttendanceStatus.HOLIDAY, i)
                }
                it.isLate -> {
                    var i = map[AttendanceStatus.LATE] ?: 0
                    i++
                    map.put(AttendanceStatus.LATE, i)
                }
                it.isAbsent -> {
                    var i = map[AttendanceStatus.ABSENT] ?: 0
                    i++
                    map.put(AttendanceStatus.ABSENT, i)
                }
                it.isAbnormalDuty -> {
                    var i = map[AttendanceStatus.ABNORMALDUTY] ?: 0
                    i++
                    map.put(AttendanceStatus.ABNORMALDUTY, i)
                }
                it.isLackOfTime -> {
                    var i = map[AttendanceStatus.LACKOFTIME] ?: 0
                    i++
                    map.put(AttendanceStatus.LACKOFTIME, i)
                }
                it.appealStatus == 9 -> {
                    var i = map[AttendanceStatus.APPEAL] ?: 0
                    i++
                    map.put(AttendanceStatus.APPEAL, i)
                }
                else -> {
                    var i = map[AttendanceStatus.NORMAL] ?: 0
                    i++
                    map.put(AttendanceStatus.NORMAL, i)
                }
            }

        }
        val yVals1 = ArrayList<Entry>()
        val xVals = ArrayList<String>()
        val colors = ArrayList<Int>()
        var i = 0
        var label = ""
        if (map.isEmpty()) {
            label = getString(R.string.attendance_no_data)
        }else {
            AttendanceStatus.values().filter { status->
                val value = map[status]?:0
                (value > 0)
            }.map { status->
                val value = map[status]?:0
                val entry = Entry(value.toFloat(), i)
                yVals1.add(entry)
                xVals.add(status.label)
                colors.add(FancySkinManager.instance().getColor(activity, status.color))
                i++
            }
        }
        val dataSet = PieDataSet(yVals1, label)
        dataSet.sliceSpace = 2f
        dataSet.selectionShift = 5f
        dataSet.colors = colors
        val pieData = PieData(xVals, dataSet)
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)
        pieData.setValueTypeface(typeFace)
        pie_attendance_chart.data = pieData
        pie_attendance_chart.highlightValues(null)
        pie_attendance_chart.invalidate()
        pie_attendance_chart.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        val legend = pie_attendance_chart.legend
        legend.isWordWrapEnabled = true
        legend.formSize = 12f
    }
}
