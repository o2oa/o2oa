<template>
  <div class="appNode systemconfig"  v-if="o2.AC.isAdministrator()">
      <div class="menuArea">
        <Menu @changeItem="loadContent"/>
      </div>

      <div class="contentArea">
        <div class="content">
          <div v-if="!!contentType" is="vue:contentComponent"></div>
          <!--        <BaseSystemInfo v-if="contentType==='BaseSystemInfo'"></BaseSystemInfo>-->
          <!--        <BaseUIConfig v-if="contentType==='BaseUIConfig'"></BaseUIConfig>-->
          <!--        <BaseResourceDeploy v-if="contentType==='BaseResourceDeploy'"></BaseResourceDeploy>-->

          <!--        <ServerServersConfig v-if="contentType==='ServerServersConfig'"></ServerServersConfig>-->
          <!--        <ServerDatabaseConfig v-if="contentType==='ServerDatabaseConfig'"></ServerDatabaseConfig>-->
          <!--        <ServerStorageConfig v-if="contentType==='ServerStorageConfig'"></ServerStorageConfig>-->
          <!--        <ServerCloudConfig v-if="contentType==='ServerCloudConfig'"></ServerCloudConfig>-->
          <!--        <ServerProcessConfig v-if="contentType==='ServerProcessConfig'"></ServerProcessConfig>-->
          <!--        <ServerMessageConfig v-if="contentType==='ServerMessageConfig'"></ServerMessageConfig>-->
          <!--        <ServerCacheConfig v-if="contentType==='ServerCacheConfig'"></ServerCacheConfig>-->
          <!--        <ServerWorktimeConfig v-if="contentType==='ServerWorktimeConfig'"></ServerWorktimeConfig>-->

          <!--        <SecurityPasswordConfig v-if="contentType==='SecurityPasswordConfig'"></SecurityPasswordConfig>-->
          <!--        <SecurityLoginConfig v-if="contentType==='SecurityLoginConfig'"></SecurityLoginConfig>-->
          <!--        <SecuritySSOConfig v-if="contentType==='SecuritySSOConfig'"></SecuritySSOConfig>-->
          <!--        <SecurityTernaryConfig v-if="contentType==='SecurityTernaryConfig'"></SecurityTernaryConfig>-->

          <!--        <MobileAppConfig v-if="contentType==='MobileAppConfig'"></MobileAppConfig>-->
          <!--        <MobilePushConfig v-if="contentType==='MobilePushConfig'"></MobilePushConfig>-->
          <!--        <MobileIntegrationConfig v-if="contentType==='MobileIntegrationConfig'"></MobileIntegrationConfig>-->
          <!--        <MobileToolsConfig v-if="contentType==='MobileToolsConfig'"></MobileToolsConfig>-->

        </div>
      </div>
  </div>
  <div class="appNode permission_denied"  v-if="!o2.AC.isAdministrator()">
    {{lp.permissionDenied}}
  </div>

</template>

<script setup>
import Menu from './components/Menu.vue';
import { ref, defineAsyncComponent, computed } from 'vue';
import {o2, lp} from "@o2oa/component";


// import BaseSystemInfo from './components/content/BaseSystemInfo';
// import BaseUIConfig from './components/content/BaseUIConfig';
// import BaseResourceDeploy from './components/content/BaseResourceDeploy';
//
// import ServerServersConfig from './components/content/ServerServersConfig';
// import ServerDatabaseConfig from './components/content/ServerDatabaseConfig';
// import ServerStorageConfig from './components/content/ServerStorageConfig';
// import ServerCloudConfig from './components/content/ServerCloudConfig';
// import ServerProcessConfig from './components/content/ServerProcessConfig';
// import ServerMessageConfig from './components/content/ServerMessageConfig';
// import ServerCacheConfig from './components/content/ServerCacheConfig';
// import ServerWorktimeConfig from './components/content/ServerWorktimeConfig';
//
// import SecurityPasswordConfig from './components/content/SecurityPasswordConfig';
// import SecurityLoginConfig from './components/content/SecurityLoginConfig';
// import SecuritySSOConfig from './components/content/SecuritySSOConfig';
// import SecurityTernaryConfig from './components/content/SecurityTernaryConfig';
//
// import MobileAppConfig from './components/content/MobileAppConfig';
// import MobilePushConfig from './components/content/MobilePushConfig';
// import MobileIntegrationConfig from './components/content/MobileIntegrationConfig';
// import MobileToolsConfig from './components/content/MobileToolsConfig';


let contentType = ref("");
function loadContent(type){
  contentType.value = type;
}
const contentComponent = computed(() => {
  const compName = contentType.value;
  return defineAsyncComponent(() => import('./components/content/'+compName+'.vue') );
});

</script>

