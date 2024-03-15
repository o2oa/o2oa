<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input" ref="contentNode">
      <el-switch ref="node" :style="inputStyle"
          v-model="ev"
          :active-text="lp.yes"
          :inactive-text="lp.no"
          @change="changeValue($event)">
      </el-switch>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import {ref, watch} from 'vue';
const emit = defineEmits(['update:value', 'change']);

const node = ref();

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
const ev = ref(!!props.value);
watch(
    () => props.value,
    (v) =>  ev.value = v
);

function changeValue(e){
  emit('change', e, node);
  emit('update:value', e);
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
  height: 32px;
  line-height: 32px;
}
.item_input{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
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
