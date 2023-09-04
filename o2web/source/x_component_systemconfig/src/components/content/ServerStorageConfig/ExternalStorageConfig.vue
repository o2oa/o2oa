<template>
  <div class="systemconfig_area">
    <div class="item_info" v-html="lp._storageServer.info"></div>
    <div class="item_info" v-html="lp._storageServer.info2"></div>

    <div v-if="storageType==='inner'" class="item_info" v-html="lp._storageServer.externalInnerInfo"></div>
    <div v-else class="item_info" v-html="lp._storageServer.externalExternalInfo"></div>

    <div v-if="storageData">
      <div class="item_title" v-html="lp._storageServer.enableExternal"></div>
      <div class="item_info" v-html="lp._storageServer.enableExternalInfo"></div>

      <div class="item_info" style="display: flex; align-items: center; justify-content: flex-start;">
        <div class="mainColor_color" v-if="storageData.enable">{{lp._systemInfo.enable}}</div>
        <div style="color: red" v-else>{{lp._systemInfo.stop}}</div>

        <button v-if="storageData.enable" style="margin-left: 30px" @click="disableExternal">{{lp._storageServer.disableExternal}}</button>
        <button class="mainColor_bg" v-else style="margin-left: 30px" @click="enableExternal">{{lp._storageServer.enableExternal}}</button>
      </div>


<!--      <BaseBoolean v-model:value="storageData.enable" @change="enableOrDisable"></BaseBoolean>-->


      <div class="item_title" v-html="lp._storageServer.externalStorageNode"></div>
      <div class="item_info">
        <div class="item_database_item" @click="addStorageNode">
          <div class="item_database_area">
            <div class="item_config_icon o2icon-plus mainColor_bg"></div>
            <div style="margin-left: 0; font-size: 16px; color: #666666">{{lp._storageServer.addStorageNode}}</div>
          </div>
        </div>
      </div>

      <div class="item_info" v-for="key in Object.keys(storageData.store)" :key="key">
        <div class="item_database_item" @click="editStorageNode(key)">
          <div class="item_database_area">
            <div class="o2icon-download item_config_icon mainColor_bg"></div>
            <div class="item_server_item_slot item_bold" style="min-width: 340px">{{storageData.store[key].protocol}} : {{key}} - {{storageData.store[key].host}}:{{storageData.store[key].port}}</div>
            <div style="display: flex; align-items: center; justify-content: flex-end; width: 100px">
              <button class="o2icon-del mainColor_bg item_database_action" @click="(e)=>{e.stopPropagation(); removeNode(e, key)}"></button>
            </div>
          </div>
        </div>
      </div>

      <div class="item_title" v-html="lp._storageServer.assignNode"></div>
      <div class="item_info" v-html="lp._storageServer.assignNodeInfo"></div>

      <div class="item_storage_module">
        <el-collapse :model-value="Object.keys(lp._storageServer.files)">
          <div class="item_info" v-for="type in Object.keys(lp._storageServer.files)" :key="type">


            <el-collapse-item :name="type">
              <template #title>
                <div class="item_server_item_slot">{{lp._storageServer.files[type]}}</div>
                <!--              <div class="systemconfig_item_info">-->
                <!--                <button class="mainColor_bg">添加存储节点</button>-->
                <!--              </div>-->
              </template>


              <div class="item_info">
                <el-table :data="storageData[type]" style="width: 100%" :empty-text="lp._storageServer.noStoreNode">
                  <el-table-column prop="store" :label="lp._storageServer.store">
                    <template #default="scope">
                      <el-select v-model="scope.row.store" size="default" popper-class="systemconfig" @change="saveStoreData(type)">
                        <el-option v-for="s in Object.keys(storageData.store)" :key="s" :value="s" :label="s"></el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column prop="prefix" :label="lp._storageServer.prefix">
                    <template #default="scope">
                      <el-input v-model="scope.row.prefix" @change="saveStoreData(type)"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column prop="deepPath" :label="lp._storageServer.deepPath">
                    <template #default="scope">
                      <el-switch v-model="scope.row.deepPath" @change="saveStoreData(type)"></el-switch>
                    </template>
                  </el-table-column>
                  <el-table-column prop="enable" :label="lp._storageServer.enable">
                    <template #default="scope">
                      <el-switch v-model="scope.row.enable" @change="saveStoreData(type)"></el-switch>
                    </template>
                  </el-table-column>

                  <el-table-column prop="enable" style="width: 100px">
                    <template #default="scope">
                      <div class="item_module_store_del o2icon-del" @click="deleteStore(type, scope)"></div>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="display: flex;justify-content: space-between;">
