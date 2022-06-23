<template>
  <div class="item_title">{{lp._systemInfo[name]}}</div>
  <div class="item_info">{{lp._systemInfo[name+'Info']}}</div>
  <div class="item_info" v-if="!editMode">
    <div  class="item_value mainColor_color">{{config}}</div>
    <button class="mainColor_bg" v-if="allowEditor" @click="toggleEditor">{{lp.operation.edit+lp._systemInfo[name]}}</button>
  </div>
  <div class="item_info" v-else>
    <el-input class="item_input" v-model="config"/>
    <button class="mainColor_bg" @click="saveConfig(this)">{{lp.operation.ok}}</button>
    <button class="grayColor_bg" @click="toggleEditor">{{lp.operation.cancel}}</button>
  </div>
</template>

<script setup>
import {defineProps, ref} from 'vue';
import {lp} from '@o2oa/component';

const emit = defineEmits(['changeConfig']);

const props = defineProps({
  name: String,
  itemId: String,
  config: String,
  allowEditor: Boolean
});

const editMode = ref(false);

function saveConfig(vm){
  emit('changeConfig', vm.config);
  toggleEditor();
}

function toggleEditor(){
  editMode.value = !editMode.value;
}

</script>

<style scoped>

.item_title{
  height: 30px;
  line-height: 30px;
  padding: 20px 0 0 30px;
  font-size: 18px;
  font-weight: bold;
  clear: both;
  color: rgb(102, 102, 102);
}
.item_info{
  overflow: hidden;
  padding: 5px 30px;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
}
.item_value{
  overflow: hidden;
  padding-right: 30px;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
  display: inline;
}
.item_input{
  /*border-radius: 100px;*/
  /*border: 1px solid #cccccc;*/
  /*height: 24px;*/
  /*line-height: 24px;*/
  width: 300px;
  /*padding: 0 10px;*/
  /*font-size: 14px;*/
  /*margin-right: 20px;*/
}
button {
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
