<template>
  <div class="systemconfig_area">

    <div class="systemconfig_title">{{lp._integrationConfig.weLink}}</div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.welinkText.enableInfo"></div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.welinkText.enableInfo2"></div>

    <div>
      <BaseBoolean :label="lp._integrationConfig.welinkText.enable" v-model:value="configData.enable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.welinkText.clientId" v-model:value="configData.clientId"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.welinkText.clientSecret" v-model:value="configData.clientSecret"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseCron  :label="lp._integrationConfig.welinkText.syncCron" v-model:value="configData.syncCron"
                 :label-style="labelStyle"></BaseCron>
      <div class="item_el_info">{{lp._integrationConfig.welinkText.syncCronInfo}}</div>

      <BaseCron  :label="lp._integrationConfig.welinkText.forceSyncCron" v-model:value="configData.forceSyncCron"
                 :label-style="labelStyle"></BaseCron>
      <div class="item_el_info">{{lp._integrationConfig.welinkText.forceSyncCronInfo}}</div>

      <BaseInput :label="lp._integrationConfig.welinkText.oapiAddress" v-model:value="configData.oapiAddress"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.welinkText.workUrl" v-model:value="configData.workUrl"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info">{{lp._integrationConfig.welinkText.workUrlInfo}}</div>

      <BaseSelect :label="lp._integrationConfig.welinkText.messageRedirectPortal" v-model:value="configData.messageRedirectPortal"
                  :label-style="labelStyle" :options="portalList"></BaseSelect>
      <div class="item_el_info">{{lp._integrationConfig.welinkText.messageRedirectPortalInfo}}</div>

      <BaseBoolean :label="lp._integrationConfig.welinkText.messageEnable" v-model:value="configData.messageEnable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>


      <div style="text-align: center; margin-bottom: 50px">
        <button class="mainColor_bg" @click="saveDingDing">{{lp._integrationConfig.welinkText.saveText}}</button>
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

const configData = ref({});
const portalList = ref([]);

const labelStyle={
  minWidth: '180px',
  textAlign: 'right',
  fontWeight: 'bold'
}

const saveDingDing = async () => {
  await saveConfigData('weLink', configData.value);
  component.notice(lp._integrationConfig.welinkText.saveSuccess, 'success');
}

const load = ()=>{
  getConfigData('weLink').then((data)=>{
    configData.value = data;
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
