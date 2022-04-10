<template>
  <div class="input_title">{{lp._systemInfo[name]}}</div>
  <div class="input_info">{{lp._systemInfo[name+'Info']}}</div>
</template>

<script setup>
import {defineProps} from 'vue';
import {o2, lp, component} from '@o2oa/component';

const props = defineProps({
  name: String,
  itemId: String
});


</script>

<style scoped>

.input_title{
  height: 30px;
  line-height: 30px;
  padding: 20px 0px 0px 30px;
  font-size: 18px;
  font-weight: bold;
  clear: both;
  color: rgb(102, 102, 102);
}
.input_info{
  overflow: hidden;
  padding: 5px 30px;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
}
</style>
