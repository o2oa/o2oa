import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";

import { getPublicData } from "../../../utils/actions";

const template = `
      <div class=" {{ $.isMax ? 'bd-map-container-fixed': 'bd-map-container'}}">
        <div class="bd-map" >
        <!-- 放百度地图 -->
        </div>
        <div class="icon bd-map-btn" @click="mapMaxOrMini" ><i class="{{  $.isMax ? 'o2icon-shrink' : 'o2icon-enlarge' }}"></i></div>
    </div>
`;

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
    this.loadBDMap();
  },
  // 加载地图api等资源
  async loadBDMap() {
    this.iconUrl = "../x_component_attendancev2/$Main/default/us_mk_icon.png"; //图标样式，发布时候修改为绝对路径
    if (!window.BDMapV2ApiLoaded) {
      const config = await getPublicData("attendanceMapConfig"); // 地图配置
      let accountkey = "sM5P4Xq9zsXGlco6RAq2CRDtwjR78WQB";
      if (config && config.baiduAccountKey) {
        accountkey = config.baiduAccountKey;
      } else {
        console.error("没有配置地图 Key ！！！");
      }
      let apiPath =
        "http://api.map.baidu.com/getscript?v=2.0&ak=" +
        accountkey +
        "&s=1&services=";
      if (window.location.protocol.toLowerCase() === "https:") {
        window.HOST_TYPE = "2";
        apiPath =
          "//api.map.baidu.com/getscript?v=2.0&ak=" +
          accountkey +
          "&s=1&services=";
      }
      o2.load(apiPath, () => {
        console.debug("地图加载API加载完成，开始载入地图！");
        window.BDMapV2ApiLoaded = true;
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
      this.location();
    }
  },
  loadMapView() {
    const point = new BMap.Point(
      this.bind.form.longitude,
      this.bind.form.latitude
    );
    this.createMap(point);
  },
  // 初始化地图
  location() {
    console.debug("开始定位！");
    const geolocation = new BMap.Geolocation();
    var self = this;
    geolocation.getCurrentPosition(
      function (r) {
        console.debug("定位返回", r);
        if (this.getStatus() == BMAP_STATUS_SUCCESS) {
          console.debug("您的位置：" + r.point.lng + "," + r.point.lat);
          self.createMap(r.point);
        } else {
          console.error("定位失败！！！！！！！！！！");
          const point = new BMap.Point(120.135431, 30.27412);
          self.createMap(point);
        }
      },
      {
        enableHighAccuracy: true,
        maximumAge: 0,
        SDKLocation: true,
      }
    );
  },
  // 加载百度地图
  createMap(point) {
    console.debug("开始创建地图！");
    this.mapNode = this.dom.querySelector(".bd-map");
    if (this.mapNode) {
      console.debug("渲染地图", point);
      if (!this.map) {
        this.map = new BMap.Map(this.mapNode); // 创建Map实例
      }
      this.map.centerAndZoom(point, 15); // 初始化地图,设置中心点坐标和地图级别
      this.map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
      this.addControls();
      if (!this.bind.form.isView) {
        this.addMapClick();
      } else {
        this.addMarkPoint(point, this.bind.form.placeName);
      }
    }
  },
  // 添加地图控件
  addControls() {
    //向地图中添加缩放控件
    var ctrl_nav = new BMap.NavigationControl({
      anchor: BMAP_ANCHOR_TOP_RIGHT,
      type: BMAP_NAVIGATION_CONTROL_LARGE,
    });
    this.map.addControl(ctrl_nav);
    // 添加地区选择器
    this.map.addControl(
      new BMap.CityListControl({
        anchor: BMAP_ANCHOR_TOP_LEFT,
        offset: new BMap.Size(10, 20),
        // 切换城市之间事件
        onChangeBefore: function () {},
        // 切换城市之后事件
        onChangeAfter: function () {},
      })
    );
  },
  addMapClick() {
    this.map.addEventListener("click", (e) => {
      console.debug("点击的经纬度：", e);
      this.addMarkPoint(new BMap.Point(e.point.lng, e.point.lat));
      this.bind.form.longitude = e.point.lng;
      this.bind.form.latitude = e.point.lat;
    });
  },
  // 添加位置点
  addMarkPoint(point, placeName) {
    this.map.clearOverlays(); // 先清除
    const mIcon = new BMap.Icon(this.iconUrl, new BMap.Size(23, 25), {
      anchor: new BMap.Size(9, 25),
      imageOffset: new BMap.Size(-46, -21),
    });
    const marker = new BMap.Marker(point, {
      icon: mIcon,
      enableDragging: false,
    });
    if (placeName) {
      const label = new BMap.Label(placeName, {
        offset: new BMap.Size(0, -20),
      });
      marker.setLabel(label);
    }
    this.map.addOverlay(marker);
  },
  // 地图放大缩小
  mapMaxOrMini() {
    this.bind.isMax = !this.bind.isMax;
  },
});
