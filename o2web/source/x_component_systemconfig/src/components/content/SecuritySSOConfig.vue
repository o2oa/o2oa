<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.ssoConfig}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._ssoConfig.ssoConfig" lazy>
          <SSOConfig></SSOConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._ssoConfig.oauthConfig" name="second" lazy>
          <OauthConfig></OauthConfig>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import SSOConfig from '@/components/content/SecuritySSOConfig/SSOConfig.vue';
import OauthConfig from '@/components/content/SecuritySSOConfig/OauthConfig.vue';
</script>

<style scoped>
</style>
