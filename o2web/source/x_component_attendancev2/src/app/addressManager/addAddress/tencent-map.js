import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";

import { getPublicData } from "../../../utils/actions";

const template = `
  <div class=" {{ $.isMax ? 'bd-map-container-fixed': 'bd-map-container'}}">
      <div class="bd-map">
          <!-- 腾讯地图 -->
      </div>
      <div class="icon bd-map-btn" @click="mapMaxOrMini" ><i class="{{  $.isMax ? 'o2icon-shrink' : 'o2icon-enlarge' }}"></i></div>
  </div>
`;

// 腾讯地图组件
export default content({
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
      form: {
        longitude: "",
        latitude: "",
        status: "a", // a 新增 ，u 更新， v 查看
        isView: false,
      },
      isMax: false, // 地图放大
    };
  },
  afterRender() {
    this.loadTencentMap();
  },
  // 加载地图api等资源
  async loadTencentMap() {
    if (!window.TencentMapApiLoaded) {
      const config = await getPublicData("attendanceMapConfig"); // 地图配置
      let accountkey = "OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77";
      if (config && config.txMapAccountKey) {
        accountkey = config.txMapAccountKey;
      } else {
        console.error("没有配置地图 Key ！！！");
      }
      const apiPath = "https://map.qq.com/api/gljs?v=1.exp&key=" + accountkey;
      o2.load(apiPath, () => {
        console.debug("腾讯地图加载API加载完成，开始载入地图！");
        window.TencentMapApiLoaded = true;
        this.loadViewOrLocation();
      });
    } else {
      this.loadViewOrLocation();
    }
  },
  loadViewOrLocation() {
    if (this.bind.form.status !== "a") {
      this.loadMapView();
    } else {
      this.initDefaultNumberMapView();
    }
  },
  // 查看工作场所 使用参数中的位置
  loadMapView() {
    const point = this.createLatLng(
      this.bind.form.longitude,
      this.bind.form.latitude
    );
    this.createMap(point);
  },
  // 默认打开地图 一个固定值
  initDefaultNumberMapView() {
    const point = this.createLatLng(120.135431, 30.27412);
    this.createMap(point);
  },
  createLatLng(lng, lat) {
    return new TMap.LatLng(Number(lat), Number(lng));
  },
  // 加载腾讯地图
  createMap(point) {
    console.debug("开始创建腾讯地图！", point);
    this.mapNode = this.dom.querySelector(".bd-map");
    if (this.mapNode) {
      if (!this.map) {
        this.map = new TMap.Map(this.mapNode, {
          zoom: 17,
          center: point,
          viewMode: "2D",
        });
      } else {
        this.map.setCenter(point);
      }
      if (!this.bind.form.isView) {
        this.addMapClick();
      }
      if (this.bind.form.status !== "a") {
        this.addMarkPoint(point, this.bind.form.placeName);
      }
    }
  },
  addMapClick() {
    if (this.mapClickAdded) {
      return;
    }
    this.mapClickAdded = true;
    this.map.on("click", (e) => {
      console.debug("点击的经纬度：", e);
      const latitude = e.latLng.getLat();
      const longitude = e.latLng.getLng();
      const point = new TMap.LatLng(latitude, longitude);
      this.addMarkPoint(point);
      this.bind.form.longitude = longitude;
      this.bind.form.latitude = latitude;
    });
  },
  // 添加位置点
  addMarkPoint(point, placeName) {
    const geometry = {
      id: "workplace",
      position: point,
    };
    if (placeName) {
      geometry.content = placeName;
    }
    if (this.markerLayer) {
      this.markerLayer.setGeometries([geometry]);
    } else {
      this.markerLayer = new TMap.MultiMarker({
        id: "workplace-marker-layer",
        map: this.map,
        geometries: [geometry],
      });
    }
  },
  // 地图放大缩小
  mapMaxOrMini() {
    this.bind.isMax = !this.bind.isMax;
  },
});
