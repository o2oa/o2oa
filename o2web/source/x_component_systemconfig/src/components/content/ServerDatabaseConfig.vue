<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.databaseServer}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._databaseServer.databaseSource" lazy>
          <DatabaseConfig></DatabaseConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._databaseServer.entity" name="second" lazy>
<!--          <EntityConfig></EntityConfig>-->
        </el-tab-pane>

        <el-tab-pane :label="lp._databaseServer.tools" name="3" lazy>
          <!--          <EntityConfig></EntityConfig>-->
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import DatabaseConfig from '@/components/content/ServerDatabaseConfig/DatabaseConfig.vue';
</script>

<style scoped>
</style>
