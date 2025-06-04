<template>
  <!-- app在线打包 -->
  <div class="systemconfig_area">

    <div class="systemconfig_item_info" v-html="lp._appTools.onlineBuildInfo"></div>
    <div class="systemconfig_item_info" v-html="lp._appTools.onlineBuildInfo1"></div>
    <div class="systemconfig_item_info error_tips" v-html="errorTip" v-if="errorTip !== ''"></div>
    <div class="item_el_info"></div>
    
    <div class="systemconfig_item_info my_row" v-if="packInfo.id">
      <label class="item_label">{{lp._appTools.appPack.statusLabel}}</label>
      <div class="error_tips">{{packStatusText()}}</div>
      <button class="mainColor_bg" @click="loadAppPackInfo" v-if="isShowRefreshBtn()">{{lp._appTools.appPack.refreshStatusBtnTitle}}</button>
    </div>
    <div class="item_el_info"></div>

    <BaseInput :label="lp._appTools.appPack.formAppName" v-model:value="packInfo.appName"
                 :label-style="labelStyle"></BaseInput>
    <div class="item_el_info">{{lp._appTools.appPack.formAppNameTip}}</div>

    <!-- logo -->
    <div class="systemconfig_item_info my_row">
      <label class="item_label">{{lp._appTools.appPack.formLogo}}</label>
      <img class="logo_img" ref="imgLogoNode" :src="formLogoUrl" alt="" v-if="formLogoUrl !== ''"/>
      <button class="mainColor_bg" @click="changeImage" >{{lp._appTools.appPack.formUploadLogoBtnTitle}}</button>
      <input type="file" ref="uploadLogoNode" @change="uploadImage" style="display: none"/>
    </div>
    <div class="item_el_info"></div>

    <BaseSelect :label="lp._appTools.appPack.formProtocol" v-model:value="packInfo.o2ServerProtocol"
                 :label-style="labelStyle" :options="protocolList"></BaseSelect>
    <div class="item_el_info">{{lp._appTools.appPack.formProtocolTip}}</div>

    <BaseInput :label="lp._appTools.appPack.formHost" v-model:value="packInfo.o2ServerHost"
                 :label-style="labelStyle"></BaseInput>
    <div class="item_el_info">{{lp._appTools.appPack.formHostTip}}</div>  
    
    <BaseInput :label="lp._appTools.appPack.formPort" v-model:value="packInfo.o2ServerPort"
                 :label-style="labelStyle"></BaseInput>
    <div class="item_el_info">{{lp._appTools.appPack.formPortTip}}</div>  
    
    <BaseInput :label="lp._appTools.appPack.formUrlMapping" v-model:value="packInfo.urlMapping"
                 :label-style="labelStyle"></BaseInput>
    <div class="item_el_info">{{lp._appTools.appPack.formUrlMappingTip}}</div> 

    <BaseInput :label="lp._appTools.appPack.formAppVersionName" v-model:value="packInfo.versionName"
                 :label-style="labelStyle"></BaseInput>
    <div class="item_el_info">{{lp._appTools.appPack.formAppVersionNameTip}}</div>

    <BaseInput :label="lp._appTools.appPack.formAppBuildNo" v-model:value="packInfo.buildNo"
                 :label-style="labelStyle"></BaseInput>
    <div class="item_el_info">{{lp._appTools.appPack.formAppBuildNoTip}}</div> 


    <BaseBoolean :label="lp._appTools.appPack.formEnableOuterPackage" v-model="isPackAppIdOuter"
                  :label-style="labelStyle"></BaseBoolean>
    <div class="item_el_info">{{lp._appTools.appPack.formEnableOuterPackageTip}}</div>

    <div class="systemconfig_item_info my_row" v-if="packInfo.id">
      <label class="item_label">{{lp._appTools.appPack.publishStatusLabel}}</label>
      <div class="error_tips">{{publishStatusText()}}</div>
      <button class="mainColor_bg" @click="publishApkTolocal" v-if="isShowPublishBtn()">{{lp._appTools.appPack.formDownloadPublishBtnTitle}}</button>
      <button class="mainColor_bg" @click="downloadApk" v-if="packInfo.appFile && packInfo.appFile.status === 1">{{lp._appTools.appPack.formDownloadApkBtnTitle}}</button>
    </div>
    <div class="item_el_info"></div>

     <div style="text-align: center; margin-bottom: 50px">
        <button class="mainColor_bg" @click="saveForm($event)">{{lp._appTools.appPack.formSubmitBtnTitle}}</button>
        <!-- <button class="mainColor_bg" @click="reInputForm" v-if="isShowReinputBtn()">{{lp._appTools.appPack.formReinputBtnTitle}}</button>
        <button class="mainColor_bg" @click="rePack" v-if="packInfo.packStatus == '2'">{{lp._appTools.appPack.formRePackBtnTitle}}</button> -->
      </div>
  </div>