<!--                  <button class="mainColor_bg" style="margin-top: 10px; margin-left: 0" @click="saveStoreData(type)">{{lp._storageServer.saveStore}}</button>-->
                  <button style="margin-top: 10px; margin-left: 0" @click="addStore(type)">{{lp._storageServer.addStore}}</button>
                </div>

              </div>

            </el-collapse-item>

          </div>
        </el-collapse>
      </div>
    </div>

    <div class="item_storage_editorArea" ref="externalEditorArea">
      <BaseInput v-if="currentNodeData.key" :label="lp._storageServer.external.key" v-model:value="currentNodeData.key"/>
      <BaseInput :label="lp._storageServer.external.name" v-model:value="currentNodeData.name"/>
      <BaseSelect :label="lp._storageServer.external.protocol" v-model:value="currentNodeData.protocol" :options="lp._storageServer.external.protocolData" @change="(value)=>{switchProtocolInfo(value)}"/>
      <div v-if="protocolInfo" v-text="protocolInfo" class="item_info" style="padding-left:120px;"></div>
      <BaseInput :label="lp._storageServer.external.host" v-model:value="currentNodeData.host"/>
      <BaseInput :label="lp._storageServer.external.port" input-type="number" v-model:value="currentNodeData.port"/>
      <BaseInput :label="lp._storageServer.external.username" v-model:value="currentNodeData.username"/>
      <BaseInput :label="lp._storageServer.external.password" input-type="password" show-password v-model:value="currentNodeData.password"/>
    </div>

  </div>
</template>

<script setup>
import {lp, component, o2} from '@o2oa/component';
import {ref, computed} from 'vue';
import {loadRuntimeConfig, saveConfigData} from "@/util/acrions";
import BaseBoolean from "@/components/item/BaseBoolean";
import BaseInput from "@/components/item/BaseInput";
import BaseSelect from "@/components/item/BaseSelect";

const protocolInfo = ref();
const storageData = ref();
const externalEditorArea = ref();
const currentNodeData = ref({});
const initData = {
  "enable": false,
  "file": [],
  "processPlatform": [],
  "mind": [],
  "meeting": [],
  "calendar": [],
  "cms": [],
  "bbs": [],
  "teamwork": [],
  "structure": [],
  "im": [],
  "general": [],
  "custom": [],
  "store": {}
}

const storageType = computed(()=>{
  return (storageData.value && storageData.value.enable) ? 'external' : 'inner'
});

const switchProtocolInfo = (protocol)=>{
  protocolInfo.value = lp._storageServer.external.protocolDataInfo[protocol];
}

