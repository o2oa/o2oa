<template>
  <div>
    <div class="item_title">{{lp._loginConfig.ldapAuthEnable}}</div>
    <div class="item_info">{{lp._loginConfig.ldapAuthEnableInfo}}</div>
    <div class="item_info">
      <el-switch
          @change="saveConfig('token', 'ldapAuth.enable', ldapAuthEnable)"
          v-model="ldapAuthEnable"
          :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>

    <div v-if="!!ldapAuthEnable">
      <BaseItem
          :title="lp._loginConfig.ldapAuthUrl"
          :info="lp._loginConfig.ldapAuthUrlInfo"
          :config="ldapAuthUrl"
          :allowEditor="true"
          type="text"
          @changeConfig="(value)=>{ldapAuthUrl = value; saveConfig('token', 'ldapAuth.ldapUrl.', value)}"></BaseItem>
      <BaseItem
          :title="lp._loginConfig.baseDn"
          :info="lp._loginConfig.baseDnInfo"
          :config="baseDn"
          :allowEditor="true"
          type="text"
          @changeConfig="(value)=>{baseDn = value; saveConfig('token', 'ldapAuth.baseDn.', value)}"></BaseItem>
      <BaseItem
          :title="lp._loginConfig.userDn"
          :info="lp._loginConfig.userDnInfo"
          :config="userDn"
          :allowEditor="true"
          type="text"
          @changeConfig="(value)=>{userDn = value; saveConfig('token', 'ldapAuth.userDn.', value)}"></BaseItem>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import BaseItem from '@/components/item/BaseItem.vue';
import {getConfigData, getConfig, saveConfig} from '@/util/acrions';

const ldapAuthEnable = ref(false);
const ldapAuthUrl = ref('');
const baseDn = ref('');
const userDn = ref('');

const load = async () => {
  const data = await getConfigData('token');
  if (data.ldapAuth){
    ldapAuthEnable.value = !!data.ldapAuth.enable;
    ldapAuthUrl.value = data.ldapAuth.ldapUrl || '';
    baseDn.value = data.ldapAuth.baseDn || '';
    userDn.value = data.ldapAuth.userDn || '';
  }
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
