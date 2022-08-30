<template>
  <div class="item" :style="getItemStyle()">
    <label class="item_label" v-if="label" :style="Object.assign(labelStyle, {lineHeight: iconHeight, height: iconHeight})">{{label}}</label>

    <BaseIconComponent v-if="itemType==='component'" :value="ev" :icon-style="iconStyle" :icon-height="iconHeight" :icon-width="iconWidth"></BaseIconComponent>
    <BaseIconGroup v-if="itemType==='group'" :value="ev" :icon-style="iconStyle" :icon-height="iconHeight" :icon-width="iconWidth"></BaseIconGroup>
    <BaseIconPortal v-if="itemType==='portal'" :value="ev" :icon-style="iconStyle" :icon-height="iconHeight" :icon-width="iconWidth"></BaseIconPortal>
    <BaseIconProcess v-if="itemType==='process'" :value="ev" :icon-style="iconStyle" :icon-height="iconHeight" :icon-width="iconWidth"></BaseIconProcess>
    <BaseIconInfor v-if="itemType==='infor'" :value="ev" :icon-style="iconStyle" :icon-height="iconHeight" :icon-width="iconWidth"></BaseIconInfor>
    <BaseIconQuery v-if="itemType==='query'" :value="ev" :icon-style="iconStyle" :icon-height="iconHeight" :icon-width="iconWidth"></BaseIconQuery>

    <button class="mainColor_bg" @click="changeIcon" v-if="canChange">{{uploadText}}</button>
    <button class="" @click="clearIcon" v-if="canChange">{{clearText}}</button>
    <input type="file" accept=".png,.jpg,.bmp,.gif,.jpeg,.jpe" ref="uploadNode" @change="uploadIcon" style="display: none" v-if="canChange">
  </div>
</template>

<script setup>
import {o2} from '@o2oa/component';
import {ref, watch, computed} from 'vue';
import BaseIconComponent from './BaseIconComponent.vue';
import BaseIconGroup from './BaseIconGroup.vue';
import BaseIconPortal from './BaseIconPortal.vue';
import BaseIconProcess from './BaseIconProcess.vue';
import BaseIconInfor from './BaseIconInfor.vue';
import BaseIconQuery from './BaseIconQuery.vue';


const uploadNode = ref();

const emit = defineEmits(['update:value']);

const props = defineProps({
  label: String,
  value: Object,
  // itemType: { type: String, default: 'component' },
  canChange: { type: Boolean, default: false },
  iconWidth: { type: String, default: '50px' },
  iconHeight: { type: String, default: '50px' },
  itemStyle: { type: Object, default: {} },
  labelStyle: { type: Object, default: {} },
  iconStyle: { type: Object, default: {} },
  uploadText: { type: String, default: 'upload' },
  clearText: { type: String, default: 'clear' }
});

const ev = ref(props.value);
watch(()=>props.value, (v) =>  ev.value = v);

const itemTypeObj = {
  group: ()=>ev.value.type==='group',
  portal: ()=>ev.value.hasOwnProperty('portalCategory'),
  component: ()=>ev.value.type==='system' || ev.value.type==='custom',
  infor: ()=>ev.value.hasOwnProperty('documentType'),
  query: ()=>ev.value.hasOwnProperty('queryCategory'),
  process: ()=>ev.value.hasOwnProperty('applicationCategory')
}

function getItemNameType(){
  for (const f of Object.keys(itemTypeObj)){
    if (itemTypeObj[f]()) return f;
  }
}

const itemType = computed(getItemNameType);


function getItemStyle(){
  const style = (props.label) ? {
    display: 'flex',
    alignItems: 'center'
  } : {};

  return Object.assign(style, props.itemStyle);
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
          const v = props.value;
          v.iconPath = o2.xDesktop.getImageSrc(json.id);
          emit('update:value', v);
        }
    );
  }
}

function clearIcon(){
  const v = props.value;
  v.iconPath = 'appicon.png';
  emit('update:value', v);
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
