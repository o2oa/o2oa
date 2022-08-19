<template xmlns="">
  <div>
    <div class="systemconfig_item_title">{{lp._serversConfig.requestLogEnable}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.requestLogInfo"></div>

      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.requestLogEnable}}</label>
        <div class="item_input_area">
          <BaseBoolean v-model:value="generalData.requestLogEnable" @change="(v)=>{saveConfig('general', 'requestLogEnable', v)}"/>
        </div>
      </div>

      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.requestLogBodyEnable}}</label>
        <div class="item_input_area">
          <BaseBoolean v-model:value="generalData.requestLogBodyEnable" @change="(v)=>{saveConfig('general', 'requestLogBodyEnable', v)}"/>
        </div>
      </div>

      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.requestLogRetainDays}}</label>
        <div class="item_input_area">
          <BaseItem
              :config="generalData.requestLogRetainDays"
              :allowEditor="true"
              type="number"
              @changeConfig="(value)=>{generalData.requestLogRetainDays=value.toInt(); saveConfig('general', 'requestLogRetainDays', value.toInt())}"
          ></BaseItem>
        </div>
      </div>

    <div class="systemconfig_item_title">{{lp._serversConfig.webSocketEnable}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.webSocketEnableInfo"></div>
    <BaseBoolean v-model:value="generalData.webSocketEnable" @change="(v)=>{saveConfig('general', 'webSocketEnable', v)}"/>

    <div class="systemconfig_item_title">{{lp._serversConfig.deployWarEnable}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.deployWarEnableInfo"></div>
    <BaseBoolean v-model:value="generalData.deployWarEnable" @change="(v)=>{saveConfig('general', 'deployWarEnable', v)}"/>

    <div class="systemconfig_item_title">{{lp._serversConfig.deployResourceEnable}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.deployResourceEnableInfo"></div>
    <BaseBoolean v-model:value="generalData.deployResourceEnable" @change="(v)=>{saveConfig('general', 'deployResourceEnable', v)}"/>


    <div class="systemconfig_item_title">{{lp._serversConfig.statEnable}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.statEnableInfo.replaceAll('{url}', getDruidUrl())"></div>
    <div class="item_info">
      <label class="item_label">{{lp._serversConfig.statEnable}}</label>
      <div class="item_input_area">
        <BaseBoolean v-model:value="generalData.statEnable" @change="(v)=>{saveConfig('general', 'statEnable', v)}"/>
      </div>
    </div>

    <div class="item_info">
      <label class="item_label">{{lp._serversConfig.statExclusions}}</label>
      <div class="item_input_area">
        <BaseItem
            :config="generalData.statExclusions"
            :allowEditor="true"
            :input-style="{width: '340px', marginRight: '0'}"
            @changeConfig="(value)=>{generalData.statExclusions=value; saveConfig('general', 'statExclusions', value)}"
        ></BaseItem>
      </div>
    </div>

    <div class="systemconfig_item_title">{{lp._serversConfig.exposeJest}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.exposeJestInfo.replaceAll('{url}', getJestUrl())"></div>
    <BaseBoolean v-model:value="generalData.exposeJest" @change="(v)=>{saveConfig('general', 'exposeJest', v)}"/>

    <BaseItem
        :title="lp._serversConfig.scriptingBlockedClasses"
        :info="lp._serversConfig.scriptingBlockedClassesInfo"
        :config="generalData.scriptingBlockedClasses"
        :allowEditor="true"
        type="textarea"
        @changeConfig="(value)=>{generalData.scriptingBlockedClasses=value; saveConfig('general', 'scriptingBlockedClasses', value.split(/\s*,\s*/g))}"
    ></BaseItem>

    <BaseItem
        :title="lp._serversConfig.refererHeadCheckRegular"
        :info="lp._serversConfig.refererHeadCheckRegularInfo"
        :config="generalData.refererHeadCheckRegular"
        :allowEditor="true"
        @changeConfig="(value)=>{generalData.refererHeadCheckRegular=value; saveConfig('general', 'refererHeadCheckRegular', value)}"
    ></BaseItem>

    <BaseItem
        :title="lp._serversConfig.accessControlAllowOrigin"
        :info="lp._serversConfig.accessControlAllowOriginInfo"
        :config="generalData.accessControlAllowOrigin"
        :allowEditor="true"
        @changeConfig="(value)=>{generalData.accessControlAllowOrigin=value; saveConfig('general', 'accessControlAllowOrigin', value)}"
    ></BaseItem>


<!--    <BaseInput v-model:value="generalData.scriptingBlockedClasses" @change="(v)=>{saveConfig('general', 'scriptingBlockedClasses', v)}" input-type="textarea"/>-->

  </div>
</template>

<script setup>
import {layout, lp} from '@o2oa/component';
import {getConfigData, saveConfig} from "@/util/acrions";
import {ref} from 'vue';
import BaseBoolean from "@/components/item/BaseBoolean";
import BaseItem from "@/components/item/BaseItem";

const generalData = ref({});

const getDruidUrl = ()=>{
  const port = layout.centerServer.port;
  const portStr = (port===80) ? '' : ':'+port;
  const host = layout.centerServer.host;
  return `${layout.config.app_protocol}//${host}${portStr}/x_program_center/druid/index.html`
}
const getJestUrl = ()=>{
  const port = layout.centerServer.port;
  const portStr = (port===80) ? '' : ':'+port;
  const host = layout.centerServer.host;
  return `${layout.config.app_protocol}//${host}${portStr}/x_program_center`
}

getConfigData('general').then((data)=>{
  generalData.value = data;

  if (!generalData.value.hasOwnProperty('exposeJest')){
    generalData.value.exposeJest = true;
  }
  if (!generalData.value.hasOwnProperty('statEnable')){
    generalData.value.statEnable = true;
  }
  if (!generalData.value.hasOwnProperty('statExclusions')){
    generalData.value.statExclusions = '*.js,*.gif,*.jpg,*.png,*.css,*.ico';
  }
});

</script>

<style scoped>
.item_label{
  width: 100px;
}
.item{
  padding: 0 30px;
}
.item_info{
  display: flex;
  align-items: center;
}
.item_input_area{
  width: 80%;
  position: relative;
}


</style>
