<template>
  <div class="systemconfig_area" style="display:block;">
    <div class="item_title">{{lp._databaseServer.databaseSource}}</div>
    <div class="systemconfig_item_info" v-if="databaseType==='external'" v-html="lp._databaseServer.infoExternal"></div>
    <div class="systemconfig_item_info" v-else v-html="lp._databaseServer.infoInner"></div>

    <div class="systemconfig_item_info" v-html="lp._databaseServer.info"></div>
    <div class="systemconfig_item_info" v-html="lp._databaseServer.info2"></div>

    <div class="item_info" style="display: flex; justify-content: flex-start;">
      <div class="item_button_area">
        <button class="mainColor_bg" @click="saveDatabaseConfig($event)">{{lp._databaseServer.saveDatabaseConfig}}</button>
        <div>{{lp._databaseServer.saveDatabaseConfigInfo}}</div>
      </div>
      <div class="item_button_area">
        <button class="" @click="reloadConfig($event)">{{lp._databaseServer.reloadDatabaseConfig}}</button>
        <div>{{lp._databaseServer.reloadDatabaseConfigInfo}}</div>
      </div>
    </div>


    <div class="item_title">{{lp._databaseServer.externalDataSources}}</div>
    <div class="item_info">{{lp._databaseServer.externalDataSourcesInfo}}</div>

    <div class="item_info">
      <div class="item_database_item" @click="addExternalDatabase">
        <div class="item_database_area">
          <div class="item_config_icon o2icon-plus mainColor_bg"></div>
          <div style="margin-left: 0; font-size: 16px; color: #666666">{{lp._databaseServer.addDatabaseConfig}}</div>
        </div>
      </div>
    </div>


    <div class="item_info" v-for="(item, index) in externalDatabase" :key="item.url">
      <div class="item_database_item" @click="editExternalDatabase(item, index)">
        <div class="item_database_area">
          <div class="o2icon-database item_config_icon mainColor_bg"></div>
          <div class="item_server_item_slot item_bold" style="min-width: 340px">{{item.url.substring(0, item.url.lastIndexOf('/'))}}</div>
          <div class="systemconfig_item_info">
            <el-switch  @click="(e)=>{e.stopPropagation()}" @change="" v-model="item.enable"></el-switch>
          </div>
          <div style="display: flex; align-items: center; justify-content: flex-end; width: 100px">
            <button class="o2icon-del mainColor_bg item_database_action" @click="(e)=>{e.stopPropagation(); removeDatabase(e, index)}"></button>
          </div>
        </div>
      </div>
    </div>


    <div class="item_title">{{lp._databaseServer.innerDataSources}}</div>
    <div class="item_info">{{lp._databaseServer.innerDataSourcesInfo}}</div>

    <div class="item_info" v-for="(item, index) in servers" :key="item.url">
      <div class="item_database_item" @click="editInnerDatabase(item.node.data, index)">
        <div class="item_database_area">
          <div class="o2icon-database item_config_icon mainColor_bg"></div>
          <div class="item_server_item_slot item_bold" style="min-width: 340px">{{item.nodeAddress}}:{{item.node.data.tcpPort}}</div>
          <div class="systemconfig_item_info">
            <el-switch v-if="databaseType==='external'" disabled @click="(e)=>{e.stopPropagation()}" @change="" :value="false"></el-switch>
            <el-switch v-else @click="(e)=>{e.stopPropagation()}" @change="" v-model="item.node.data.enable"></el-switch>
          </div>
        </div>
      </div>
    </div>


    <div class="item_database_editorArea" ref="externalEditorArea">
      <BaseSwitch :label="lp._databaseServer.enable" v-model:value="currentDatabaseData.enable"/>
      <BaseInput :label="lp._databaseServer.databaseUrl" input-type="textarea" v-model:value="currentDatabaseData.url" :options="{rows: 3, spellcheck: false, style: 'word-break: break-all;'}"/>
      <BaseInput :label="lp._databaseServer.username" v-model:value="currentDatabaseData.username"/>
      <BaseInput :label="lp._databaseServer.password" input-type="password" show-password v-model:value="currentDatabaseData.password"/>
    </div>

    <div class="item_database_editorArea" ref="innerEditorArea">
      <BaseSwitch v-if="databaseType==='external'" :options="{disabled: true}" :label="lp._databaseServer.enable" :value="false" :label-style="{width: '120px'}"/>
      <BaseSwitch v-else :label="lp._databaseServer.enable" v-model:value="currentDatabaseData.enable" :label-style="{width: '120px'}"/>

      <BaseInput :label="lp._databaseServer.tcpPort" input-type="number" v-model:value="currentDatabaseData.tcpPort" :label-style="{width: '120px'}" />
      <div class="item_dlg_info">{{lp._databaseServer.tcpPortInfo}}</div>

      <BaseInput :label="lp._databaseServer.webPort" input-type="number" v-model:value="currentDatabaseData.webPort" :label-style="{width: '120px'}" />
      <div class="item_dlg_info">{{lp._databaseServer.webPortInfo}}</div>

      <BaseSwitch :label="lp._databaseServer.jmxEnable" v-model:value="currentDatabaseData.jmxEnable" :label-style="{width: '120px'}"/>
      <div class="item_dlg_info">{{lp._databaseServer.jmxEnableInfo}}</div>

      <BaseInput :label="lp._databaseServer.cacheSize" input-type="number" v-model:value="currentDatabaseData.cacheSize" :label-style="{width: '120px'}"/>
      <div class="item_dlg_info">{{lp._databaseServer.cacheSizeInfo}}</div>

      <BaseSelect :label="lp._databaseServer.logLevel" v-model:value="currentDatabaseData.logLevel" :options="{'FATAL':'FATAL', 'ERROR':'ERROR', 'WARN':'WARN','INFO':'INFO'}" :label-style="{width: '120px'}"/>

      <BaseInput :label="lp._databaseServer.maxTotal" input-type="number" v-model:value="currentDatabaseData.maxTotal" :label-style="{width: '120px'}"/>

      <BaseInput :label="lp._databaseServer.maxIdle" input-type="number" v-model:value="currentDatabaseData.maxIdle" :label-style="{width: '120px'}" />


      <BaseSwitch :label="lp._databaseServer.statEnable" v-model:value="currentDatabaseData.statEnable" :label-style="{width: '120px'}"/>

      <BaseInput :label="lp._databaseServer.statFilter" v-model:value="currentDatabaseData.statFilter" :label-style="{width: '120px'}" />

      <BaseInput :label="lp._databaseServer.slowSqlMillis" input-type="number" v-model:value="currentDatabaseData.slowSqlMillis" :label-style="{width: '120px'}"/>
      <div class="item_dlg_info">{{lp._databaseServer.slowSqlMillisInfo}}</div>

      <BaseInput :label="lp._databaseServer.lockTimeout" input-type="number" v-model:value="currentDatabaseData.lockTimeout" :label-style="{width: '120px'}"/>
    </div>

  </div>



