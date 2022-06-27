<template>
  <div>
    <div class="systemconfig_item_title">{{lp._uiConfig.openStatus}}</div>
    <div class="systemconfig_item_info">{{lp._uiConfig.openStatusInfo}}</div>
    <BaseRadio :group-style="{flexDirection: 'column', alignItems: 'flex-start'}"
               :options="[
                   {label: 'default', value: 'default', text: lp._uiConfig.openStatusCurrent},
                   {label: 'indexWithApp', value: 'indexWithApp', text: lp._uiConfig.openStatusApp},
                    {label: 'index', value: 'index', text: lp._uiConfig.openStatusIndex},
                   ]"
               :value="config.openStatus"
               @change="(value)=>{saveConfig('web', 'openStatus', value)}" />

    <div class="systemconfig_item_title">{{lp._uiConfig.skinConfig}}</div>
    <div class="systemconfig_item_info">{{lp._uiConfig.skinConfigInfo}}</div>
    <BaseBoolean :value="config.skinConfig" @change="(value)=>{saveConfig('web', 'skinConfig', value)}" />

    <div class="systemconfig_item_title">{{lp._uiConfig.skinDefault}}</div>
    <div class="systemconfig_item_info">{{lp._uiConfig.skinDefaultInfo}}</div>
    <div v-for="style in styles" key="style.style">
      <div :style="{backgroundColor: style.color}"></div>
      <div></div>
    </div>

  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import BaseRadio from '@/components/item/BaseRadio.vue';
import {getConfig, saveConfig} from "@/util/acrions";
import {ref} from 'vue';
import BaseBoolean from "@/components/item/BaseBoolean";

const config = ref({
  openStatus: 'default',
  skinConfig: true
});

getConfig('web').then((data)=>{
  config.value.openStatus = data.openStatus || 'default';
  config.value.skinConfig = data.skinConfig!==false;
});

o2.JSON.get("../o2_core/o2/xDesktop/$Default/styles.json", (json)=>{
  debugger;
})


</script>

<style scoped>
</style>
