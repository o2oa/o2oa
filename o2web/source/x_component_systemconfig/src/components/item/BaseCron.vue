<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input_area">
      <el-input ref="inputNode" class="item_input" :style="inputStyle"
                v-model="ev" :type="inputType" :show-password="showPassword"
                @change="changeValue($event)" v-bind="options"/>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, nextTick, watch} from "vue";
import {component, o2} from "@o2oa/component";

const inputNode = ref();
const emit = defineEmits(['update:value', 'change']);

const props = defineProps({
  label: String,
  value: '',
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
  }
});
const ev = ref(props.value);
watch(
    () => props.value,
    (v) =>  ev.value = v
);
function changeValue(e){
  let v = (props.inputType==='number') ? e.toFloat() : e;
  emit('update:value', v);
  emit('change', v);
}
onMounted(()=>{
  nextTick(()=>{
    const node = inputNode.value.input;
    o2.requireApp("Template", "widget.CronPicker", ()=>{
      const cronPicker = new MWF.xApplication.Template.widget.CronPicker( component.content, node, component, {}, {
        style : "design",
        position : { //node 固定的位置
          x : "right",
          y : "auto"
        },
        onSelect : ( value )=>{
          node.set("value", value );
          changeValue(value);
        },
        onQueryLoad : ()=>{
          if( !cronPicker.node  ){
            cronPicker.options.value = props.value;
          }else{
            cronPicker.setCronValue( props.value );
          }
        }
      });
    });
  });
});

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
