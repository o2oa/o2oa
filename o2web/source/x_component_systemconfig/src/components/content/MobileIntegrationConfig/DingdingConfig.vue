<template>
  <div class="systemconfig_area">

    <div class="systemconfig_title">{{lp._integrationConfig.dingding}}</div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.enableInfo"></div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.enableInfo2"></div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.enableInfo3"></div>
<!--    <BaseBoolean v-model:value="dingdingData.enable" @change="(value)=>{dingdingData.enable = value; saveConfig('dingding', 'enable', value)}"></BaseBoolean>-->

    <div>
      <BaseBoolean :label="lp._integrationConfig.enable" v-model:value="dingdingData.enable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.corpId" v-model:value="dingdingData.corpId"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.agentId" v-model:value="dingdingData.agentId"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.appKey" v-model:value="dingdingData.appKey"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.appSecret" v-model:value="dingdingData.appSecret"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseCron  :label="lp._integrationConfig.syncCron" v-model:value="dingdingData.syncCron"
                 :label-style="labelStyle"></BaseCron>
      <div class="item_el_info">{{lp._integrationConfig.syncCronInfo}}</div>

      <BaseCron  :label="lp._integrationConfig.forceSyncCron" v-model:value="dingdingData.forceSyncCron"
                 :label-style="labelStyle"></BaseCron>
      <div class="item_el_info">{{lp._integrationConfig.forceSyncCronInfo}}</div>

      <BaseInput :label="lp._integrationConfig.oapiAddress" v-model:value="dingdingData.oapiAddress"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info">{{ lp._integrationConfig.oapiAddressInfo }}</div>

      <BaseInput :label="lp._integrationConfig.token" v-model:value="dingdingData.token"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.encodingAesKey" v-model:value="dingdingData.encodingAesKey"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.workUrl" v-model:value="dingdingData.workUrl"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info">{{lp._integrationConfig.workUrlInfo}}</div>

      <BaseSelect :label="lp._integrationConfig.messageRedirectPortal" v-model:value="dingdingData.messageRedirectPortal"
                 :label-style="labelStyle" :options="portalList"></BaseSelect>
      <div class="item_el_info">{{lp._integrationConfig.messageRedirectPortalInfo}}</div>

      <BaseBoolean :label="lp._integrationConfig.messageEnable" v-model:value="dingdingData.messageEnable"
                 :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseBoolean :label="lp._integrationConfig.attendanceSyncEnable" v-model:value="dingdingData.attendanceSyncEnable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseBoolean :label="lp._integrationConfig.scanLoginEnable" v-model:value="dingdingData.scanLoginEnable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <div v-if="dingdingData.scanLoginEnable">
        <BaseInput :label="lp._integrationConfig.scanLoginAppId" v-model:value="dingdingData.scanLoginAppId"
                   :label-style="labelStyle"></BaseInput>
        <div class="item_el_info"></div>

        <BaseInput :label="lp._integrationConfig.scanLoginAppSecret" v-model:value="dingdingData.scanLoginAppSecret"
                   :label-style="labelStyle"></BaseInput>
        <div class="item_el_info"></div>
      </div>

      <div style="text-align: center; margin-bottom: 50px">
        <button class="mainColor_bg" @click="saveDingDing">{{lp._integrationConfig.saveDingding}}</button>
      </div>
    </div>


  </div>

</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {getConfigData, loadPortals, saveConfigData} from "@/util/acrions";
import BaseSelect from '@/components/item/BaseSelect.vue';
import BaseInput from '@/components/item/BaseInput.vue';
import BaseCron from '@/components/item/BaseCron.vue';
import BaseBoolean from '@/components/item/BaseBoolean.vue';

const dingdingData = ref({});
const portalList = ref([]);

const labelStyle={
  minWidth: '180px',
  textAlign: 'right',
  fontWeight: 'bold'
}

const saveDingDing = async () => {
  await saveConfigData('dingding', dingdingData.value);
  component.notice(lp._integrationConfig.saveDingdingSuccess, 'success');
}

const load = ()=>{
  getConfigData('dingding').then((data)=>{
    dingdingData.value = data;
  });
  loadPortals().then((data)=>{
    const o = {}
    data.forEach((d)=>{
      o[d.id] = d.name
    });
    portalList.value = o;
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
}
</style>
