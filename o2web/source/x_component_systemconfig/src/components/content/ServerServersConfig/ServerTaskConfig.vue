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

    <div class="systemconfig_item_title">{{lp._serversConfig.personUnitOrderByAsc}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.personUnitOrderByAscInfo"></div>
    <BaseBoolean v-model:value="personUnitOrderByAsc" @change="(v)=>{saveConfig('person', 'personUnitOrderByAsc', v)}"/>

    <div class="systemconfig_item_title">{{lp._serversConfig.attachmentConfig}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.attachmentConfigInfo"></div>
    <div class="item_info">
      <label class="item_label" style="width: 130px">{{lp._serversConfig.fileSize}}</label>
      <div class="item_input_area">
        <BaseItem
            :config="generalData.attachmentConfig.fileSize"
            :allowEditor="true"
            type="number"
            @changeConfig="(value)=>{generalData.attachmentConfig.fileSize=value.toInt(); saveConfig('general', 'attachmentConfig.fileSize', value.toInt())}"
        ></BaseItem>
        <div class="item_info">{{lp._serversConfig.fileSizeInfo}}</div>
      </div>
    </div>

    <div class="item_info">
      <label class="item_label" style="width: 130px">{{lp._serversConfig.fileTypeIncludes}}</label>
      <div class="item_input_area">
        <BaseItem
            :config="generalData.attachmentConfig.fileTypeIncludes"
            :allowEditor="true"
            type="textarea"
            :input-style="{width: '310px'}"
            :options="{rows: 2, spellcheck: false, style: 'word-break: break-all;'}"
            @changeConfig="(value)=>{const v = (value && !Array.isArray(value)) ? value.split(/\s*,\s*/g): (value||[]); generalData.attachmentConfig.fileTypeIncludes=v; saveConfig('general', 'attachmentConfig.fileTypeIncludes', v)}"
        ></BaseItem>
        <div class="item_info">{{lp._serversConfig.fileTypeIncludesInfo}}</div>
      </div>
    </div>

    <div class="item_info">
      <label class="item_label" style="width: 130px">{{lp._serversConfig.fileTypeExcludes}}</label>
      <div class="item_input_area">
        <BaseItem
            :config="generalData.attachmentConfig.fileTypeExcludes"
            :allowEditor="true"
            type="textarea"
            :input-style="{width: '310px'}"
            :options="{rows: 2, spellcheck: false, style: 'word-break: break-all;'}"
            @changeConfig="(value)=>{const v = (value && !Array.isArray(value)) ? value.split(/\s*,\s*/g): (value||[]); generalData.attachmentConfig.fileTypeExcludes=v; saveConfig('general', 'attachmentConfig.fileTypeExcludes', v)}"
        ></BaseItem>
        <div class="item_info">{{lp._serversConfig.fileTypeExcludesInfo}}</div>
      </div>
    </div>



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

    <div class="systemconfig_item_title">{{lp._serversConfig.storageEncrypt}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.storageEncryptInfo"></div>
    <BaseBoolean v-model:value="generalData.storageEncrypt" @change="(v)=>{saveConfig('general', 'storageEncrypt', !v ? 0 : 1)}"/>

<!--    <BaseItem-->
<!--        :title="lp._serversConfig.scriptingBlockedClasses"-->
<!--        :info="lp._serversConfig.scriptingBlockedClassesInfo"-->
<!--        :config="generalData.scriptingBlockedClasses"-->
<!--        :allowEditor="true"-->
<!--        type="textarea"-->
<!--        @changeConfig="(value)=>{const v = (value && !Array.isArray(value)) ? value.split(/\s*,\s*/g): (value||[]); generalData.scriptingBlockedClasses=v; saveConfig('general', 'scriptingBlockedClasses', v)}"-->
<!--    ></BaseItem>-->

    <BaseItem
            :title="lp._serversConfig.httpWhiteList"
            :info="lp._serversConfig.httpWhiteListInfo"
            :config="generalData.httpWhiteList"
            :allowEditor="true"
            type="textarea"
            @changeConfig="(value)=>{const v = (value && !Array.isArray(value)) ? value.split(/\s*,\s*/g): (value||['*']); generalData.httpWhiteList=v; saveConfig('general', 'httpWhiteList', v)}"
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

    <BaseItem
        :title="lp._serversConfig.contentSecurityPolicy"
        :info="lp._serversConfig.contentSecurityPolicyInfo"
        :config="generalData.contentSecurityPolicy"
        :allowEditor="true"
        @changeConfig="(value)=>{generalData.contentSecurityPolicy=value; saveConfig('general', 'contentSecurityPolicy', value)}"
    ></BaseItem>
    <div class="item_info" v-html="lp._serversConfig.contentSecurityPolicyInfo2"></div>

<!--    <BaseInput v-model:value="generalData.scriptingBlockedClasses" @change="(v)=>{saveConfig('general', 'scriptingBlockedClasses', v)}" input-type="textarea"/>-->

  </div>
</template>

<script setup>
import {layout, lp} from '@o2oa/component';
import {getConfigData, saveConfig} from "@/util/acrions";
import {ref} from 'vue';
import BaseBoolean from "@/components/item/BaseBoolean";
import BaseItem from "@/components/item/BaseItem";

const generalData = ref({attachmentConfig:{}});
const personUnitOrderByAsc = ref(true);

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

  if (!generalData.value.hasOwnProperty('attachmentConfig')){
    generalData.value.attachmentConfig = {
      "fileSize": 0,
      "fileTypeIncludes": [],
      "fileTypeExcludes": [
        "jsp",
        "exe",
        "sh",
        "tmp"
      ]
    };
  }

  if (!generalData.value.hasOwnProperty('exposeJest')){
    generalData.value.exposeJest = true;
  }
  if (!generalData.value.hasOwnProperty('statEnable')){
    generalData.value.statEnable = true;
  }
  if (!generalData.value.hasOwnProperty('statExclusions')){
    generalData.value.statExclusions = '*.js,*.gif,*.jpg,*.png,*.css,*.ico';
  }
  if (!generalData.value.hasOwnProperty('httpWhiteList')){
    generalData.value.httpWhiteList = ['*'];
  }
});
getConfigData('person').then((data)=>{
  personUnitOrderByAsc.value = data.personUnitOrderByAsc;
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
