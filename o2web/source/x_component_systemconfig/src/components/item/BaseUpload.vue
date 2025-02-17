<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input">
      <div class="item_upload" @click="upload" @dragover="dragover" @drop="drop" @dragleave="dragleave" @dragenter="dragenter">
        <i class="o2icon-upload"></i>
        <div class="item_upload_info">{{lp.uploadInfo}}</div>
      </div>
    </div>
    <div class="item_upload_warn">
      <div class="item_file" v-for="file in uploadFiles" key="file.name">
        <div>{{file.name}}</div><div class="o2icon-close" @click="removeFile(file)"></div>
      </div>
      <div class="item_warn" v-if="warn">{{warn}}</div>
    </div>

  </div>
  <input type="file" :accept="accept" ref="uploadNode" :multiple="multiple" @change="uploadChange" style="display: none">
</template>

<script setup>
import {lp} from '@o2oa/component';
import {nextTick, onUpdated, ref} from 'vue';

const uploadNode = ref();
const emit = defineEmits(['upload', 'remove']);
const props = defineProps({
  label: String,
  labelStyle: { type: Object, default: {} },
  multiple: { type: Boolean, default: false },
  warn: String,
  uploadFiles: { type: Array, default: [] },
  accept: { type: String, default: '' },
});

function upload(){
  uploadNode.value.click();
}
function clearFile(){
  if (props.uploadFiles.length){
    props.uploadFiles.forEach((file)=>{
      removeFile(file);
    });
  }
}
function uploadChange(){
  uploadFile(uploadNode.value.files);
  // 清空文件输入
  uploadNode.value.value = '';
}
function removeFile(file){
  emit('remove', file);
}
function uploadFile(files){
  debugger;
  if (files.length){
    if (!props.multiple) clearFile();
    const acceptTypes = (props.accept) ? props.accept.split(/,\s*/) : [];
    for (const file of files){
      const ext = file.name.substring(file.name.lastIndexOf('.'));
      if (!acceptTypes.length || acceptTypes.includes(ext) || acceptTypes.includes(file.type)){
        emit('upload', file);
      }
    }
  }
}
function dragover(e){
  e.preventDefault();
  e.stopPropagation();
}
function dragleave(e){
  e.target.removeClass('lightColor_bg');
  e.preventDefault();
  e.stopPropagation();
}
function dragenter(e){
  e.target.addClass('lightColor_bg');
  e.preventDefault();
  e.stopPropagation();
}
function drop(e){
  e.target.removeClass('lightColor_bg');
  uploadFile(e.dataTransfer.files);
  e.preventDefault();
  e.stopPropagation();
}

onUpdated(()=>{
  nextTick(()=>{
    if(uploadNode && uploadNode.value)uploadNode.value.value = '';
  });
})


</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 20px;
  font-size: 14px;
  color: #666666;
  clear: both;
  display: flex;
  flex-direction: row;
  align-items: center;
}
.item_file_area{
  overflow: hidden;
  padding: 0 20px;
  font-size: 14px;
  color: #666666;
  clear: both;
  display: flex;
  flex-direction: row;
  align-items: center;
}
.item_label{
  text-align: left;
  overflow: hidden;
  font-size: 14px;
  color: #333333;
  clear: both;
  display: block;
  float: left;
  width: 80px;
  /*height: 32px;*/
  /*line-height: 32px;*/
}
.item_upload{
  height: 100px;
  width: 260px;
  background: #FFFFFF;
  border-radius: 10px;
  border: 1px dashed #999999;
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  cursor: pointer;
}
.item_upload_info{
  margin-top: 5px;
  color: #bbbbbb;
  display: inline;
  padding: 5px;
}
.item_upload_warn{
  padding: 0 20px;
}
.item_file{
  height: 24px;
  line-height: 24px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
}
.item_file:hover{
  background-color: #f1f1f1;
}
.item_warn{
  width: 240px;
  padding: 10px;
  background: #FFFAF0;
  border-radius: 10px;
  border: 1px solid rgba(204, 155, 41, 0.25);
  margin: 5px 0;
}
.o2icon-close{
  height: 24px;
  line-height: 24px;
  color: #999999;
  padding: 0 3px;
}
.o2icon-close:hover{
  color: var(--el-color-primary);
}
i{
  color: #bbbbbb;
  font-size: 36px;
}
</style>
