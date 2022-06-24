<template xmlns="">
  <div>
    <div class="systemconfig_item_title">{{lp._resource.serviceResource}}</div>
    <div class="systemconfig_item_info">{{lp._resource.serviceResourceInfo}}</div>

    <div style="padding: 20px 10px">
      <BaseUpload :label-style="labelStyle"
                  :label="lp._resource.upload"
                  :warn="lp._resource.serviceUploadWarn"
                  accept=".jar,.war"
                  :upload-files="deloyData.file"
                  @upload="uploadFile"
                  multiple
                  @remove="removeFile"/>

      <button class="mainColor_bg" @click="deploy($event)">{{lp._resource.serviceResource}}</button>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {component, lp} from '@o2oa/component';
import {deployWebResource} from '@/util/acrions';
import BaseUpload from '@/components/item/BaseUpload.vue';

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
    component.notice(lp._resource.noDeployFile, "error", e.target, {x: 'left', y: 'top'}, {x: 0, y: 50});
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
    component.notice(lp._resource.deploySuccess, "success");
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
