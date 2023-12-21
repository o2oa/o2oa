import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";

import { getPublicData } from "../../../utils/actions";
const template = `
  <div class=" {{ $.isMax ? 'bd-map-container-fixed': 'bd-map-container'}}">
      <div class="bd-map" id="amap-container">
          <!-- 高德地图 -->
      </div>
      <div class="icon bd-map-btn" @click="mapMaxOrMini" ><i class="{{  $.isMax ? 'o2icon-shrink' : 'o2icon-enlarge' }}"></i></div>
  </div>
`;

// 高德地图组件
export default content({
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
      form: {
        longitude: "",
        latitude: "",
        isView: false,
      },
      isMax: false, // 地图放大
    };
  },
  afterRender() {
    this.loadAMap();
  },
  // 加载地图api等资源
  async loadAMap() {
    this.iconUrl = "../x_component_attendancev2/$Main/default/us_mk_icon.png"; //图标样式，发布时候修改为绝对路径
    if (!window.AMapApiLoaded) {
      const config = await getPublicData("attendanceMapConfig"); // 地图配置
      let accountkey =  "72232ca87eeec7abc23cf41a89ed7019";
      if (config && config.aMapAccountKey) {
        accountkey = config.aMapAccountKey;
      } else {
        console.error("没有配置地图 Key ！！！");
      }
      let apiPath = "http://webapi.amap.com/maps?v=1.4.15&key=" + accountkey;
      if (window.location.protocol.toLowerCase() === "https:") {
        window.HOST_TYPE = "2";
        apiPath = "//webapi.amap.com/maps?v=1.4.15&key=" + accountkey;
      }
      o2.load(apiPath, () => {
        console.debug("高德地图加载API加载完成，开始载入地图！");
        window.AMapApiLoaded = true;
        this.loadViewOrLocation();
      });
    } else {
      this.loadViewOrLocation();
    }
  },
  loadViewOrLocation() {
    if (this.bind.form.isView) {
      this.loadMapView();
    } else {
      this.initDefaultNumberMapView();
    }
  },
  // 查看工作场所 使用参数中的位置
  loadMapView() {
    const point = new AMap.LngLat(
      this.bind.form.longitude,
      this.bind.form.latitude
    );
    this.createMap(point);
  },
  // 默认打开地图 一个固定值
  initDefaultNumberMapView() {
    const point = new AMap.LngLat(120.135431, 30.27412);
    this.createMap(point);
  },
  // 加载百度地图
  createMap(point) {
    console.debug("开始创建高德地图！", point);
    if (!this.map) {
      this.map = new AMap.Map("amap-container", {
        zoom: 17, //级别
        center: point, //中心点坐标
        viewMode: "3D", //使用3D视图
      }); // 创建Map实例
    }

    this.addControls();
    if (!this.bind.form.isView) {
      // 创建工作场所需要添加点击事件
      this.addMapClick();
    } else {
      this.addMarkPoint(point, this.bind.form.placeName);
    }
  },
  // 添加地图控件
  addControls() {
    // 同时引入工具条插件，比例尺插件和鹰眼插件
    AMap.plugin(
      [
        "AMap.ToolBar",
        "AMap.Scale",
        "AMap.OverView",
        "AMap.MapType",
        "AMap.Geolocation",
      ],
      () => {
        // 在图面添加工具条控件，工具条控件集成了缩放、平移、定位等功能按钮在内的组合控件
        this.map.addControl(new AMap.ToolBar());
        // 在图面添加比例尺控件，展示地图在当前层级和纬度下的比例尺
        this.map.addControl(new AMap.Scale());
        if (!this.bind.form.isView) {
          console.log("开始定位！！！！！");
          // 创建工作场所需要定位当前位置
          const geolocation = new AMap.Geolocation({
            enableHighAccuracy: true, //是否使用高精度定位，默认:true
            timeout: 10000, //超过10秒后停止定位，默认：无穷大
            showCircle: false, //定位成功后用圆圈表示定位精度范围，默认：true
            panToLocation: true, //定位成功后将定位到的位置作为地图中心点，默认：true
            zoomToAccuracy: true, //定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
          });
          this.map.addControl(geolocation);
          geolocation.getCurrentPosition();
        }
      }
    );
  },
  addMapClick() {
    this.map.on("click", (e) => {
      console.debug("点击的经纬度：", e);
      this.addMarkPoint(new AMap.LngLat(e.lnglat.getLng(), e.lnglat.getLat()));
      this.bind.form.longitude = e.lnglat.getLng();
      this.bind.form.latitude = e.lnglat.getLat();
    });
  },
  // 添加位置点
  addMarkPoint(point, placeName) {
    this.map.clearMap(); // 先清除
    const marker = new AMap.Marker({
      icon: new AMap.Icon(),
      position: point,
      label: {
        content: placeName || "",
        offset: new AMap.Pixel(0, -20),
      },
    });

    this.map.add(marker);
  },
  // 地图放大缩小
  mapMaxOrMini() {
    this.bind.isMax = !this.bind.isMax;
  },
});