<style scoped>
.appNode{
  overflow: hidden;
  height: 100%;
}
.menuArea{
  max-width: 300px;
  min-width: 200px;
  width: 15%;
  height: 100%;
  background: #FFFFFF;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.25);
  float: left;

}
.contentArea {
  height: 100%;
  background: #F0F0F0;
  display: flex;
  flex-direction:column;
}
.content{
  height: 100%;
  margin: 10px;
  background: #FFFFFF;
  border-radius: 4px;
  overflow: auto;
  /*display: flex;*/
  text-align: left;
  position: relative;
}
.permission_denied{
  font-size: 18px;
  color: #999999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60%;
}
</style>
<style>
.systemconfig_title{
  height: 40px;
  padding: 20px 30px 0;
  font-size: 20px;
  color: rgb(51, 51, 51);
  font-weight: bold;
  line-height: 40px;
}
.systemconfig_item_title{
  height: 30px;
  line-height: 30px;
  padding: 20px 0 0 30px;
  font-size: 16px;
  font-weight: bold;
  color: rgb(102, 102, 102);
}
.systemconfig_item_info{
  overflow: hidden;
  padding: 5px 30px;
  font-size: 14px;
  line-height: 24px;
  color: rgb(153, 153, 153);
}
.systemconfig_tab_area{
  padding: 20px 30px;
  width: 80%;
  min-width: 1100px;
  max-width: 2000px;
  margin-right: 100px;
}
.systemconfig_area{
  display: flex;
  flex-direction: column;
  width: 100%;
}
.systemconfig .item_title{
  height: 30px;
  line-height: 30px;
  padding: 20px 0 0 30px;
  font-size: 16px;
  font-weight: bold;
  clear: both;
  color: rgb(102, 102, 102);
}
.systemconfig .item_info{
  overflow: hidden;
  padding: 5px 30px;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
}
.systemconfig .item_dlg_info{
  overflow: hidden;
  padding: 0 30px 10px 30px;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
}
.systemconfig .item_value{
  overflow: hidden;
  padding-right: 30px;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
  display: inline-flex;
  height: 30px;
}

.systemconfig button{
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}
.systemconfig button:disabled{
    opacity: 0.6;
    background-color: #cccccc!important;
    color: #333333!important;
    cursor: not-allowed;
}
.systemconfig *::-webkit-scrollbar {
  width:8px;
  height: 8px;
  border-radius: 8px;
  background-color: #dddddd;
}
.systemconfig *::-webkit-scrollbar-thumb{
  width: 8px;
  border-radius: 8px;
  background-color: #bbbbbb;
  cursor: pointer;
}
.systemconfig *::-webkit-scrollbar-thumb:hover{
  width: 8px;
  border-radius: 8px;
  background-color: #666666;
  cursor: pointer;
}
.systemconfig * {
  scrollbar-color: #bbbbbb #dddddd;
  scrollbar-width: thin;
}
.systemconfig .el-table_head {
  font-weight: 700;
  color: var(--el-text-color-regular);
  background-color: var(--el-fill-color-light)!important;
}
.systemconfig .item_descriptions_label{
  font-weight: normal!important;
}
.systemconfig .item_descriptions_content{
  font-weight: 300!important;
}
.systemconfig .item_div_button{
  margin: 0;
  height: var(--el-input-height, 40px);
  border-radius: 100px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  cursor: pointer;
}
.systemconfig .item_validate_info{
  color: red;
  height: 10px;
  padding: 5px 15px;
  display: none;
}
.systemconfig .el-form-item__error{
  margin-left: 15px;
  font-size: 14px!important;
}
.systemconfig .el-form-item--large{
  margin-bottom: 32px!important;
}
.systemconfig .el-textarea__inner{
  border-radius: 15px!important;
}
.systemconfig .item_info_content{

}

.systemconfig .el-transfer-panel__filter {
  text-align: center!important;
  padding: 10px!important;;
  margin: 0!important;;
  box-sizing: border-box!important;
  width: 100%!important;;
}
.systemconfig .el-transfer__buttons{
  display: none!important;
}
.systemconfig .el-transfer-panel{
  margin-left: 20px;
}
.systemconfig .el-transfer-panel *{
  box-sizing: border-box!important;
}
.systemconfig .item_log{
  color: #999999;
  word-break: keep-all;
  word-wrap: normal;
}
.systemconfig .item_log_completed{
  color: #00b516;
  word-break: keep-all;
  word-wrap: normal;
}
.systemconfig .item_storage_module .el-collapse-item__header {
  padding: 0 20px!important;
  border-radius: 30px!important;
  background-color: #f3f3f3!important;
}
.systemconfig .item_message_module .el-collapse-item__header {
  width: 98%!important;
  height: 32px!important;
  font-size: 12px!important;
  padding: 0 20px!important;
  border-radius: 30px!important;
  background-color: #f3f3f3!important;
  font-weight: bold;
}
.systemconfig .item_message_module .el-collapse-item__content{
  padding-bottom: 0!important;
}

</style>
