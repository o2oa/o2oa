<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.worktimeConfig}}</div>

    <BaseItem
        :title="lp._worktimeConfig.amWorktime"
        :info="lp._worktimeConfig.amWorktimeInfo"
        :config="amWorktime"
        :allowEditor="true"
        type="time"
        :options="{isRange: true, rangeSeparator: lp._worktimeConfig.timeRangeTo, startPlaceholder: lp._worktimeConfig.startTime, endPlaceholder: lp._worktimeConfig.endTime}"
        @changeConfig="(value)=>{amWorktime = value; saveWorktime(value, 'am')}"
        :format-text="formatTimeRange"
    ></BaseItem>

    <BaseItem
        :title="lp._worktimeConfig.pmWorktime"
        :info="lp._worktimeConfig.pmWorktimeInfo"
        :config="pmWorktime"
        :allowEditor="true"
        type="time"
        :options="{isRange: true, rangeSeparator: lp._worktimeConfig.timeRangeTo, startPlaceholder: lp._worktimeConfig.startTime, endPlaceholder: lp._worktimeConfig.endTime}"
        @changeConfig="(value)=>{pmWorktime = value; saveWorktime(value, 'pm')}"
        :format-text="formatTimeRange"
    ></BaseItem>

    <BaseItem
        :title="lp._worktimeConfig.holidays"
        :info="lp._worktimeConfig.holidaysInfo"
        :config="worktimeData.holidays"
        :allowEditor="true"
        type="date"
        :options="{type: 'dates'}"
        @changeConfig="(value)=>{worktimeData.holidays = value; saveConfig('workTime', 'holidays', value)}"
    ></BaseItem>

    <BaseItem
        :title="lp._worktimeConfig.workdays"
        :info="lp._worktimeConfig.workdaysInfo"
        :config="worktimeData.workdays"
        :allowEditor="true"
        type="date"
        :options="{type: 'dates'}"
        @changeConfig="(value)=>{worktimeData.workdays = value; saveConfig('workTime', 'workdays', value)}"
    ></BaseItem>

    <div class="systemconfig_item_title">{{lp._worktimeConfig.weekends}}</div>
    <div class="systemconfig_item_info" v-html="lp._worktimeConfig.weekendsInfo"></div>
    <div class="item_info">
      <el-checkbox-group v-model="worktimeData.weekends" @change="(v)=>{saveConfig('workTime', 'weekends', v)}">
        <el-checkbox v-for="k in Object.keys(lp._worktimeConfig.weekData)" :key="k" :label="lp._worktimeConfig.weekData[k]">{{k}}</el-checkbox>
      </el-checkbox-group>
    </div>

  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import {getConfigData, saveConfigData, saveConfig} from "@/util/acrions";
import BaseItem from '@/components/item/BaseItem.vue';


const amWorktime = ref([]);
const pmWorktime = ref([]);
const worktimeData = ref({});

const formatTimeRange = (v)=>{
  if (v && v.length){
    return v.join('-')
  }
  return '';
}

const saveWorktime = (value, t)=>{
  if (value && value.length){
    worktimeData.value[t+'Start'] = value[0];
    worktimeData.value[t+'End'] = value[1];
    saveConfigData('workTime', worktimeData.value);
  }
}

const formatToDate = (d)=>{
  if (d){
    try{
      const now = new Date();
      const arr = d.split(':');
      return new Date(now.getFullYear(), now.getMonth(), now.getDate(), arr[0], arr[1], arr[2]);
    }catch(e){}
  }
  return '';
}

getConfigData('workTime').then((data)=>{
  amWorktime.value = [(data.amStart),(data.amEnd)];
  pmWorktime.value = [(data.pmStart),(data.pmEnd)];
  worktimeData.value = data;
});



</script>

<style scoped>
</style>
