package jiguang.chat.location.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.UUID;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import jiguang.chat.application.JGApplication;
import jiguang.chat.location.adapter.MapPickerAdapter;
import jiguang.chat.location.service.LocationService;
import jiguang.chat.utils.BitmapLoader;



public class MapPickerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView list;
    private TextView status;
    private ProgressBar loading;
    private View defineMyLocationButton;

    //百度地图相关
    private LocationService locationService;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // 当前经纬度和地理信息
    private LatLng mLoactionLatLng;
    private String mAddress;
    private String mStreet;
    private String mName;
    private String mCity;
    // 设置第一次定位标志
    private boolean isFirstLoc = true;
    // MapView中央对于的屏幕坐标
    private Point mCenterPoint = null;
    // 地理编码
    private GeoCoder mGeoCoder = null;
    // 位置列表
    MapPickerAdapter mAdapter;
    ArrayList<PoiInfo> mInfoList;
    PoiInfo mCurentInfo;
    private View mPopupView;
    Conversation conv;
    protected int mWidth;
    protected int mHeight;
    protected float mDensity;
    protected int mDensityDpi;

    private double mLatitude;
    private double mLongitude;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private boolean mSendLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_map_picker);
        if (locationService == null) {
            locationService = new LocationService(getApplicationContext());
        }
        locationService.registerListener(mListener);//是否应该在onStart中注册

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;

        mPopupView = LayoutInflater.from(this).inflate(R.layout.location_popup_layout, null);

        linearLayout = (LinearLayout) findViewById(R.id.listNearbyHolder);
        relativeLayout = (RelativeLayout) findViewById(R.id.mapholder);
        defineMyLocationButton = findViewById(R.id.define_my_location);

        initMap();
        initIntent();

        String targetId = getIntent().getStringExtra(JGApplication.TARGET_ID);
        String targetAppKey = getIntent().getStringExtra(JGApplication.TARGET_APP_KEY);
        long groupId = getIntent().getLongExtra(JGApplication.GROUP_ID, 0);

        if (groupId != 0) {
            conv = JMessageClient.getGroupConversation(groupId);
        } else {
            conv = JMessageClient.getSingleConversation(targetId, targetAppKey);
        }


    }

    private void initIntent() {
        Intent intent = getIntent();
        mSendLocation = intent.getBooleanExtra("sendLocation", false);
        if (mSendLocation) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    MapPickerActivity.this.getResources().getDimensionPixelOffset(R.dimen.location));
            relativeLayout.setLayoutParams(params);

            getSupportActionBar().setTitle("发送位置");
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            findViewById(R.id.root).setBackgroundColor(Color.parseColor("#ffffff"));


            defineMyLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    turnBack();
                }
            });

        } else {//接收方显示
            locationService.unregisterListener(mListener);
            defineMyLocationButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(params);

            getSupportActionBar().setTitle("位置信息");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            findViewById(R.id.root).setBackgroundColor(Color.parseColor("#ffffff"));


            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);


            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(100).direction(90.f).latitude(latitude).longitude(longitude).build();
            mBaiduMap.setMyLocationData(locationData);
            mBaiduMap.setMyLocationEnabled(true);

            LatLng ll = new LatLng(latitude, longitude);
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.oval);
            OverlayOptions options = new MarkerOptions().position(ll).icon(descriptor).zIndex(10);
            mBaiduMap.addOverlay(options);

            turnBack();


