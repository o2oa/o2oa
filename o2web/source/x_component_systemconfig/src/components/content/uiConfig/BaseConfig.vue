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
    <div class="item_skin_area">
      <div class="item_skin" v-for="skin in skins" key="skin.style">
        <div class="item_skin_color" :style="{backgroundColor: skin.color}" @click="selectSkin(skin)">
          <div v-if="config.defaultSkin===skin.style" class="item_skin_current">√</div>
        </div>
        <div>{{skin.title}}</div>
      </div>
    </div>

    <div class="systemconfig_item_title">{{lp._uiConfig.scaleConfig}}</div>
    <div class="systemconfig_item_info">{{lp._uiConfig.scaleConfigInfo}}</div>
    <BaseBoolean :value="config.scaleConfig" @change="(value)=>{saveConfig('web', 'scaleConfig', value)}" />


  </div>
</template>

<script setup>
import {o2, lp} from '@o2oa/component';
import BaseRadio from '@/components/item/BaseRadio.vue';
import {getConfigData, saveConfig} from "@/util/acrions";
import {ref} from 'vue';
import BaseBoolean from "@/components/item/BaseBoolean";

const config = ref({
  openStatus: 'default',
  skinConfig: true,
  defaultSkin: 'blue',
  scaleConfig: true
});
const skins = ref([]);

getConfigData('web').then((data)=>{
  config.value.openStatus = data.openStatus || 'default';
  config.value.skinConfig = data.skinConfig!==false;
  config.value.defaultSkin = data.defaultSkin || 'blue';
  config.value.scaleConfig = data.scaleConfig!==false;
});

o2.JSON.get("../o2_core/o2/xDesktop/$Default/styles.json", (json)=>{
  skins.value = json;
})

function selectSkin(skin){
  const path = o2.session.path+'/xDesktop/$Default/';
  o2.removeCss(path+config.value.defaultSkin+ '/style-skin.css');
  config.value.defaultSkin = skin.style;
  layout.desktop.node.loadCss(path+config.value.defaultSkin+ '/style-skin.css');
  saveConfig('web', 'defaultSkin', skin.style);
}

</script>

<style scoped>
.item_skin_area{
  display: flex;
  padding: 10px 30px;
  text-align: center;
}
.item_skin{
  margin-right: 10px;
  overflow: hidden;
  cursor: pointer;
}
.item_skin_current{

}
.item_skin_color{
  width: 40px;
  height: 40px;
  border-radius: 40%;
  line-height: 40px;
  color: #ffffff;
  font-weight: bold;
  font-size: 18px;
}

</style>
