<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.storageServer}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right" @tab-click="changeTabPane" model-value="inner">
        <el-tab-pane :label="lp._storageServer.innerStorage"  name="inner" lazy>
          <InnerStorageConfig ref="innerComponent"></InnerStorageConfig>
        </el-tab-pane>

        <el-tab-pane :label="lp._storageServer.externalStorage"  name="external" lazy>
          <ExternalStorageConfig ref="externalComponent"></ExternalStorageConfig>
        </el-tab-pane>

        <el-tab-pane :label="lp._databaseServer.tools" lazy>
          <DatabaseTools></DatabaseTools>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import InnerStorageConfig from '@/components/content/ServerStorageConfig/InnerStorageConfig.vue';
import ExternalStorageConfig from '@/components/content/ServerStorageConfig/ExternalStorageConfig.vue';
import DatabaseTools from '@/components/content/ServerDatabaseConfig/DatabaseTools.vue';
import {ref} from 'vue';

const innerComponent = ref();
const externalComponent = ref();

const changeTabPane = (pane)=>{
  if (pane.paneName === 'inner' && !pane.active){
    if (innerComponent.value) innerComponent.value.load();
  }
  debugger;
  if (pane.paneName === 'external' && !pane.active){

    if (externalComponent.value) externalComponent.value.load();
  }
}

</script>

<style scoped>
</style>
