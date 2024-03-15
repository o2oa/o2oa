<template>
  <div>
    <div class="item_title">{{lp._systemInfo.moduleStatus}}</div>
    <div v-if="modules">
      <div class="item_info">
        <el-radio-group v-model="displayType">
          <el-radio-button :label="lp._systemInfo.byModule" />
          <el-radio-button :label="lp._systemInfo.byServer" />
        </el-radio-group>
      </div>

      <div class="item_info" v-if="displayType===lp._systemInfo.byModule">
        <el-collapse>
          <el-collapse-item v-for="key in Object.keys(modules)" :key="key">
            <template #title>
              <div class="item_server_item_slot">
                <div style="display: flex; align-items: center; line-height: 20px">
                  <div :class="{'o2icon-play': modules[key].status==='normal', 'o2icon-stop': modules[key].status==='timeout', 'mainColor_color': modules[key].status==='normal', 'grayColor_color': modules[key].status==='timeout'}"></div>
                  <div class="item_server_item_title">({{modules[key].nodes.length}}) {{modules[key].name}}</div>
                </div>
                <div class="item_server_item_title" style="line-height: 20px">{{modules[key].className}}</div>
              </div>
            </template>

            <div v-for="item in modules[key].nodes" :key="item.node">
              <BaseServerInfoItem :data="item" :item-lp="lp._systemInfo.moduleData"></BaseServerInfoItem>
            </div>

          </el-collapse-item>
        </el-collapse>
      </div>

      <div v-if="displayType===lp._systemInfo.byServer">
        <div class="item_info">

          <el-collapse>
            <el-collapse-item v-for="key in Object.keys(modulesByServer)" :key="key">
              <template #title>
                <div class="item_server_item_slot">
                  <div style="display: flex; align-items: center; line-height: 20px">
                    <div class="o2icon-play mainColor_color"></div>
                    <div class="item_server_item_title">{{lp._systemInfo.node}} {{modulesByServer[key].node}}:{{modulesByServer[key].port}}</div>
                  </div>
                  <div class="item_server_item_title" style="line-height: 20px">{{modulesByServer[key].proxyHost}}:{{modulesByServer[key].proxyPort}}</div>
                </div>
              </template>

              <div class="item_info">
                <el-table :data="modulesByServer[key].modules" style="width: 98%; margin-top: 10px" :border="true"
                          header-cell-class-name="el-table_head"
                          size="small"
                          table-layout="auto">

                  <el-table-column prop="name" :label="lp._systemInfo.moduleData.moduleName"></el-table-column>
                  <el-table-column :label="lp._systemInfo.moduleData.reportDate">
                    <template  #default="scope">
                      <div style="display: flex;align-items: center; min-width:100px">
                        <div :class="{'o2icon-play': scope.row.status==='normal', 'o2icon-stop': scope.row.status==='timeout', 'mainColor_color': scope.row.status==='normal', 'grayColor_color': scope.row.status==='timeout'}"></div>
                        <span style="margin-left: 10px">{{ scope.row.reportDate }}</span>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column prop="contextPath" :label="lp._systemInfo.moduleData.contextPath" />
                  <el-table-column prop="className" :label="lp._systemInfo.moduleData.className" />

                </el-table>
              </div>


            </el-collapse-item>
          </el-collapse>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import {getModules} from '@/util/acrions';
import BaseServerInfoItem from './BaseServerInfoItem.vue';

const modules = ref();
const modulesByServer = ref();
const displayType = ref(lp._systemInfo.byModule);

const load = async () => {
  getModules().then((data)=>{
    const modulesData = {};
    const modulesByServerData = {};
    Object.keys(data).forEach((key)=>{
      const m = data[key];
      const mData = {
        className: key,
        name: '',
        contextPath: '',
        status: 'normal',
        nodes: []
      }
      m.forEach((n, i)=>{
        if (i===0){
          mData.name = n.name;
          mData.contextPath = n.contextPath;
        }
        if (n.reportDate){
          const reportDuration = Date.parse(n.reportDate).diff(new Date(), 'second');
          if (reportDuration>60){
            mData.status = 'timeout';
            n.status = 'timeout';
          }else{
            n.status = 'normal';
          }
        }else{
          mData.status = 'timeout';
        }
        mData.nodes.push(n);

        if (!modulesByServerData[n.node]){
          //const {node, port} = n;
          modulesByServerData[n.node] = {
            "node": n.node,
            "port": n.port,
            "sslEnable": n.sslEnable,
            "proxyHost": n.proxyHost,
            "proxyPort": n.proxyPort
          };
          modulesByServerData[n.node].modules = [];
        }
        modulesByServerData[n.node].modules.push(n)
      })
      modulesData[key] = mData;
    });

    modules.value = modulesData;
    modulesByServer.value = modulesByServerData;
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
  justify-content: flex-start;
  width: 90%;
}
.el-collapse-item__header{
  width: 100%;
}
</style>
