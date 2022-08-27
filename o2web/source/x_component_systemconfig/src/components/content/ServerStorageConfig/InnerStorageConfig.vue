<template>
  <div class="systemconfig_area">
    <div class="item_info" v-html="lp._storageServer.info"></div>
    <div class="item_info" v-html="lp._storageServer.info2"></div>

    <div v-if="storageType==='inner'" class="item_info" v-html="lp._storageServer.innerInnerInfo"></div>
    <div v-else class="item_info" v-html="lp._storageServer.innerExternalInfo"></div>

    <div class="item_title" v-html="lp._storageServer.innerStorageConfig"></div>
    <div class="item_info" v-for="d in storageData" :key="d.nodeAddress">
      <div class="item_database_item lightColor_bg">
        <div class="item_database_area">
          <div class="o2icon-download item_config_icon mainColor_bg"></div>
          <div class="item_server_item_slot item_bold" style="min-width: 340px">{{d.nodeAddress}}:{{d.port}}</div>
          <button class="mainColor_bg" @click="saveData(d)">{{lp._storageServer.saveStorage}}</button>
        </div>
      </div>

      <BaseBoolean :label="lp._storageServer.enable" v-model:value="d.enable"></BaseBoolean>
      <BaseInput :label="lp._storageServer.port" v-model:value="d.port"></BaseInput>
      <BaseInput :label="lp._storageServer.name" v-model:value="d.name"></BaseInput>
      <BaseInput :label="lp._storageServer.prefix" v-model:value="d.prefix"></BaseInput>
      <BaseBoolean :label="lp._storageServer.deepPath" v-model:value="d.deepPath"></BaseBoolean>
    </div>
  </div>
</template>

<script setup>
import {lp, component} from '@o2oa/component';
import {ref, computed} from 'vue';
import {getServers, loadRuntimeConfig, saveConfigData} from "@/util/acrions";
import BaseBoolean from "@/components/item/BaseBoolean";
import BaseInput from "@/components/item/BaseInput";

const servers = ref([]);
const storageData = ref([]);

const runtimeExternalStorage = ref();
const storageType = computed(()=>{
  return (runtimeExternalStorage.value && runtimeExternalStorage.value.enable) ? 'external' : 'inner'
});

const saveData = async (data) => {
  const name = 'node_' + data.nodeAddress;
  const d = {
    storage: {
      enable: data.enable,
      port: data.port,
      name: data.name,
      prefix: data.prefix,
      deepPath: data.deepPath
    }
  }
  await saveConfigData(name, d);
  component.notice(lp._storageServer.saveStorageSuccess, 'success');
}

const load = ()=>{
  loadRuntimeConfig('externalStorageSources').then((data)=>{
    runtimeExternalStorage.value = data;
  });
  getServers().then((data)=>{
    servers.value = data.nodeList;
    const o = [];
    servers.value.forEach((s)=>{
      o.push({
        nodeAddress: s.nodeAddress,
        enable: true,
        port: 20040,
        name: "251",
        prefix: "",
        deepPath: false
      });
    });
    storageData.value = o;
  });
}

load();

defineExpose({load});
</script>

<style scoped>
.item_button_area{
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid #dddddd;
  margin: 5px 10px;
  border-radius: 15px;
  width: 300px;
  align-content: center;
}
.item_button_area div{
  padding: 5px;
}
.item_database_item{
  height: 60px;
  cursor: pointer;
  border-radius: 20px;
}
.item_database_item:hover{
  background-color: #f1f1f1;
}
.item_database_area{
  display: flex;
  align-items: center;
  padding: 10px;
}
.item_config_icon{
  width: 40px;
  height: 40px;
  text-align: center;
  line-height: 40px!important;
  border-radius: 40%;
  margin-right: 10px;
}
.item_bold{
  font-weight: bold;
  color: #333333;
}
</style>
