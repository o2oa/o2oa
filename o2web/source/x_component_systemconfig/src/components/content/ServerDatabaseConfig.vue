<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.databaseServer}}</div>
    <div class="systemconfig_item_info" v-if="externalDatabase" v-html="lp._databaseServer.infoExternal"></div>
    <div class="systemconfig_item_info" v-else v-html="lp._databaseServer.infoInner"></div>






<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.enable}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.enableInfo"></div>-->
<!--    <BaseBoolean :value="ternaryManagementEnable" @change="(value)=>{saveConfig('ternaryManagement', 'enable', value)}" />-->


<!--&lt;!&ndash;    @changeConfig="(value)=>{failureCount = value.toInt(); saveConfig('person', 'failureCount', value.toInt())}"&ndash;&gt;-->

<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.logRetainDays}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.logRetainDaysInfo"></div>-->
<!--    <div>-->
<!--      <div class="item_info" style="display: inline-flex; align-items: center;">-->
<!--        <label class="item_label">{{lp._ternaryManagement.logRetainDays}}</label>-->
<!--        <div class="item_input_area">-->
<!--          <BaseItem-->
<!--              :config="logRetainDays"-->
<!--              :allowEditor="true"-->
<!--              type="number"></BaseItem>-->

<!--        </div>-->
<!--      </div>-->
<!--    </div>-->



<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.logBodyEnable}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.logBodyEnableInfo"></div>-->

  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import {getConfigData, saveConfig, getServers, getDataEntrys} from "@/util/acrions";
import BaseBoolean from '@/components/item/BaseBoolean.vue';
import BaseItem from '@/components/item/BaseItem.vue';

const externalDatabase = ref(null);
const servers = ref([]);
const dataEntitys = ref([])

const load = ()=>{
  getConfigData('externalDataSources').then((data)=>{
    externalDatabase.value = data;
    if (!data){
      getServers().then((data)=>{
        servers.value = data.nodeList;
      });
    }
  });
  getDataEntrys().then((data)=>{
    dataEntitys.value = data;
  });
}

load();

</script>

<style scoped>
</style>
