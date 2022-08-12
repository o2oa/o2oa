<template>
  <div>
    <div class="item_title">{{lp._systemInfo.serverInfo}}</div>

    <div class="item_info" v-for="server in servers" :key="server.nodeAddress">
      <div class="item_server_item lightColor_bg" >
        <div class="item_server_area">
          <div class="item_server_run_status">
            <div class="item_server_icon o2icon-servers" :class="{'mainColor_bg': server.node.enable, 'grayColor_bg': !server.node.enable}"></div>
            <div class="item_server_text" style="font-size: 16px; width: 300px">{{lp._systemInfo.node}}: {{server.nodeAddress}}</div>
          </div>
          <div class="item_server_run_status">
            <div style="line-height: 40px!important;" :class="{'o2icon-play': server.node.enable, 'o2icon-stop': !server.node.enable, 'mainColor_color': server.node.enable, 'grayColor_color': !server.node.enable}"></div>
            <div class="item_server_text mainColor_color" style="font-size: 14px;">{{server.node.enable ? lp._systemInfo.running : lp._systemInfo.stop}}</div>
          </div>
        </div>
      </div>
      <div class="item_info" style="padding-top:0; padding-bottom: 20px">

        <el-collapse>
          <el-collapse-item>
            <template #title>
              <div class="item_server_item_slot">
                <div style="display: flex; align-items: center;">
                  <div :class="{'o2icon-play': server.node.center.enable, 'o2icon-stop': !server.node.center.enable, 'mainColor_color': server.node.center.enable, 'grayColor_color': !server.node.center.enable}"></div>
                  <div class="item_server_item_title">{{lp._systemInfo.centerServer}}:{{server.node.center.port}}</div>
                </div>
                <div>{{server.node.center.proxyHost}}:{{server.node.center.proxyPort ||server.node.center.port}}</div>
              </div>
            </template>
            <BaseServerInfoItem :data="server.node.center"></BaseServerInfoItem>
          </el-collapse-item>

          <el-collapse-item>
            <template #title>
              <div class="item_server_item_slot">
                <div style="display: flex; align-items: center;">
                  <div :class="{'o2icon-play': server.node.web.enable, 'o2icon-stop': !server.node.web.enable, 'mainColor_color': server.node.web.enable, 'grayColor_color': !server.node.web.enable}"></div>
                  <div class="item_server_item_title">{{lp._systemInfo.webServer}}:{{server.node.web.port}}</div>
                </div>
                <div>{{server.node.web.proxyHost}}:{{server.node.web.proxyPort || server.node.web.port}}</div>
              </div>
            </template>
            <BaseServerInfoItem :data="server.node.web"></BaseServerInfoItem>
          </el-collapse-item>

          <el-collapse-item>
            <template #title>
              <div class="item_server_item_slot">
                <div style="display: flex; align-items: center;">
                  <div :class="{'o2icon-play': server.node.application.enable, 'o2icon-stop': !server.node.application.enable, 'mainColor_color': server.node.application.enable, 'grayColor_color': !server.node.application.enable}"></div>
                  <div class="item_server_item_title">{{lp._systemInfo.appServer}}:{{server.node.application.port}}</div>
                </div>
                <div>{{server.node.application.proxyHost}}:{{server.node.application.proxyPort || server.node.application.port}}</div>
              </div>
            </template>
            <BaseServerInfoItem :data="server.node.application"></BaseServerInfoItem>
          </el-collapse-item>

          <el-collapse-item v-if="!externalData.length">
            <template #title>
              <div class="item_server_item_slot">
                <div style="display: flex; align-items: center;">
                  <div :class="{'o2icon-play': server.node.data.enable, 'o2icon-stop': !server.node.data.enable, 'mainColor_color': server.node.data.enable, 'grayColor_color': !server.node.data.enable}"></div>
                  <div class="item_server_item_title">{{lp._systemInfo.dataServer}}:{{server.node.data.tcpPort}}</div>
                </div>
              </div>
            </template>
            <BaseServerInfoItem :data="server.node.data"></BaseServerInfoItem>
          </el-collapse-item>

          <el-collapse-item v-if="!externalStorage || !externalStorage.length">
            <template #title>
              <div class="item_server_item_slot">
                <div style="display: flex; align-items: center;">
                  <div :class="{'o2icon-play': server.node.storage.enable, 'o2icon-stop': !server.node.storage.enable, 'mainColor_color': server.node.storage.enable, 'grayColor_color': !server.node.storage.enable}"></div>
                  <div class="item_server_item_title">{{lp._systemInfo.storageServer}}:{{server.node.storage.port}}</div>
                </div>
              </div>
            </template>
            <BaseServerInfoItem :data="server.node.storage" :item-lp="lp._systemInfo.storageData"></BaseServerInfoItem>

            <el-table v-if="server.node.storage.accounts && server.node.storage.accounts.length" :data="server.node.storage.accounts" style="width: 98%; margin-top: 10px" :border="true" header-cell-class-name="el-table_head" size="small">
              <el-table-column prop="username" :label="lp._systemInfo.storageAccounts.username" />
              <el-table-column prop="protocol" :label="lp._systemInfo.storageAccounts.protocol"/>
              <el-table-column prop="weight" :label="lp._systemInfo.storageAccounts.weight" />
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>


    <div v-if="externalData && externalData.length">
      <div class="item_info">
        <div class="item_server_item lightColor_bg">
          <div class="item_server_area">
            <div class="item_server_run_status">
              <div class="item_server_icon o2icon-database mainColor_bg"></div>
              <div class="item_server_text" style="font-size: 16px; ">{{lp._systemInfo.dataNode}}</div>
            </div>
          </div>
        </div>


        <div class="item_info" style="padding-top:0; padding-bottom: 20px">
          <el-collapse>
            <el-collapse-item v-for="server in externalData" :key="server.url">
              <template #title>
                <div class="item_server_item_slot">
                  <div style="display: flex; align-items: center;">
                    <div :class="{'o2icon-play': server.enable, 'o2icon-stop': !server.enable, 'mainColor_color': server.enable, 'grayColor_color': !server.enable}"></div>
                    <div class="item_server_item_title">{{lp._systemInfo.dataServer}}: {{(server.url) ? server.url.substring(5, server.url.indexOf('?')) : ''}}</div>
                  </div>
                </div>
              </template>

                <el-descriptions :column="1" size="small" border>
                  <el-descriptions-item width="80px" :label="lp._systemInfo.databaseUrl" label-class-name="item_descriptions_label" class-name="item_descriptions_content">
                    {{server.url}}
                  </el-descriptions-item>

                  <el-descriptions-item width="80px" :label="lp._systemInfo.serverData.includes" label-class-name="item_descriptions_label" class-name="item_descriptions_content">
                    {{server.includes}}
                  </el-descriptions-item>

                  <el-descriptions-item width="80px" :label="lp._systemInfo.serverData.excludes" label-class-name="item_descriptions_label" class-name="item_descriptions_content">
                    {{server.excludes}}
                  </el-descriptions-item>
                </el-descriptions>

            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </div>

    <div v-if="externalStorage && externalStorage.length">
      <div class="item_info">
        <div class="item_server_item lightColor_bg">
          <div class="item_server_area">
            <div class="item_server_run_status">
              <div class="item_server_icon o2icon-download mainColor_bg"></div>
              <div class="item_server_text" style="font-size: 16px; ">{{lp._systemInfo.storageNode}}</div>
            </div>
          </div>
        </div>

        <div class="item_info" style="padding-top:0; padding-bottom: 20px">
          <el-table :data="externalStorage" style="width: 98%; margin-top: 10px" :border="true"
                    header-cell-class-name="el-table_head"
                    size="small"
                    table-layout="auto"
                    :span-method="objectSpanMethod">

            <el-table-column prop="username" :label="lp._systemInfo.storageAccounts.username"></el-table-column>
            <el-table-column prop="protocol" :label="lp._systemInfo.storageAccounts.protocol" />
            <el-table-column :label="lp._systemInfo.storageAccounts.host">
              <template  #default="scope">
                <div style="display: flex;align-items: center; min-width:100px">
                  <div :class="{'o2icon-play': scope.row.enable, 'o2icon-stop': !scope.row.enable, 'mainColor_color': scope.row.enable, 'grayColor_color': !scope.row.enable}"></div>
                  <span style="margin-left: 10px">{{ scope.row.host }}</span>
                </div>
              </template>
            </el-table-column>>
            <el-table-column prop="port" :label="lp._systemInfo.storageAccounts.port" />

            <el-table-column prop="name" :label="lp._systemInfo.storageAccounts.name" />
            <el-table-column prop="prefix" :label="lp._systemInfo.storageAccounts.prefix" />
            <el-table-column prop="weight" :label="lp._systemInfo.storageAccounts.weight" />
            <el-table-column prop="deepPath" :label="lp._systemInfo.storageAccounts.deepPath" />
          </el-table>
        </div>
      </div>

    </div>


  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import BaseServerInfoItem from './BaseServerInfoItem.vue';
