<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input" ref="contentNode">
      <el-switch :style="inputStyle"
          v-model="value"
          active-text="是"
          inactive-text="否"
          @change="$emit('update:value', $event)">
      </el-switch>
    </div>
  </div>
</template>

<script setup>
import {defineProps, defineEmits} from 'vue';

const emit = defineEmits(['update:value']);

const props = defineProps({
  label: String,
  value: Boolean,
  labelStyle: {
    type: Object,
    default: {}
  },
  inputStyle: {
    type: Object,
    default: {}
  }
});

// const editMode = ref(false);
//
// function saveConfig(vm){
//   emit('changeConfig', vm.config);
//   toggleEditor();
// }
//
// function toggleEditor(){
//   editMode.value = !editMode.value;
// }

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
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
  margin-left: 80px;
}

button {
  border-radius: 100px;
  border: 0;
  padding: 0;
  cursor: pointer;
  margin-left: 10px;
  margin-top: 4px;
}
</style>
