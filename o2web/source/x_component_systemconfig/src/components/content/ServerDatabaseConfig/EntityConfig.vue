<template>
  <div class="systemconfig_area" style="display:block;">
    <div class="item_title">{{lp._databaseServer.entityConfig}}</div>
    <div class="systemconfig_item_info" v-html="lp._databaseServer.entityConfigInfo"></div>

    <div v-if="(enabledDatabase && enabledDatabase.length && enabledDatabase.length<=1)">
      <div class="systemconfig_item_info" v-html="lp._databaseServer.oneDatabase"></div>

      <div class="item_info">
        <div class="item_database_item">
          <div class="item_database_area">
            <div class="o2icon-database item_config_icon mainColor_bg"></div>
            <div v-if="databaseType==='external'" class="item_server_item_slot item_bold" style="min-width: 340px">{{enabledDatabase[0].url.substring(0, enabledDatabase[0].url.lastIndexOf('/'))}}</div>
            <div v-else class="item_server_item_slot item_bold" style="min-width: 340px">{{enabledDatabase[0].url}}:{{enabledDatabase[0].tcpPort}}</div>
          </div>
        </div>
      </div>

      <div style="padding: 20px">
        <div class="item_info_content">
          <div class="o2icon-disable" style="width: 50px; font-size: 36px; color: #cccccc"></div>
          <div  v-html="lp._databaseServer.oneDatabaseInfo"></div>
        </div>
      </div>
    </div>

    <div v-if="(enabledDatabase && enabledDatabase.length && enabledDatabase.length>1)">

      <div class="item_info" style="display: flex; justify-content: flex-start;">
        <div class="item_button_area">
          <button class="mainColor_bg" @click="saveDatabaseConfig($event)">{{lp._databaseServer.saveEntityConfig}}</button>
          <div>{{lp._databaseServer.saveDatabaseConfigInfo}}</div>
        </div>
        <div class="item_button_area">
          <button class="" @click="reloadConfig($event)">{{lp._databaseServer.reloadEntityConfig}}</button>
          <div>{{lp._databaseServer.reloadDatabaseConfigInfo}}</div>
        </div>
      </div>

      <div v-for="db in enabledDatabase">

        <EntityEditor v-if="databaseType==='external'" :database="db" :database-type="databaseType" :entity="dataEntitys"></EntityEditor>
        <EntityEditor v-else :database="db" :address="db.url" :database-type="databaseType" :entity="dataEntitys"></EntityEditor>

      </div>
    </div>

  </div>



</template>

<script setup>
import {ref, computed} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {loadRuntimeConfig, saveConfig, getServers, getDataEntrys, saveConfigData, getConfigData} from "@/util/acrions";
import EntityEditor from '@/components/content/ServerDatabaseConfig/EntityEditor';

const externalDatabase = ref([]);
const innerDatabase = ref([]);
const dataEntitys = ref([]);
const databaseType = computed(()=>{
  const d = (externalDatabase.value) ? externalDatabase.value.filter((db)=>{ return db.enable}) : null;
  return (!d || !d.length) ? 'inner' : 'external';
});

const enabledDatabase = computed(()=>{
  const d = (externalDatabase.value) ? externalDatabase.value.filter((db)=>{ return db.enable}) : null;
  if (d && d.length) return d;
  return innerDatabase.value.filter((db)=>{return db.enable});
});