import {getServers, loadRuntimeConfig} from '@/util/acrions';

const servers = ref([]);
const externalStorage = ref([]);
const externalStorageType = ref();
const externalData = ref([]);

const objectSpanMethod = (spanProps)=>{
  return (spanProps.columnIndex === 0) ? [spanProps.row.span || 0, (spanProps.row.span) ? 1 : 0] : [1, 1];
}

const load = async () => {
  getServers().then((data)=>{
    servers.value = data.nodeList;
  });
  Promise.all([loadRuntimeConfig('externalStorageSources'), loadRuntimeConfig('externalStorageType')]).then((arr)=>{
    const storageData = arr[0];
    let storageType = arr[1];
    if (storageData){
      if (storageData.store){
        if (!storageType) storageType = storageData.store;
        delete storageData.store;
      }

      const storages = [];
      Object.keys(storageData).forEach((key)=>{
        storageData[key].forEach((d, i)=>{
          d.username = key;
          if (i===0) d.span = storageData[key].length;
          if (d.store && storageType && storageType[d.store] ){
            Object.assign(d, storageType[d.store])
          }
          storages.push(d);
        });
      });
      externalStorage.value = storages;
      externalStorageType.value = storageType;
    }
  });

  loadRuntimeConfig('externalDataSources').then((data)=>{
    externalData.value = data || [];
  });
}

load();

</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 30px;
  font-size: 14px;
  color: #666666;
  clear: both;
}
.item_server_item{
  height: 60px;
  cursor: pointer;
  border-radius: 20px;
  background-color: #f1f1f1;
}
.item_server_area{
  padding: 10px 20px;
  display: flex;
  border-bottom: 1px solid #ebeef5;
  justify-content: space-between;
}
.item_server_text{
  line-height: 40px;
  color: #666666;
  font-size: 14px;
  margin-left: 10px;
}
.item_server_icon{
  width: 40px;
  height: 40px;
  text-align: center;
  line-height: 40px!important;
  border-radius: 40%;
  margin-right: 10px;
}
.item_server_item_title{
  font-weight: bold;
  margin-left: 10px;
  margin-right: 10px;
}
.item_server_run_status{
  display: flex;
}
.item_server_item_slot{
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 90%;
}
</style>
