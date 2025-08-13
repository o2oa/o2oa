<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input">
      <el-select v-if="arr && arr.length" v-model="ev" @change="changeValue" size="default" popper-class="systemconfig" :style="selectStyle">
        <el-option v-for="k in arr" :key="k.label || k" :value="(k.value==null) ? k : k.value" :label="k.label || k"></el-option>
      </el-select>
      <el-select v-else v-model="ev" @change="changeValue" size="default" popper-class="systemconfig" :style="selectStyle">
        <el-option v-for="k in Object.keys(options)" :key="k" :value="k" :label="options[k]"></el-option>
      </el-select>
    </div>
  </div>
</template>

<script setup>
import {ref, watch} from "vue";

const emit = defineEmits(['update:value', 'change']);

const props = defineProps({
  label: String,
  value: String,
  labelStyle: {
    type: Object,
    default: {}
  },
  selectStyle: {
    type: Object,
    default: {}
  },
  options: {
    type: Object,
    default: {}
  },
  arr: Array,
  groupStyle: {
    type: Object,
    default: {}
  }
});

const ev = ref(props.value);
watch(
    () => props.value,
    (v) =>  ev.value = v
);

function changeValue(e){
  emit('change', e);
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
  /*height: 32px;*/
  line-height: 32px;
}
.item_input{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
  width: calc(100% - 80px);
}

button {
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
