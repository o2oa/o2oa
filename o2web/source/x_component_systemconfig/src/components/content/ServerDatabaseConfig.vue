<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.databaseServer}}</div>
    <div class="systemconfig_item_info" v-html="lp._databaseServer.info"></div>

    <div class="systemconfig_item_title">{{lp._ternaryManagement.enable}}</div>
    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.enableInfo"></div>
    <BaseBoolean :value="ternaryManagementEnable" @change="(value)=>{saveConfig('ternaryManagement', 'enable', value)}" />


<!--    @changeConfig="(value)=>{failureCount = value.toInt(); saveConfig('person', 'failureCount', value.toInt())}"-->

    <div class="systemconfig_item_title">{{lp._ternaryManagement.logRetainDays}}</div>
    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.logRetainDaysInfo"></div>
    <div>
      <div class="item_info" style="display: inline-flex; align-items: center;">
        <label class="item_label">{{lp._ternaryManagement.logRetainDays}}</label>
        <div class="item_input_area">
          <BaseItem
              :config="logRetainDays"
              :allowEditor="true"
              type="number"></BaseItem>

        </div>
      </div>
    </div>



    <div class="systemconfig_item_title">{{lp._ternaryManagement.logBodyEnable}}</div>
    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.logBodyEnableInfo"></div>

  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import {getConfigData, saveConfig} from "@/util/acrions";
import BaseBoolean from '@/components/item/BaseBoolean.vue';
import BaseItem from '@/components/item/BaseItem.vue';


const ternaryManagementEnable = ref(false);

const logRetainDays = ref(7);


getConfigData('ternaryManagement').then((data)=>{
  ternaryManagementEnable.value = data.enable
});

</script>

<style scoped>
</style>
