<template>
    <div v-if="consumersData">
      <div class="item_title">{{lp._messageConfig.consumerInfoTitle}}</div>
      <div class="item_info">{{lp._messageConfig.consumerInfo}}</div>
      <div class="item_info" v-html="lp._messageConfig.consumerInfo2"></div>

      <div class="item_info">
        <div class="item_consumer_item" @click="addConsumer">
          <div class="item_consumer_area">
            <div class="item_config_icon o2icon-plus mainColor_bg"></div>
            <div style="margin-left: 0; font-size: 16px; color: #666666">{{lp._messageConfig.addConsumer}}</div>
          </div>
        </div>
      </div>

      <div class="item_info">
        <div class="item_consumer_item"  v-for="key in Object.keys(consumersData)" :key="key" @click="editConsumer(key)">
          <div class="item_consumer_area">
            <div class="o2icon-mq item_config_icon mainColor_bg"></div>
            <div class="item_server_item_slot item_bold" style="width: 300px"> {{key}} : {{lp._messageConfig.consumerTypes[consumersData[key].type]}}</div>
            <div v-if="!lp._messageConfig.consumerTypes[key]">
              <button class="o2icon-del grayColor_bg item_consumer_action" @click="(e)=>{e.stopPropagation(); removeConsumer(e, key)}"></button>
            </div>
          </div>
        </div>
      </div>



      <div class="item_consumer_editorArea" ref="consumerEditorArea">
        <BaseInput v-if="currentData.key" :label="lp._messageConfig.consumerLabel.key" v-model:value="currentData.key" :label-style="labelStyle"/>

        <BaseSelect :label="lp._messageConfig.consumerLabel.type" v-model:value="currentData.type" :options="lp._messageConfig.consumerTypes" :label-style="labelStyle"/>

        <BaseSelect :label="lp._messageConfig.consumerLabel.filter" v-model:value="currentData.filter" :arr="[{value: '', label: lp._messageConfig.none}].concat(Object.keys(filterData))" :label-style="labelStyle"/>
        <BaseSelect :label="lp._messageConfig.consumerLabel.loader" v-model:value="currentData.loader" :arr="[{value: '', label: lp._messageConfig.none}].concat(Object.keys(loaderData))" :label-style="labelStyle"/>

        <div v-if="currentData.type==='kafka'">
          <BaseInput label="bootstrapServers" v-model:value="currentData.bootstrapServers" :label-style="labelStyle"/>
          <BaseInput label="topic" v-model:value="currentData.topic" :label-style="labelStyle"/>
          <BaseInput label="securityProtocol" v-model:value="currentData.securityProtocol" :label-style="labelStyle"/>
          <BaseInput label="saslMechanism" v-model:value="currentData.saslMechanism" :label-style="labelStyle"/>
          <BaseInput label="username" v-model:value="currentData.username" :label-style="labelStyle"/>
          <BaseInput label="password" v-model:value="currentData.password" :label-style="labelStyle"/>
        </div>

        <div v-if="currentData.type==='activemq'">
          <BaseInput label="url" v-model:value="currentData.url" :label-style="labelStyle"/>
          <BaseInput label="queueName" v-model:value="currentData.queueName" :label-style="labelStyle"/>
          <BaseInput label="username" v-model:value="currentData.username" :label-style="labelStyle"/>
          <BaseInput label="password" v-model:value="currentData.password" :label-style="labelStyle"/>
        </div>

        <div v-if="currentData.type==='restful'">
          <BaseInput label="url" v-model:value="currentData.url" :label-style="labelStyle"/>
          <BaseInput label="method" v-model:value="currentData.method" :label-style="labelStyle"/>
          <BaseSwitch label="internal" v-model:value="currentData.internal" :label-style="labelStyle"/>
        </div>

        <div v-if="currentData.type==='mail'">
          <BaseInput label="host" v-model:value="currentData.host" :label-style="labelStyle"/>
          <BaseInput label="port" v-model:value="currentData.port" :label-style="labelStyle"/>
          <BaseSwitch label="sslEnable" v-model:value="currentData.sslEnable" :label-style="labelStyle"/>
          <BaseSwitch label="auth" v-model:value="currentData.auth" :label-style="labelStyle"/>
          <BaseSwitch :label="lp._messageConfig.consumerLabel.startTlsEnable" v-model:value="currentData.startTlsEnable" :label-style="labelStyle"/>
          <BaseInput label="from" v-model:value="currentData.from" :label-style="labelStyle"/>
          <BaseInput label="password" v-model:value="currentData.password" :label-style="labelStyle"/>
        </div>

        <div v-if="currentData.type==='jdbc'">
          <BaseInput label="driverClass" v-model:value="currentData.driverClass" :label-style="labelStyle"/>
          <BaseInput label="url" v-model:value="currentData.url" :label-style="labelStyle"/>
          <BaseInput label="catalog" v-model:value="currentData.catalog" :label-style="labelStyle"/>
          <BaseInput label="schema" v-model:value="currentData.schema" :label-style="labelStyle"/>
          <BaseInput label="table" v-model:value="currentData.table" :label-style="labelStyle"/>
          <BaseInput label="username" v-model:value="currentData.username" :label-style="labelStyle"/>
          <BaseInput label="password" v-model:value="currentData.password" :label-style="labelStyle"/>
        </div>

        <div v-if="currentData.type==='table'">
          <BaseInput label="table" v-model:value="currentData.table" :label-style="labelStyle"/>
        </div>

