<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input">
      <div class="item_person" v-for="v in value" key="v">{{o2.name.cn(v)}}</div>
    </div>
    <button class="mainColor_bg" @click="selectPerson">+</button>
  </div>
</template>

<script setup>
import {o2, component} from '@o2oa/component';
import {defineProps, defineEmits} from 'vue';

const emit = defineEmits(['update:value']);

const props = defineProps({
  label: String,
  value: Array,
  labelStyle: {
    type: Object,
    default: {}
  }
});

function loadPersion(e, v){
  return v;
}

function selectPerson(){
  o2.xDesktop.requireApp("Selector", "package", ()=>{
    var options = {
      "type": "",
      "types": ["person", "group", "role"],
      "values": props.value,
      "onComplete": (items)=>{
        props.value.splice(0, props.value.length);
        items.each(function(item){
          props.value.push(item.data.distinguishedName);
        }.bind(this));
        emit("update:value", props.value)
      }
    };
    var selector = new MWF.O2Selector(component.content, options);
  });

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
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  cursor: pointer;
  margin-left: 10px;
  padding: 0;
  margin-top: 4px;
  font-size: 16px;
}
.item_person{
  float: left;
  padding: 0 10px;
  height: 24px;
  background-color: #eeeeee;
  border-radius: 100px;
  line-height: 24px;
  margin-right: 10px;
  margin-top: 4px;
}
</style>
