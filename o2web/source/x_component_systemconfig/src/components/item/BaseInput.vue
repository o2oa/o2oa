<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input">
      <el-input :style="inputStyle"
                v-model="value"
                @change="changeValue($event)"/>
    </div>
  </div>
</template>

<script setup>
import {defineProps, defineEmits} from 'vue';

const emit = defineEmits(['update:value', 'change']);

const props = defineProps({
  label: String,
  value: String,
  labelStyle: {
    type: Object,
    default: {}
  },
  inputStyle: {
    type: Object,
    default: {}
  }
});

function changeValue(e){
  emit('change', e);
  emit('update:value', e);
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
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
  margin-left: 80px;
}

button {
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
