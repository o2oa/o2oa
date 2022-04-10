<template>
  <div>
    <div class="title">{{name+"###"}}
    <button @click="test">{{name}}</button>
    <button @click="test1">bbb</button>
    </div>
    <BaseInput name="systemName" />
    <BaseInput name="systemSubTitle" />
    <BaseInput name="systemVersion" />
  </div>
</template>

<script setup>
import {ref} from 'vue';
import BaseInput from '../item/BaseInput.vue';
import {o2, lp, component} from '@o2oa/component';

let name = ref("111");
function test(e){
  e.currentTarget.textContent = "333333";
}
function test1(e){
  name.value = '3333'
}
</script>

<style scoped>
.title{
  height: 40px;
  padding: 20px 30px 0px;
  font-size: 24px;
  color: rgb(51, 51, 51);

  font-weight: bold;
  line-height: 40px;
}
</style>
