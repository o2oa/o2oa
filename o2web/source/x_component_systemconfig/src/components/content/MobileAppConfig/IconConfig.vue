<template>
  <div class="systemconfig_area">

    <div v-if="appStyle">

      <div v-for="(img, index) in appStyle.images" style="margin-bottom: 30px">
        <IconEditor :value="img" @change="load"></IconEditor>
      </div>


<!--      <div class="item_title">{{lp._appConfig.simpleMode}}</div>-->
<!--      <div class="item_info" v-html="lp._appConfig.simpleModeInfo"></div>-->

<!--      <BaseItem-->
<!--          :title="lp._appConfig.mobileIndex"-->
<!--          :info="lp._appConfig.mobileIndexInfo"-->
<!--          :config="mobileIndex"-->
<!--          :allowEditor="true"-->
<!--          type="select"-->
<!--          :options="portalList"-->
<!--          @changeConfig="saveMobileIndex"-->
<!--      ></BaseItem>-->

<!--      <div class="item_title">{{lp._appConfig.simpleMode}}</div>-->
<!--      <div class="item_info" v-html="lp._appConfig.simpleModeInfo"></div>-->
<!--      <BaseBoolean v-model:value="appStyle.simpleMode" @change="(value)=>{appStyle.simpleMode = value; saveAppStyle(appStyle)}"></BaseBoolean>-->

<!--      <div class="item_title">{{lp._appConfig.systemMessageSwitch}}</div>-->
<!--      <div class="item_info" v-html="lp._appConfig.systemMessageSwitchInfo"></div>-->
<!--      <BaseBoolean v-model:value="appStyle.systemMessageSwitch" @change="(value)=>{appStyle.systemMessageSwitch = value; saveAppStyle(appStyle)}"></BaseBoolean>-->

<!--      <BaseItem-->
<!--          :title="lp._appConfig.contactPermissionView"-->
<!--          :info="lp._appConfig.contactPermissionViewInfo"-->
<!--          :config="appStyle.contactPermissionView"-->
<!--          :allowEditor="true"-->
<!--          @changeConfig="(value)=>{appStyle.contactPermissionView = value; saveAppStyle(appStyle)}"-->
<!--      ></BaseItem>-->

<!--      <div class="item_title">{{lp._appConfig.nativeAppList}}</div>-->
<!--      <div class="item_info" v-html="lp._appConfig.nativeAppListInfo"></div>-->
<!--      <div v-for="m in appStyle.nativeAppList">-->
<!--        <BaseBoolean :label="m.name" v-model:value="m.enable" @change="(value)=>{m.enable = value; saveAppStyle(appStyle)}" :label-style="{fontWeight: 'bold'}"></BaseBoolean>-->
<!--      </div>-->

    </div>
  </div>

</template>

<script setup>
import {ref} from 'vue';
import {getAppStyle, loadPortals, saveAppStyle} from "@/util/acrions";
import IconEditor from "@/components/content/MobileAppConfig/IconEditor";

const appStyle = ref();

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
  console.log(layout.path);
  getAppStyle().then((data)=>{
    appStyle.value = {
      images: data.images
    }
  });
}
load();

</script>

<style scoped>
</style>
