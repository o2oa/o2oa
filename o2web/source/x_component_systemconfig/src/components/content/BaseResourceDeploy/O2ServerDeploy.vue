<template>
  <div v-loading="loading" :element-loading-text="lp._resource.uploading">
    <div class="systemconfig_item_title">{{lp._resource.o2ServerResource}}</div>
    <div class="systemconfig_item_info">{{lp._resource.o2ServerResourceInfo}}</div>
    <div class="systemconfig_item_info" style="color:red;">{{lp._resource.o2ServerResourceNote}}</div>

    <div style="padding: 20px 10px" v-if="general.deployWarEnable">
      <BaseUpload :label-style="labelStyle"
                  :label="lp._resource.upload"
                  :warn="lp._resource.o2ServerUploadWarn"
                  accept=".zip"
                  :upload-files="deloyData.file"
                  @upload="uploadFile"
                  @remove="removeFile"/>

     <BaseInput :label-style="labelStyle" :label="lp._resource.title" v-model:value="deloyData.title"/>
      <div class="editorPathInfo">{{lp._resource.titleInfo}}</div>

      <BaseInput inputType="textarea" :label-style="labelStyle" :label="lp._resource.remark" v-model:value="deloyData.remark"/>
      <div class="editorPathInfo">{{lp._resource.remarkInfo}}</div>

      <BaseInput :label-style="labelStyle" :label="lp._resource.version" v-model:value="deloyData.version"/>
      <div class="editorPathInfo">{{lp._resource.o2VersionInfo}}</div>

      <button class="mainColor_bg" @click="deploy($event)">{{lp._resource.o2ServerResource}}</button>
    </div>

    <div class="systemconfig_item_info" v-else v-html="lp._resource.notServiceResource"></div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {component, lp, layout} from '@o2oa/component';
import {deployO2Server, getConfigData} from '@/util/acrions';
import BaseUpload from '@/components/item/BaseUpload.vue';
import BaseInput from '@/components/item/BaseInput.vue';

const loading = ref(false);

const deloyData = ref({
  file: [],
  overwrite: 'true',
  path: '',
  title: '',
  version: '',
  remark: ''
});
const labelStyle = {
  fontWeight: 'bold',
  fontSize: '16px'
}
function uploadFile(file){
  deloyData.value.file.push(file);
}
function removeFile(file){
  deloyData.value.file.erase(file);
}
async function deploy(e) {
  if (!deloyData.value.file.length) {
    component.notice(lp._resource.noO2ServerFile, "error", e.target, {x: 'left', y: 'top'}, {x: 0, y: 50});
    return false;
  }
  if (!deloyData.value.title.length) {
    component.notice(lp._resource.noDeployTitle, "error", e.target, {x: 'left', y: 'top'}, {x: 0, y: 50});
    return false;
  }

  debugger;
  const o = {
    file: deloyData.value.file,
    title: deloyData.value.title,
    version: deloyData.value.version,
    remark: deloyData.value.remark
  }
  loading.value = true;
  deployO2Server(o, (json)=>{
    console.log('deployO2Server success', json)
    component.notice(lp._resource.deploySuccess, "success");
    deloyData.value.title = '';
    deloyData.value.remark = '';
    deloyData.value.version = '';
    loading.value = false;
  }, (error)=>{
    component.notice(lp._resource.deployFailure, "error");
    console.log('deployO2Server failure', error);
    loading.value = false;
  });
}

const general = ref({});
getConfigData('general').then((data)=>{
  general.value = data;
});


</script>

<style scoped>
.editorPathInfo{
  padding: 5px 20px;
  text-align: left;
  margin-left: 90px;
  color: #999999;
}
button {
  border-radius: 100px;
  border: 0;
  height: 24px;
  line-height: 24px;
  text-align: center;
  cursor: pointer;
  margin: 20px 30px 20px 110px;
  padding: 5px 60px;
  font-size: 16px;
}
</style>
