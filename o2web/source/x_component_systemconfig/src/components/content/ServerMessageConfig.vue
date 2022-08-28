<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.messageConfig}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._messageConfig.messageConsumers" lazy>
          <ConsumerConfig v-if="loaded" :message="messageData"></ConsumerConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._messageConfig.messageType" lazy>
          <MessageTypeConfig v-if="loaded" :message="messageData"></MessageTypeConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._messageConfig.messageFilter" lazy>
          <FilterConfig v-if="loaded" :message="messageData"></FilterConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._messageConfig.messageLoader" lazy>
          <LoaderConfig v-if="loaded" :message="messageData"></LoaderConfig>
        </el-tab-pane>


      </el-tabs>
    </div>



  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import ConsumerConfig from '@/components/content/ServerMessageConfig/ConsumerConfig.vue';
import MessageTypeConfig from "@/components/content/ServerMessageConfig/MessageTypeConfig";
import FilterConfig from "@/components/content/ServerMessageConfig/FilterConfig";
import LoaderConfig from "@/components/content/ServerMessageConfig/LoaderConfig";

import {loadRuntimeConfig, getConfigData} from "@/util/acrions";
import {ref} from 'vue';
import consumersJson from './ServerMessageConfig/consumers.json';
import messageTypeJson from './ServerMessageConfig/messageType.json';


const messageData = ref();
const loaded = ref(false);
const load = ()=>{
  getConfigData('messages').then((data)=>{
    debugger;
    if (data){
      if (!data.consumers || !Object.keys(data.consumers).length){
        data.consumers = consumersJson;
      }
      messageData.value = data;
    }else{
      // messageData.value = messageTypeJson;
      // messageData.value.consumers = consumersJson;
      // messageData.value.loaders = {};
      // messageData.value.filters = {};
      // messageData.value.clean = {
      //   "enable": true,
      //   "cron": "30 30 6 * * ?",
      //   "keep": 7.0
      // };
    }
    loaded.value = true;
  });
}
load();

</script>

<style scoped>
</style>
