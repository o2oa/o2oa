<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.systemInfo}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._systemInfo.baseInfo" lazy>
          <BaseInfo></BaseInfo>
        </el-tab-pane>
        <el-tab-pane :label="lp._systemInfo.systemStatus" name="second" lazy>
<!--          <ServiceDeploy></ServiceDeploy>-->
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import BaseInfo from './systemInfo/BaseInfo.vue';
</script>

<style scoped>

</style>
