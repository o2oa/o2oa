import {component as content} from '@o2oa/oovm';
import {lp, layout} from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';
import { isEmpty, lpFormat } from '../../utils/common';
import { mobileAction, getPublicData } from '../../utils/actions';
 

export default content({
    template,
    style,
    autoUpdate: true,
    bind(){
        return {
            lp,
            person: {},
             // 打卡按钮
             checkInCycle: {
                allowFieldWork: false, // 是否允许外勤
                requiredFieldWorkRemarks: true, // 外勤是否必须打卡
                canCheckIn: false, // 是否可打卡
                title: lp.mobile.menu.checkIn, // 打卡按钮名称
                time: "", // 当前时间显示
            },
            // 位置信息
            location: {
                status: false, // 是否定位成功
                title: lp.mobile.locationIng, // 显示地址
                address: "", // 定位地址
                // 定位的经纬度
                lnglat: {}, 
                isNotFieldWork: false, //当是否范围内打卡
            },
            bdKey: "Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg",
            // 打卡工作场所
            workPlaceList:[], 
            recordItemList: [], // 打卡记录和对象列表
            nextCheckInRecord: null, // 下一个打卡对象
        };
    },
    afterRender() {
        this.startTickTime();
        // 用户信息
        this.getCurrentPerson();
        // 定位信息
        this.loadBDMap();
        // 打卡数据查询
        this.getPreCheckData();
    },
    // 当前用户信息
    getCurrentPerson() {
        if (layout.session && layout.session.user) {
            this.bind.person.name = layout.session.user.name;
            const identityList = layout.session.user.identityList;
            if (identityList && identityList.length > 0) {
                this.bind.person.unit = identityList[0].unitLevelName;
            }
            const dAction = o2.Actions.load("x_organization_assemble_control").PersonAction.action;
            let url =  dAction.getAddress() + dAction.actions.getIconWithPerson.uri;
            url = url.replace("{flag}", encodeURIComponent(layout.session.user.id));
            console.debug(url);
            this.bind.person.iconUrl = url;
        }
    },
    // 显示打卡按钮上的当前时间
    startTickTime() {
        this.tickTime();
        //TODO 啥时候删除 ？？？？
        this.tickTimeInterval = setInterval(() => {
            this.tickTime();
        }, 1000);
    },
    tickTime() {
        const date = new Date();
        this.bind.checkInCycle.time = `${date.getHours() > 9 ? date.getHours() : '0'+date.getHours()}:${date.getMinutes() > 9 ? date.getMinutes() : '0'+date.getMinutes()}:${date.getSeconds() > 9 ? date.getSeconds() : '0'+date.getSeconds()}`;
    },
    // 加载地图api等资源 因为后面计算距离啥的要用
    async loadBDMap() {
        const bdKey = await getPublicData("baiduAccountKey");
        const accountkey = bdKey || "sM5P4Xq9zsXGlco6RAq2CRDtwjR78WQB";
        this.bind.bdKey = accountkey;
        let apiPath = "http://api.map.baidu.com/getscript?v=2.0&ak="+accountkey+"&s=1&services=";
        if( window.location.protocol.toLowerCase() === "https:" ){
            window.HOST_TYPE = '2';
            apiPath = "//api.map.baidu.com/getscript?v=2.0&ak="+accountkey+"&s=1&services=";
        }
        if( !window.bMapV2ApiLoaded ){
            o2.load(apiPath, () => {
                this.location();
            });
        } else {
            this.location();
        }
    },
    // 定位
    location() {
        window.bMapV2ApiLoaded = true;
        var self = this;
        const geolocation = new BMap.Geolocation();
        // 开启SDK辅助定位 app webview 支持
        // geolocation.enableSDKLocation();
        geolocation.getCurrentPosition(function(r){
            console.debug("定位返回", r);
            if(this.getStatus() == BMAP_STATUS_SUCCESS){
                console.debug('您的位置：'+r.point.lng+','+r.point.lat);
                 self.bind.location.lnglat.longitude = r.point.lng;
                 self.bind.location.lnglat.latitude = r.point.lat;
                 self.getGeoAddress(r.point);
            } else {
                console.log("定位失败。。。。。。。");
                self.bind.location.title = lp.mobile.locationError;
            } 
        }, {
            enableHighAccuracy: true,
            maximumAge: 0,
            SDKLocation: true,
        });
        // if (navigator.geolocation){
        //     try{
        //         navigator.geolocation.getCurrentPosition(this.callGpsLocation.bind(this), this.callGpsLocation.bind(this));
        //     }catch( e ){
        //         console.error(e);
        //         this.bind.location.title = lp.mobile.locationError;
        //     }
        // } else {
        //     console.error("没有定位！。。。");
        //     this.bind.location.title = lp.mobile.locationError;
        // }
    },
    // 接收gps定位地址
    callGpsLocation(position) {
        console.debug("gps定位位置信息", position);
        if (position && position.coords) {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;
            console.debug("latitude", latitude, "longitude", longitude);
            const gpsPoint = new BMap.Point(longitude, latitude);
            const convertor = new BMap.Convertor();
            const pointArr = [];
            pointArr.push(gpsPoint);
            console.debug("开始转化百度位置");
            convertor.translate(pointArr, 1, 5, this.translateBMapPointSuccess.bind(this));
        } else {
            console.error(" gps定位错误！");
            this.bind.location.title = lp.mobile.locationError;
        }
    },
    // 转化百度坐标
    translateBMapPointSuccess(data) {
        console.debug("转化百度位置返回结果", data);
        if (data.status === 0 && data.points && data.points[0]) {
            console.debug('您的位置：'+data.points[0].lng+','+data.points[0].lat);
            this.bind.location.lnglat.longitude = data.points[0].lng;
            this.bind.location.lnglat.latitude = data.points[0].lat;
            this.getGeoAddress(data.points[0]);
        } else {
            console.error("百度转化gps位置错误！");
            this.bind.location.title = lp.mobile.locationError; 
        }
    },
    // 百度查询地址
    async getGeoAddress(point) {
        console.debug("开始查询定位的详细地址")
        const gc = new BMap.Geocoder();
        gc.getLocation(point, (rs) => {
            console.debug(rs);
            this.bind.location.status = true;
            this.bind.location.address = rs.address;
            this.bind.location.title = rs.address;
            this._calDistance();
        });
    },
    // 查询打卡数据
    async getPreCheckData() {
        const preCheckData = await mobileAction("preCheckIn");
        if (preCheckData) {
            this.bind.checkInCycle.canCheckIn = preCheckData.canCheckIn || false;
            this.bind.checkInCycle.allowFieldWork = preCheckData.allowFieldWork || false;
            this.bind.checkInCycle.requiredFieldWorkRemarks = preCheckData.requiredFieldWorkRemarks || false;
            this.bind.workPlaceList = preCheckData.workPlaceList || [];
            this._calDistance();
            if (this.bind.checkInCycle.canCheckIn) {
                const recordItemList = preCheckData.checkItemList || [];
                // 查找下一条要打卡的数据
                const nextList = recordItemList.filter(r=> r.checkInResult === 'PreCheckIn');
                if (nextList != null && nextList.length > 0) {
                    this.bind.nextCheckInRecord = nextList[0];
                    this.bind.checkInCycle.title = (nextList[0].checkInType === 'OnDuty') ? lp.onDuty : lp.offDuty;
                    this.bind.checkInCycle.canCheckIn = true;
                } else {
                    this.bind.nextCheckInRecord = null;
                    this.bind.checkInCycle.title = lp.mobile.menu.checkIn;
                    this.bind.checkInCycle.canCheckIn = false; // 没有可打卡的数据
                }
                // 处理打卡信息列表
                for (let i = 0; i < recordItemList.length; i++) {
                    const item = recordItemList[i];
                    let isRecord = false;
                    let recordTime = '';
                    if (item.checkInResult !== 'PreCheckIn') {
                        isRecord = true;
                        let signTime = item.recordDate || '';
                        if (signTime.length > 16) {
                          signTime = signTime.substring(11, 16);
                        }
                        recordTime = lpFormat(lp, 'mobile.checkInWithTime', {time: signTime});
                    }
                    item.recordTime = recordTime;
                    item.isRecord = isRecord; // 是否已经打卡
                    item.checkInTypeString =  (item.checkInType === 'OnDuty') ? lp.onDuty : lp.offDuty;
                    let preDutyTime = item.preDutyTime || '';
                    if (isEmpty(item.shiftId)) {
                      preDutyTime = ''; // 如果没有班次信息 表示 自由工时 或者 休息日 不显示 打卡时间
                    }
                    item.preDutyTime = preDutyTime;
                    // 处理是否是最后一个已经打卡的记录
                    if (item.checkInResult !== 'PreCheckIn') {
                        if (i == recordItemList.length-1) { // 最后一条
                            item.isLastRecord = true; // 最后一条已经打卡的记录
                        } else {
                            const nextItem = recordItemList[i+1];
                            if (nextItem.checkInResult === 'PreCheckIn') {
                                item.isLastRecord = true;
                            }
                        }
                    }
                    recordItemList[i] = item;
                }
                this.bind.recordItemList = recordItemList;
            }
        } else {
            console.error("请求错误，没有返回打卡数据！");
        }
    },
    
    // 计算距离
    _calDistance() {
        if (this.bind.location.status && this.bind.workPlaceList.length > 0) {
            // 开始计算是否在打卡范围内
            if (!this.map) {
                this.map = new BMap.Map(".bmap"); 
            }
            for (let index = 0; index < this.bind.workPlaceList.length; index++) {
                const workPlace = this.bind.workPlaceList[index];
                const testA = new BMap.Point(workPlace.longitude, workPlace.latitude);
                const testB = new BMap.Point(this.bind.location.lnglat.longitude, this.bind.location.lnglat.latitude);
                const range = this.map.getDistance(testA, testB).toFixed(2);
                console.log(range);
                if (range <= workPlace.errorRange) {
                    console.log("范围内打卡", workPlace);
                    this.bind.location.workPlace = workPlace;
                    this.bind.location.isNotFieldWork = true;
                    this.bind.location.title = workPlace.placeName;
                    break;
                }
            }
            console.log(this.bind);
        }
    },
    // 更新打卡
    updateCheckIn(record) {
        if (record && record.isLastRecord) {
            if (this.bind.location.isNotFieldWork) {
                // 正常打卡
                console.log('正常打卡！');
                this.checkInPost(record, this.bind.location.workPlace.id, false, null);
            } else {
                console.debug('外勤打卡！');
                this.outSide(record);
            }
        }
    },
    // 点击打卡
    actionCheckIn() {
        if (this.bind.checkInCycle.canCheckIn) {
            if (this.bind.location.isNotFieldWork) {
                // 正常打卡
                console.log('正常打卡！');
                this.checkInPost(this.bind.nextCheckInRecord, this.bind.location.workPlace.id, false, null);
            } else {
                console.debug('外勤打卡！');
                this.outSide(this.bind.nextCheckInRecord);
            }
        }
    },
    // 外勤打卡
    outSide(record) {
        if (!record) {
            console.error("错误的数据！");
            return;
        }
        if (!this.bind.checkInCycle.allowFieldWork) {
            o2.api.page.notice(lp.mobile.outsideNotAllow, "error");
            return;
        }
        // 必须填写外勤说明
        if (this.bind.checkInCycle.requiredFieldWorkRemarks) {
            var _self = this;
            o2.DL.open({
                "title": lp.mobile.outsideTitle,
                "width": "100%",
                "height": "150",
                "style" : "user",
                "html": "<div style='margin-top:10px;'><input style='width:100%;' type='text' placeholder='"+lp.mobile.outsideRemarkPlaceholder+"'></div>",
                "buttonList": [
                    {
                        "text": lp.positive,
                        "class":"comment_dlg_button_ok",
                        "action": function(){
                            const value = this.node.getElement("input").value; //this指向对话框对象
                            if( !value ){
                                o2.api.page.notice(lp.mobile.outsideRemarkPlaceholder, "error");
                            }else{
                                _self.checkInPost(record, null, true, value);
                                this.close();
                            }
                        }
                    },
                    {
                        "type": "cancel",
                        "text": lp.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });
        } else {
            this.checkInPost(record, null, true, null);
        }
    },
    // 提交打卡数据
    async checkInPost(record, workPlaceId, isOutside, signDesc) {
        const post = {
            recordId: record.id,
            checkInType: record.checkInType,
            workPlaceId: workPlaceId,
            fieldWork: isOutside,
            signDescription: signDesc,
            latitude: this.bind.location.lnglat.latitude,
            longitude: this.bind.location.lnglat.longitude,
            recordAddress:  this.bind.location.address,
        };
        console.debug(post);
        const result = await mobileAction("checkIn", post);
        console.log(result);
        this.getPreCheckData();
    }
  
  
     
});
