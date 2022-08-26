<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.appTools}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">

        <el-tab-pane :label="lp._appTools.onlineBuild" lazy>
          <OnlineBuild></OnlineBuild>
        </el-tab-pane>
        <el-tab-pane :label="lp._appTools.mpweixinMenu" lazy>
          <MPweixinConfig></MPweixinConfig>
        </el-tab-pane>

      </el-tabs>
    </div>

  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import OnlineBuild from '@/components/content/MobileToolsConfig/OnlineBuild.vue';
import MPweixinConfig from '@/components/content/MobileIntegrationConfig/MPweixinConfig.vue';
import QYweixinConfig from "@/components/content/MobileIntegrationConfig/QYweixinConfig";
import WelinkConfig from "@/components/content/MobileIntegrationConfig/WelinkConfig";
</script>

<style scoped>
</style>
