import { component as content } from '@o2oa/oovm';
import { lp } from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';
import { getPublicData, mobileAction, invokeAction, qywxAuthAction } from '../../utils/actions';
import { WGS84_TO_GCJ02, getDistance } from '../../utils/common';


export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            checkInCycle: {
                canCheckIn: false,
                title: lp.mobile.menu.checkIn,
                time: '',
                tip: '',
            },
            location: {
                status: false,
                locating: true,
                inRange: false,
                title: lp.mobile.locationIng,
                address: '',
                lnglat: {},
                workPlace: null,
            },
            workPlaceList: [],
            recordItemList: [],
            nextCheckInRecord: null,
            bMapV2ApiLoaded: false,
            qywxSdkLoaded: false,
        };
    },
    afterRender() {
        this.startTickTime();
        this.getPreCheckData();
        this.loadQywxSdk();
    },
    startTickTime() {
        this.tickTime();
        this.tickTimeInterval = setInterval(() => {
            this.tickTime();
        }, 1000);
    },
    tickTime() {
        const date = new Date();
        this.bind.checkInCycle.time = `${this.pad(date.getHours())}:${this.pad(date.getMinutes())}`;
    },
    pad(value) {
        return value > 9 ? value : `0${value}`;
    },
    async getPreCheckData() {
        const preCheckData = await mobileAction('preCheckIn');
        if (!preCheckData) {
            this.bind.checkInCycle.canCheckIn = false;
            this.bind.checkInCycle.title = lp.mobile.menu.checkIn;
            this.bind.checkInCycle.tip = lp.dataError;
            return;
        }

        this.bind.workPlaceList = preCheckData.workPlaceList || [];
        this.bind.recordItemList = this.buildRecordList(preCheckData.checkItemList || []);
        this.bind.nextCheckInRecord = this.bind.recordItemList.find((item) => item.checkInResult === 'PreCheckIn') || null;
        this.bind.checkInCycle.canCheckIn = !!(preCheckData.canCheckIn && this.bind.nextCheckInRecord);

        if (this.bind.nextCheckInRecord) {
            this.bind.checkInCycle.title = this.bind.nextCheckInRecord.checkInType === 'OnDuty' ? lp.onDuty : lp.offDuty;
            this.bind.checkInCycle.tip = this.getCheckInTip(this.bind.nextCheckInRecord);
        } else {
            this.bind.checkInCycle.title = lp.mobile.menu.checkIn;
            this.bind.checkInCycle.tip = '今日暂无待打卡班次';
        }
        this.qywxCalDistance();
    },
    buildRecordList(list) {
        return list.map((item, index) => {
            const checked = item.checkInResult !== 'PreCheckIn';
            const preDutyTime = item.preDutyTime || this.formatTime(item.recordDate);
            return Object.assign({}, item, {
                checked,
                preDutyTime,
                checkInTypeShort: item.checkInType === 'OnDuty' ? lp.onDutySimple : lp.offDutySimple,
                checkInTypeText: item.checkInType === 'OnDuty' ? lp.onDuty : lp.offDuty,
                isLast: index === list.length - 1,
            });
        });
    },
    formatTime(dateString) {
        if (!dateString || dateString.length < 16) {
            return '';
        }
        return dateString.substring(11, 16);
    },
    getCheckInTip(record) {
        if (!record || !record.preDutyTime) {
            return '';
        }
        return record.checkInType === 'OffDuty' ? `请 ${record.preDutyTime} 后打卡` : `请 ${record.preDutyTime} 前打卡`;
    },
    async loadBDMap() {
        const bdKey = await getPublicData('baiduAccountKey');
        const accountkey = bdKey || 'DlUbKMs0YhvdqFJa403U7ofmsHfIRvCK';
        let apiPath = `http://api.map.baidu.com/getscript?v=2.0&ak=${accountkey}&s=1&services=`;
        if (window.location.protocol.toLowerCase() === 'https:') {
            window.HOST_TYPE = '2';
            apiPath = `//api.map.baidu.com/getscript?v=2.0&ak=${accountkey}&s=1&services=`;
        }
        if (!window.bMapV2ApiLoaded) {
            o2.load(apiPath, () => {
                window.bMapV2ApiLoaded = true;
                this.bind.bMapV2ApiLoaded = true;
                this.location();
            });
        } else {
            this.bind.bMapV2ApiLoaded = true;
            this.location();
        }
    },
    // 企业微信sdk加载
    async loadQywxSdk() {
        let sdkPath = 'https://wwcdn.weixin.qq.com/node/wework/wwopen/js/wecom-jssdk-2.4.0.js';
        if (window.location.protocol.toLowerCase() === 'https:') {
            window.HOST_TYPE = '2';
            sdkPath = `//wwcdn.weixin.qq.com/node/wework/wwopen/js/wecom-jssdk-2.4.0.js`;
        }
        if (!window.qywxSdkLoaded) {
            o2.load(sdkPath, () => {
                window.qywxSdkLoaded = true;
                this.bind.qywxSdkLoaded = true;
                this.qywxLocation();
            });
        } else {
            this.bind.qywxSdkLoaded = true;
            this.qywxLocation();
        }
    },
    async getConfigSignature() {
        const url = window.location.href.split('#')[0];
        const config = await qywxAuthAction('info', {
            url: url,
            nonceStr: 'o2oa'
        });
        return { timestamp: config.timestamp, nonceStr: config.nonceStr, signature: config.signature, corpId: config.corpid  }
    },
    async getConfigSignatureCache() {
        if (!this.configSignature) {
            this.configSignature = await this.getConfigSignature();
        }
        return this.configSignature;
    },
    //企业微信定位
    async qywxLocation() {
        this.bind.location.locating = true;
        this.bind.location.title = lp.mobile.locationIng;
        this.bind.location.inRange = false;
        this.bind.location.workPlace = null;
        this.configSignature = await this.getConfigSignature();
        // 先注册 api
        const re = await ww.register({
            corpId: this.configSignature.corpId,
            jsApiList: ['getLocation'],
            getConfigSignature: this.getConfigSignatureCache.bind(this), 
        })
        console.log('register result', re);
        ww.getLocation({
            type: 'gcj02'
        }).then((res) => {
            if (res && res.latitude && res.longitude) {
                this.bind.location.lnglat.longitude = res.longitude;
                this.bind.location.lnglat.latitude = res.latitude;
                // 查询地址
                this.getQywxGeoAddress(res.latitude, res.longitude);
            } else {
                console.error('获取企业微信定位失败', res);
                this.setLocationError();
            }
        }).catch((err) => {
            console.error('获取企业微信定位失败', err);
            this.setLocationError();
        });
    },
    // 百度地图定位
    location() {
        this.bind.location.locating = true;
        this.bind.location.title = lp.mobile.locationIng;
        this.bind.location.inRange = false;
        this.bind.location.workPlace = null;

        if (!window.BMap) {
            this.setLocationError();
            return;
        }

        const geolocation = new BMap.Geolocation();
        geolocation.getCurrentPosition((result) => {
            if (geolocation.getStatus() === BMAP_STATUS_SUCCESS && result && result.point) {
                this.bind.location.lnglat.longitude = result.point.lng;
                this.bind.location.lnglat.latitude = result.point.lat;
                this.getGeoAddress(result.point);
            } else {
                this.setLocationError();
            }
        }, {
            enableHighAccuracy: true,
            maximumAge: 0,
            SDKLocation: true,
        });
    },
    // 百度地图查询地址
    getGeoAddress(point) {
        const geocoder = new BMap.Geocoder();
        geocoder.getLocation(point, (result) => {
            const address = result && result.address ? result.address : '';
            this.bind.location.status = true;
            this.bind.location.locating = false;
            this.bind.location.address = address;
            this.bind.location.title = address || '已获取当前位置';
            this.calDistance();
        });
    },
    async getQywxGeoAddress(latitude, longitude) {
        const result = await invokeAction('execute', 'wx_geocoder', {
            latitude,
            longitude
        });
        const address = result && result.value ? result.value.address : '';
        const err = result && result.value ? result.value.err : '';
        if (err) {
            this.setLocationError();
            return;
        }
        this.bind.location.status = true;
        this.bind.location.locating = false;
        this.bind.location.address = address;
        this.bind.location.title = address || '已获取当前位置';
        this.qywxCalDistance();
    },
    // 企业微信计算距离
    qywxCalDistance() {
        if (!this.bind.location.status || !this.bind.location.lnglat.longitude || !this.bind.location.lnglat.latitude) {
            return;
        }

        if (!this.bind.workPlaceList.length) {
            this.bind.location.inRange = false;
            this.bind.location.workPlace = null;
            this.bind.location.title = this.bind.location.address || lp.mobile.locationError;
            return;
        }
        let matchedPlace = null;
        for (let i = 0; i < this.bind.workPlaceList.length; i++) {
            const place = this.bind.workPlaceList[i];
            const longitude = parseFloat(place.gpsLng);
            const latitude = parseFloat(place.gpsLat);
            const range = parseFloat(place.errorRange);
            if (Number.isNaN(longitude) || Number.isNaN(latitude) || Number.isNaN(range)) {
                continue;
            }
            const gcj02Point = WGS84_TO_GCJ02.transform(latitude, longitude);
            const distance = getDistance(gcj02Point.latitude, gcj02Point.longitude, this.bind.location.lnglat.latitude, this.bind.location.lnglat.longitude);
            if (distance <= range) {
                matchedPlace = place;
                break;
            }
        }
        this.bind.location.inRange = !!matchedPlace;
        this.bind.location.workPlace = matchedPlace;
        this.bind.location.title = matchedPlace ? (matchedPlace.placeAlias || matchedPlace.placeName) : this.bind.location.address;

    },
    setLocationError() {
        this.bind.location.status = false;
        this.bind.location.locating = false;
        this.bind.location.inRange = false;
        this.bind.location.workPlace = null;
        this.bind.location.title = lp.mobile.locationError;
    },
    // 百度地图计算距离
    calDistance() {
        if (!this.bind.location.status || !this.bind.location.lnglat.longitude || !this.bind.location.lnglat.latitude) {
            return;
        }

        if (!this.bind.workPlaceList.length) {
            this.bind.location.inRange = false;
            this.bind.location.workPlace = null;
            this.bind.location.title = this.bind.location.address || lp.mobile.locationError;
            return;
        }

        if (!this.map) {
            this.map = new BMap.Map(this.dom.querySelector('.check-in-bmap'));
        }

        const currentPoint = new BMap.Point(this.bind.location.lnglat.longitude, this.bind.location.lnglat.latitude);
        let matchedPlace = null;
        for (let i = 0; i < this.bind.workPlaceList.length; i++) {
            const place = this.bind.workPlaceList[i];
            const longitude = parseFloat(place.longitude);
            const latitude = parseFloat(place.latitude);
            const range = parseFloat(place.errorRange);
            if (Number.isNaN(longitude) || Number.isNaN(latitude) || Number.isNaN(range)) {
                continue;
            }
            const workPoint = new BMap.Point(longitude, latitude);
            const distance = this.map.getDistance(workPoint, currentPoint);
            if (distance <= range) {
                matchedPlace = place;
                break;
            }
        }

        this.bind.location.inRange = !!matchedPlace;
        this.bind.location.workPlace = matchedPlace;
        this.bind.location.title = matchedPlace ? (matchedPlace.placeAlias || matchedPlace.placeName) : this.bind.location.address;
    },
    actionCheckIn() {
        // 打卡提交功能后续接入；当前页面先完成预打卡、定位和范围判断展示。
    },
});
