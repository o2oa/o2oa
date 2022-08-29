<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.appTools}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">

        <el-tab-pane :label="lp._appTools.onlineBuild" lazy>
          <OnlineBuild></OnlineBuild>
        </el-tab-pane>
        <el-tab-pane :label="lp._appTools.mpweixinMenu" lazy>
          <MPweixinMenuManage></MPweixinMenuManage>
        </el-tab-pane>

      </el-tabs>
    </div>

  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import OnlineBuild from '@/components/content/MobileToolsConfig/OnlineBuild.vue';
import MPweixinMenuManage from '@/components/content/MobileToolsConfig/MPweixinMenuManage.vue';
</script>

<style scoped>
</style>
