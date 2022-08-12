<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.uiConfig}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._uiConfig.baseConfig" lazy>
          <BaseConfig/>
        </el-tab-pane>
        <el-tab-pane :label="lp._uiConfig.menuConfig" lazy>
          <MenuConfig/>
        </el-tab-pane>
        <el-tab-pane :label="lp._uiConfig.userConfig" lazy>
        <!--          <ServiceDeploy></ServiceDeploy>-->
        </el-tab-pane>

      </el-tabs>
    </div>

  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import BaseConfig from './BaseUIConfig/BaseConfig.vue';
import MenuConfig from './BaseUIConfig/MenuConfig.vue';
</script>

<style scoped>
</style>
