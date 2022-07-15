<template>
  <div class="item" ref="itemNode">
    <div class="item_input_area">
      <div class="item_script_area" ref="scriptNode"></div>
    </div>
  </div>
</template>

<script setup>
import {o2} from '@o2oa/component';
import {ref} from "vue";

const emit = defineEmits(['update:value', 'change']);

const props = defineProps({
  label: String,
  value: String,
  inputType: {
    type: String,
    default: 'text'
  }
});
const scriptNode = ref();

const changeValue = (e)=>{
  emit('change', e);
  emit('update:value', e);
}

let scriptArea = null;
const createEditor = ()=>{
  const content = scriptNode.value.getParent('.content')
  o2.require('o2.widget.ScriptArea', ()=>{
    scriptArea = new o2.widget.ScriptArea(scriptNode.value, {
      title: props.label,
      maxObj: content,
      style: 'o2',
      onChange: ()=>{
        if (scriptArea.editor){
          changeValue(scriptArea.editor.getValue());
        }
      }
    });
    scriptArea.load({code: props.value});
  });
}

defineExpose({createEditor});
// const setEditorValue = (value)=>{
//   if (scriptArea){
//     scriptArea.editor.
//   }
// }
//
// onUpdated(()=>{
//   if (scriptArea){
//
//   }
// })
// onMounted(()=>{
//   createEditor();
// });

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
.item_input_area{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
}
button {
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
