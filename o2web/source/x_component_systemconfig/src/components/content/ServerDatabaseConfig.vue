<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.databaseServer}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right" @tab-click="changeTabPane" model-value="db">
        <el-tab-pane :label="lp._databaseServer.databaseSource" name="db">
          <DatabaseConfig ref="database"></DatabaseConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._databaseServer.entity" name="entity">
          <EntityConfig ref="entity"></EntityConfig>
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
import {ref} from 'vue';
import DatabaseConfig from '@/components/content/ServerDatabaseConfig/DatabaseConfig.vue';
import EntityConfig from '@/components/content/ServerDatabaseConfig/EntityConfig.vue';
import DatabaseTools from '@/components/content/ServerDatabaseConfig/DatabaseTools.vue';

const entity = ref();
const database = ref();

const changeTabPane = (pane, e)=>{
  if (pane.paneName === 'entity' && !pane.active){
    entity.value.load();
  }
  if (pane.paneName === 'db' && !pane.active){
    database.value.load();
  }
}

</script>

<style scoped>
</style>
