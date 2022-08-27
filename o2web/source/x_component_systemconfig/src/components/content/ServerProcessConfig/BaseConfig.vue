<template>
    <div v-if="processData">
      <BaseItem :title="lp._processConfig.maintenanceIdentity"
                :info="lp._processConfig.maintenanceIdentityInfo"
                :config="processData.maintenanceIdentity"
                :allowEditor="true"
                :edit-method="selectMaintenanceIdentity" />

      <BaseItem :title="lp._processConfig.formVersionCount"
                :info="lp._processConfig.formVersionCountInfo"
                :config="processData.formVersionCount"
                type="number"
                :allowEditor="true"
                @changeConfig="(v)=>{processData.formVersionCount = v.toInt(); saveConfig('processPlatform', 'formVersionCount', v.toInt());}" />

      <BaseItem :title="lp._processConfig.processVersionCount"
                :info="lp._processConfig.processVersionCountInfo"
                :config="processData.processVersionCount"
                type="number"
                :allowEditor="true"
                @changeConfig="(v)=>{processData.processVersionCount = v.toInt(); saveConfig('processPlatform', 'processVersionCount', v.toInt());}" />

      <BaseItem :title="lp._processConfig.scriptVersionCount"
                :info="lp._processConfig.scriptVersionCountInfo"
                :config="processData.scriptVersionCount"
                type="number"
                :allowEditor="true"
                @changeConfig="(v)=>{processData.scriptVersionCount = v.toInt(); saveConfig('processPlatform', 'scriptVersionCount', v.toInt());}" />

      <BaseItem :title="lp._processConfig.docToWordType"
                :info="lp._processConfig.docToWordTypeInfo"
                :config="processData.docToWordType"
                type="select"
                :options="lp._processConfig.docWordTypeSelect"
                :allowEditor="true"
                @changeConfig="(v)=>{processData.docToWordType = v; saveConfig('processPlatform', 'docToWordType', v);}" />


      <div class="item_title">{{lp._processConfig.press}}</div>
      <div class="item_info">{{lp._processConfig.pressInfo}}</div>
      <div class="item_info" style="display: flex; justify-content: flex-start; align-items: center;}">
        <div style="margin: 0 5px">{{lp._processConfig.pressInfo1}}</div>

        <div v-if="!editPress" style="margin: 0 5px; " class="mainColor_color">{{processData.press.intervalMinutes}}</div>
        <el-input-number v-else :min="0" v-model="processData.press.intervalMinutes"></el-input-number>

        <div style="margin: 0 5px">{{lp._processConfig.pressInfo2}}</div>

        <div v-if="!editPress" style="margin: 0 5px; " class="mainColor_color">{{processData.press.count}}</div>
        <el-input-number v-else :min="0" v-model="processData.press.count"></el-input-number>

        <div style="margin: 0 5px">{{lp._processConfig.pressInfo3}}</div>

        <button v-if="!editPress" class="mainColor_bg" @click="()=>{editPress=true;}">{{lp.operation.edit+lp._processConfig.press}}</button>
        <button v-if="editPress" class="mainColor_bg" @click="()=>{saveConfig('processPlatform', 'press', processData.press); editPress=false;}">{{ lp.operation.ok }}</button>
        <button v-if="editPress" @click="()=>{editPress=false;}">{{ lp.operation.cancel }}</button>
      </div>


      <BaseItem :title="lp._processConfig.executorCount"
                :info="lp._processConfig.executorCountInfo"
                :config="processData.executorCount"
                type="number"
                :allowEditor="true"
                @changeConfig="(v)=>{processData.executorCount = v; saveConfig('processPlatform', 'executorCount', v);}" />

      <BaseItem :title="lp._processConfig.executorQueueBusyThreshold"
                :info="lp._processConfig.executorQueueBusyThresholdInfo"
                :config="processData.executorQueueBusyThreshold"
                type="number"
                :allowEditor="true"
                @changeConfig="(v)=>{processData.executorQueueBusyThreshold = v; saveConfig('processPlatform', 'executorQueueBusyThreshold', v);}" />

    </div>
</template>

<script setup>
import {lp, o2, component} from '@o2oa/component';
import {ref} from 'vue';
import {getConfigData, saveConfig} from '@/util/acrions';
import BaseItem from '@/components/item/BaseItem.vue';

const processData = ref();
const editPress = ref(false);

const selectMaintenanceIdentity = ()=>{
  o2.requireApp('Selector', 'package', ()=>{
    new MWF.O2Selector(component.content, {
      count: 1,
      type: 'identity',
      title: lp._processConfig.selectMaintenanceIdentity,
      values: (processData.value.maintenanceIdentity) ? [processData.value.maintenanceIdentity] : [],
      onComplete: (items)=>{
        if (items && items.length){
          processData.value.maintenanceIdentity = items[0].data.distinguishedName
        }else{
          processData.value.maintenanceIdentity = '';
        }
        saveConfig('processPlatform', 'maintenanceIdentity', processData.value.maintenanceIdentity);
      }
    })
  })
}



getConfigData('processPlatform').then((data)=>{
  processData.value = data;
});


</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 30px;
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
.item_pass_input{
  width: 100px;
  text-align: center;
}
.item_input_area{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
  margin-left: 80px;
}
.item_slider{
  width: 300px;
}
.item_hide{
  display: none;
}
</style>
