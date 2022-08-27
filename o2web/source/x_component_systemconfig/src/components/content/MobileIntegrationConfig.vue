<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp._integrationConfig.title}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._integrationConfig.dingding" lazy>
          <DingdingConfig></DingdingConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._integrationConfig.mPweixin" lazy>
          <MPweixinConfig></MPweixinConfig>
        </el-tab-pane>

        <el-tab-pane :label="lp._integrationConfig.qiyeweixin" lazy>
          <QYweixinConfig></QYweixinConfig>
        </el-tab-pane>

        <el-tab-pane :label="lp._integrationConfig.weLink" lazy>
          <WelinkConfig></WelinkConfig>
        </el-tab-pane>

      </el-tabs>
    </div>

  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import DingdingConfig from '@/components/content/MobileIntegrationConfig/DingdingConfig.vue';
import MPweixinConfig from '@/components/content/MobileIntegrationConfig/MPweixinConfig.vue';
import QYweixinConfig from "@/components/content/MobileIntegrationConfig/QYweixinConfig";
import WelinkConfig from "@/components/content/MobileIntegrationConfig/WelinkConfig";
</script>

<style scoped>
</style>
