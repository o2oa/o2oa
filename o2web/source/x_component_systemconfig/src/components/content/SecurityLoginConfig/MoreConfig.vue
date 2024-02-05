<template>
  <div>
    <div class="item_title">{{lp._loginConfig.enableSafeLogout}}</div>
    <div class="item_info">{{lp._loginConfig.enableSafeLogoutInfo}}</div>
    <div class="item_info">
      <el-switch
          @change="saveConfig('person', 'enableSafeLogout', enableSafeLogout)"
          v-model="enableSafeLogout"
          :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>

    <div class="item_title">{{lp._loginConfig.loginError}}</div>
    <div class="item_info">{{lp._loginConfig.loginErrorInfo}}</div>
    <div>
      <div class="item_info" style="display: inline-flex; align-items: center;">
        <label class="item_label">{{lp._loginConfig.loginErrorCount}}</label>
        <div class="item_input_area">
          <BaseItem
              :config="failureCount"
              :allowEditor="true"
              type="number"
              @changeConfig="(value)=>{failureCount = value.toInt(); saveConfig('person', 'failureCount', value.toInt())}"></BaseItem>
        </div>
      </div>
    </div>
    <div>
      <div class="item_info" style="display: inline-flex; align-items: center;">
        <label class="item_label">{{lp._loginConfig.lockTime}}</label>
        <div class="item_input_area">
          <BaseItem
              :config="failureInterval"
              :allowEditor="true"
              type="number"
              @changeConfig="(value)=>{failureInterval = value; saveConfig('person', 'failureInterval', value)}"></BaseItem>
        </div>
      </div>
    </div>

    <BaseItem
        :title="lp._loginConfig.tokenExpired"
        :info="lp._loginConfig.tokenExpiredInfo"
        :config="tokenExpiredMinutes"
        :allowEditor="true"
        min="1"
        type="number"
        @changeConfig="(value)=>{tokenExpiredMinutes = value.toInt(); saveConfig('person', 'tokenExpiredMinutes', value.toInt())}"></BaseItem>

    <BaseItem
        :title="lp._loginConfig.appTokenExpired"
        :info="lp._loginConfig.appTokenExpiredInfo"
        :config="appTokenExpiredMinutes"
        :allowEditor="true"
        min="1"
        type="number"
        @changeConfig="(value)=>{appTokenExpiredMinutes = value.toInt(); saveConfig('person', 'appTokenExpiredMinutes', value.toInt())}"></BaseItem>

    <BaseItem
        :title="lp._loginConfig.tokenName"
        :info="lp._loginConfig.tokenNameInfo"
        :config="tokenName"
        :allowEditor="true"
        type="text"
        @changeConfig="(value)=>{tokenName = value; saveConfig('person', 'tokenName', value)}"></BaseItem>

    <div class="item_title">{{lp._loginConfig.superPermission}}</div>
    <div class="item_info">{{lp._loginConfig.superPermissionInfo}}</div>
    <div class="item_info">
      <el-switch
          @change="saveConfig('person', 'superPermission', superPermission)"
          v-model="superPermission"
          :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>

    <div class="item_title">{{lp._loginConfig.tokenCookieHttpOnly}}</div>
    <div class="item_info">{{lp._loginConfig.tokenCookieHttpOnlyInfo}}</div>
    <div class="item_info">
      <el-switch
              @change="saveConfig('person', 'tokenCookieHttpOnly', tokenCookieHttpOnly)"
              v-model="tokenCookieHttpOnly"
              :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>

    <div class="item_title">{{lp._loginConfig.tokenCookieSecure}}</div>
    <div class="item_info">{{lp._loginConfig.tokenCookieSecureInfo}}</div>
    <div class="item_info">
      <el-switch
              @change="saveConfig('person', 'tokenCookieSecure', tokenCookieSecure)"
              v-model="tokenCookieSecure"
              :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>

  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import BaseItem from '@/components/item/BaseItem.vue';
import {getConfigData, getConfig, saveConfig} from '@/util/acrions';

const failureCount = ref(5);
const failureInterval = ref(15);
const tokenExpiredMinutes = ref(4320);
const appTokenExpiredMinutes = ref(4320);
const tokenName = ref('x-token');
const enableSafeLogout = ref(false);
const superPermission = ref(true);
const tokenCookieHttpOnly = ref(false);
const tokenCookieSecure = ref(false);

const load = async () => {
  const data = await getConfigData('person');
  if (data.failureCount) failureCount.value = data.failureCount;
  if (data.failureInterval) failureInterval.value = data.failureInterval;
  if (data.tokenExpiredMinutes) tokenExpiredMinutes.value = data.tokenExpiredMinutes;
  if (data.appTokenExpiredMinutes) {
    appTokenExpiredMinutes.value = data.appTokenExpiredMinutes;
  }else if(data.tokenExpiredMinutes){
    appTokenExpiredMinutes.value = data.tokenExpiredMinutes;
  }
  if (data.tokenName) tokenName.value = data.tokenName;
  enableSafeLogout.value = !!data.enableSafeLogout;
  superPermission.value = data.superPermission!==false;
  tokenCookieHttpOnly.value = data.tokenCookieHttpOnly===true;
  tokenCookieSecure.value = data.tokenCookieSecure===true;
}

load();
</script>

<style scoped>
.item_input_area{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
}

</style>
