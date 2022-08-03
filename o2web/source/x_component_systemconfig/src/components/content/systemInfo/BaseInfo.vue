<template>
  <div>
    <BaseItem name="systemName" :config="systemName" :allowEditor="true" @changeConfig="(value)=>{systemName=value; saveConfig('collect', 'title', value)}"/>
    <BaseItem name="systemSubTitle" :config="systemSubTitle" :allowEditor="true" @changeConfig="(value)=>{systemSubTitle=value; saveConfig('collect', 'footer', value)}"/>

    <BaseItem
        :title="lp._systemInfo.language"
        :info="lp._systemInfo.languageInfo"
        :config="language"
        :allowEditor="true"
        :options="lp._systemInfo.languageValues"
        type="select"
        @changeConfig="(value)=>{language = value; saveConfig('person', 'language', value)}"></BaseItem>

<!--    <div class="item_title">{{lp._systemInfo.language}}</div>-->
<!--    <div class="item_info">{{lp._systemInfo.languageInfo}}</div>-->
<!--    <BaseSelect @change="(value)=>{saveConfig('person', 'language', value)}" v-model:value="language" :options="lp._systemInfo.languageValues"></BaseSelect>-->

    <BaseItem name="systemVersion" :config="systemVersion" :allowEditor="false"/>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import BaseItem from '@/components/item/BaseItem.vue';
import BaseSelect from '@/components/item/BaseSelect.vue';
import {getConfigData, getConfig, saveConfig} from '@/util/acrions';

const systemVersion = layout.config.version;

const systemName = ref('');
const systemSubTitle = ref('');
const language = ref('zh-CN');

const load = async () => {
  getConfigData('collect').then((data)=>{
    systemName.value = data.title;
    systemSubTitle.value = data.footer;
  });

  getConfigData('person').then((data)=>{
    language.value = data.language || 'zh-CN';
  })
}

load();


const test = ()=>{
  systemName.value='www'
}
</script>

<style scoped>
</style>
