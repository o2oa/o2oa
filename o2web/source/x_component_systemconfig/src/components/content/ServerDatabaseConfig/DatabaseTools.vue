<template>
  <div class="systemconfig_area" style="display:block;">
    <div class="item_title">{{lp._databaseServer.dumpRestoreTools}}</div>
    <div class="systemconfig_item_info" v-html="lp._databaseServer.toolsInfo"></div>

    <div class="item_title" v-html="lp._databaseServer.dumpTools"></div>
    <div class="item_info" v-html="lp._databaseServer.dumpToolsInfo"></div>
    <div class="item_info" style="padding-left: 20px">
      <button class="mainColor_bg" @click="dump($event)">{{lp._databaseServer.dumpTools}}</button>
    </div>
    <div class="item_info">
      <div class="item_log_area">
        <div class="item_log_wait" v-if="dumpStatus==='stop'">{{lp._databaseServer.dumpWaitLog}}</div>
        <div class="item_log_wait" v-if="dumpStatus==='error'">{{lp._databaseServer.dumpErrorLog}}</div>
        <div ref="dumpLogArea">
<!--          <div class="item_log" v-for="log in dumpLog">{{log}}</div>-->
        </div>
      </div>
    </div>



  </div>
</template>

<script setup>
import {ref, computed} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {loadRuntimeConfig, saveConfig, getServers, executeCommand, getSystemLog} from "@/util/acrions";
import EntityEditor from '@/components/content/ServerDatabaseConfig/EntityEditor';

const dumpLogArea = ref();
const dumpLog = ref([]);
const servers = ref([]);
const dumpStatus = ref('stop');


const dump = (e)=>{
  component.confirm("warn", e, lp._databaseServer.dumpBegin, lp._databaseServer.dumpBeginInfo, 500, 100, (dlg)=>{
    dumpLogArea.value.empty();
    const body ={
      ctl: "ctl -dd",
      nodeName: servers.value[0].nodeAddress,
      nodePort: servers.value[0].node.nodeAgentPort
    }
    executeCommand(body).then((data)=>{
      if (data.status==="success"){
        const id = o2.uuid();
        dumpStatus.value = 'running';
        readDumpLog(id);
      }else{
        dumpStatus.value = 'error';
      }
    });
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);
}

const printDumpLog = (log, className)=>{
  const node = document.createElement("div");
  node.className = className || 'item_log';
  node.textContent = log.lineLog;
  dumpLogArea.value.append(node);
  dumpLogArea.value.getParent().scrollTo(0,dumpLogArea.value.scrollHeight);
}
const readDumpLog = (id)=>{
  debugger;
  setTimeout(async () => {
    const data = await getSystemLog(id);
    data.forEach((log)=>{
      if (log.lineLog.includes('dump') || log.lineLog.includes('ctl -dd')){
        printDumpLog(log);
      }
    });
    if (dumpStatus.value = 'running'){
      readDumpLog(id);
    }
  }, 1000)
}



getServers().then((data)=>{
  servers.value = data.nodeList;
});

</script>

<style scoped>
.item_log_area{
  height: 500px;
  background-color: #333333;
  border-radius: 15px;
  padding: 10px;
  overflow: auto;
}
.item_log_wait{
  color: #999999;
  text-align: center;
  font-size: 18px;
  padding: 50px;
}


</style>
