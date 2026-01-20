<template>
  <div>
    <BaseItem name="systemName" :config="systemName" :allowEditor="true" @changeConfig="(value)=>{systemName=value; saveConfig('collect', 'title', value)}"/>
    <BaseItem name="systemSubTitle" :config="systemSubTitle" :allowEditor="true" @changeConfig="(value)=>{systemSubTitle=value; saveConfig('collect', 'footer', value)}"/>

<!--    <BaseItem-->
<!--        :title="lp._systemInfo.language"-->
<!--        :info="lp._systemInfo.languageInfo"-->
<!--        :config="language"-->
<!--        :allowEditor="true"-->
<!--        :options="lp._systemInfo.languageValues"-->
<!--        type="select"-->
<!--        @changeConfig="(value)=>{language = value; saveConfig('person', 'language', value)}"></BaseItem>-->

    <BaseItem name="supportedLanguages" :config="supportedLanguages" :allowEditor="false"/>
    <div class="item_info">{{lp._systemInfo.supportedLanguagesInfo2}} <span @click="openAppstore" class="mainColor_color" style="cursor: pointer; text-decoration: underline;">{{lp._systemInfo.supportedLanguagesSetup}}</span></div>

    <BaseItem name="systemVersion" :config="systemVersion" :allowEditor="false"/>

    <BaseItem name="license" :config="license" :allowEditor="false"/>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {o2, lp, layout} from '@o2oa/component';
import BaseItem from '@/components/item/BaseItem.vue';
import BaseSelect from '@/components/item/BaseSelect.vue';
import {getConfigData, getConfig, saveConfig, getLicense} from '@/util/acrions';

const systemVersion = layout.config.version;
const supportedLanguages = Object.values(layout.config.supportedLanguages).join(", ");

const systemName = ref('');
const systemSubTitle = ref('');
const language = ref('zh-CN');
const license = ref('');

const load = async () => {
  getConfigData('collect').then((data)=>{
    systemName.value = data.title;
    systemSubTitle.value = data.footer;
  });

  getConfigData('person').then((data)=>{
    language.value = data.language || 'zh-CN';
  });
  getLicense().then((data)=>{
    if (data.name){
      license.value = `${lp._systemInfo.licenseTo}${data.name}（${data.expireTime}）`;
    }else{
      license.value = data.version;
    }
  });
}

load();

const openAppstore = function(){
  o2.api.page.openApplication('appstore');
}
</script>

<style scoped>
</style>
