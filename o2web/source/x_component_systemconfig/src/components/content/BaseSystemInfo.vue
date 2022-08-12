<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.systemInfo}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._systemInfo.baseInfo" lazy>
          <BaseInfo></BaseInfo>
        </el-tab-pane>
        <el-tab-pane :label="lp._systemInfo.systemStatus" lazy>
          <BaseServerInfo></BaseServerInfo>
        </el-tab-pane>
        <el-tab-pane :label="lp._systemInfo.moduleStatus" lazy>
          <BaseModuleInfo></BaseModuleInfo>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import BaseInfo from './BaseSystemInfo/BaseInfo.vue';
import BaseServerInfo from './BaseSystemInfo/BaseServerInfo.vue';
import BaseModuleInfo from './BaseSystemInfo/BaseModuleInfo.vue';

</script>

<style scoped>

</style>