<!--        <div v-if="currentData.type==='hadoop'">-->
<!--          <BaseInput label="fsDefaultFS" v-model:value="currentData.fsDefaultFS" :label-style="labelStyle"/>-->
<!--          <BaseInput label="path" v-model:value="currentData.path" :label-style="labelStyle"/>-->
<!--          <BaseInput label="username" v-model:value="currentData.username" :label-style="labelStyle"/>-->
<!--        </div>-->

      </div>
    </div>
</template>

<script setup>
import {lp, o2, component} from '@o2oa/component';
import {ref} from 'vue';
import {delConfig, saveConfig} from '@/util/acrions';
import BaseSwitch from '@/components/item/BaseSwitch.vue';
import BaseSelect from '@/components/item/BaseSelect.vue';
import BaseInput from '@/components/item/BaseInput.vue';

const props = defineProps({
  message: Object
});

const consumerEditorArea = ref();
const consumersData = ref({});
const filterData = ref({});
const loaderData = ref({});
const currentData = ref({});

const labelStyle = {
  width: '130px'
}

const saveData = ()=>{
  saveConfig('messages', 'consumers', consumersData.value);
}

const openEditDlg = (data, node, cb, width, height)=>{
  currentData.value = Object.clone(data);
  const container = component.content.getElement('.systemconfig');
  const content = node.value;
  content.show();

  o2.DL.open({
    title: lp._messageConfig.editConsumer,
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

const editConsumer = (key)=>{
  const data = (o2.typeOf(key)==='string') ? consumersData.value[key] : key;
  openEditDlg(data, consumerEditorArea, (dlg)=>{
    if (data.hasOwnProperty('key') && !data.key){
      component.notice(lp._messageConfig.inputKey, 'error', dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
    }else if (data.hasOwnProperty('key') && consumersData.value[data.key]) {
      component.notice(lp._messageConfig.hasKey, 'error', dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
    }else{
      const id = (o2.typeOf(key)==='string') ? key : currentData.value.key;
      consumersData.value[id] = {
        type: currentData.value.type,
        loader: currentData.value.loader,
        filter: currentData.value.filter,
        enable: true
      }
      if (lp._messageConfig.consumerData[currentData.value.type]){
        lp._messageConfig.consumerData[currentData.value.type].forEach((k)=>{
          consumersData.value[id][k] = currentData.value[k];
        });
      }
      saveData();
      dlg.close();
    }
  }, 700, 500)
}
const addConsumer = ()=>{
  let n = 1;
  while (consumersData.value['consumer'+n]){
    n++;
  }

  const data = {
    "key": "consumer"+n,
    "type": "ws",
    "loader": "",
    "filter": "",
    "enable": true,

    "bootstrapServers": "",
    "topic": "o2oa",
    "securityProtocol": "SASL_PLAINTEXT",
    "saslMechanism": "PLAIN",
    "username": "",
    "password": "",

    "url": "",
    "queueName": "",

    "method": "get",
    "internal": true,

    "host": "",
    "port": 465.0,
    "sslEnable": true,
    "auth": true,
    "from": "admin@o2oa.net",

    "driverClass": "com.mysql.cj.jdbc.Driver",
    "catalog": "",
    "schema": "",
    "table": "",

    "fsDefaultFS": "hdfs://",
    "path": "",
  }

  editConsumer(data)
}

const removeConsumer = (e, key)=>{
  e.stopPropagation();
  const text = lp._messageConfig.deleteConsumerInfo.replace('{name}', key);
  const item = e.currentTarget.getParent('.item_consumer_item');
  item.addClass('item_consumer_item_del');
  component.confirm("warn", e, lp._messageConfig.deleteConsumerTitle, text, 350, 170, (dlg)=>{
    delete consumersData.value[key];
    delConfig('messages', 'consumers.'+key);
    dlg.close();
  }, (dlg)=>{
    item.removeClass('item_consumer_item_del');
    dlg.close();
  }, null, component.content);
}

const load = ()=>{
  if (props.message){
    if (props.message.consumers && Object.keys(props.message.consumers).length){
      consumersData.value = Object.clone(props.message.consumers);
    }
    filterData.value = props.message.filters;
    loaderData.value = props.message.loaders;
  }
}
load();

</script>

<style scoped>
.item_consumer_item{
  height: 50px;
  cursor: pointer;
  border-radius: 15px;
  width: 350px;
  float: left;
}
.item_consumer_item:hover{
  background-color: #f1f1f1;
}
.item_consumer_area{
  display: flex;
  align-items: center;
  padding: 10px;
}
.item_config_icon{
  width: 30px;
  min-width: 30px;
  height: 30px;
  text-align: center;
  line-height: 30px!important;
  border-radius: 40%;
  margin-right: 10px;
}
.item_bold{
  font-weight: bold;
  color: #333333;
}
.item_consumer_action{
  line-height: 30px;
  padding: 0;
  width: 30px;
  height: 30px;
  opacity: 0;
  border-radius: 40%;
  transition: opacity 0.5s;
}
.item_consumer_area:hover .item_consumer_action{
  opacity: 1;
}
.item_consumer_editorArea{
  padding: 20px;
  display: none;
}
.item_consumer_item_del{
  background-color: #ffecec;
}
</style>
