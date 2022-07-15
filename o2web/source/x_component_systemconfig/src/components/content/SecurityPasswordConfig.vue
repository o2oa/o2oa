<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.passwordConfig}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._passwordConfig.personPassword" lazy>
          <PersonPassword></PersonPassword>
        </el-tab-pane>
        <el-tab-pane :label="lp._passwordConfig.adminPassword" name="second" lazy>
<!--          <ServiceDeploy></ServiceDeploy>-->
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import PersonPassword from './SecurityPasswordConfig/PersonPassword.vue';
</script>

<style scoped>

</style>
