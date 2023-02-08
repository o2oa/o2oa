<template>
  <div class="systemconfig_area">
    <div v-if="appStyle">
      <BaseItem
          :title="lp._appConfig.mobileIndex"
          :info="lp._appConfig.mobileIndexInfo"
          :config="mobileIndex"
          :allowEditor="true"
          type="select"
          :options="portalList"
          @changeConfig="saveMobileIndex"
      ></BaseItem>

      <div class="item_title">{{lp._appConfig.simpleMode}}</div>
      <div class="item_info" v-html="lp._appConfig.simpleModeInfo"></div>
      <BaseBoolean v-model:value="appStyle.simpleMode" @change="(value)=>{appStyle.simpleMode = value; saveAppStyle(appStyle)}"></BaseBoolean>

      <div class="item_title">{{lp._appConfig.systemMessageSwitch}}</div>
      <div class="item_info" v-html="lp._appConfig.systemMessageSwitchInfo"></div>
      <BaseBoolean v-model:value="appStyle.systemMessageSwitch" @change="(value)=>{appStyle.systemMessageSwitch = value; saveAppStyle(appStyle)}"></BaseBoolean>
      <div v-if="appStyle.systemMessageSwitch == true">
        <div class="item_info" v-html="lp._appConfig.systemMessageCanClickInfo"></div>
        <BaseBoolean v-model:value="appStyle.systemMessageCanClick" @change="(value)=>{appStyle.systemMessageCanClick = value; saveAppStyle(appStyle)}"></BaseBoolean>
      </div>

      <BaseItem
          :title="lp._appConfig.appExitAlert"
          :info="lp._appConfig.appExitAlertInfo"
          :config="appStyle.appExitAlert"
          :allowEditor="true"
          @changeConfig="(value)=>{appStyle.appExitAlert = value; saveAppStyle(appStyle)}"
      ></BaseItem>

      <BaseItem
          :title="lp._appConfig.contactPermissionView"
          :info="lp._appConfig.contactPermissionViewInfo"
          :config="appStyle.contactPermissionView"
          :allowEditor="true"
          @changeConfig="(value)=>{appStyle.contactPermissionView = value; saveAppStyle(appStyle)}"
      ></BaseItem>

      <div class="item_title">{{lp._appConfig.nativeAppList}}</div>
      <div class="item_info" v-html="lp._appConfig.nativeAppListInfo"></div>
      <div v-for="m in appStyle.nativeAppList">
        <BaseBoolean :label="m.name" v-model:value="m.enable" @change="(value)=>{m.enable = value; saveAppStyle(appStyle)}" :label-style="{fontWeight: 'bold'}"></BaseBoolean>
      </div>

    </div>
  </div>

</template>

<script setup>
import {ref, computed} from 'vue';
import {lp} from '@o2oa/component';
import {getAppStyle, loadPortals, saveAppStyle} from "@/util/acrions";
import BaseItem from '@/components/item/BaseItem.vue';
import BaseBoolean from "@/components/item/BaseBoolean";

const appStyle = ref();
const portalList = ref({});
const mobileIndex = computed(()=>{
  return (appStyle.value && appStyle.value.indexType==='portal') ? appStyle.value.indexPortal : 'default';
});


const saveMobileIndex = (v)=>{
  if (v==='default'){
    appStyle.value.indexType = 'default';
  }else{
    appStyle.value.indexType = 'portal';
    appStyle.value.indexPortal = v;
  }
  saveAppStyle(appStyle.value);
}

const load = ()=>{
  getAppStyle().then((data)=>{
    appStyle.value = {
      indexPortal: data.indexPortal,
      indexType: data.indexType,
      nativeAppList: data.nativeAppList,
      simpleMode: data.simpleMode,
      systemMessageSwitch: data.systemMessageSwitch,
      systemMessageCanClick: data.systemMessageCanClick,
      appExitAlert: data.appExitAlert,
      contactPermissionView: data.contactPermissionView
    }
  });
  loadPortals().then((data)=>{
    const o = {'default': lp.default};
    data.forEach((d)=>{
      o[d.id] = d.name;
    });
    portalList.value = o;
  });
}
load();

</script>

<style scoped>
</style>
