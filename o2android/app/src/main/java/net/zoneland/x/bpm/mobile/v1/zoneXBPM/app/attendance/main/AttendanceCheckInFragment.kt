package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.DistanceUtil
import kotlinx.android.synthetic.main.fragment_attendance_check_in.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInWorkplaceInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class AttendanceCheckInFragment : BaseMVPViewPagerFragment<AttendanceCheckInContract.View, AttendanceCheckInContract.Presenter>(),
        AttendanceCheckInContract.View, BDLocationListener {

    override var mPresenter: AttendanceCheckInContract.Presenter = AttendanceCheckInPresenter()

    override fun layoutResId(): Int = R.layout.fragment_attendance_check_in

    private val recordList = ArrayList<MobileCheckInJson>()
    private val workplaceList = ArrayList<MobileCheckInWorkplaceInfoJson>()
    private val recordAdapter: CommonRecycleViewAdapter<MobileCheckInJson> by lazy {
        object : CommonRecycleViewAdapter<MobileCheckInJson>(activity, recordList, R.layout.item_attendance_check_in_record_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MobileCheckInJson?) {
                holder?.setText(R.id.tv_item_attendance_check_in_time, t?.signTime)
                        ?.setText(R.id.tv_item_attendance_check_in_location, t?.recordAddress)
            }
        }
    }

    private var mBaiduMap: BaiduMap? = null
    private val mLocationClient: LocationClient by lazy { LocationClient(activity) }
    private var workplaceCircle: Circle? = null //最近的工作地点打卡范围的蓝圈
    private var myLocation: BDLocation? = null //当前我的位置
    private var checkInPosition: MobileCheckInWorkplaceInfoJson? = null//离的最近的工作地点位置
    private var isInCheckInPositionRange = false


    val handler = Handler { msg ->
        if (msg?.what == 1) {
            val nowTime = DateHelper.nowByFormate("HH:mm:ss")
            tv_attendance_check_in_time?.text = nowTime
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
    private val timer: Timer = Timer()


    override fun initUI() {
        mBaiduMap = map_baidu_attendance_check_in.map
        mBaiduMap?.mapType = BaiduMap.MAP_TYPE_NORMAL
        // 开启定位图层
        mBaiduMap?.isMyLocationEnabled = true
        mLocationClient.registerLocationListener(this)
        initBaiduLocation()
        mLocationClient.start()

        rv_attendance_check_in_record_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_attendance_check_in_record_list.adapter = recordAdapter

        timer.schedule(timerTask, 0, 1000)
        ll_attendance_check_in_button.setOnClickListener { clickCheckIn() }
    }

    private fun clickCheckIn() {
        if (!isInCheckInPositionRange) {
            XToast.toastShort(activity, "还未进入打卡范围，无法打卡！")
            return
        }
        if (myLocation!=null ){
            tv_attendance_check_in_button_label.text = getString(R.string.attendance_check_in_knock_loading)
            tv_attendance_check_in_time.gone()
            val signDate = DateHelper.nowByFormate("yyyy-MM-dd")
            val signTime = DateHelper.nowByFormate("HH:mm:ss")
            mPresenter.checkIn(myLocation!!.latitude.toString(), myLocation!!.longitude.toString(), myLocation!!.addrStr, "", signDate, signTime, "")
        }
    }

    override fun lazyLoad() {
        mPresenter.findTodayCheckInRecord(O2SDKManager.instance().distinguishedName)
        mPresenter.loadAllWorkplace()
    }

    override fun onDestroyView() {
        timer.cancel()
        timerTask.cancel()
        try {
            map_baidu_attendance_check_in.onDestroy()
        } catch (e: Exception) {
        }
        super.onDestroyView()

    }

    override fun onDestroy() {
        // 退出时销毁定位
        mLocationClient.stop()
        // 关闭定位图层
        mBaiduMap?.isMyLocationEnabled = false
        super.onDestroy()
    }

    override fun onReceiveLocation(location: BDLocation?) {
        // 画定位点
        XLog.debug("onReceive locType:${location?.locType}, latitude:${location?.latitude}, longitude:${location?.longitude}")
        if (location != null) {
            doAsync {
                myLocation = location
                // 构造定位数据
                val locData = MyLocationData.Builder()
                        .accuracy(location.radius)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(location.direction)
                        .latitude(location.latitude)
                        .longitude(location.longitude).build()
                // 设置定位数据
                mBaiduMap?.setMyLocationData(locData)
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                val bit: BitmapDescriptor = BitmapDescriptorFactory
                        .fromResource(R.mipmap.task_red_point)
                val config = MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, bit)
                mBaiduMap?.setMyLocationConfiguration(config)

                uiThread {

                    if (workplaceCircle == null) { //定位和获取workplace数据有可能有时间间隔
                        drawCheckInWorkplaceCircle()
                    }
                    checkIsInWorkplace()
                }
            }


        }
    }


    override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
        XLog.debug("onConnectHotSpotMessage, p0:$p0, p1:$p1")
    }

    override fun workplaceList(list: List<MobileCheckInWorkplaceInfoJson>) {
        // 画公司打卡范围蓝圈
        workplaceList.clear()
        workplaceList.addAll(list)
        drawCheckInWorkplaceCircle()
    }


    override fun todayCheckInRecord(list: List<MobileCheckInJson>) {
        XLog.debug("todayCheckInRecord  size:${list.size}")
        recordList.clear()
        recordList.addAll(list)
        recordAdapter.notifyDataSetChanged()
    }

    override fun checkIn(result: Boolean) {
        tv_attendance_check_in_button_label.text = getString(R.string.attendance_check_in_knock)
        tv_attendance_check_in_time.visible()
        if (result) {
            XToast.toastShort(activity, "打卡成功！")
        } else {
            XToast.toastShort(activity, "打卡失败！")
        }
        mPresenter.findTodayCheckInRecord(O2SDKManager.instance().distinguishedName)
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
                tv_attendance_check_in_alert_label?.text = "您已进入考勤范围：${checkInPosition?.placeName}"
                ll_attendance_check_in_alert_banner?.visible()
            } else {
                isInCheckInPositionRange = false
                ll_attendance_check_in_alert_banner?.gone()

            }
        }
    }

    /**
     * 画出打卡范围的蓝圈
     */
    private fun drawCheckInWorkplaceCircle() {
        if (myLocation != null) {
            calNearestWorkplace()
            if (checkInPosition != null) {
                val llCircle = LatLng(checkInPosition!!.latitude.toDouble(), checkInPosition!!.longitude.toDouble())
                val ooCircle = CircleOptions()
                        .center(llCircle)
                        .fillColor(ContextCompat.getColor(activity, R.color.overlay_work_place))
                        .stroke(Stroke(2, ContextCompat.getColor(activity, R.color.overlay_work_place_border)))
                        .radius(checkInPosition!!.errorRange)
                workplaceCircle = mBaiduMap?.addOverlay(ooCircle) as Circle
                XLog.info("draw circle over...............zoom map..")
                val mapStatus = mBaiduMap?.mapStatus
                if (mapStatus != null) {
                    val newStatus = MapStatus.Builder(mapStatus).zoom(17f).build()
                    val update = MapStatusUpdateFactory.newMapStatus(newStatus)
                    mBaiduMap?.animateMapStatus(update)
                }
            }
        }
    }

    /**
     * 找到最近的打卡地点
     */
    private fun calNearestWorkplace() {
        if (!workplaceList.isEmpty() && myLocation!=null) {
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
