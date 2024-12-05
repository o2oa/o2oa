<template>
  <div class="systemconfig_item_info" v-html="lp._processConfig.timerInfo"></div>

  <div v-if="processData">
    <div class="item_title">{{lp._processConfig.urge}}</div>
    <div class="item_info">{{lp._processConfig.urgeInfo}}</div>
    <div class="item_info">
      <BaseBoolean v-model:value="processData.urge.enable" :label="lp._processConfig.enable" @change="(value)=>{saveConfig('processPlatform', 'urge.enable', value)}"></BaseBoolean>
      <BaseCron v-model:value="processData.urge.cron" :label="lp._processConfig.cron" @change="save('urge')"></BaseCron>
    </div>

    <div class="item_title">{{lp._processConfig.expire}}</div>
    <div class="item_info">{{lp._processConfig.expireInfo}}</div>
    <div class="item_info">
      <BaseBoolean v-model:value="processData.expire.enable" :label="lp._processConfig.enable" @change="(value)=>{saveConfig('processPlatform', 'expire.enable', value)}"></BaseBoolean>
      <BaseCron v-model:value="processData.expire.cron" :label="lp._processConfig.cron" @change="save('expire')"></BaseCron>
    </div>

    <div class="item_title">{{lp._processConfig.touchDelay}}</div>
    <div class="item_info">{{lp._processConfig.touchDelayInfo}}</div>
    <div class="item_info">
      <BaseBoolean v-model:value="processData.touchDelay.enable" :label="lp._processConfig.enable" @change="(value)=>{saveConfig('processPlatform', 'touchDelay.enable', value)}"></BaseBoolean>
      <BaseCron v-model:value="processData.touchDelay.cron" :label="lp._processConfig.cron" @change="save('touchDelay')"></BaseCron>
    </div>

    <div class="item_title">{{lp._processConfig.deleteDraft}}</div>
    <div class="item_info">{{lp._processConfig.deleteDraftInfo}}</div>
    <div class="item_info">
      <BaseBoolean v-model:value="processData.deleteDraft.enable" :label="lp._processConfig.enable" @change="(value)=>{saveConfig('processPlatform', 'deleteDraft.enable', value)}"></BaseBoolean>
      <BaseCron v-model:value="processData.deleteDraft.cron" :label="lp._processConfig.cron" @change="save('deleteDraft')"></BaseCron>

      <div class="item_info" style="display: inline-flex; align-items: center;">
        <label class="item_label">{{lp._processConfig.thresholdMinutes}}</label>
        <div class="item_input_area" style="margin-left: -30px">
          <BaseItem
              :config="processData.deleteDraft.thresholdMinutes"
              :allowEditor="true"
              type="number"
              @changeConfig="(v)=>{processData.deleteDraft.thresholdMinutes = v.toFloat() ;save('deleteDraft')}"></BaseItem>
          <div class="item_info">{{lp._processConfig.thresholdMinutesInfo}}</div>
        </div>
      </div>
    </div>

    <div class="item_title">{{lp._processConfig.passExpired}}</div>
    <div class="item_info">{{lp._processConfig.passExpiredInfo}}</div>
    <div class="item_info">
      <BaseBoolean v-model:value="processData.passExpired.enable" :label="lp._processConfig.enable" @change="(value)=>{saveConfig('processPlatform', 'passExpired.enable', value)}"></BaseBoolean>
      <BaseCron v-model:value="processData.passExpired.cron" :label="lp._processConfig.cron" @change="save('passExpired')"></BaseCron>
    </div>

    <div class="item_title">{{lp._processConfig.touchDetained}}</div>
    <div class="item_info">{{lp._processConfig.touchDetainedInfo}}</div>
    <div class="item_info">
      <BaseBoolean v-model:value="processData.touchDetained.enable" :label="lp._processConfig.enable" @change="(value)=>{saveConfig('processPlatform', 'touchDetained.enable', value)}"></BaseBoolean>
      <BaseCron v-model:value="processData.touchDetained.cron" :label="lp._processConfig.cron" @change="save('touchDetained')"></BaseCron>

      <div class="item_info" style="display: inline-flex; align-items: center;">
        <label class="item_label">{{lp._processConfig.thresholdMinutes}}</label>
        <div class="item_input_area" style="margin-left: -30px">
          <BaseItem
              :config="processData.touchDetained.thresholdMinutes"
              :allowEditor="true"
              type="number"
              @changeConfig="(v)=>{processData.touchDetained.thresholdMinutes = v.toFloat() ;save('deleteDraft')}"></BaseItem>
          <div class="item_info">{{lp._processConfig.thresholdMinutesInfo_touchDetained}}</div>
        </div>
      </div>
    </div>

    <div class="item_title">{{lp._processConfig.updateTable}}</div>
    <div class="item_info">{{lp._processConfig.updateTableInfo}}</div>
    <div class="item_info">
      <BaseBoolean v-model:value="processData.updateTable.enable" :label="lp._processConfig.enable" @change="(value)=>{saveConfig('processPlatform', 'updateTable.enable', value)}"></BaseBoolean>
      <BaseCron v-model:value="processData.updateTable.cron" :label="lp._processConfig.cron" @change="save('updateTable')"></BaseCron>
    </div>

<!--    <div class="item_title">{{lp._processConfig.archiveHadoop}}</div>-->
<!--    <div class="item_info">{{lp._processConfig.archiveHadoopInfo}}</div>-->
<!--    <div class="item_info">-->
<!--      <BaseBoolean v-model:value="processData.archiveHadoop.enable" :label="lp._processConfig.enable" :label-style="{width: '100px'}"></BaseBoolean>-->
<!--      <BaseCron v-model:value="processData.archiveHadoop.cron" :label="lp._processConfig.cron" :label-style="{width: '100px'}"></BaseCron>-->
<!--      <BaseInput v-model:value="processData.archiveHadoop.fsDefaultFS" :label="lp._processConfig.fsDefaultFS" :label-style="{width: '100px'}"></BaseInput>-->
<!--      <BaseInput v-model:value="processData.archiveHadoop.username" :label="lp._processConfig.username" :label-style="{width: '100px'}"></BaseInput>-->
<!--      <BaseInput v-model:value="processData.archiveHadoop.path" :label="lp._processConfig.path" :label-style="{width: '100px'}"></BaseInput>-->

<!--      <button class="mainColor_bg" @click="saveHadoop('archiveHadoop')">{{saveHadoopText}}</button>-->
<!--    </div>-->

  </div>
</template>

<script setup>
import {lp, o2, component} from '@o2oa/component';
import {ref} from 'vue';
import {getConfigData, saveConfig} from '@/util/acrions';
import BaseInput from '@/components/item/BaseInput.vue';
import BaseCron from '@/components/item/BaseCron.vue';
import BaseItem from '@/components/item/BaseItem.vue';
import BaseBoolean from "@/components/item/BaseBoolean";

const processData = ref();
// const saveHadoopText = ref(lp._processConfig.saveHadoop)
//
// const saveHadoop = (key)=>{
//   save(key).then(()=>{
//     component.notice(lp._processConfig.saveHadoopSuccess, 'success');
//   });
// }

const save = (key)=>{
    debugger;
  return saveConfig('processPlatform', key, processData.value[key]);
}



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
    debugger;
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
