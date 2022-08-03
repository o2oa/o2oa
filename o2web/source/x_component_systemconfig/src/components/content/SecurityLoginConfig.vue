<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.loginConfig}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._loginConfig.baseConfig" lazy>
          <BaseConfig></BaseConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._loginConfig.moreConfig" name="second" lazy>
          <MoreConfig></MoreConfig>
        </el-tab-pane>

        <el-tab-pane :label="lp._loginConfig.ldapConfig" name="ldap" lazy>
          <LdapConfig></LdapConfig>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import BaseConfig from '@/components/content/SecurityLoginConfig/BaseConfig.vue';
import MoreConfig from '@/components/content/SecurityLoginConfig/MoreConfig.vue';
import LdapConfig from '@/components/content/SecurityLoginConfig/LdapConfig.vue';
</script>

<style scoped>
</style>
