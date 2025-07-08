<template xmlns="">
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.resourceDeploy}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._resource.componentResource" lazy>
          <ComponentDeploy></ComponentDeploy>
        </el-tab-pane>
        <el-tab-pane :label="lp._resource.webResource" lazy>
          <WebDeploy></WebDeploy>
        </el-tab-pane>
        <el-tab-pane :label="lp._resource.serviceResource" name="second" lazy>
          <ServiceDeploy></ServiceDeploy>
        </el-tab-pane>
        <el-tab-pane :label="lp._resource.deployLog" lazy>
          <DeployLog></DeployLog>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';

import WebDeploy from './BaseResourceDeploy/WebDeploy.vue';
import ServiceDeploy from './BaseResourceDeploy/ServiceDeploy.vue';
import ComponentDeploy from './BaseResourceDeploy/ComponentDeploy.vue';
import DeployLog from './BaseResourceDeploy/DeployLog.vue';

</script>

<style scoped>
</style>
