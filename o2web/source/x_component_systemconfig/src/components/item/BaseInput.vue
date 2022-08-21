<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input_area">
      <el-input class="item_input" :style="inputStyle"
                v-model="value" :type="inputType" :show-password="showPassword"
                @change="changeValue($event)" v-bind="options"/>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
const emit = defineEmits(['update:value', 'change']);

const props = defineProps({
  label: String,
  value: String,
  inputType: {
    type: String,
    default: 'text'
  },
  showPassword: {
    type: Boolean,
    default: false
  },
  labelStyle: {
    type: Object,
    default: {}
  },
  inputStyle: {
    type: Object,
    default: {}
  },
  options: {
    type: Object,
    default: null
  },
});

function changeValue(e){
  const v = (props.inputType==='number') ? e.toFloat() : e;
  emit('update:value', v);
  emit('change', v);
}

</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 30px;
  font-size: 14px;
  color: #666666;
  clear: both;
  display: flex;
  justify-content: flex-start;
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
  width: calc(100% - 80px);
  /*margin-left: 80px;*/
}
.item_input{
  /*width: 300px;*/
}
button {
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}

</style>
