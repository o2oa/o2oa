<template>
  <div v-if="!!title || !!name" class="item_title">{{title || lp._systemInfo[name]}}</div>
  <div v-if="!!info || !!name" class="item_info">{{info || lp._systemInfo[name+'Info']}}</div>
  <div class="item_info" v-if="!editMode">
    <div  class="item_value mainColor_color">{{configText}}</div>
    <button class="mainColor_bg" v-if="allowEditor" @click="toggleEditor">{{lp.operation.edit+(title || lp._systemInfo[name] || '')}}</button>
  </div>
  <div class="item_info" v-else>
    <el-input-number v-if="type==='number'" class="item_number_input" v-model="configValue" size="default" :min="0" v-bind="options"></el-input-number>

    <el-select v-else-if="type==='select'" v-model="configValue" size="default">
      <el-option v-for="k in Object.keys(options)" :key="k" :value="k" :label="options[k]"></el-option>
    </el-select>

    <el-time-picker v-else-if="type==='time'" v-model="configValue" v-bind="options" value-format="HH:mm:ss" format="HH:mm:ss"/>

    <el-date-picker class="item_input" style="width: 450px" v-else-if="type==='date'" v-model="configValue" v-bind="options" value-format="YYYY-MM-DD" format="YYYY-MM-DD"/>

    <el-checkbox-group  v-else-if="type==='checkbox'" v-model="configValue">
      <el-checkbox v-for="k in Object.keys(options)" :key="k" :model-value="options[k]" :label="k"></el-checkbox>
    </el-checkbox-group>

    <el-input v-else :type="type" class="item_input" v-model="configValue" size="default" v-bind="options" :style="inputStyle"/>

    <button class="mainColor_bg" @click="saveConfig">{{lp.operation.ok}}</button>
    <button class="grayColor_bg" @click="cancel">{{lp.operation.cancel}}</button>
  </div>
</template>

<script setup>
import {ref, computed} from 'vue';
import {lp} from '@o2oa/component';

const emit = defineEmits(['changeConfig', 'cancel']);

const configText = computed(()=>{
  if (props.type==='select'){
    return props.options[props.config]
  }else{
    return (props.formatText) ? props.formatText(props.config) : props.config;
  }
  // return (props.type==='select') ? props.options[props.config] : props.config
})

const props = defineProps({
  name: String,
  itemId: String,
  config: '',
  allowEditor: Boolean,
  title: String,
  info: String,
  type: { type: String, default: 'text' },
  options: {},
  formatText: Function,
  inputStyle: {type:Object, default: null}
});
const configValue = ref('');
const editMode = ref(false);

function saveConfig(){
  emit('changeConfig', configValue.value);
  toggleEditor();
}
function cancel(){
  toggleEditor();
  emit('cancel');
}

function toggleEditor(){
  configValue.value = props.config;
  editMode.value = !editMode.value;
}
</script>

<style scoped>
.item_input{
  width: 450px;
  margin-right: 30px;
}
.item_number_input{

}
</style>
