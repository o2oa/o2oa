import { component as content } from '@o2oa/oovm';
import { lp, o2 } from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';
import { getPublicData, mobileAction, invokeAction, qywxAuthAction, configAction } from '../../utils/actions';
import { WGS84_TO_GCJ02, getDistance } from '../../utils/common';


export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            checkInCycle: {
                allowFieldWork: false,
                requiredFieldWorkRemarks: false,
                requiredFieldWorkPhoto: false,
                canCheckIn: false,
                title: lp.mobile.menu.checkIn,
                time: '',
                tip: '',
                submitting: false,
            },
            showAMap: false, //  ture 显示高德地图
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
            updateOffDutyRecord: null,
            checkInAlertConfigEnable: true,
            bMapV2ApiLoaded: false,
            qywxSdkLoaded: false,
        };
    },
    async beforeRender() {
        await this.loadCheckInAlertConfigEnable();
    },
    afterRender() {
        this.startTickTime();
        this.getPreCheckData();
        this.loadAMap();
        this.loadQywxSdk();

    },
    // 加载高德地图api等资源
    async loadAMap() {
        if (!window.AMapApiLoaded) {
            const config = await getPublicData("attendanceMapConfig"); // 地图配置
            if (config && config.aMapAccountKey) {
                this.bind.showAMap = true;
                 let apiPath = "http://webapi.amap.com/maps?v=1.4.15&key=" + config.aMapAccountKey;
                if (window.location.protocol.toLowerCase() === "https:") {
                    window.HOST_TYPE = "2";
                    apiPath = "//webapi.amap.com/maps?v=1.4.15&key=" + config.aMapAccountKey;
                }
                o2.load(apiPath, () => {
                    console.debug("高德地图加载API加载完成，开始载入地图！");
                    window.AMapApiLoaded = true;
                    this.showAMap();
                });
            } else {
                console.error("没有配置地图 Key ！！！");
                this.bind.showAMap = false; // 不显示地图，显示定位信息
            }
        } else {
            this.bind.showAMap = true;
            this.showAMap();
        }
    },
    // 添加高德地图
    async showAMap() {
        const point = new AMap.LngLat(109.173571, 18.328807);
        this.createAMap(point);
    },
    // 创建高德地图
    createAMap(point) {
        console.debug("开始创建高德地图！", point);
        if (!this.amap) {
            this.amap = new AMap.Map("amap-container", {
                zoom: 17, //级别
                center: point, //中心点坐标
                viewMode: "3D", //使用3D视图
            }); // 创建Map实例
        }
    },
    // 添加高德地图圆形范围
    addAMapCircle(point, radius) {
        // 先清除
        if (this.amapCircle) {
            this.amap.remove(this.amapCircle);
            this.amapCircle = null;
        }
        //创建圆形 Circle 实例
        this.amapCircle = new AMap.Circle({
            center: point, //圆心
            radius: radius, //半径
            bubble: true, //允许覆盖物点击事件冒泡到地图，避免拦截地图 click
            // borderWeight: 3, //描边的宽度
            strokeColor: "#1791fc", //轮廓线颜色
            strokeOpacity: 1, //轮廓线透明度
            strokeWeight: 1, //轮廓线宽度
            fillOpacity: 0.4, //圆形填充透明度
            // strokeStyle: "dashed", //轮廓线样式
            fillColor: "#1791fc", //圆形填充颜色
            zIndex: 50, //圆形的叠加顺序
        });
        this.amap.add(this.amapCircle); //在地图上添加圆形
    },
    // 添加高德地图标记点
    addAMapMarkPoint(point, placeName) {
        // 先清除
        if (this.aMapMarker) {
            this.amap.remove(this.aMapMarker);
            this.aMapMarker = null;
        }
        this.aMapMarker = new AMap.Marker({
            icon: new AMap.Icon(),
            position: point,
            label: {
                content: placeName || "",
                offset: new AMap.Pixel(0, -20),
            },
        });
        this.amap.add(this.aMapMarker);
        // 地图移动到当前点的位置
        this.amap.setCenter(point);
    },
    async loadCheckInAlertConfigEnable() {
        try {
            const config = await configAction('get');
            this.bind.checkInAlertConfigEnable = !config || config.checkInAlertEnable !== false;
        } catch (err) {
            console.error('查询考勤配置失败', err);
            this.bind.checkInAlertConfigEnable = true;
        }
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
            this.bind.updateOffDutyRecord = null;
            return;
        }

        this.bind.workPlaceList = preCheckData.workPlaceList || [];
        this.bind.checkInCycle.allowFieldWork = !!preCheckData.allowFieldWork;
        this.bind.checkInCycle.requiredFieldWorkRemarks = !!preCheckData.requiredFieldWorkRemarks;
        this.bind.checkInCycle.requiredFieldWorkPhoto = !!preCheckData.requiredFieldWorkPhoto;
        this.bind.recordItemList = this.buildRecordList(preCheckData.checkItemList || []);
        this.bind.nextCheckInRecord = this.bind.recordItemList.find((item) => item.checkInResult === 'PreCheckIn') || null;
        this.bind.updateOffDutyRecord = this.getUpdateOffDutyRecord(this.bind.recordItemList);
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
            const checkStatusIcon = this.getCheckStatusIcon(item.checkInResult);
            const preDutyTime = item.checkInResult === 'PreCheckIn' ? item.preDutyTime : this.formatTime(item.recordDate);
            return Object.assign({}, item, {
                checkStatusIcon,
                checkStatusClass: item.checkInResult === 'Normal' ? 'shift-checked' : 'shift-abnormal',
                preDutyTime,
                checkInTypeShort: item.checkInType === 'OnDuty' ? lp.onDutySimple : lp.offDutySimple,
                checkInTypeText: item.checkInType === 'OnDuty' ? lp.onDuty : lp.offDuty,
                isLast: index === list.length - 1,
            });
        });
    },
    getCheckStatusIcon(checkInResult) {
        if (!checkInResult || checkInResult === 'PreCheckIn') {
            return '';
        }
        return checkInResult === 'Normal' ? '✓' : '!';
    },
    getUpdateOffDutyRecord(list) {
        if (!list.length || list.some((item) => item.checkInResult === 'PreCheckIn')) {
            return null;
        }
        for (let i = list.length - 1; i >= 0; i--) {
            if (list[i].checkInType === 'OffDuty') {
                return list[i];
            }
        }
        return null;
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
            jsApiList: ['getLocation', 'chooseImage', 'getLocalImgData'],
            getConfigSignature: this.getConfigSignatureCache.bind(this), 
        })
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
        const result = await invokeAction('execute', 'geocoder_search_address', {
            latitude,
            longitude
        });
        const address = result && result.value ? result.value.address : '';
        const err = result && result.value ? result.value.err : '';
        if (err) {
            console.error('查询地址错误',  err);
            // this.setLocationError();
            // return;
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
            // 下面地图模式要使用
            place.longitude = gcj02Point.longitude;
            place.latitude = gcj02Point.latitude;
            const distance = getDistance(gcj02Point.latitude, gcj02Point.longitude, this.bind.location.lnglat.latitude, this.bind.location.lnglat.longitude);
            if (distance <= range) {
                matchedPlace = place;
                break;
            }
        }
        this.bind.location.inRange = !!matchedPlace;
        this.bind.location.workPlace = matchedPlace;
        this.bind.location.title = matchedPlace ? (matchedPlace.placeAlias || matchedPlace.placeName) : this.bind.location.address;
        // 地图模式
        if (this.bind.showAMap && this.amap && matchedPlace) {
            const point = new AMap.LngLat( parseFloat(matchedPlace.longitude), parseFloat(matchedPlace.latitude));
            const range = matchedPlace.errorRange || 200;
            this.addAMapCircle(point, range);
            const locationPoint = new AMap.LngLat(this.bind.location.lnglat.longitude, this.bind.location.lnglat.latitude);
            this.addAMapMarkPoint(locationPoint);
        }

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
        if (!this.bind.checkInCycle.canCheckIn || this.bind.checkInCycle.submitting) {
            return;
        }

        const record = this.bind.nextCheckInRecord;
        if (!record) {
            o2.api.page.notice(lp.dataError, 'error');
            return;
        }

        this.submitCheckInRecord(record);
    },
    actionUpdateOffDuty() {
        if (!this.bind.updateOffDutyRecord || this.bind.checkInCycle.submitting) {
            return;
        }
        const record = this.bind.updateOffDutyRecord;
        this.confirmCheckIn('会将当前时间更新到最后一条下班打卡记录，是否继续？', () => {
            this.submitCheckInRecord(record);
        });
    },
    async openCheckInAlertConfig() {
        try {
            const personConfig = await this.loadPersonCheckInAlertConfig();
            this.showCheckInAlertConfigDialog(personConfig);
        } catch (err) {
            console.error('查询个人打卡提醒配置失败', err);
            o2.api.page.notice(lp.dataError, 'error');
        }
    },
    async loadPersonCheckInAlertConfig() {
        const personConfig = await configAction('getPersonConfig') || {};
        if (!personConfig.properties) {
            personConfig.properties = {};
        }
        if (personConfig.properties.checkInAlertOnDutyEnable !== false) {
            personConfig.properties.checkInAlertOnDutyEnable = true;
        }
        if (personConfig.properties.checkInAlertOffDutyEnable !== false) {
            personConfig.properties.checkInAlertOffDutyEnable = true;
        }
        return personConfig;
    },
    showCheckInAlertConfigDialog(personConfig) {
        const node = document.createElement('div');
        node.className = 'check-in-alert-config-dialog';
        node.style.padding = '4px 0';
        node.innerHTML = this.getCheckInAlertConfigDialogHtml(personConfig);
        const _self = this;
        Array.prototype.forEach.call(node.querySelectorAll('.check-in-alert-switch-row'), (item) => {
            item.addEventListener('click', function () {
                _self.toggleCheckInAlertConfig(personConfig, this.getAttribute('data-key'), node);
            });
        });
        o2.DL.open({
            title: '打卡提醒设置',
            width: '100%',
            height: '220',
            style: 'user',
            content: node,
            buttonList: [
                {
                    type: 'cancel',
                    text: lp.close || lp.cancel,
                    action: function () {
                        this.close();
                    }
                }
            ]
        });
    },
    getCheckInAlertConfigDialogHtml(personConfig) {
        const properties = personConfig.properties || {};
        return [
            this.getCheckInAlertSwitchRowHtml('checkInAlertOnDutyEnable', '上班打卡提醒', properties.checkInAlertOnDutyEnable !== false),
            this.getCheckInAlertSwitchRowHtml('checkInAlertOffDutyEnable', '下班打卡提醒', properties.checkInAlertOffDutyEnable !== false)
        ].join('');
    },
    getCheckInAlertSwitchRowHtml(key, label, enable) {
        const switchClass = enable ? 'check-in-alert-switch check-in-alert-switch-on' : 'check-in-alert-switch';
        const switchStyle = enable ? 'background:#35a854;' : 'background:#d9d9d9;';
        const switchDotStyle = enable ? 'left:22px;' : 'left:2px;';
        return [
            `<div class="check-in-alert-switch-row" data-key="${key}" style="min-height:52px;padding:0 4px;border-bottom:1px solid #f0f0f0;display:flex;align-items:center;justify-content:space-between;cursor:pointer;">`,
            `<div class="check-in-alert-switch-label" style="color:#222222;font-size:16px;line-height:24px;">${label}</div>`,
            `<div class="${switchClass}" style="width:48px;height:28px;border-radius:14px;position:relative;transition:background 0.2s;${switchStyle}"><span style="width:24px;height:24px;border-radius:50%;background:#ffffff;box-shadow:0 2px 5px rgba(0,0,0,0.18);position:absolute;top:2px;transition:left 0.2s;${switchDotStyle}"></span></div>`,
            '</div>'
        ].join('');
    },
    async toggleCheckInAlertConfig(personConfig, key, node) {
        if (!key || !personConfig || this.isCheckInAlertConfigDialogSaving(node)) {
            return;
        }
        if (!personConfig.properties) {
            personConfig.properties = {};
        }
        const oldValue = personConfig.properties[key] !== false;
        personConfig.properties[key] = !oldValue;
        this.setCheckInAlertConfigDialogSaving(node, true);
        this.renderCheckInAlertConfigDialog(node, personConfig);
        try {
            await configAction('postPersonConfig', personConfig);
            o2.api.page.notice(lp.saveSuccess, 'success');
        } catch (err) {
            personConfig.properties[key] = oldValue;
            console.error('保存打卡提醒配置失败', err);
            o2.api.page.notice(lp.saveFail || '保存失败', 'error');
        }
        this.setCheckInAlertConfigDialogSaving(node, false);
        this.renderCheckInAlertConfigDialog(node, personConfig);
    },
    isCheckInAlertConfigDialogSaving(node) {
        return !!(node && node.getAttribute('data-saving') === 'true');
    },
    setCheckInAlertConfigDialogSaving(node, saving) {
        if (!node) {
            return;
        }
        node.setAttribute('data-saving', saving ? 'true' : 'false');
        node.style.opacity = saving ? '0.72' : '1';
        node.style.pointerEvents = saving ? 'none' : 'auto';
    },
    renderCheckInAlertConfigDialog(node, personConfig) {
        if (!node) {
            return;
        }
        const properties = personConfig.properties || {};
        ['checkInAlertOnDutyEnable', 'checkInAlertOffDutyEnable'].forEach((key) => {
            const row = node.querySelector(`[data-key="${key}"]`);
            if (!row) {
                return;
            }
            const switchNode = row.querySelector('.check-in-alert-switch');
            if (switchNode) {
                const enable = properties[key] !== false;
                const switchDotNode = switchNode.querySelector('span');
                switchNode.className = enable ? 'check-in-alert-switch check-in-alert-switch-on' : 'check-in-alert-switch';
                switchNode.style.background = enable ? '#35a854' : '#d9d9d9';
                if (switchDotNode) {
                    switchDotNode.style.left = enable ? '22px' : '2px';
                }
            }
        });
    },
    submitCheckInRecord(record) {
        if (!this.bind.location.status || !this.bind.location.lnglat.longitude || !this.bind.location.lnglat.latitude) {
            o2.api.page.notice(lp.mobile.locationError, 'error');
            return;
        }

        const submit = () => {
            if (this.bind.location.inRange) {
                this.checkInPost(record, this.bind.location.workPlace.id, false, '', []);
            } else {
                this.fieldWorkCheckIn(record);
            }
        };

        const confirmMessage = this.getExceptionCheckInMessage(record);
        if (confirmMessage) {
            this.confirmCheckIn(confirmMessage, submit);
        } else {
            submit();
        }
    },
    confirmCheckIn(message, okAction) {
        o2.api.page.confirm(
            'warn',
            lp.alert,
            message,
            300,
            120,
            function () {
                okAction();
                this.close();
            },
            function () {
                this.close();
            }
        );
    },
    getExceptionCheckInMessage(record) {
        const dutyTime = this.getRecordDutyTime(record);
        if (!dutyTime) {
            return '';
        }
        const now = new Date();
        if (record.checkInType === 'OnDuty' && now.getTime() > dutyTime.getTime()) {
            return '当前已超过上班打卡时间，打卡可能会记为迟到，是否继续？';
        }
        if (record.checkInType === 'OffDuty' && now.getTime() < dutyTime.getTime()) {
            return '当前未到下班打卡时间，打卡可能会记为早退，是否继续？';
        }
        return '';
    },
    getRecordDutyTime(record) {
        if (!record) {
            return null;
        }
        if (record.recordDate) {
            const date = new Date(record.recordDate);
            if (!Number.isNaN(date.getTime())) {
                return date;
            }
        }
        if (!record.preDutyTime) {
            return null;
        }
        const date = new Date();
        const time = record.preDutyTime.split(':');
        if (time.length < 2) {
            return null;
        }
        date.setHours(parseInt(time[0], 10), parseInt(time[1], 10), 0, 0);
        return Number.isNaN(date.getTime()) ? null : date;
    },
    fieldWorkCheckIn(record) {
        if (!this.bind.checkInCycle.allowFieldWork) {
            o2.api.page.notice(lp.mobile.outsideNotAllow, 'error');
            return;
        }
        if (!this.bind.checkInCycle.requiredFieldWorkRemarks && !this.bind.checkInCycle.requiredFieldWorkPhoto) {
            this.checkInPost(record, null, true, '', []);
            return;
        }
        this.openFieldWorkDialog(record);
    },
    openFieldWorkDialog(record) {
        const requiredRemark = this.bind.checkInCycle.requiredFieldWorkRemarks;
        const requiredPhoto = this.bind.checkInCycle.requiredFieldWorkPhoto;
        const html = [
            "<div class='check-in-fieldwork-dialog' style='position:relative;padding:10px 0;'>",
            "<textarea class='check-in-fieldwork-remark' style='box-sizing:border-box;width:100%;height:96px;padding:8px;border:1px solid #dcdcdc;border-radius:4px;font-size:14px;line-height:20px;resize:none;' placeholder='" + lp.mobile.outsideRemarkPlaceholder + "'></textarea>",
            requiredPhoto ? "<div class='check-in-fieldwork-photo-tip' style='margin-top:8px;color:#8c8c8c;font-size:13px;line-height:20px;'>点击确定后需拍照提交</div>" : '',
            "<div class='check-in-fieldwork-loading' style='display:none;position:fixed;left:0;right:0;top:0;bottom:0;z-index:9999;align-items:center;justify-content:center;background:rgba(255,255,255,0.72);color:#333;font-size:15px;line-height:24px;text-align:center;'>正在提交，请稍候...</div>",
            '</div>'
        ].join('');
        const _self = this;
        let submitting = false;
        o2.DL.open({
            title: lp.mobile.outsideTitle,
            width: '100%',
            height: requiredPhoto ? '250' : '200',
            style: 'user',
            html: html,
            buttonList: [
                {
                    text: lp.positive,
                    class: 'comment_dlg_button_ok',
                    action: function () {
                        if (submitting) {
                            return;
                        }
                        const remarkNode = this.node.getElement('.check-in-fieldwork-remark');
                        const signDescription = remarkNode ? remarkNode.value.trim() : '';
                        if (requiredRemark && !signDescription) {
                            o2.api.page.notice(lp.mobile.outsideRemarkPlaceholder, 'error');
                            return;
                        }
                        const dialog = this;
                        submitting = true;
                        _self.setFieldWorkDialogLoading(dialog, true);
                        _self.prepareFieldWorkPhotoIds(requiredPhoto).then((photoIds) => {
                            _self.checkInPost(record, null, true, signDescription, photoIds);
                            dialog.close();
                        }).catch((err) => {
                            console.error('外勤拍照上传失败', err);
                            o2.api.page.notice('外勤拍照上传失败，请重试！', 'error');
                            submitting = false;
                            _self.setFieldWorkDialogLoading(dialog, false);
                        });
                    }
                },
                {
                    type: 'cancel',
                    text: lp.cancel,
                    action: function () {
                        if (submitting) {
                            return;
                        }
                        this.close();
                    }
                }
            ]
        });
    },
    setFieldWorkDialogLoading(dialog, loading) {
        if (!dialog || !dialog.node) {
            return;
        }
        const loadingNode = dialog.node.getElement('.check-in-fieldwork-loading');
        const remarkNode = dialog.node.getElement('.check-in-fieldwork-remark');
        if (loadingNode) {
            loadingNode.setStyle('display', loading ? 'flex' : 'none');
        }
        if (remarkNode) {
            remarkNode.disabled = !!loading;
        }
    },
    async prepareFieldWorkPhotoIds(requiredPhoto) {
        if (!requiredPhoto) {
            return [];
        }
        if (!window.ww || !ww.chooseImage || !ww.getLocalImgData) {
            throw new Error('qywx image sdk not ready');
        }
        const chooseResult = await ww.chooseImage({
            count: 1,
            sizeType: ['compressed'],
            sourceType: ['camera'],
            defaultCameraMode: 'normal'
        });
        const localIds = chooseResult && chooseResult.localIds ? chooseResult.localIds : [];
        if (!localIds.length) {
            throw new Error('no image selected');
        }
        const localDataResult = await ww.getLocalImgData({
            localId: localIds[0]
        });
        const localData = localDataResult && localDataResult.localData ? localDataResult.localData : '';
        const blob = this.localImageDataToBlob(localData);
        const watermarkedBlob = await this.addFieldWorkPhotoWatermark(blob);
        const fileId = await this.uploadFieldWorkPhoto(watermarkedBlob);
        return [fileId];
    },
    localImageDataToBlob(localData) {
        let data = localData || '';
        let mimeType = 'image/jpeg';
        if (data.indexOf('data:') === 0) {
            const parts = data.split(',');
            const match = parts[0].match(/data:(.*);base64/);
            mimeType = match && match[1] ? match[1] : mimeType;
            data = parts[1] || '';
        }
        data = data.replace(/\s/g, '');
        const binary = window.atob(data);
        const bytes = new Uint8Array(binary.length);
        for (let i = 0; i < binary.length; i++) {
            bytes[i] = binary.charCodeAt(i);
        }
        return new Blob([bytes], { type: mimeType });
    },
    addFieldWorkPhotoWatermark(blob) {
        return new Promise((resolve, reject) => {
            const image = new Image();
            const objectUrl = window.URL || window.webkitURL;
            if (!objectUrl) {
                reject(new Error('object url not supported'));
                return;
            }
            const url = objectUrl.createObjectURL(blob);
            image.onload = () => {
                try {
                    const canvas = document.createElement('canvas');
                    canvas.width = image.naturalWidth || image.width;
                    canvas.height = image.naturalHeight || image.height;
                    const ctx = canvas.getContext('2d');
                    if (!canvas.width || !canvas.height || !ctx) {
                        throw new Error('canvas not ready');
                    }
                    ctx.drawImage(image, 0, 0, canvas.width, canvas.height);
                    this.drawFieldWorkPhotoWatermark(ctx, canvas.width, canvas.height);
                    objectUrl.revokeObjectURL(url);
                    if (canvas.toBlob) {
                        canvas.toBlob((watermarkedBlob) => {
                            if (watermarkedBlob) {
                                resolve(watermarkedBlob);
                            } else {
                                reject(new Error('watermark image failed'));
                            }
                        }, 'image/jpeg', 0.9);
                    } else {
                        resolve(this.localImageDataToBlob(canvas.toDataURL('image/jpeg', 0.9)));
                    }
                } catch (err) {
                    objectUrl.revokeObjectURL(url);
                    reject(err);
                }
            };
            image.onerror = () => {
                objectUrl.revokeObjectURL(url);
                reject(new Error('load image failed'));
            };
            image.src = url;
        });
    },
    drawFieldWorkPhotoWatermark(ctx, width, height) {
        const fontSize = Math.max(Math.min(width, height) * 0.06, 28);
        const lineHeight = fontSize * 1.25;
        const maxTextWidth = Math.min(width, height) * 1.15;
        ctx.font = `700 ${fontSize}px sans-serif`;
        const lines = this.getFieldWorkPhotoWatermarkLines().reduce((result, line) => {
            return result.concat(this.wrapWatermarkText(ctx, line, maxTextWidth));
        }, []);
        if (!lines.length) {
            return;
        }
        const contentWidth = lines.reduce((maxWidth, line) => Math.max(maxWidth, ctx.measureText(line).width), 0);
        const contentHeight = lineHeight * lines.length;
        ctx.save();
        ctx.translate(width / 2, height / 2);
        ctx.rotate(-Math.PI / 4);
        ctx.translate(-contentWidth / 2, -contentHeight / 2);
        ctx.textBaseline = 'top';
        ctx.textAlign = 'center';
        ctx.lineJoin = 'round';
        ctx.lineWidth = Math.max(fontSize * 0.12, 3);
        ctx.strokeStyle = 'rgba(0, 0, 0, 0.45)';
        ctx.fillStyle = 'rgba(255, 255, 255, 0.55)';
        lines.forEach((line, index) => {
            const x = contentWidth / 2;
            const y = lineHeight * index;
            ctx.strokeText(line, x, y);
            ctx.fillText(line, x, y);
        });
        ctx.restore();
    },
    getFieldWorkPhotoWatermarkLines() {
        const user = typeof layout !== 'undefined' && layout.session && layout.session.user ? layout.session.user : {};
        return [
            [user.name || '', user.employee || ''].filter((value) => !!value).join(' '),
            this.formatWatermarkTime(new Date()),
            this.bind.location.address || this.bind.location.title || ''
        ].filter((value) => !!value);
    },
    formatWatermarkTime(date) {
        return `${date.getFullYear()}-${this.pad(date.getMonth() + 1)}-${this.pad(date.getDate())} ${this.pad(date.getHours())}:${this.pad(date.getMinutes())}`;
    },
    wrapWatermarkText(ctx, text, maxWidth) {
        const lines = [];
        let line = '';
        for (let i = 0; i < text.length; i++) {
            const testLine = line + text.charAt(i);
            if (line && ctx.measureText(testLine).width > maxWidth) {
                lines.push(line);
                line = text.charAt(i);
            } else {
                line = testLine;
            }
        }
        if (line) {
            lines.push(line);
        }
        return lines;
    },
    uploadFieldWorkPhoto(blob) {
        return new Promise((resolve, reject) => {
            const formData = new FormData();
            formData.append('file', blob, `field-work-${Date.now()}.jpg`);
            o2.Actions.load('x_attendance_assemble_control').FileAction.upload(
                formData,
                {},
                (json) => {
                    const id = json && json.data ? json.data.id : '';
                    if (id) {
                        resolve(id);
                    } else {
                        reject(new Error('empty upload result'));
                    }
                },
                reject,
                false
            );
        });
    },
    async checkInPost(record, workPlaceId, fieldWork, signDescription, fieldWorkPhotoFileIdList) {
        const post = {
            recordId: record.id,
            checkInType: record.checkInType,
            workPlaceId: workPlaceId || '',
            fieldWork: !!fieldWork,
            signDescription: signDescription || '',
            fieldWorkPhotoFileIdList: fieldWorkPhotoFileIdList || [],
            sourceDevice: this.getSourceDevice(),
            longitude: `${this.bind.location.lnglat.longitude}`,
            latitude: `${this.bind.location.lnglat.latitude}`,
            recordAddress: this.bind.location.address || '',
            sourceType: 'USER_CHECK'
        };
        this.bind.checkInCycle.submitting = true;
        let checked = false;
        try {
            const result = await mobileAction('checkIn', post);
            console.debug('打卡结果', result);
            checked = true;
            o2.api.page.notice('打卡成功！', 'success');
        } catch (err) {
            console.error('打卡失败', err);
            o2.api.page.notice('打卡失败，请重试！', 'error');
        } finally {
            this.bind.checkInCycle.submitting = false;
        }
        if (checked) {
            this.getPreCheckData().catch((err) => {
                console.error('刷新预打卡数据失败', err);
            });
        }
    },
    getSourceDevice() {
        const ua = window.navigator.userAgent || '';
        let deviceType = 'Other';
        if (/android/i.test(ua)) {
            deviceType = 'Android';
        } else if (/iphone|ipad|ipod/i.test(ua)) {
            deviceType = 'IOS';
        } else if (/macintosh|mac os x/i.test(ua)) {
            deviceType = 'Mac';
        } else if (/windows/i.test(ua)) {
            deviceType = 'Windows';
        }
        return `qywx_${deviceType}`;
    },
    gotoMyRecord() {
        let myRecordUrl = `appMobile.html?app=attendancev2&page=myRecord`;
        const url = window.location.href;
        if (url.indexOf("debugger") != -1) {
            myRecordUrl += "&debugger";
        }
        window.location.href = myRecordUrl;
    }
});
