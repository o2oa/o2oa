package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.setting


import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import kotlinx.android.synthetic.main.activity_attendance_location_setting.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInWorkplaceInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.text2String
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport


class AttendanceLocationSettingActivity : BaseMVPActivity<AttendanceLocationSettingContract.View, AttendanceLocationSettingContract.Presenter>(),
        AttendanceLocationSettingContract.View, BDLocationListener {
    override var mPresenter: AttendanceLocationSettingContract.Presenter = AttendanceLocationSettingPresenter()
    override fun layoutResId(): Int = R.layout.activity_attendance_location_setting

    val WORK_PLACE_ID = "WORK_PLACE_ID"

    val mLocationClient: LocationClient by lazy { LocationClient(this) }
    lateinit var mBaiduMap: BaiduMap
    var marker: Marker? = null
    var latitude = ""
    var longitude = ""


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.title_activity_attendance_location_setting), true)
        mLocationClient.registerLocationListener(this)

        initBaiduLocation()
        mBaiduMap = map_attendance_location_setting_baidu.map
        mBaiduMap.mapType = BaiduMap.MAP_TYPE_NORMAL
        mBaiduMap.setOnMapClickListener(object : BaiduMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng?) {
                XLog.debug("onMapClick latitude:${latLng?.latitude}, longitude:${latLng?.longitude}")
                markerPoint(latLng)
            }

            override fun onMapPoiClick(poi: MapPoi?): Boolean {
                val latLng = poi?.position
                XLog.debug("onMapPoiClick latitude:${latLng?.latitude}, longitude:${latLng?.longitude}")
                markerPoint(latLng)
                return false
            }
        })
        mBaiduMap.setOnMarkerClickListener { marker ->
            val bundle = marker.extraInfo
            if (bundle != null) {
                val id = bundle.getString(WORK_PLACE_ID)
                deleteWorkplace(id)
            }
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_attendance_location_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_work_place_save -> {
                saveWorkplace()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        map_attendance_location_setting_baidu.onResume()
        refreshMap()
    }

    override fun onPause() {
        super.onPause()
        map_attendance_location_setting_baidu.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_attendance_location_setting_baidu.onDestroy()
    }

    override fun onReceiveLocation(location: BDLocation?) {
        if (location == null) {
            return
        }
        XLog.debug("onReceiveLocation locType:${location.locType}")
        val latitude = location.latitude
        val longitude = location.longitude
        XLog.info("定位成功,address:${location.addrStr}, latitude:$latitude, longitude:$longitude")
        //定义Maker坐标点
        val point = LatLng(latitude, longitude)
        //地图显示在当前位置
        val builder = MapStatus.Builder().target(point).zoom(18.0f)
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
        //完成定位
        mLocationClient.stop()
    }

    override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
        XLog.info("connectHotSpot $p0, $p1")
    }

    override fun deleteWorkplace(flag: Boolean) {
        hideLoadingDialog()
        if (flag) {
            XToast.toastShort(this, "删除工作场所成功！")
            refreshMap()
        }else {
            XToast.toastShort(this, "删除工作场所失败！")
        }
    }

    override fun saveWorkplace(flag: Boolean) {
        hideLoadingDialog()
        if (flag) {
            XToast.toastShort(this, "保存工作场所成功！")
            edit_attendance_location_setting_error_range.setText("")
            edit_attendance_location_setting_name.setText("")
            refreshMap()
        }else {
            XToast.toastShort(this, "保存工作场所失败！")
        }
    }

    override fun workplaceList(list: List<MobileCheckInWorkplaceInfoJson>) {
        val bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_map_location_green)
        list.map {
            val point = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
            val bundle = Bundle()
            bundle.putString(WORK_PLACE_ID, it.id)
            val options = MarkerOptions()
                    .position(point)  //设置marker的位置
                    .title(it.placeName)
                    .extraInfo(bundle)
                    .icon(bitmap)  //设置marker图标
                    .zIndex(9)
            mBaiduMap.addOverlay(options)
        }
    }

    private fun refreshMap() {
        mBaiduMap.clear()
        mLocationClient.start()
        mPresenter.loadAllWorkplace()
    }

    private fun saveWorkplace() {
        val name = edit_attendance_location_setting_name.text2String()
        if (TextUtils.isEmpty(name)) {
            XToast.toastShort(this, "工作场所名称不能为空！")
            return
        }
        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude) ) {
            XToast.toastShort(this, "请在地图上点击进行位置标记！")
            return
        }
        var errorRange = edit_attendance_location_setting_error_range.text2String()
        if (TextUtils.isEmpty(errorRange)) {
            errorRange = "100"
        }
        showLoadingDialog()
        mPresenter.saveWorkplace(name, errorRange, latitude, longitude)
    }

    private fun deleteWorkplace(id: String?) {
        if (TextUtils.isEmpty(id)) {
            XLog.error("id is null!!!!")
            return
        }
        O2DialogSupport.openConfirmDialog(this, "确定要删除这个工作场所吗？",{
            showLoadingDialog()
            mPresenter.deleteWorkplace(id!!)
        })
    }

    private fun markerPoint(latLng: LatLng?) {
        if (latLng==null) {
            XLog.error("坐标为空")
            return
        }
        latitude =latLng.latitude.toString()
        longitude = latLng.longitude.toString()
        if (marker == null) {
            //构建Marker图标
            val bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.icon_map_location)
            val options = MarkerOptions()
                    .position(latLng)  //设置marker的位置
                    .icon(bitmap)  //设置marker图标
                    .zIndex(9)
            //将marker添加到地图上
            marker = mBaiduMap.addOverlay(options) as Marker
        }else {
            marker?.position = latLng
        }

    }

    private fun initBaiduLocation() {
        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll")//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0)//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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
