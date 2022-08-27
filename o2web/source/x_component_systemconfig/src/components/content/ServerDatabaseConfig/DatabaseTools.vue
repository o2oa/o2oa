<template>
  <div class="systemconfig_area" style="display:block;">
    <div class="item_title">{{lp._databaseServer.dumpRestoreTools}}</div>
    <div class="systemconfig_item_info" v-html="lp._databaseServer.toolsInfo"></div>

    <div class="item_title" v-html="lp._databaseServer.dumpTools"></div>
    <div class="item_info" v-html="lp._databaseServer.dumpToolsInfo"></div>
    <div class="item_info" style="padding-left: 20px">
      <button
          :class="{'item_disable': dumpStatus==='check' || dumpStatus==='running' || restoreStatus==='check' || restoreStatus==='running', 'mainColor_bg': dumpStatus!=='check' && dumpStatus!=='running' && restoreStatus!=='check' && restoreStatus!=='running'}"
          @click="dump($event)">{{lp._databaseServer.dumpTools}}</button>

      <span class="item_info" v-if="dumpStatus==='check'">{{lp._databaseServer.dumpCheck}}</span>
      <span class="item_info" v-if="dumpStatus==='stop'">{{lp._databaseServer.dumpStop}}</span>
      <span class="item_info" v-if="dumpStatus==='running'">{{lp._databaseServer.dumpRunning}}</span>
      <span class="item_info" v-if="dumpStatus==='end'">{{lp._databaseServer.dumpEnd}}</span>
      <button v-if="dumpStatus==='stop' || dumpStatus==='end'" @click="dumpCheck($event)">{{lp._databaseServer.dumpCheckButton}}</button>
    </div>
    <div class="item_info">
      <div class="item_log_area" :style="{height: (dumpStatus==='running' || dumpStatus==='end') ? '500px': '130px'}">
        <div class="item_log_wait" v-if="dumpStatus==='stop'">{{lp._databaseServer.dumpWaitLog}}</div>
        <div class="item_log_wait" v-if="dumpStatus==='error'">{{lp._databaseServer.dumpErrorLog}}</div>
        <div ref="dumpLogArea"></div>
      </div>
    </div>

    <div class="item_title" v-html="lp._databaseServer.restoreTools"></div>
    <div class="item_info" v-html="lp._databaseServer.restoreToolsInfo"></div>
    <div class="item_info" v-html="lp._databaseServer.restoreToolsInfo2"></div>
    <div class="item_info" style="padding-left: 20px">
      <button
          :class="{'item_disable': dumpStatus==='check' || dumpStatus==='running' || restoreStatus==='check' || restoreStatus==='running', 'mainColor_bg': dumpStatus!=='check' && dumpStatus!=='running' && restoreStatus!=='check' && restoreStatus!=='running'}"
          @click="restore($event)">{{lp._databaseServer.restoreTools}}</button>
<!--      <el-input v-model="dumpDataName" style="width: 210px; margin-left: 10px"></el-input>-->

      <el-select v-model="dumpDataName" style="margin-left: 10px">
        <el-option v-for="d in dumpDataList" :key="d.value" :value="d.value"></el-option>
      </el-select>

      <span class="item_info" v-if="restoreStatus==='check'">{{lp._databaseServer.restoreCheck}}</span>
      <span class="item_info" v-if="restoreStatus==='stop'">{{lp._databaseServer.restoreStop}}</span>
      <span class="item_info" v-if="restoreStatus==='running'">{{lp._databaseServer.restoreRunning}}</span>
      <span class="item_info" v-if="restoreStatus==='end'">{{lp._databaseServer.restoreEnd}}</span>
      <button v-if="restoreStatus==='stop' || restoreStatus==='end'" @click="restoreCheck($event)">{{lp._databaseServer.restoreCheckButton}}</button>
    </div>
    <div class="item_info">
      <div class="item_log_area" :style="{height: (restoreStatus==='running' || restoreStatus==='end') ? '500px': '130px'}">
        <div class="item_log_wait" v-if="restoreStatus==='stop'">{{lp._databaseServer.restoreWaitLog}}</div>
        <div class="item_log_wait" v-if="restoreStatus==='error'">{{lp._databaseServer.restoreErrorLog}}</div>
        <div ref="restoreLogArea"></div>
      </div>
    </div>



  </div>
</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {getServers, executeCommand, getSystemLog, listDumpData} from "@/util/acrions";

const servers = ref([]);
const dumpLogArea = ref();
const dumpStatus = ref('stop');
const dumpDataName = ref('');
const dumpDataList = ref([]);

let checkTimes = ref(0);

const restoreLogArea = ref();
const restoreStatus = ref('stop');
let restoreCheckTimes = ref(0);