</template>

<script setup>
import {ref, computed} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {loadRuntimeConfig, getConfigData, saveConfig, getServers, getDataEntrys, saveConfigData} from "@/util/acrions";
import BaseInput from '@/components/item/BaseInput.vue';
import BaseSwitch from '@/components/item/BaseSwitch.vue';
import BaseSelect from '@/components/item/BaseSelect.vue';


const externalDatabase = ref([]);
const servers = ref([]);
const dataEntitys = ref([]);
const databaseType = computed(()=>{
  const d = (externalDatabase.value) ? externalDatabase.value.filter((db)=>{ return db.enable}) : null;
  return (!d || !d.length) ? 'inner' : 'external';
});
const externalEditorArea = ref();
const innerEditorArea = ref();
const currentDatabaseData = ref({});

const removeDatabase = (e, idx)=>{
  const dbUrl = externalDatabase.value[idx].url;
  const text = lp._databaseServer.removeDatabaseConfig.replace(/{name}/, dbUrl.substring(0, dbUrl.lastIndexOf('/')));
  const item = e.currentTarget.getParent('.item_database_item');
  item.addClass('item_database_item_del');
  component.confirm("warn", e, lp._databaseServer.removeDatabaseConfigTitle, {html: text}, 560, 230, function(){
    externalDatabase.value.splice(idx, 1);
    // saveConfig('token', 'ssos', ssos.value);
    this.close();
  }, function(){
    item.removeClass('item_database_item_del');
    this.close();
  }, null, component.content);
}

