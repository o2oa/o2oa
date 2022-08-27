<template>
  <div class="systemconfig_area">

<!--    <div class="systemconfig_title">{{lp._appTools.onlineBuild}}</div>-->
    <div class="systemconfig_item_info" v-html="lp._appTools.onlineBuildInfo"></div>
    <div class="systemconfig_item_info" v-html="lp._appTools.onlineBuildInfo1"></div>



  </div>

</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {getConfigData, loadPortals, saveConfigData} from "@/util/acrions";
import BaseSelect from '@/components/item/BaseSelect.vue';
import BaseInput from '@/components/item/BaseInput.vue';
import BaseCron from '@/components/item/BaseCron.vue';
import BaseBoolean from '@/components/item/BaseBoolean.vue';

const configData = ref({});
const portalList = ref([]);

const labelStyle={
  minWidth: '180px',
  textAlign: 'right',
  fontWeight: 'bold'
}

const saveDingDing = async () => {
  await saveConfigData('weLink', configData.value);
  component.notice(lp._integrationConfig.welinkText.saveSuccess, 'success');
}

const load = ()=>{
  getConfigData('weLink').then((data)=>{
    configData.value = data;
  });
  loadPortals().then((data)=>{
    const o = {}
    data.forEach((d)=>{
      o[d.id] = d.name
    });
    portalList.value = o;
  });
}
load();

</script>

<style scoped>

</style>
