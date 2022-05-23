<template>
  <div>
    <div class="title">{{lp.systemInfo}}</div>
    <BaseItem name="systemName" :config="systemName" :allowEditor="true" @changeConfig="(value)=>{saveConfig('collect', 'systemName', value)}"/>
    <BaseItem name="systemSubTitle" :config="systemSubTitle" :allowEditor="true"/>
    <BaseItem name="systemVersion" :config="systemVersion" :allowEditor="false"/>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import BaseItem from '../item/BaseItem.vue';
import {getConfig, saveConfig} from '@/util/acrions';

const systemVersion = layout.config.version;

const systemName = ref('');
const systemSubTitle = ref('');

getConfig('collect').then((data)=>{
  systemName.value = data.title;
  systemSubTitle.value = data.footer;
});


// function saven(path, value){
//   saveConfig
// }

</script>

<style scoped>
.title{
  height: 40px;
  padding: 20px 30px 0;
  font-size: 24px;
  color: rgb(51, 51, 51);

  font-weight: bold;
  line-height: 40px;
}
</style>
