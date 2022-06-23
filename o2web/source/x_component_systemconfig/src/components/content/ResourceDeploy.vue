<template xmlns="">
  <div>
    <div class="title">{{lp.resourceDeploy}}</div>
    <div class="item_title">{{lp.resource.webResource}}</div>
    <div class="item_info">{{lp.resource.webResourceInfo}}</div>

    <div style="padding: 20px 10px">
      <BaseUpload :label-style="labelStyle"
                  :label="lp.resource.upload"
                  :warn="lp.resource.uploadWarn"
                  :upload-files="deloyData.file"
                  @upload="uploadFile"
                  multiple
                  @remove="removeFile"/>
      <BaseRadio :label-style="labelStyle"
                 :label="lp.resource.overwrite"
                 :options="[{label: 'true', value: 'true', text: lp.resource.overwriteTrue}, {label: 'false', value: 'false', text: lp.resource.overwriteFalse}]"
                 v-model:value="deloyData.overwrite"/>

      <BaseInput :label-style="labelStyle" :label="lp.resource.deployPath" v-model:value="deloyData.path"/>
      <div class="editorPathInfo">{{lp.resource.deployPathInfo}}</div>

      <button class="mainColor_bg" @click="deploy($event)">{{lp.resource.webResource}}</button>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {component, lp} from '@o2oa/component';
import {deployWebResource} from '@/util/acrions';
import BaseUpload from '../item/BaseUpload.vue';
import BaseRadio from '../item/BaseRadio.vue';
import BaseInput from '../item/BaseInput.vue';

const deloyData = ref({
  file: [],
  overwrite: 'true',
  path: ''
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
    component.notice(lp.resource.noDeployFile, "error", e.target, {x: 'left', y: 'top'}, {x: 0, y: 50});
    return false;
  }
  var p = [];
  deloyData.value.file.forEach((f)=>{
    const o = {
      file: [f],
      overwrite: deloyData.value.overwrite,
      path: deloyData.value.path
    }
    p.push(deployWebResource(o));
  });
  // const data = await deployWebResource(deloyData.value);
  Promise.all(p).then(()=>{
    component.notice(lp.resource.deploySuccess, "success");
  });
}

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
