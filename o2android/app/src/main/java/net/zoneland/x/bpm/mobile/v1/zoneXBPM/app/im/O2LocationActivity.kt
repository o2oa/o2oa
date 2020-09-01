package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.geocode.*
import kotlinx.android.synthetic.main.activity_o2_location.*
import kotlinx.android.synthetic.main.snippet_appbarlayout_toolbar.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.BottomSheetMenu
import org.jetbrains.anko.doAsync
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class O2LocationActivity : AppCompatActivity(), BDLocationListener {


    companion object{
        const val RESULT_LOCATION_KEY = "RESULT_LOCATION_KEY"

        const val mode_key = "mode_key"
        const val location_data_key = "location_data_key"
        /**
         * 开始选择位置
         */
        fun startChooseLocation(): Bundle {
            val bundle = Bundle()
            bundle.putInt(mode_key, 0)
            return bundle
        }

        /**
         * 查看位置
         */
        fun showLocation(data: LocationData): Bundle {
            val bundle = Bundle()
            bundle.putInt(mode_key, 1)
            bundle.putParcelable(location_data_key, data)
            return bundle
        }
    }

    private var mode = 0 //模式 0 选择位置 1查看位置
    private var locationData: LocationData? = null

    //百度地图
    private var mBaiduMap: BaiduMap? = null
    private val mLocationClient: LocationClient by lazy { LocationClient(this) }
    private var marker: Marker? = null
    private val geoCoder: GeoCoder by lazy { GeoCoder.newInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o2_location)

        //数据初始化
        mode = intent.getIntExtra(mode_key, 0)
        locationData = intent.getParcelableExtra(location_data_key)
        if (mode == 0) {
            setupToolBar("点击地图选择位置")
        }else {
            if (locationData == null) {
                XToast.toastShort(this, "传入参数错误！")
                finish()
            }
            setupToolBar(locationData?.address ?: "位置")
        }


        //地图
        mBaiduMap = map_baidu_o2_location.map
        val builder = MapStatus.Builder()
        builder.zoom(19.0f)
        mBaiduMap?.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
        mBaiduMap?.mapType = BaiduMap.MAP_TYPE_NORMAL
        if (mode == 0) { //选择位置模式 需要加入点击功能
            //点击地图 设置位置数据 画图钉
            mBaiduMap?.setOnMapClickListener(object : BaiduMap.OnMapClickListener {
                override fun onMapClick(latLng: LatLng?) {
                    XLog.debug("onMapClick latitude:${latLng?.latitude}, longitude:${latLng?.longitude}")
                    markerPoint(latLng, null)
                    searchAddress(latLng)
                }

                override fun onMapPoiClick(poi: MapPoi?): Boolean {
                    val latLng = poi?.position
                    XLog.debug("onMapPoiClick latitude:${latLng?.latitude}, longitude:${latLng?.longitude}")
                    markerPoint(latLng, null)
                    searchAddress(latLng)
                    return false
                }
            })
            // 开启定位图层
            mBaiduMap?.isMyLocationEnabled = true
            mLocationClient.registerLocationListener(this)
            initBaiduLocation()
            mLocationClient.start()
        }else { //查看模式 把位置图钉画上去
            val lat = LatLng(locationData?.latitude!!, locationData?.longitude!!)
            markerPoint(lat, locationData?.addressDetail)
            val mapStatus: MapStatusUpdate = MapStatusUpdateFactory.newLatLng(lat)
            mBaiduMap?.setMapStatus(mapStatus)
        }

    }

    override fun onDestroy() {
        geoCoder.destroy()
        super.onDestroy()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (mode == 0) {
            menuInflater.inflate(R.menu.menu_location_send, menu)
        }else {
            menuInflater.inflate(R.menu.menu_location_open, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.location_send -> {
                if (locationData == null){
                    XToast.toastShort(this, "请先选择一个位置！")
                }else {
                    intent.putExtra(RESULT_LOCATION_KEY, locationData)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                return true
            }
            R.id.location_open -> {
                BottomSheetMenu(this@O2LocationActivity).setItems(
                        arrayListOf("百度地图", "高德地图", "腾讯地图"),
                        ContextCompat.getColor(this@O2LocationActivity, R.color.blue)){ index ->
                            when(index) {
                                0 -> goToBaiduMap()
                                1 -> goToGaodeMap()
                                2 -> goToTencentMap()
                            }
                        }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onReceiveLocation(location: BDLocation?) {
        XLog.debug("onReceive locType:${location?.locType}, latitude:${location?.latitude}, longitude:${location?.longitude}")
        if (location != null) {
            doAsync {
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
                //定位成功后关闭
                mLocationClient.stop()
            }


        }
    }

    override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
        XLog.debug("onConnectHotSpotMessage, p0:$p0, p1:$p1")
    }

    private fun setupToolBar(title:String = "") {
        toolbar_snippet_top_bar.title = ""
        setSupportActionBar(toolbar_snippet_top_bar)
        toolbar_snippet_top_bar.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar_snippet_top_bar.setNavigationOnClickListener {
            XLog.debug("点了 关闭了。。。。。。。")
            finish()
        }

        tv_snippet_top_title.text = title
    }


    /**
     * 标记位置到地图上
     */
    private fun markerPoint(latLng: LatLng?, address: String?) {
        if (latLng==null) {
            XLog.error("坐标为空")
            return
        }
        if (marker == null) {
            //构建Marker图标
            val bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.icon_map_location)
            val options = MarkerOptions()
                    .position(latLng)  //设置marker的位置
                    .title(address ?: "")
                    .icon(bitmap)  //设置marker图标
                    .zIndex(9)
            //将marker添加到地图上
            marker = mBaiduMap?.addOverlay(options) as Marker
        }else {
            marker?.position = latLng
        }
    }

    /**
     * 根据经纬度查询地址信息
     */
    private fun searchAddress(latLng: LatLng?) {
        //搜索地址信息
        geoCoder.setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener {
            override fun onGetGeoCodeResult(p0: GeoCodeResult?) {
            }

            override fun onGetReverseGeoCodeResult(p0: ReverseGeoCodeResult?) {
                if(locationData == null) {
                    locationData = LocationData()
                }
                locationData?.address = p0?.address
                locationData?.addressDetail = p0?.sematicDescription
                locationData?.latitude = p0?.location?.latitude
                locationData?.longitude = p0?.location?.longitude
                runOnUiThread {
                    tv_o2_location_address.text = p0?.address
                    tv_o2_location_address.visible()
                }
            }
        })
        geoCoder.reverseGeoCode(ReverseGeoCodeOption().location(latLng))
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

    /**
     * 跳转百度地图
     */
    private fun goToBaiduMap() {
        try {
            val uri = Uri.parse(("baidumap://map/direction?destination=latlng:"
                     + locationData?.latitude.toString()) + ","
                     + locationData?.longitude.toString() + "|name:" + locationData?.address +  // 终点
                     "&mode=driving" +  // 导航路线方式
                     "&src=" + packageName)
            val intent = Intent("android.intent.action.VIEW", uri)
            startActivity(intent)
        } catch (e: Exception) {
            XToast.toastShort(this, "未安装百度地图，无法打开")
        }
    }

    /**
     * 跳转高德地图
     */
    private fun goToGaodeMap() {
        try {
            val lat = LatLng(locationData?.latitude!!, locationData?.longitude!!)
            val endPoint = BD2GCJ(lat) //坐标转换
//        val stringBuffer = StringBuffer("androidamap://navi?sourceApplication=").append("O2OA")
//        stringBuffer.append("&lat=").append(endPoint!!.latitude)
//                .append("&lon=").append(endPoint.longitude).append("&keywords=${locationData?.address}")
//                .append("&dev=").append(0)
//                .append("&style=").append(2)
            val stringBuffer = StringBuffer("androidamap://route/plan/?sourceApplication=O2OA&dlat=")
            stringBuffer.append(endPoint!!.latitude)
                    .append("&dlon=").append(endPoint.longitude)
                    .append("&dname=${locationData?.address}")
                    .append("&dev=0&t=0")
            val intent = Intent("android.intent.action.VIEW", Uri.parse(stringBuffer.toString()))
            startActivity(intent)
        } catch (e: Exception) {
            XToast.toastShort(this, "未安装高德地图，无法打开")
        }
    }

    /**
     * 跳转腾讯地图
     */
    private fun goToTencentMap() {
        try {
            val lat = LatLng(locationData?.latitude!!, locationData?.longitude!!)
            val endPoint = BD2GCJ(lat) //坐标转换
            val stringBuffer = StringBuffer("qqmap://map/routeplan?type=drive")
                    .append("&tocoord=").append(endPoint!!.latitude).append(",").append(endPoint.longitude).append("&to=${locationData?.address}")
            val intent = Intent("android.intent.action.VIEW", Uri.parse(stringBuffer.toString()))
            startActivity(intent)
        } catch (e: Exception) {
            XToast.toastShort(this, "未安装腾讯地图，无法打开")
        }
    }


    /**
     * BD-09 坐标转换成 GCJ-02 坐标
     */
    private fun BD2GCJ(bd: LatLng): LatLng? {
        val x = bd.longitude - 0.0065
        val y = bd.latitude - 0.006
        val z = sqrt(x * x + y * y) - 0.00002 * sin(y * Math.PI)
        val theta = atan2(y, x) - 0.000003 * cos(x * Math.PI)
        val lng = z * cos(theta) //lng
        val lat = z * sin(theta) //lat
        return LatLng(lat, lng)
    }


    /**
     * 地址数据对象
     */
    class LocationData(
            var address: String? = null, //type=location的时候位置信息
            var addressDetail: String? = null,
            var latitude: Double? = null,//type=location的时候位置信息
            var longitude: Double? = null//type=location的时候位置信息
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readString() ?: "",
                source.readString() ?: "",
                source.readValue(Double::class.java.classLoader) as Double?,
                source.readValue(Double::class.java.classLoader) as Double?
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(address)
            writeString(addressDetail)
            writeValue(latitude)
            writeValue(longitude)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<LocationData> = object : Parcelable.Creator<LocationData> {
                override fun createFromParcel(source: Parcel): LocationData = LocationData(source)
                override fun newArray(size: Int): Array<LocationData?> = arrayOfNulls(size)
            }
        }
    }
}

