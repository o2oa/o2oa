<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.serversConfig}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._serversConfig.serverInfo" lazy>
          <BaseServerInfo></BaseServerInfo>
        </el-tab-pane>

        <el-tab-pane v-for="server in servers" :key="server.nodeAddress" :label="lp._systemInfo.node+':'+server.nodeAddress" lazy>
          <ServerConfig :server="server.node" :nodeName="'node_'+server.nodeAddress"/>
        </el-tab-pane>

        <el-tab-pane v-if="servers.length" :label="lp._serversConfig.serverTaskConfig" lazy>
          <ServerTaskConfig></ServerTaskConfig>
        </el-tab-pane>

        <!--        <el-tab-pane :label="lp._uiConfig.userConfig" lazy>-->
        <!--        &lt;!&ndash;          <ServiceDeploy></ServiceDeploy>&ndash;&gt;-->
        <!--        </el-tab-pane>-->

      </el-tabs>
    </div>

  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import BaseServerInfo from './BaseSystemInfo/BaseServerInfo.vue';
import ServerConfig from './ServerServersConfig/ServerConfig.vue';
import ServerTaskConfig from './ServerServersConfig/ServerTaskConfig.vue';


import {getServers} from "@/util/acrions";
import {ref} from "vue";

const servers = ref([]);
// const renderTaskTab = ref(false);

getServers().then((data)=>{
  servers.value = data.nodeList;
  //renderTaskTab.value = true;
});
</script>

<style scoped>
</style>