</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {doAppPackAction} from "@/util/acrions";
import {downloadFile} from "@/util/common";
import BaseSelect from '@/components/item/BaseSelect.vue';
import BaseInput from '@/components/item/BaseInput.vue';
import BaseBoolean from '@/components/item/BaseBoolean.vue';


const uploadLogoNode = ref();
const packInfo = ref({});
const isPackAppIdOuter = ref(false);
const errorTip = ref("");
const formLogoUrl = ref("");
let downloadAPKUrl = "";

const protocolList = ref({
  "http":"http",
  "https":"https"
});


const labelStyle={
  minWidth: '180px',
  textAlign: 'right',
  fontWeight: 'bold'
}
 

const load = ()=>{
  doAppPackAction('connect').then((data)=>{
    if (data.status === 1001) { // 成功 获取token
        loadAppPackInfo();
    } else if (data.status === 1) { // o2云未连接 o2云未启用
        // component.notice(lp._appTools.appPack.messageO2cloudNotEnable, 'error');
        errorTip.value = lp._appTools.appPack.messageO2cloudNotEnable;
    } else if (data.status === 2) { // o2云未登录
        // component.notice(lp._appTools.appPack.messageO2cloudNotLogin, 'error');
        errorTip.value = lp._appTools.appPack.messageO2cloudNotLogin;
    } else if (data.status === 3) { // 打包服务器未认证通过
        // component.notice(lp._appTools.appPack.messageO2cloudLoginFail, 'error');
        errorTip.value = lp._appTools.appPack.messageO2cloudLoginFail;
    }
  });
}

const loadAppPackInfo = () => {
  doAppPackAction('packInfo').then((data)=>{
    packInfo.value = data;
    isPackAppIdOuter.value = data.isPackAppIdOuter === '2';
    formLogoUrl.value = o2.filterUrl(o2.Actions.getHost("x_program_center") + "/x_program_center/jaxrs/apppack/pack/info/logo");
    if (data.appFile && data.appFile.id) {
      var url = o2.Actions.getHost("x_program_center") 
        + "/x_program_center/jaxrs/apppack/pack/info/file/download/" + data.appFile.id;
      downloadAPKUrl = o2.filterUrl(url);
    }
  });
}

const changeImage = (e)=>{
  uploadLogoNode.value.click();
}
const uploadImage = (e) => {
  if (uploadLogoNode.value.files && uploadLogoNode.value.files.length) {
    const file = uploadLogoNode.value.files[0];
    var URL = window.URL || window.webkitURL;
    // 通过 file 生成目标 url
    var imgURL = URL.createObjectURL(file);
    formLogoUrl.value = imgURL;
  }
}

const packStatusText = () => {
  if (packInfo.value.packStatus === "0") {
    return lp._appTools.appPack.statusOrderInline;
  } else if (packInfo.value.packStatus === "1") {
    return lp._appTools.appPack.statusPacking;
  } else if (packInfo.value.packStatus === "2") {
    return lp._appTools.appPack.statusPackEnd;
  } 
  return lp._appTools.appPack.statusPackError;
}
const publishStatusText = () => {
  if (packInfo.value.appFile) {
    if (packInfo.value.appFile.status === 0) {
      return lp._appTools.appPack.publishStatusDoing;
    } else if (packInfo.value.appFile.status === 1) {
      return lp._appTools.appPack.publishStatusCompleted;
    } else if (packInfo.value.appFile.status === 2) {
      return lp._appTools.appPack.publishStatusFail;
    }
  }
  return lp._appTools.appPack.publishStatusNone;
}

const isShowRefreshBtn = () => {
  return (packInfo.value.packStatus === "0" || packInfo.value.packStatus === "1");
}

const isShowReinputBtn = () => {
  return packInfo.value.id && packInfo.value.packStatus !== "0" && packInfo.value.packStatus !== "1";
}

const isShowPublishBtn = () => {
  if (packInfo.value.appFile) {
    if (packInfo.value.appFile.status === 0 || packInfo.value.appFile.status === 1) {
      return false;
    }
  }
  return true;
}

