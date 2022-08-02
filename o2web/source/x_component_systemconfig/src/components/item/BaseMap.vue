<template>
  <div class="item" ref="itemNode">
    <div class="item_input_area">
      <div class="item_map_area" ref="mapNode"></div>
    </div>
  </div>
</template>

<script setup>
import {o2} from '@o2oa/component';
import {ref, onMounted, onUpdated} from "vue";

const emit = defineEmits(['update:value', 'change']);

const props = defineProps({
  label: String,
  value: {
    type: Object,
    default: {}
  }
});
const mapNode = ref();

const changeValue = (e)=>{
  emit('change', e);
  emit('update:value', e);
}

let nodeArea = null;
const createEditor = ()=>{
  o2.require('o2.widget.Maplist', ()=>{
    nodeArea = new o2.widget.Maplist(mapNode.value, {
      title: props.label,
      style: 'o2',
      onChange: ()=>{
        changeValue(nodeArea.toJson());
      }
    });
    nodeArea.load(props.value);
  });
}
const destroyEditor = ()=>{
  if (nodeArea){
    nodeArea.destroy();
    nodeArea = null;
  }
}
defineExpose({createEditor, destroyEditor});

onUpdated(()=>{
  if (!nodeArea) createEditor();
});
onMounted(()=>{
  if (!nodeArea) createEditor();
})


</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 20px;
  font-size: 14px;
  color: #666666;
  clear: both;
}

.item_input_area{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
}
</style>
