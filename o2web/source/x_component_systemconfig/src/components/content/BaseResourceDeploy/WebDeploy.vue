<template xmlns="">
  <div>
    <div class="systemconfig_item_title">{{lp._resource.webResource}}</div>
    <div class="systemconfig_item_info">{{lp._resource.webResourceInfo}}</div>

    <div style="padding: 20px 10px" v-if="general.deployResourceEnable">
      <BaseUpload :label-style="labelStyle"
                  :label="lp._resource.upload"
                  :warn="lp._resource.webUploadWarn"
                  :upload-files="deloyData.file"
                  @upload="uploadFile"
                  multiple
                  @remove="removeFile"/>
      <BaseRadio :label-style="labelStyle"
                 :label="lp._resource.overwrite"
                 :options="[{label: 'false', value: 'false', text: lp._resource.overwriteTrue}, {label: 'true', value: 'true', text: lp._resource.overwriteFalse}]"
                 v-model:value="deloyData.overwrite"/>

      <BaseInput :label-style="labelStyle" :label="lp._resource.deployPath" v-model:value="deloyData.path"/>
      <div class="editorPathInfo">{{lp._resource.deployPathInfo}}</div>

      <BaseInput :label-style="labelStyle" :label="lp._resource.title" v-model:value="deloyData.title"/>
      <div class="editorPathInfo">{{lp._resource.titleInfo}}</div>

      <BaseInput inputType="textarea" :label-style="labelStyle" :label="lp._resource.remark" v-model:value="deloyData.remark"/>
      <div class="editorPathInfo">{{lp._resource.remarkInfo}}</div>

      <BaseInput :label-style="labelStyle" :label="lp._resource.version" v-model:value="deloyData.version"/>
      <div class="editorPathInfo">{{lp._resource.versionInfo}}</div>

      <button class="mainColor_bg" @click="deploy($event)">{{lp._resource.webResource}}</button>
    </div>
    <div class="systemconfig_item_info" v-else v-html="lp._resource.notWebResource"></div>

  </div>
</template>

<script setup>
import {ref} from 'vue';
import {component, lp, layout} from '@o2oa/component';
import {deployWebResource, getConfigData} from '@/util/acrions';
import BaseUpload from '@/components/item/BaseUpload.vue';
import BaseRadio from '@/components/item/BaseRadio.vue';
import BaseInput from '@/components/item/BaseInput.vue';

const deloyData = ref({
  file: [],
  overwrite: 'false',
  path: '',
  title: '',
  version: layout.config.version,
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
    component.notice(lp._resource.noDeployFile, "error", e.target, {x: 'left', y: 'top'}, {x: 0, y: 50});
    return false;
  }
  if (!deloyData.value.title.length) {
    component.notice(lp._resource.noDeployTitle, "error", e.target, {x: 'left', y: 'top'}, {x: 0, y: 50});
    return false;
  }
  var p = [];
  deloyData.value.file.forEach((f)=>{
    const o = {
      file: [f],
      overwrite: deloyData.value.overwrite,
      path: deloyData.value.path,
      title: deloyData.value.title,
      version: deloyData.value.version,
      remark: deloyData.value.remark
    }
    p.push(deployWebResource(o));
  });
  // const data = await deployWebResource(deloyData.value);
  Promise.all(p).then(()=>{
    component.notice(lp._resource.deploySuccess, "success");
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