const saveDatabaseConfig = (e)=>{
  component.confirm("warn", e, lp._databaseServer.saveEntityConfig, {html: lp._databaseServer.saveEntityConfirm}, 500, 100, function(){
    const p = [];

    p.push(saveConfigData('externalDataSources', externalDatabase.value, true));

    //if (databaseType==='external'){
    //   p.push(loadRuntimeConfig('externalDataSources', true).then((dbData, idx)=>{
    //     const data = dbData || [];
    //     data.forEach((d, idx)=>{
    //       Object.keys(d).forEach((k)=>{
    //         if (k!=='includes' && k!=='excludes'){
    //           d[k] = externalDatabase.value[idx][k];
    //         }
    //       });
    //     });
    //     return saveConfigData('externalDataSources', data, true);
    //   }));
    //}else {
      p.push(getServers().then((data) => {
        const saveP = [];
        if (data.nodeList && data.nodeList.length) {
          data.nodeList.forEach((d, idx) => {

            Object.keys(d.node.data).forEach((k)=>{
              if (k!=='includes' && k!=='excludes'){
                d.node.data[k] = servers.value[idx].node.data[k];
              }
            });
            saveP.push(saveConfig('node_' + d.nodeAddress, 'data', d.node.data));
          });
        }
        return Promise.all(saveP);
      }));
    //}
    Promise.all(p).then(()=>{
      component.notice(lp._databaseServer.saveDatabaseConfigSuccess, "success");
    });
    this.close();
  }, function(){
    this.close();
  }, null, component.content);


  // component.confirm("warn", e, lp._databaseServer.saveDatabaseConfig, {html: lp._databaseServer.saveDatabaseConfirm}, 500, 100, function(){
  //   const p = [saveConfigData('externalDataSources', externalDatabase.value)];
  //   servers.value.forEach((s)=>{
  //     p.push(saveConfig('node_'+s.nodeAddress, 'data', s.node.data));
  //   });
  //   Promise.all(p).then(()=>{
  //     component.notice(lp._databaseServer.saveDatabaseConfigSuccess, "success");
  //   });
  //   this.close();
  // }, function(){
  //   this.close();
  // }, null, component.content);
}

const reloadConfig = (e)=>{
  component.confirm("warn", e, lp._databaseServer.reloadDatabaseConfig, lp._databaseServer.reloadDatabaseConfirm, 500, 100, function(){
    load();
    this.close();
  }, function(){
    this.close();
  }, null, component.content);
}

const addExternalDatabase = ()=>{
  const data ={
    url:'jdbc:mysql://127.0.0.1:3306/X?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8',
    username : '',
    password :'',
    includes: [],
    excludes: [],
    enable : true
  }
  editExternalDatabase(data);
}
const openEditDlg = (data, node, cb, width, height)=>{
  currentDatabaseData.value = Object.clone(data);
  const container = component.content.getElement('.systemconfig');
  const content = node.value;
  content.show();

  o2.DL.open({
    title: lp._databaseServer.editDatabase,
    container,
    maskNode: container,
    width,
    height,
    content,
    onQueryClose: () => {
      content.hide();
      content.inject(container);
    },
    buttonList: [{
      text: lp.operation.ok,
      type: 'ok',
      action: async (dlg) => {
        cb(dlg);
      }
    }, {
      text: lp.operation.cancel,
      type: 'cancel',
      action: dlg => dlg.close()
    }]
  })
}
const editExternalDatabase = (data, idx)=>{
  openEditDlg(data, externalEditorArea, (dlg)=>{
    if (currentDatabaseData.value.url){
      if (idx || idx===0){
        externalDatabase.value[idx] = currentDatabaseData.value;
      }else{
        externalDatabase.value.push(currentDatabaseData.value);
      }
      dlg.close();
    }else{
      component.notice( lp._databaseServer.inputDatabaseUrl, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
    }
  }, 740, 400);
}

const editInnerDatabase = (data, idx)=>{
  openEditDlg(data, innerEditorArea, (dlg)=>{
    servers.value[idx].node.data = currentDatabaseData.value;
    dlg.close();
  },740, 660);
}

const load = ()=>{
  loadRuntimeConfig('externalDataSources', true).then((data)=>{
    externalDatabase.value = data || [];
  });
  getServers().then((data)=>{
    servers.value = data.nodeList;
  });
}

load();
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
.item_database_item_del{
  background-color: #ffecec;
}
.item_database_editorArea{
  display: none;
  padding: 20px;
}
.item_button_area{
  padding: 15px;
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
</style>
