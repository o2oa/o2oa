<template>
  <div class="item" :style="getItemStyle()">
    <label class="item_label" v-if="label" :style="Object.assign(labelStyle, {lineHeight: iconHeight, height: iconHeight})">{{label}}</label>
    <div class="item_input" v-if="value.path">
      <div :class="getClass(value)" :style="Object.assign(getIcon(value), iconStyle)"></div>
    </div>
    <button class="mainColor_bg" @click="changeIcon" v-if="canChange">{{uploadText}}</button>
    <button class="" @click="clearIcon" v-if="canChange">{{clearText}}</button>
    <input type="file" accept=".png,.jpg,.bmp,.gif,.jpeg,.jpe" ref="uploadNode" @change="uploadIcon" style="display: none" v-if="canChange">
  </div>
</template>

<script setup>
import {layout, o2} from '@o2oa/component';
import {defineProps, defineEmits, ref} from 'vue';

const uploadNode = ref();

const emit = defineEmits(['update:value']);

const props = defineProps({
  label: String,
  value: Object,
  canChange: { type: Boolean, default: false },
  iconWidth: { type: String, default: '50px' },
  iconHeight: { type: String, default: '50px' },
  labelStyle: { type: Object, default: {} },
  iconStyle: { type: Object, default: {} },
  uploadText: { type: String, default: 'upload' },
  clearText: { type: String, default: 'clear' }
});

if (!layout.iconJson){
  layout.iconJson = fetch('../o2_core/o2/xDesktop/$Default/icons.json').then((res)=>{
    return res.json();
  });
}
const iconJson = ref({});
layout.iconJson.then((data)=>{
  iconJson.value = data;
});

function getItemStyle(){
  return (props.label) ? {
    display: 'flex',
    alignItems: 'center'
  } : {}
}

function getClass(cmpt){
  const startWidthUrl = cmpt.path.startsWith('@url')
  const path = (startWidthUrl) ? (cmpt.iconPath || 'Url') : cmpt.path;
  return (iconJson.value[path]) ? 'componentItemIconSystem' : 'componentItemIconCustom';
}

function getIcon(cmpt){
  const isUrl = cmpt.path.startsWith('@url');
  const iconObj = iconJson.value[cmpt.path] || ((isUrl && cmpt.iconPath==='appicon.png') ? iconJson.value['Url'] : null);

  return (iconObj) ? {
    backgroundColor: iconObj.color,
    backgroundImage: `url('../o2_core/o2/xDesktop/$Default/appicons/${iconObj.icon}')`,
    height: props.iconHeight,
    width: props.iconWidth
  } : {
    backgroundImage: (isUrl) ? `url('${cmpt.iconPath}')` : `url('../x_component_${cmpt.path.replace(/\./g, '_')}/$Main/${cmpt.iconPath}')`,
    height: props.iconHeight,
    width: props.iconWidth
  };
}

function changeIcon(){
  uploadNode.value.click();
}
function uploadIcon(){
  const files = uploadNode.value.files;
  if (files.length){
    const file = files.item(0);
    const formData = new FormData();
    formData.append('file', file);
    o2.xDesktop.uploadImage(
        "component",
        "component",
        formData,
        file,
        (json)=>{
          props.value.iconPath = o2.xDesktop.getImageSrc(json.id);
          emit('update:value', props.value);
        }
    );
  }
}

function clearIcon(){
  props.value.iconPath = 'appicon.png';
  emit('update:value', props.value);
}
</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 20px;
  font-size: 14px;
  color: #666666;
  clear: both;
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
  height: 32px;
  line-height: 32px;
}
.item_input{

}
.componentItemIconSystem{
  background-size: 26px 26px;
  background-position: center;
  background-repeat: no-repeat;
  border-radius: 40%;
  margin: auto;
}
.componentItemIconCustom{
  background-size: 50px 50px;
  background-position: center;
  background-repeat: no-repeat;
  margin: auto;
}
button {
  border-radius: 100px;
  border: 0;
  height: 24px;
  line-height: 24px;
  text-align: center;
  cursor: pointer;
  margin-left: 10px;
  padding: 0 10px;
}
</style>