const enableExternal = (e)=>{
  const title = lp._storageServer.enableExternalTitle;
  const text = lp._storageServer.enableExternalConfirm;
  component.confirm("warn", e, title, {html: text}, 560, 230, (dlg)=>{
    storageData.value.enable = true;
    saveData();
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);
}
const disableExternal = (e)=>{
  const title = lp._storageServer.disableExternalTitle;
  const text = lp._storageServer.disableExternalConfirm;
  component.confirm("warn", e, title, {html: text}, 560, 230, (dlg)=>{
    storageData.value.enable = false;
    saveData();
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);
}
//
// const enableOrDisable = (v, node)=>{
//   const title = (v) ? lp._storageServer.enableExternalTitle : lp._storageServer.disableExternalTitle;
//   const text = (v) ? lp._storageServer.enableExternalConfirm : lp._storageServer.disableExternalConfirm;
//   component.confirm("warn", node.value.input, title, {html: text}, 560, 230, (dlg)=>{
//     //storageData.value.enable = v;
//     saveData();
//     dlg.close();
//   }, (dlg)=>{
//     storageData.value.enable = !v;
//     dlg.close();
//   }, null, component.content);
// }

const saveStoreData = (type)=>{
  const d = storageData.value[type].filter((s)=>{
    return !!s.store;
  });
  storageData.value[type] = d;
  saveData();
}
const saveData = async (notice) => {
  await saveConfigData('externalStorageSources', storageData.value);
  if (notice) component.notice(lp._storageServer.saveStorageSuccess, 'success');
}

const addStore = (type)=>{
  storageData.value[type].push({
    "store": "",
    "prefix": "",
    "enable": false,
    "deepPath": false
  });
  // saveStoreData(type);
}
const deleteStore = (type, scope)=>{
  storageData.value[type].splice(scope.$index,1)
}

const openEditDlg = (data, node, cb, width, height)=>{
  currentNodeData.value = Object.clone(data);
  const container = component.content.getElement('.systemconfig');
  const content = node.value;
  content.show();

  o2.DL.open({
    title: lp._storageServer.editStorageNode,
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

const editStorageNode = (key)=>{
  const data = (o2.typeOf(key)==='string') ? storageData.value.store[key] : key;

  if( data && data.protocol ){
    switchProtocolInfo(data.protocol);
  }

  openEditDlg(data, externalEditorArea, (dlg)=>{
    if (currentNodeData.value.hasOwnProperty('key') && !currentNodeData.value.key){
      // component.notice(lp._storageServer.inputStorageNodeKey, 'error');
      component.notice(lp._storageServer.inputStorageNodeKey, "error", externalEditorArea.value);
      currentNodeData.value.key = ' ';
    }else if (!currentNodeData.value.name){
      component.notice(lp._storageServer.inputStorageNodeName, 'error', externalEditorArea.value);

    }else{
      storageData.value.store[currentNodeData.value.key || key] = {
        protocol: currentNodeData.value.protocol,
        username: currentNodeData.value.username,
        password: currentNodeData.value.password,
        host: currentNodeData.value.host,
        port: currentNodeData.value.port,
        name: currentNodeData.value.name
      }
      saveData();
      dlg.close();
    }

  }, 740, 520);
}
const addStorageNode = ()=>{
  let n = 1;
  while (storageData.value.store['node'+n]){
    n++;
  }
  const data ={
    key: 'node'+n,
    protocol: 'ftp',
    username: '',
    password: '',
    host: '',
    port: 21,
    name: ''
  }
  editStorageNode(data);
}

const removeNode = (e, key)=>{
  const text = lp._storageServer.removeNodeConfig.replace(/{name}/g, key);
  const item = e.currentTarget.getParent('.item_database_item');
  item.addClass('item_database_item_del');
  component.confirm("warn", e, lp._storageServer.removeNodeConfigTitle, {html: text}, 560, 230, function(){
    delete storageData.value.store[key];
    saveData();
    this.close();
  }, function(){
    item.removeClass('item_database_item_del');
    this.close();
  }, null, component.content);
}


const load = ()=>{
  loadRuntimeConfig('externalStorageSources').then((data)=>{
    storageData.value = data;
    if (!data){
      storageData.value = initData;
    }
    if (!storageData.value.hasOwnProperty('enable')) storageData.value.enable = true;
    if (!storageData.value.store) storageData.value.store = {};
  });
}

load();

defineExpose({load});
</script>

<style scoped>
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
.item_bold{
  font-weight: bold;
  color: #333333;
}
.item_storage_editorArea{
  padding: 20px;
  display: none;
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
.item_database_item_del{
  background-color: #ffecec;
}
.item_server_item_slot {
  font-size: 16px;
  font-weight: bold;
  color: rgb(102, 102, 102);
}
.item_module_store_del{
  height: 30px;
  width: 30px;
  line-height: 30px;
  background-color: #f1f1f1;
  border-radius: 15px;
  cursor: pointer;
  text-align: center;
  color: #555555;
}
.item_module_store_del:hover{
  background-color: #e34141;
  color: #ffffff;
  transition: background-color 1s;
}

</style>
