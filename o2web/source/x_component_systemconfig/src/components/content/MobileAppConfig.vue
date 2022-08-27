<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.appConfig}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._appConfig.connectConfig" lazy>
          <ConnectConfig></ConnectConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._appConfig.moduleConfig" lazy>
          <ModuleConfig></ModuleConfig>
        </el-tab-pane>

        <el-tab-pane :label="lp._appConfig.iconConfig" lazy>
          <IconConfig></IconConfig>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import ConnectConfig from '@/components/content/MobileAppConfig/ConnectConfig.vue';
import ModuleConfig from "@/components/content/MobileAppConfig/ModuleConfig";
import IconConfig from "@/components/content/MobileAppConfig/IconConfig";
</script>

<style scoped>
</style>
