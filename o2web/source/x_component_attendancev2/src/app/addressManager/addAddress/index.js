import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { isPositiveInt, isEmpty } from '../../../utils/common';
import { getPublicData, attendanceWorkPlaceV2Action } from '../../../utils/actions';
import template from './temp.html';
import oInput from '../../../components/o-input';
import oTextarea from '../../../components/o-textarea';


export default content({
    template,
    components: {oInput, oTextarea},
    autoUpdate: true,
    bind(){
        return {
            lp,
            fTitle: lp.workAddressAdd,
            form: {
              placeName: "",
              errorRange: "200",
              longitude: "",
              latitude: "",
              description: "",
            },
        };
    },
    afterRender() {
        this.loadBDMap();
    },
    // 加载地图api等资源
    async loadBDMap() {
        this.iconUrl = "../x_component_attendancev2/$Main/default/us_mk_icon.png"; //图标样式，发布时候修改为绝对路径
        const bdKey = await getPublicData("baiduAccountKey");
        const accountkey = bdKey || "Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg";
        const apiPath = "//api.map.baidu.com/getscript?v=2.0&ak="+accountkey+"&s=1&services=";
        if( !window.BDMapV2ApiLoaded ){
            o2.load(apiPath, () => {
                this.location();
            });
        } else {
            this.location();
        }
    },
    // 初始化地图
    location() {
        console.debug("地图加载API加载完成，开始载入地图！");
        window.BDMapV2ApiLoaded = true;
        const geolocation = new BMap.Geolocation();
        var self = this;
        geolocation.getCurrentPosition(function(r){
            console.debug("定位返回", r);
            if(this.getStatus() == BMAP_STATUS_SUCCESS){
                console.debug('您的位置：'+r.point.lng+','+r.point.lat);
                self.createMap(r.point);
            } else {
                console.error("定位失败！！！！！！！！！！");
                const point = new BMap.Point(120.135431, 30.27412);
                self.createMap(point);
            } 
        }, {
            enableHighAccuracy: true,
            maximumAge: 0,
            SDKLocation: true,
        });

        // if (navigator.geolocation){
        //     try{
        //         navigator.geolocation.getCurrentPosition(this.initBDMap.bind(this), this.initBDMap.bind(this));
        //     }catch( e ){
        //         console.error(e);
        //         this.initBDMap();
        //     }
        // }else{
        //     this.initBDMap();
        // }
    },
    // 初始化地图
    initBDMap(position) {
        console.debug("位置信息", position);
        this.mapNode = this.dom.querySelector(".bd-map");
        if (this.mapNode) {
            let longitude = 120.135431;
            let latitude = 30.27412;
            if (position && position.coords) {
                latitude = position.coords.latitude || 30.27412;
                longitude = position.coords.longitude || 120.135431;
            }
            const gpsPoint = new BMap.Point(longitude, latitude);
            const convertor = new BMap.Convertor();
            const pointArr = [];
            pointArr.push(gpsPoint);
            console.debug("开始转化百度位置");
            convertor.translate(pointArr, 1, 5, this.translateBMapPoint.bind(this));
        } else {
            console.error("map node 不存在？？？");
        }
    },
    // 转化百度坐标
    translateBMapPoint(data) {
        console.debug("转化百度位置成功", data);
        if (data.status === 0 && data.points && data.points[0]) {
            this.createMap(data.points[0]);
        }
    },
    // 加载百度地图
    createMap(point) {
        this.mapNode = this.dom.querySelector(".bd-map");
        if (this.mapNode) {
            console.debug("渲染地图", point);
            if (!this.map) {
                this.map = new BMap.Map(this.mapNode);    // 创建Map实例
            }
            this.map.centerAndZoom(point, 15);  // 初始化地图,设置中心点坐标和地图级别
            this.map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
            // 测试定位 
            // const marker = new BMap.Marker(point);
            // this.map.addOverlay(marker);
            // this.map.panTo(point);
            this.addControls();
            this.addMapClick();
        }
    },
    // 添加地图控件
    addControls(){
        //向地图中添加缩放控件
        var ctrl_nav = new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_RIGHT,type:BMAP_NAVIGATION_CONTROL_LARGE});
        this.map.addControl(ctrl_nav);
        // 添加地区选择器
        this.map.addControl(new BMap.CityListControl({
            anchor: BMAP_ANCHOR_TOP_LEFT,
            offset: new BMap.Size(10, 20),
            // 切换城市之间事件
             onChangeBefore: function( ){
             },
            // 切换城市之后事件
             onChangeAfter:function( ){
             }
        }));
    },
    addMapClick() {
        this.map.addEventListener('click', (e)=> {
            console.debug('点击的经纬度：' , e);
            this.addMarkPoint( new BMap.Point(e.point.lng, e.point.lat));
            this.bind.form.longitude = e.point.lng;
            this.bind.form.latitude = e.point.lat;
        });
    },
    // 添加位置点
    addMarkPoint(point, placeName) {
        this.map.clearOverlays(); // 先清除
        const mIcon = new BMap.Icon(this.iconUrl, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-46, -21)});
        const marker = new BMap.Marker(point,{
            icon: mIcon,
            enableDragging : false
        });
        if (placeName) {
            const label = new BMap.Label(placeName, {"offset":new BMap.Size(0,-20)});
            marker.setLabel(label);
        } 
        this.map.addOverlay(marker);
    },
    close() {
        this.$parent.closeFormVm();
    },
    async submitAdd() {
        let myForm = this.bind.form;
        if (isEmpty(myForm.longitude) || isEmpty(myForm.latitude)) {
            o2.api.page.notice(lp.workAddressForm.lnglatNotEmpty, 'error');
            return ;
        }
        if (isEmpty(myForm.placeName)) {
            o2.api.page.notice(lp.workAddressForm.titleNotEmpty, 'error');
            return ;
        }
        if (isEmpty(myForm.errorRange)) {
            o2.api.page.notice(lp.workAddressForm.rangeNotEmpty, 'error');
            return ;
        }
        if (!isPositiveInt(myForm.errorRange)) {
            o2.api.page.notice(lp.workAddressForm.rangeNeedNumber, 'error');
            return ;
        }
        const json = await attendanceWorkPlaceV2Action("post", myForm);
        console.debug('新增成功', json);
        o2.api.page.notice(lp.workAddressForm.success, 'success');
        this.close();
    },
   
});
