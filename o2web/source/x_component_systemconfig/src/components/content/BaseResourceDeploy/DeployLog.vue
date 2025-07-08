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
      <el-pagination layout="prev, pager, next" :default-page-size="size" :total="total" small/>
  </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import {listDeployLog} from '@/util/acrions';

const deployLogs = ref([]);
const size = 20;
const total = ref(0);

const loadPaging = async (page) => {
  const json = await listDeployLog(page, size);
  deployLogs.value = json.data;
  total.value = json.size;
}

loadPaging(1);

</script>

<style scoped>

</style>
