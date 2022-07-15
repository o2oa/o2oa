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
import {ref} from 'vue';
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
.item_input{
  width: 300px;
}
</style>