const saveDatabaseConfig = (e)=>{
  component.confirm("warn", e, lp._databaseServer.saveEntityConfig, {html: lp._databaseServer.saveEntityConfirm}, 500, 100, function(){
    const p = [];
    if (databaseType==='external'){
      p.push(loadRuntimeConfig('externalDataSources', true).then((data, idx)=>{
        data.forEach((d)=>{
          if (d.enable){
            d.includes = (externalDatabase.value[idx].includes.trim()) ? externalDatabase.value[idx].includes.split(/\s*[,\n\r]\s*/g) : [];
            d.excludes = (externalDatabase.value[idx].excludes.trim()) ? externalDatabase.value[idx].excludes.split(/\s*[,\n\r]\s*/g) : [];
          }
        });

        return saveConfigData('externalDataSources', data);
      }));
    }else {
      p.push(getConfigData('node').then((data) => {
        // const saveP = [];
        // if (data && data.length) {
        const saveP = data.map((d, idx) => {
          if (d.node.data.enable) {
            d.node.data.includes = (innerDatabase.value[idx].includes.trim()) ? innerDatabase.value[idx].includes.split(/\s*[,\n\r]\s*/g) : [];
            d.node.data.excludes = (innerDatabase.value[idx].excludes.trim()) ? innerDatabase.value[idx].excludes.split(/\s*[,\n\r]\s*/g) : [];
          }
          // saveP.push(saveConfig('node_' + d.nodeAddress, 'data', d.node.data));
          return saveConfig('node_' + d.nodeAddress, 'data', d.node.data);
        });
        // }
        return Promise.all(saveP);
      }));


      // p.push(getServers().then((data) => {
      //   const saveP = [];
      //   if (data.nodeList && data.nodeList.length) {
      //     data.nodeList.forEach((d, idx) => {
      //       if (d.node.data.enable) {
      //         d.node.data.includes = (innerDatabase.value[idx].includes.trim()) ? innerDatabase.value[idx].includes.split(/\s*[,\n\r]\s*/g) : [];
      //         d.node.data.excludes = (innerDatabase.value[idx].excludes.trim()) ? innerDatabase.value[idx].excludes.split(/\s*[,\n\r]\s*/g) : [];
      //       }
      //       saveP.push(saveConfig('node_' + d.nodeAddress, 'data', d.node.data));
      //     });
      //   }
      //   return Promise.all(saveP);
      // }));
    }
    Promise.all(p).then(()=>{
      component.notice(lp._databaseServer.saveEntityConfigSuccess, "success");
    });
    this.close();
  }, function(){
    this.close();
  }, null, component.content);
}

const reloadConfig = (e)=>{
  component.confirm("warn", e, lp._databaseServer.reloadEntityConfig, lp._databaseServer.reloadEntityConfirm, 500, 100, function(){
    load();
    this.close();
  }, function(){
    this.close();
  }, null, component.content);
}

const load = ()=>{
  loadRuntimeConfig('externalDataSources', true).then((data)=>{
    if (data && data.length){
      externalDatabase.value = data.map((d)=>{
        return {
          url: d.url,
          enable: d.enable,
          includes: d.includes.join('\n'),
          excludes: d.excludes.join('\n')
        };
      });
    }
  });

  getConfigData('node').then((data)=>{
    if (data && data.length){
      innerDatabase.value = data.map((d)=>{
        return {
          url: d.nodeAddress,
          tcpPort: d.node.data.tcpPort,
          enable: d.node.data.enable,
          includes: d.node.data.includes.join('\n'),
          excludes: d.node.data.excludes.join('\n')
        };
      });
    }
  });
  // getServers().then((data)=>{
  //   if (data.nodeList && data.nodeList.length){
  //     innerDatabase.value = data.nodeList.map((d)=>{
  //       return {
  //         url: d.nodeAddress,
  //         tcpPort: d.node.data.tcpPort,
  //         enable: d.node.data.enable,
  //         includes: d.node.data.includes.join('\n'),
  //         excludes: d.node.data.excludes.join('\n')
  //       };
  //     });
  //   }
  // });

  getDataEntrys().then((data)=>{
    dataEntitys.value = data.map((d)=>{
      return {
        label: d.name+'('+d.value+')',
        key: d.value
      }
    });
  });
}

// load();

defineExpose({load});

</script>

<style scoped>
.item_database_item{
  height: 60px;
  cursor: pointer;
  border-radius: 20px;
}
.item_database_item:hover{
  background-color: #f1f1f1;
}
.item_database_area{
  display: flex;
  align-items: center;
  padding: 10px;
}
.item_config_icon{
  width: 40px;
  height: 40px;
  text-align: center;
  line-height: 40px!important;
  border-radius: 40%;
  margin-right: 10px;
}
.item_database_action{
  line-height: 20px;
  padding: 0;
  width: 34px;
  height: 34px;
  opacity: 0;
  border-radius: 40%;
  transition: opacity 0.5s;
}
.item_database_area:hover .item_database_action{
  opacity: 1;
}
.item_label{
  width: 100px;
}
.item_input_area{
  width: 80%;
  position: relative;
}
.item_content_info{
  display: flex;
  align-items: center;
}
.item_content_db{
  padding: 10px;
  margin: 0 20px;
}
.item_bold{
  font-weight: bold;
  color: #333333;
}

.item_database_editorArea{
  display: none;
  padding: 20px;
}
.item_button_area{
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid #dddddd;
  margin: 5px 10px;
  border-radius: 15px;
  width: 300px;
  align-content: center;
}
.item_button_area div{
  padding: 5px;
}
.item_info_content{
  padding: 20px;
  font-size: 18px;
  color: #999999;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
</style>
