<template>
  <div class="systemconfig_area">

    <div class="systemconfig_title">{{lp._integrationConfig.mPweixin}}</div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.mpweixinText.enableInfo"></div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.mpweixinText.enableInfo2"></div>

    <div>
      <BaseBoolean :label="lp._integrationConfig.mpweixinText.enable" v-model:value="mPweixinData.enable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseBoolean :label="lp._integrationConfig.mpweixinText.enablePublish" v-model:value="mPweixinData.enablePublish"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.mpweixinText.appid" v-model:value="mPweixinData.appid"
                   :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.mpweixinText.appSecret" v-model:value="mPweixinData.appSecret"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.mpweixinText.token" v-model:value="mPweixinData.token"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.mpweixinText.encodingAesKey" v-model:value="mPweixinData.encodingAesKey"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>
 
      <BaseInput :label="lp._integrationConfig.mpweixinText.workUrl" v-model:value="mPweixinData.workUrl"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info">{{lp._integrationConfig.mpweixinText.workUrlInfo}}</div>

      <BaseSelect :label="lp._integrationConfig.mpweixinText.portalId" v-model:value="mPweixinData.portalId"
                  :label-style="labelStyle" :options="portalList"></BaseSelect>
      <div class="item_el_info">{{lp._integrationConfig.mpweixinText.portalIdInfo}}</div>
      <!--   绑定用户地址   -->
      <div class="item">
        <label class="item_label" :style="labelStyle">{{lp._integrationConfig.mpweixinText.copyUrl}}</label>
        <div class="item_input_area">
          <button class="mainColor_bg"   @click="copyBindUrl">{{lp._integrationConfig.mpweixinText.copyUrlBtn}}</button>
        </div>
      </div>
      <div class="item_el_info">{{lp._integrationConfig.mpweixinText.copyUrlInfo}}</div>

      <BaseSelect :label="lp._integrationConfig.mpweixinText.scriptId" v-model:value="mPweixinData.scriptId"
                  :label-style="labelStyle" :options="invokeList"></BaseSelect>
      <div class="item_el_info">{{lp._integrationConfig.mpweixinText.scriptIdInfo}}</div>


      <BaseBoolean :label="lp._integrationConfig.mpweixinText.messageEnable" v-model:value="mPweixinData.messageEnable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <div v-if="mPweixinData.messageEnable">
        <BaseInput :label="lp._integrationConfig.mpweixinText.tempMessageId" v-model:value="mPweixinData.tempMessageId"
                   :label-style="labelStyle"></BaseInput>
        <div class="item_el_info"></div>

        <div class="item">
          <label class="item_label" :style="labelStyle">{{lp._integrationConfig.mpweixinText.fieldList}}</label>
          <div class="item_input_area">
            <div v-for="f in fieldListEditor" class="item_wx_fieldList">
              <div class="item_wx_fieldList_text">{{lp._integrationConfig.mpweixinText.tempName}}</div>
              <el-input v-model="f.tempName" style="margin-right: 20px" @change="checkAddLine"></el-input>
              <div class="item_wx_fieldList_text">{{lp._integrationConfig.mpweixinText.name}}</div>
              <el-input v-model="f.name" @change="checkAddLine"></el-input>
            </div>
          </div>
        </div>
        <div class="item_el_info">{{lp._integrationConfig.mpweixinText.fieldListInfo}}</div>

      </div>
    </div>

    <div style="text-align: center; margin-bottom: 50px; margin-top: 20px">
      <button class="mainColor_bg" @click="saveWeixin">{{lp._integrationConfig.mpweixinText.saveMpweixin}}</button>
    </div>

  </div>

</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {getConfigData, loadPortals, saveConfigData, loadInvokes} from "@/util/acrions";
import BaseSelect from '@/components/item/BaseSelect.vue';
import BaseInput from '@/components/item/BaseInput.vue';
import BaseBoolean from '@/components/item/BaseBoolean.vue';

const mPweixinData = ref({});
const portalList = ref([]);
const invokeList = ref([]);
const fieldListEditor = ref([]);

const labelStyle={
  minWidth: '180px',
  textAlign: 'right',
  fontWeight: 'bold'
}

const copyBindUrl = () => {
  if (!mPweixinData.value.appid) {
    component.notice(lp._integrationConfig.mpweixinText.appidNotEmpty, 'error');
    return;
  }
  if (!mPweixinData.value.portalId) {
    component.notice(lp._integrationConfig.mpweixinText.portalIdNotEmpty, 'error');
    return;
  }
  if (!mPweixinData.value.workUrl) {
    component.notice(lp._integrationConfig.mpweixinText.workUrlNotEmpty, 'error');
    return;
  }
  // https://open.weixin.qq.com/connect/oauth2/authorize?appid=【wx1c0c41e607a8b6fe】&redirect_uri=【https】%3A%2F%2F【sample.o2oa.net】%2Fx_desktop%2Fmpweixinsso.html%3Ftype%3Dbind&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect
  const redirectUrl = encodeURIComponent( 'portalmobile.html?id=' + mPweixinData.value.portalId);
  const o2oaUrl = encodeURIComponent(mPweixinData.value.workUrl + 'mpweixinsso.html?type=login&redirect='+redirectUrl);
  const ssoUrl = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid='+mPweixinData.value.appid+'&redirect_uri='+o2oaUrl+'&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect';
  copyText(ssoUrl);
}

const  copyText = async (text) => {
  await navigator.clipboard.writeText(text);
  component.notice(lp._integrationConfig.mpweixinText.copyUrlSuccess, 'success');
}

const checkAddLine = ()=>{
  const fList = fieldListEditor.value.filter((d)=>{
    return (d.tempName || d.name)
  });
  fList.push({
    tempName: '',
    name: ''
  });
  fieldListEditor.value = fList;
}


const saveWeixin = async () => {
  const fList = fieldListEditor.value.filter((d)=>{
    return (d.tempName || d.name)
  });
  mPweixinData.value.fieldList= fList;

  await saveConfigData('mpweixin', mPweixinData.value);
  component.notice(lp._integrationConfig.mpweixinText.saveMpweixinSuccess, 'success');
}

const load = ()=>{
  getConfigData('mpweixin').then((data)=>{
    mPweixinData.value = data || {};
    fieldListEditor.value = mPweixinData.value.fieldList || [];
    checkAddLine();
  });

  loadPortals().then((data)=>{
    const o = {}
    data.forEach((d)=>{
      o[d.id] = d.name
    });
    portalList.value = o;
  });

  loadInvokes().then((data)=>{
    const o = {}
    data.forEach((d)=>{
      o[d.id] = d.name
    });
    invokeList.value = o;
  });
}
load();

</script>

<style scoped>
.item_el_info{
  margin-top: -5px;
  margin-bottom: 10px;
  margin-left: 195px;
  padding: 0 30px;
  overflow: hidden;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
  word-break: break-all;
}
.item{
  overflow: hidden;
  padding: 10px 30px;
  font-size: 14px;
  color: #666666;
  clear: both;
  display: flex;
  justify-content: flex-start;
}
.item_label{
  text-align: left;
  overflow: hidden;
  font-size: 14px;
  color: #333333;
  clear: both;
  display: block;
  float: left;
  width: 80px;
  height: 32px;
  line-height: 32px;
}
.item_input_area{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
  width: calc(100% - 80px);
}
.item_wx_fieldList{
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  margin-bottom: 10px;
}
.item_wx_fieldList_text{
  min-width: 62px;
}

</style>
