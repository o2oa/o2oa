package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Message
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.ImageView
import android.widget.TextView
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.DistanceUtil
import kotlinx.android.synthetic.main.fragment_attendance_check_in.*
import kotlinx.android.synthetic.main.fragment_attendance_check_in_new.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.GridLayoutItemDecoration
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.dip
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fancyLou on 2020-07-17.
 * Copyright © 2020 O2. All rights reserved.
 */

class AttendanceCheckInNewFragment : BaseMVPViewPagerFragment<AttendanceCheckInContract.View, AttendanceCheckInContract.Presenter>(),
        AttendanceCheckInContract.View, BDLocationListener {
    override var mPresenter: AttendanceCheckInContract.Presenter = AttendanceCheckInPresenter()
    override fun layoutResId(): Int = R.layout.fragment_attendance_check_in_new


    private val recordList = ArrayList<MobileScheduleInfo>()
    private var lastRecord: MobileCheckInJson? = null
    private val recordAdapter: CommonRecycleViewAdapter<MobileScheduleInfo> by lazy {
        object : CommonRecycleViewAdapter<MobileScheduleInfo>(activity, recordList, R.layout.item_attendance_check_in_schdule_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MobileScheduleInfo?) {
                if (holder != null && t != null) {
                    holder.setText(R.id.tv_item_attendance_check_in_schedule_list_type, t.checkinType)
                            .setText(R.id.tv_item_attendance_check_in_schedule_list_time, t.signTime)
                            .setText(R.id.tv_item_attendance_check_in_schedule_list_status, t.checkinStatus)
//                    val image = holder.getView<ImageView>(R.id.image_item_attendance_check_in_schedule_list_enable_icon)
                    val updateBtn = holder.getView<TextView>(R.id.tv_item_attendance_check_in_schedule_list_update_btn)
                    updateBtn.gone()
                    if (t.checkinStatus == "已打卡") {
//                        image.visible()
                        holder.setText(R.id.tv_item_attendance_check_in_schedule_list_status, t.checkinTime.substring(0, 5)+t.checkinStatus)
                        if (lastRecord != null && lastRecord!!.id == t.recordId) {
                            updateBtn.visible()
                        }
                    }else {
//                        image.gone()
                    }
                }

            }
        }
    }
    private val workplaceList = ArrayList<MobileCheckInWorkplaceInfoJson>()

    //定位
    private val mLocationClient: LocationClient by lazy { LocationClient(activity) }
    private var myLocation: BDLocation? = null //当前我的位置
    private var checkInPosition: MobileCheckInWorkplaceInfoJson? = null//离的最近的工作地点位置
    private var isInCheckInPositionRange = false
    private var needCheckIn = false //是否需要打卡 根据打卡结果和打卡班次判断

    //刷新打卡按钮的时间
    private val handler = Handler { msg ->
        if (msg?.what == 1) {
            val nowTime = DateHelper.nowByFormate("HH:mm:ss")
            tv_attendance_check_in_new_now_time?.text = nowTime
        }
        return@Handler true
    }
    private val timerTask = object : TimerTask() {
        override fun run() {
            val message = Message()
            message.what = 1
            handler.sendMessage(message)
        }
    }
    private val timer: Timer by lazy { Timer() }


    override fun lazyLoad() {
        mPresenter.listMyRecords()
        mPresenter.loadAllWorkplace()
    }

    override fun initUI() {
        //定位
        mLocationClient.registerLocationListener(this)
        initBaiduLocation()
        mLocationClient.start()

        //打卡班次
        rv_attendance_check_in_new_schedules.layoutManager = GridLayoutManager(activity, 2)
        rv_attendance_check_in_new_schedules.addItemDecoration(GridLayoutItemDecoration(activity?.dip(10) ?: 10, activity?.dip(10) ?: 10, 2))
        rv_attendance_check_in_new_schedules.adapter = recordAdapter
        recordAdapter.setOnItemClickListener { view, position ->
            val t = recordList[position]
            if (t.checkinStatus == "已打卡") {
                if (lastRecord != null && lastRecord!!.id == t.recordId) {
                    updateCheckIn(t)
                }
            }
        }

        //打卡按钮
        rl_attendance_check_in_new_knock_btn.setOnClickListener {
            if (!needCheckIn) {
                XToast.toastShort(activity, "今日已经不需要打卡！")
                return@setOnClickListener
            }
            if (!isInCheckInPositionRange) {
                XToast.toastShort(activity, "还未进入打卡范围，无法打卡！")
                return@setOnClickListener
            }
            if (myLocation != null ){
                tv_attendance_check_in_new_check_in.text = getString(R.string.attendance_check_in_knock_loading)
                tv_attendance_check_in_new_now_time.gone()
                val signDate = DateHelper.nowByFormate("yyyy-MM-dd")
                val signTime = DateHelper.nowByFormate("HH:mm:ss")
                val checkType = calCheckType()
                mPresenter.checkIn(myLocation!!.latitude.toString(), myLocation!!.longitude.toString(),
                        myLocation!!.addrStr, "", signDate, signTime, "", checkType)
            }
        }
        //时间
        timer.schedule(timerTask, 0, 1000)
    }

    /**
     * 更新打卡
     */
    private fun updateCheckIn(info: MobileScheduleInfo) {
        O2DialogSupport.openConfirmDialog(activity, "确定要更新这条打卡记录？",{ _ ->
            val signDate = DateHelper.nowByFormate("yyyy-MM-dd")
            val signTime = DateHelper.nowByFormate("HH:mm:ss")
            mPresenter.checkIn(myLocation!!.latitude.toString(), myLocation!!.longitude.toString(),
                    myLocation!!.addrStr, "", signDate, signTime, info.recordId, info.checkinType)
        })
    }


    /**
     * 计算下次打什么卡
     */
    private fun calCheckType() : String {
        val list = recordList.reversed()
        XLog.debug(list.joinToString())
        val newList = ArrayList<MobileScheduleInfo>()
        for (info in list) {
            if(info.checkinStatus == "未打卡") {
                newList.add(info)
            }else {
                break
            }
        }
        XLog.debug(newList.joinToString())
        if (newList.isNotEmpty()) {
            return newList.last().checkinType
        }
        return ""
    }

    override fun onDestroyView() {
        timer.cancel()
        timerTask.cancel()
        super.onDestroyView()

    }

    override fun onDestroy() {
        // 退出时销毁定位
        mLocationClient.stop()
        super.onDestroy()
    }


    override fun myRecords(records: MobileMyRecords?) {
        if (records != null) {
            val rList = records.records//打卡结果
            lastRecord = rList.lastOrNull()
            recordList.clear()
            var unCheckNumber = 0
            val list = records.scheduleInfos.map {
                val s = MobileScheduleInfo()
                s.signSeq = it.signSeq
                s.checkinType = it.checkinType
                //是否已打卡
                val record = rList.firstOrNull { re -> re.checkin_type == it.checkinType }
                if (record != null) {
                    s.checkinStatus =  "已打卡"
                    s.checkinTime = record.signTime
                    s.recordId = record.id
                    unCheckNumber = 0 //清零
                }else{
                    s.checkinStatus =  "未打卡"
                    unCheckNumber++
                }
                s.signDate = it.signDate
                s.signTime = it.signTime
                s
            }
            recordList.addAll(list)
            recordAdapter.notifyDataSetChanged()
            if (unCheckNumber > 0) {
                needCheckIn = true
                val draw = rl_attendance_check_in_new_knock_btn.background as? GradientDrawable
                activity?.let {
                    draw?.setColor(ContextCompat.getColor(it, R.color.z_color_primary))
                }

            }else {
                needCheckIn = false
                val draw = rl_attendance_check_in_new_knock_btn.background as? GradientDrawable
                activity?.let {
                    draw?.setColor(ContextCompat.getColor(it, R.color.disabled))
                }
            }
        }else {
            needCheckIn = false
            val draw = rl_attendance_check_in_new_knock_btn.background as? GradientDrawable
            activity?.let {
                draw?.setColor(ContextCompat.getColor(it, R.color.disabled))
            }
            XToast.toastShort(activity, "没有获取到当前用户打卡的信息！")
        }
    }

    override fun workplaceList(list: List<MobileCheckInWorkplaceInfoJson>) {
        workplaceList.clear()
        workplaceList.addAll(list)
        //计算
        calNearestWorkplace()
    }

    override fun todayCheckInRecord(list: List<MobileCheckInJson>) {
    }

    override fun checkIn(result: Boolean) {
        tv_attendance_check_in_new_check_in?.setText(R.string.attendance_check_in_knock)
        tv_attendance_check_in_new_now_time?.visible()
        if (result) {
            XToast.toastShort(activity, "打卡成功！")
        } else {
            XToast.toastShort(activity, "打卡失败！")
        }
        mPresenter.listMyRecords()
    }

    override fun onReceiveLocation(location: BDLocation?) {
        // 刷新定位信息
        XLog.debug("onReceive locType:${location?.locType}, latitude:${location?.latitude}, longitude:${location?.longitude}")
        if (location != null) {
            myLocation = location
            //计算
            calNearestWorkplace()
        }
    }

    override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
        XLog.debug("onConnectHotSpotMessage, p0:$p0, p1:$p1")
    }

    /**
     * 检查是否进入打卡范围
     */
    private fun checkIsInWorkplace() {
        XLog.info("checkIsInWorkplace.....${checkInPosition?.placeName}, ${myLocation?.addrStr}")
        if (checkInPosition != null && myLocation != null) {
            val workplacePosition = LatLng(checkInPosition!!.latitude.toDouble(), checkInPosition!!.longitude.toDouble())
            val position = LatLng(myLocation!!.latitude, myLocation!!.longitude)
            val distance = DistanceUtil.getDistance(position, workplacePosition)
            XLog.info("distance:$distance")
            if (distance < checkInPosition!!.errorRange) {
                isInCheckInPositionRange = true
                activity?.runOnUiThread {
                    tv_attendance_check_in_new_workplace.text = checkInPosition?.placeName
                    image_attendance_check_in_new_location_check_icon.setImageResource(R.mipmap.list_selected)
                }
            } else {
                isInCheckInPositionRange = false
                activity?.runOnUiThread {
                    tv_attendance_check_in_new_workplace.text = myLocation?.addrStr
                    image_attendance_check_in_new_location_check_icon.setImageResource(R.mipmap.icon_delete_people)
                }
            }
        }
    }

    /**
     * 找到最近的打卡地点
     */
    private fun calNearestWorkplace() {
        if (workplaceList.isNotEmpty() && myLocation!=null) {
            var minDistance: Double = -1.0
            XLog.debug("calNearestWorkplace...................")
            workplaceList.map {
                val p2 = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                val position = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                val distance = DistanceUtil.getDistance(position, p2)
                if (minDistance == -1.0) {
                    minDistance = distance
                    checkInPosition = it
                } else {
                    if (minDistance > distance) {
                        minDistance = distance
                        checkInPosition = it
                    }
                }
            }
            XLog.info("checkInposition:${checkInPosition?.placeName}")
            checkIsInWorkplace()
        }
    }

    private fun initBaiduLocation() {
        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll")//百度坐标系 可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(5000)//5秒一次定位 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true)//可选，设置是否需要地址信息，默认不需要
        option.isOpenGps = true//可选，默认false,设置是否使用gps
        option.isLocationNotify = true//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true)//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true)//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false)//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false)//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false)//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.locOption = option
    }
}