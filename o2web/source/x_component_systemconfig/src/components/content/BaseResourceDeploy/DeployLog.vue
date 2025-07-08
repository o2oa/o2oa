<template>
  <div class="systemconfig_area">
    <div class="systemconfig_item_title">{{lp._resource.deployLog}}</div>
    <div class="systemconfig_item_info">{{lp._resource.deployLogInfo}}</div>

    <div class="item_info">
      <el-table :data="deployLogs" stripe style="width: 100%">
        <el-table-column prop="title" :label="lp._resource.title"/>
        <el-table-column prop="name" :label="lp._resource.fileName"/>
        <el-table-column prop="version" :label="lp._resource.version"/>
        <el-table-column prop="installPerson" :label="lp._resource.installPerson"/>
        <el-table-column prop="installTime" :label="lp._resource.installTime"/>
      </el-table>
  </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import {getConfigData, listDeployLog} from '@/util/acrions';

const deployLogs = ref([]);

const loadPaging = async () => {
  deployLogs.value = await listDeployLog(1, 20)
}
loadPaging();

</script>

<style scoped>

</style>
