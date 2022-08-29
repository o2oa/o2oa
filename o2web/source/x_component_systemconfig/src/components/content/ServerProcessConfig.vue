<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.processConfig}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._processConfig.baseConfig" lazy>
          <BaseConfig></BaseConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._processConfig.timerConfig" lazy>
          <TimerConfig></TimerConfig>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import BaseConfig from '@/components/content/ServerProcessConfig/BaseConfig.vue';
import TimerConfig from '@/components/content/ServerProcessConfig/TimerConfig.vue';


</script>

<style scoped>
</style>
