<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.pushConfig}}</div>

    <div v-if="confgiData">
      <div class="systemconfig_item_info" v-html="lp._pushConfig.pushType"></div>
      <div class="systemconfig_item_info" v-html="lp._pushConfig.pushTypeInfo"></div>
      <BaseRadio v-model:value="confgiData.pushType" :options="lp._pushConfig.pushTypeData" @change="(v)=>{confgiData.pushType=v; saveData()}"></BaseRadio>


      <div v-if="confgiData.pushType==='jpush'">

        <BaseItem
            :title="lp._pushConfig.appKey"
            :info="lp._pushConfig.appKeyInfo"
            :config="confgiData.jpush.appKey"
            :allowEditor="true"
            @changeConfig="(value)=>{confgiData.jpush.appKey = value; saveData()}"
        ></BaseItem>

        <BaseItem
            :title="lp._pushConfig.masterSecret"
            :info="lp._pushConfig.masterSecretInfo"
            :config="confgiData.jpush.masterSecret"
            :allowEditor="true"
            @changeConfig="(value)=>{confgiData.jpush.masterSecret = value; saveData()}"
        ></BaseItem>

      </div>

    </div>

  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp, component} from '@o2oa/component';
import {getConfigData, saveConfigData} from "@/util/acrions";
import BaseInput from '@/components/item/BaseInput.vue';
import BaseBoolean from '@/components/item/BaseBoolean.vue';
import BaseItem from '@/components/item/BaseItem.vue';
import BaseRadio from "@/components/item/BaseRadio";


const confgiData = ref();

const saveData = async () => {
  const d = {
    enable: confgiData.value.pushType==='jpush',
    appKey: confgiData.value.jpush.appKey,
    masterSecret: confgiData.value.jpush.masterSecret
  }
  await saveConfigData('jpushConfig', d);
}

getConfigData('jpushConfig').then((data)=>{
  confgiData.value = {
    pushType: (data.enable) ? 'jpush' :  'none',
    jpush: {
      appKey: data.appKey,
      masterSecret: data.masterSecret
    }
  };
});

</script>

<style scoped>
</style>