const saveForm = (e) => {
  if (packInfo.value.id && (packInfo.value.packStatus === "0" || packInfo.value.packStatus === "1")) {
    component.notice(lp._appTools.appPack.messageSubmitNotAtStatus, 'error');
    return;
  }
  if (!packInfo.value.appName || packInfo.value.appName === "") {
    component.notice(lp._appTools.appPack.messageAppnameNotEmpty, "error");
    return;
  }
  if (packInfo.value.appName.length > 6) {
    component.notice(lp._appTools.appPack.messageAppnameLenMax6, "error");
    return;
  }
  if (uploadLogoNode.value.files && uploadLogoNode.value.files.length) {
    const file = uploadLogoNode.value.files[0];
    const fileExt = file.name.substring(file.name.lastIndexOf("."));
    if (fileExt.toLowerCase() !== ".png") {
        component.notice(lp._appTools.appPack.messageAppLogoNeedPng, "error");
        return;
    }
  } else {
    component.notice(lp._appTools.appPack.messageAppLogoNotEmpty, "error");
    return;
  }
  if (!packInfo.value.o2ServerProtocol || packInfo.value.o2ServerProtocol === "") {
    component.notice(lp._appTools.appPack.messagePortocolNotEmpty, "error");
    return;
  }
  if (packInfo.value.o2ServerProtocol !== "http" && packInfo.value.o2ServerProtocol !== "https") {
    component.notice(lp._appTools.appPack.messagePortocolMustBeHttpHttps, "error");
    return;
  }
  if (!packInfo.value.o2ServerHost || packInfo.value.o2ServerHost === "") {
    component.notice(lp._appTools.appPack.messageHostNotEmpty, "error");
    return;
  }
  if (packInfo.value.o2ServerHost.startsWith("http://") || packInfo.value.o2ServerHost.startsWith("https://")) {
      component.notice(lp._appTools.appPack.messageHostFormatError, "error");
    return;
  }
  if (packInfo.value.o2ServerHost.indexOf(":") > 0) {
      component.notice(lp._appTools.appPack.messageHostFormatError, "error");
    return;
  }
  if (!packInfo.value.o2ServerPort || packInfo.value.o2ServerPort === "") {
    component.notice(lp._appTools.appPack.messagePortNotEmpty, "error");
    return;
  }
  component.confirm("warn", e, lp._appTools.appPack.messageAlertTitle, {html: lp._appTools.appPack.messageAlertSubmit}, 560, 230, function(){
    var formData = new FormData();
    const file = uploadLogoNode.value.files[0];
    formData.append('file', file);
    formData.append('fileName', file.name);
    formData.append('appName', packInfo.value.appName  || '');
    formData.append('o2ServerProtocol', packInfo.value.o2ServerProtocol  || '');
    formData.append('o2ServerHost', packInfo.value.o2ServerHost  || '');
    formData.append('o2ServerPort', packInfo.value.o2ServerPort  || '');
    formData.append('o2ServerContext', '/x_program_center');
    formData.append('appVersionName', packInfo.value.versionName || '');
    formData.append('appBuildNo', packInfo.value.buildNo || '');
    formData.append('isPackAppIdOuter', isPackAppIdOuter.value ? "2" : "1");
    formData.append('deleteHuawei', "1");
    formData.append('urlMapping', packInfo.value.urlMapping || '');
    o2.Actions.load('x_program_center').AppPackAction['androidPackStart'](formData, "{}", ()=>{
      loadAppPackInfo();
    });
    this.close();
  }, function(){
     
    this.close();
  }, null, component.content);
}
const reInputForm = () => {

}
const rePack = () => {

}
const publishApkTolocal = () => {
  if (packInfo.value.id && packInfo.value.packStatus === "2") {
    var url = o2.Actions.getHost("");
    var data = {
        'apkPath': packInfo.value.apkPath,
        'packInfoId': packInfo.value.id,
        'appVersionName': packInfo.value.versionName,
        'appVersionNo': packInfo.value.buildNo,
        'isPackAppIdOuter': packInfo.value.isPackAppIdOuter,
        'webUrl': url,
    };
    doAppPackAction('publishApk', data).then((data)=>{
      loadAppPackInfo();
    });

  }
}
const downloadApk = () => {
  downloadFile(downloadAPKUrl); 
}

load();

</script>

<style scoped>
.error_tips {
  color: #fb4747;
  font-weight: bold;
}
.my_row {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.logo_img {
  width: 72px;
  height: 72px;
}
.item_label {
  min-width: 180px;
  text-align: right;
  font-weight: bold;
  color: #333333;
  margin-right: 20px
}
.item_el_info{
  margin-top: -5px;
  margin-bottom: 10px;
  margin-left: 195px;
  padding: 0 30px;
  overflow: hidden;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
}
</style>