const check = (status, times, m)=>{
  if (status.value==='stop' || status.value==='end'){
    status.value='check';
    times.value = 0;
    const id = o2.uuid();
    m(id);
  }
}
const dumpCheck = ()=>{
  debugger;
  check(dumpStatus, checkTimes, readDumpLog);
}
const restoreCheck = ()=>{
  check(restoreStatus, restoreCheckTimes, readRestoreLog);
}

const runCommand = (e, command, title, info, area, status, logMethod, server)=>{
  component.confirm("warn", e, title, info, 500, 100, (dlg)=>{
    area.value.empty();
    const body ={
      ctl: command,
      nodeName: (server) ? server.nodeAddress : servers.value[0].nodeAddress,
      nodePort: (server) ? server.node.nodeAgentPort : servers.value[0].node.nodeAgentPort
    }
    executeCommand(body).then((data)=>{
      if (data.status==="success"){
        const id = o2.uuid();
        status.value = 'running';
        logMethod(id);
      }else{
        status.value = 'error';
      }
    });
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);
}
const dump = (e)=>{
  runCommand(e, 'ctl -dd', lp._databaseServer.dumpBegin, lp._databaseServer.dumpBeginInfo, dumpLogArea, dumpStatus, readDumpLog);
}
const restore = (e)=>{
  const d = dumpDataList.value.find((data)=>{
    return data.value===dumpDataName.value;
  });
  if (d){
    const s = servers.value.find((server)=>{
      return server.nodeAddress===d.node;
    });

    if (s){
      const path = d.value.includes('_') ? d.value.substring(d.value.lastIndexOf('_')+1) : d.value;
      runCommand(e, 'ctl -rd '+path, lp._databaseServer.restoreBegin, lp._databaseServer.restoreBeginInfo, restoreLogArea, restoreStatus, readRestoreLog, s);
    }
  }



}

const printLog = (log, area, className)=>{
  const node = document.createElement("div");
  node.className = className || 'item_log';

  let timeStr = log.lineLog.substring(0, log.lineLog.indexOf('['));
  timeStr = timeStr.substring(0, timeStr.lastIndexOf('.'));
  let content = log.lineLog.substring(log.lineLog.indexOf(']'));
  content = content.substring(content.indexOf('-'));

  node.textContent = timeStr+' '+content;
  area.value.append(node);
  area.value.getParent().scrollTo(0,area.value.scrollHeight);
}

const printDumpLog = (log, className)=>{
  printLog(log, dumpLogArea, className);
}
const printRestoreLog = (log, className)=>{
  printLog(log, restoreLogArea, className);
}

const readLog = (id, isComplete, isLog, status, logMethod, times, complete)=>{
  setTimeout(async () => {
    const data = await getSystemLog(id);
    let isGetLog = false;
    if (data){
      data.forEach((log)=>{
        if (isComplete(log)){
          logMethod(log, 'item_log_completed');
          status.value='end';
          isGetLog = true;

          if (complete) complete(log)
        }else if ( isLog(log) ){
          logMethod(log);
          isGetLog = true;
        }
      });
    }

    if (isGetLog && status.value!=='end'){
      status.value='running';
      times.value = 0;
    }
    if (status.value === 'check') times.value++;
    if (status.value === 'check' && times.value>5){
      status.value = 'stop';
      times.value = 0;
    }
    if (status.value !== 'stop' && status.value !== 'end'){
      readLog(id, isComplete, isLog, status, logMethod, times, complete);
    }
  }, 1000)
}
const readDumpLog = (id)=>{
  readLog(id, (log)=>{
    return log.lineLog.includes('dump data completed')
  }, (log)=>{
    return log.lineLog.includes('dump') && !log.lineLog.includes('restore data') && !log.lineLog.includes('/dump/');
  }, dumpStatus, printDumpLog, checkTimes, (log)=>{
    let path = log.lineLog.substring(log.lineLog.indexOf('directory:'));
    path = path.substring(0, path.indexOf(','));
    path = path.substring(path.lastIndexOf('/')+1);
    dumpDataName.value = path;
    listDumpData().then((data)=>{
      dumpDataList.value = data;
    });
  });
}

const readRestoreLog = (id)=>{
  readLog(id, (log)=>{
    return log.lineLog.includes('restore data completed')
  }, (log)=>{
    return log.lineLog.includes('restore');
  }, restoreStatus, printRestoreLog, restoreCheckTimes);
}


const load = ()=>{
  getServers().then((data)=>{
    servers.value = data.nodeList;
    dumpCheck();
    restoreCheck();
  });

  listDumpData().then((data)=>{
    dumpDataList.value = data;
  });
}

load();


</script>

<style scoped>
.item_log_area{
  height: 130px;
  background-color: #333333;
  border-radius: 15px;
  padding: 10px;
  overflow: auto;
  transition: height 0.5s;
}
.item_log_wait{
  color: #999999;
  text-align: center;
  font-size: 18px;
  padding: 50px;
}
.item_disable{
  color: #999999;
  cursor: not-allowed;
}


</style>
