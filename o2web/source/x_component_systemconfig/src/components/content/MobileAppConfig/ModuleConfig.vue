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

      <div class="item_title">{{lp._appConfig.appIndexPage}}</div>
      <div class="item_info" v-html="lp._appConfig.appIndexPageInfo"></div>
      <div class="item_input_area">
        <el-checkbox v-model="appIndexPagesCheckValues.home" disabled>{{lp._appConfig.appIndexPageHome}}</el-checkbox><br/>
        <el-checkbox @change="saveAppIndexPagesCheckValues" v-model="appIndexPagesCheckValues.im">{{lp._appConfig.appIndexPageIM}}</el-checkbox><br/>
        <el-checkbox @change="saveAppIndexPagesCheckValues" v-model="appIndexPagesCheckValues.contact">{{lp._appConfig.appIndexPageContact}}</el-checkbox><br/>
        <el-checkbox @change="saveAppIndexPagesCheckValues" v-model="appIndexPagesCheckValues.app">{{lp._appConfig.appIndexPageApp}}</el-checkbox><br/>
        <el-checkbox v-model="appIndexPagesCheckValues.settings" disabled>{{lp._appConfig.appIndexPageSettings}}</el-checkbox>
      </div>

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
const appIndexPagesCheckValues = ref({
  home: true,
  im: true,
  contact: true,
  app: true,
  settings: true
});
const saveAppIndexPagesCheckValues = ()=> {
  let pages = []
  pages.push('home');
  if (appIndexPagesCheckValues.value.im) {
    pages.push('im');
  }
  if (appIndexPagesCheckValues.value.contact) {
    pages.push('contact');
  }
  if (appIndexPagesCheckValues.value.app) {
    pages.push('app');
  }
  pages.push('settings');
  appStyle.value.appIndexPages = pages;
  saveAppStyle(appStyle.value);
}

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
    if (data.appIndexPages && data.appIndexPages.length > 0) {
      appIndexPagesCheckValues.value.im = data.appIndexPages.indexOf("im") > -1;
      appIndexPagesCheckValues.value.contact = data.appIndexPages.indexOf("contact") > -1;
      appIndexPagesCheckValues.value.app = data.appIndexPages.indexOf("app") > -1;
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
.item_input_area{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
  margin-left: 80px;
}
</style>
