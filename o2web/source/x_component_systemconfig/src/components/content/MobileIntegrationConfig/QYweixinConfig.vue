<template>
  <div class="systemconfig_area">

    <div class="systemconfig_title">{{lp._integrationConfig.qiyeweixin}}</div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.qywenxinText.enableInfo"></div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.qywenxinText.enableInfo2"></div>

    <div>
      <BaseBoolean :label="lp._integrationConfig.qywenxinText.enable" v-model:value="configData.enable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.qywenxinText.corpId" v-model:value="configData.corpId"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.qywenxinText.agentId" v-model:value="configData.agentId"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.qywenxinText.corpSecret" v-model:value="configData.corpSecret"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseCron  :label="lp._integrationConfig.qywenxinText.syncCron" v-model:value="configData.syncCron"
                 :label-style="labelStyle"></BaseCron>
      <div class="item_el_info">{{lp._integrationConfig.qywenxinText.syncCronInfo}}</div>

      <BaseCron  :label="lp._integrationConfig.qywenxinText.forceSyncCron" v-model:value="configData.forceSyncCron"
                 :label-style="labelStyle"></BaseCron>
      <div class="item_el_info">{{lp._integrationConfig.qywenxinText.forceSyncCronInfo}}</div>

      <BaseInput :label="lp._integrationConfig.qywenxinText.apiAddress" v-model:value="configData.apiAddress"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>
       <BaseInput :label="lp._integrationConfig.qywenxinText.oauth2Address" v-model:value="configData.oauth2Address"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>
       <BaseInput :label="lp._integrationConfig.qywenxinText.qrConnectAddress" v-model:value="configData.qrConnectAddress"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.qywenxinText.syncSecret" v-model:value="configData.syncSecret"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.qywenxinText.token" v-model:value="configData.token"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.qywenxinText.encodingAesKey" v-model:value="configData.encodingAesKey"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>

      <BaseInput :label="lp._integrationConfig.qywenxinText.workUrl" v-model:value="configData.workUrl"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info">{{lp._integrationConfig.qywenxinText.workUrlInfo}}</div>

      <BaseSelect :label="lp._integrationConfig.qywenxinText.messageRedirectPortal" v-model:value="configData.messageRedirectPortal"
                  :label-style="labelStyle" :options="portalList"></BaseSelect>
      <div class="item_el_info">{{lp._integrationConfig.qywenxinText.messageRedirectPortalInfo}}</div>

      <BaseBoolean :label="lp._integrationConfig.qywenxinText.messageEnable" v-model:value="configData.messageEnable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseBoolean :label="lp._integrationConfig.qywenxinText.scanLoginEnable" v-model:value="configData.scanLoginEnable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <BaseBoolean :label="lp._integrationConfig.qywenxinText.attendanceSyncEnable" v-model:value="configData.attendanceSyncEnable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info"></div>

      <div v-if="configData.attendanceSyncEnable">
        <BaseInput :label="lp._integrationConfig.qywenxinText.attendanceSyncAgentId" v-model:value="configData.attendanceSyncAgentId"
                   :label-style="labelStyle"></BaseInput>
        <div class="item_el_info"></div>

        <BaseInput :label="lp._integrationConfig.qywenxinText.attendanceSyncSecret" v-model:value="configData.attendanceSyncSecret"
                   :label-style="labelStyle"></BaseInput>
        <div class="item_el_info"></div>
      </div>
      <BaseBoolean :label="lp._integrationConfig.qywenxinText.bindEnable" v-model:value="configData.bindEnable"
                   :label-style="labelStyle"></BaseBoolean>
      <div class="item_el_info">{{lp._integrationConfig.qywenxinText.bindEnableInfo}}</div>

      <div style="text-align: center; margin-bottom: 50px">
        <button class="mainColor_bg" @click="saveDingDing">{{lp._integrationConfig.qywenxinText.saveText}}</button>
      </div>

    </div>

    <div class="systemconfig_title">{{lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageTitle}}</div>
    <div class="systemconfig_item_info" v-html="lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageDesc"></div>
    <div >
      <BasePersonAndOrg :label="lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageConsumerList" v-model:value="messageConsumerList"/>
      <div class="item_el_info"></div>
      <BaseInput :label="lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageFormTitle" v-model:value="messageFormTitle"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>
      <BaseInput :label="lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageFormContent" v-model:value="messageFormContent"
                 :label-style="labelStyle"></BaseInput>
      <div class="item_el_info"></div>
      <div style="text-align: center; margin-bottom: 50px">
        <button class="mainColor_bg" @click="sendGetPrivateInfoMessage($event)">{{lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageSendBtn}}</button>
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
import BasePersonAndOrg from '@/components/item/BasePersonAndOrg.vue';

const configData = ref({});
const portalList = ref([]);
const messageConsumerList = ref([]);
const messageFormTitle = ref();
const messageFormContent = ref("");

const labelStyle={
  minWidth: '180px',
  textAlign: 'right',
  fontWeight: 'bold'
}

const saveDingDing = async () => {
  await saveConfigData('qiyeweixin', configData.value);
  component.notice(lp._integrationConfig.qywenxinText.saveSuccess, 'success');
}
const sendGetPrivateInfoMessage = (ev) => {
    const list = messageConsumerList.value;
    if (list.length === 0) {
      component.notice(lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageConsumerEmpty, "error");
      return
    }
    component.confirm(
      "warn",
      ev,
      lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageConfirmTitle,
      { html: lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageConfirmText },
      560,
      230,
      function () {
        const body = {
          consumerList: list,
          messageTitle: messageFormTitle.value,
          messageContent: messageFormContent.value
        }
        o2.Actions.load('x_program_center').QiyeweixinAction['getPrivateInfoMessage'](body, ()=>{
          component.notice(lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageSendSuccess, "success");
        });
        this.close();
      },
      function () {
        this.close();
      },
      null,
      component.content
    );
  
}

// 选择组织或人员 发送消息用
const choosePeopleOrOrg = () => {
  o2.requireApp('Selector', 'package', ()=>{
    new MWF.O2Selector(component.content, {
      count: 1,
      type: 'identity',
      title: lp._processConfig.selectMaintenanceIdentity,
      values: (processData.value.maintenanceIdentity) ? [processData.value.maintenanceIdentity] : [],
      onComplete: (items)=>{
        if (items && items.length){
          processData.value.maintenanceIdentity = items[0].data.distinguishedName
        }else{
          processData.value.maintenanceIdentity = '';
        }
        saveConfig('processPlatform', 'maintenanceIdentity', processData.value.maintenanceIdentity);
      }
    })
  })
}

const load = ()=>{
  messageFormTitle.value = lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageFormTitleDefault;
  messageFormContent.value = lp._integrationConfig.qywenxinText.getUserPrivateInfoMessageFormContentDefault;
  getConfigData('qiyeweixin').then((data)=>{
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