//            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
//            mBaiduMap.setMapStatus(update);
//
//            TextView location = (TextView) mPopupView.findViewById(R.id.location_tips);
//            location.setText(intent.getStringExtra("locDesc"));
        }
    }

    private void initMap() {
        //ricky init baidumap begin
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(touchListener);
        // 初始化POI信息列表
        mInfoList = new ArrayList<PoiInfo>();
        // 初始化当前MapView中心屏幕坐标，初始化当前地理坐标
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLoactionLatLng = mBaiduMap.getMapStatus().target;
        // 定位
        mBaiduMap.setMyLocationEnabled(true);
        // 隐藏百度logo ZoomControl
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        // 隐藏比例尺
        //mMapView.showScaleControl(false);
        // 地理编码

        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(GeoListener);
        list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(this);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        loading = (ProgressBar) findViewById(R.id.loading);
        status = (TextView) findViewById(R.id.status);
        mAdapter = new MapPickerAdapter(MapPickerActivity.this, mInfoList);
        list.setAdapter(mAdapter);
    }


    public void turnBack() {
        MyLocationData location = mBaiduMap.getLocationData();

        if (location == null) {
            return;
        }
        // 实现动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(location.latitude, location.longitude));
        mBaiduMap.animateMapStatus(u);
        mBaiduMap.clear();
        // 发起反地理编码检索
        mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                .location(new LatLng(location.latitude, location.longitude)));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_send:
                if (mLoactionLatLng != null) {
                    int left = mWidth / 4;
                    int top = (int) (mHeight - 1.1 * mWidth);
                    Rect rect = new Rect(left, top, mWidth - left, mHeight - (int) (1.2 * top));
                    mBaiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            if (null != bitmap && null != conv) {
                                String fileName = UUID.randomUUID().toString();
                                String path = BitmapLoader.saveBitmapToLocal(bitmap, fileName);
                                Intent intent = new Intent();

                                intent.putExtra("latitude", mLatitude);
                                intent.putExtra("longitude", mLongitude);
                                intent.putExtra("mapview", mMapView.getMapLevel());
                                intent.putExtra("street", mStreet);
                                intent.putExtra("path", path);

                                setResult(JGApplication.RESULT_CODE_SEND_LOCATION, intent);
                                finish();
                            } else {
                                Toast.makeText(MapPickerActivity.this, getString(R.string.send_location_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mSendLocation) {
            getMenuInflater().inflate(R.menu.picker_map, menu);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        // 通知是适配器第position个item被选择了
        mAdapter.setNotifyTip(position);
        mAdapter.notifyDataSetChanged();
        BitmapDescriptor mSelectIco = BitmapDescriptorFactory
                .fromResource(R.drawable.picker_map_geo_icon);
        mBaiduMap.clear();
        PoiInfo info = (PoiInfo) mAdapter.getItem(position);
        LatLng la = info.location;
        // 动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);
        // 添加覆盖物
        OverlayOptions ooA = new MarkerOptions().position(la)
                .icon(mSelectIco).anchor(0.5f, 0.5f);
        mBaiduMap.addOverlay(ooA);
        mLoactionLatLng = info.location;
        mAddress = info.address;
        mName = info.name;
        mCity = info.city;

        mLatitude = info.location.latitude;
        mLongitude = info.location.longitude;
        mStreet = info.name;//地图对应位置文字描述
    }


    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        locationService.stop(); // 停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------

        // 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的

        // 注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        locationService.start();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        locationService.unregisterListener(mListener); // 注销掉监听
        locationService.stop();
        mMapView.onDestroy();
        mGeoCoder.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        locationService.stop();
        mMapView.onPause();
    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                //

                MyLocationData data = new MyLocationData.Builder()//
                        // .direction(mCurrentX)//
                        .accuracy(location.getRadius())//
                        .latitude(location.getLatitude())//
                        .longitude(location.getLongitude())//
                        .build();
                mBaiduMap.setMyLocationData(data);
                // 设置自定义图标
                MyLocationConfiguration config = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, true, null);
                mBaiduMap.setMyLocationConfigeration(config);
                mAddress = location.getAddrStr();
                mName = location.getStreet();
                mCity = location.getCity();

                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mLoactionLatLng = currentLatLng;
                // 是否第一次定位
                if (isFirstLoc) {
                    isFirstLoc = false;
                    // 实现动画跳转
                    MapStatusUpdate u = MapStatusUpdateFactory
                            .newLatLng(currentLatLng);
                    mBaiduMap.animateMapStatus(u);
                    mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                            .location(currentLatLng));
                    return;
                }
            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }

    };
    // 地理编码监听器
    OnGetGeoCoderResultListener GeoListener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检索到结果
            }
            // 获取地理编码结果
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有找到检索结果
                status.setText(R.string.picker_internalerror);
                status.setVisibility(View.VISIBLE);
            }
            // 获取反向地理编码结果
            else {
                status.setVisibility(View.GONE);
                // 当前位置信息
                mLoactionLatLng = result.getLocation();
                mAddress = result.getAddress();
                mName = result.getAddressDetail().street;
                mStreet = result.getAddressDetail().street;
                mCity = result.getAddressDetail().city;

                //滑动地图.中间位置坐标记录下来发送.相当于默认点击了一下listView中的第一条
                mLatitude = result.getLocation().latitude;
                mLongitude = result.getLocation().longitude;

                mCurentInfo = new PoiInfo();
                mCurentInfo.address = result.getAddress();
                mCurentInfo.location = result.getLocation();
                mCurentInfo.name = "[当前位置]";
                mInfoList.clear();
                mInfoList.add(mCurentInfo);
                // 将周边信息加入表
                if (result.getPoiList() != null) {
                    mInfoList.addAll(result.getPoiList());
                }
                mAdapter.setNotifyTip(0);
                // 通知适配数据已改变
                mAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);

            }
        }
    };
    // 地图触摸事件监听器
    BaiduMap.OnMapTouchListener touchListener = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (mCenterPoint == null) {
                    return;
                }
                // 获取当前MapView中心屏幕坐标对应的地理坐标(不重新设置的话中心点在左上角)
                mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
                LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(mCenterPoint);
                // 发起反地理编码检索
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(currentLatLng));
                loading.setVisibility(View.VISIBLE);

            }
        }
    };


}
