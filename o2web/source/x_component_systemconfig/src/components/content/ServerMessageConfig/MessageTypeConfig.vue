<template>
    <div v-if="consumersData" ref="componentNode">
      <div class="item_title">{{lp._messageConfig.messageTypeTitle}}</div>
      <div class="item_info">{{lp._messageConfig.messageTypeInfo}}</div>
      <div class="item_info" v-html="lp._messageConfig.consumerInfo2"></div>

      <div class="item_info">
        <div class="item_message_item" @click="addMessageType">
          <div class="item_message_area">
            <div class="item_config_icon o2icon-plus mainColor_bg"></div>
            <div style="margin-left: 0; font-size: 16px; color: #666666">{{lp._messageConfig.addMessageType}}</div>
          </div>
        </div>
      </div>

      <div class="item_message_module">
        <el-collapse :model-value="Object.keys(messageTypes)">
          <div class="item_info" v-for="type in Object.keys(messageTypes)" :key="type">
            <el-collapse-item v-if="type!=='custom'" :name="type">
              <template #title>
                <div style="display: flex; align-items: center; width: 100%;">
                  <div class="item_server_item_slot" style="width: 70%">{{messageTypes[type].description}}({{type}})</div>
                  <div v-if="type.startsWith('custom_') && type!=='custom_create'" class="item_module_store_del o2icon-del" @click="deleteTmpMessage($event, type)"></div>
                </div>
              </template>
              <div class="item_info" style="display: flex; align-items: center;">
                <div v-if="messageTypes[type].consumers.length" style="width: 100%">
                  <span v-for="i in messageTypes[type].consumers" :key="i">
                    <el-tag v-if="i.consumer" closable style="margin-right: 5px; margin-bottom: 5px;" @close="closeConsumer(type, i)" type="info">
                      {{i.consumer}}
                    </el-tag>
                    <el-tag v-else closable style="margin-right: 5px; margin-bottom: 5px; cursor: pointer" @close="closeConsumer(type, i)" @click="editConsumer(type, i)">
                      {{i.type}}
                    </el-tag>
                  </span>
                </div>
                <div v-else style="width: 100%">
                  <div class="item_no_consumer">{{lp._messageConfig.noConsumer}}</div>
                </div>
                <div style="min-width: 100px; display: flex; flex-direction: column; align-items: center; justify-content: space-around;">
                  <button class="item_msaage_button mainColor_bg" @click="selectConsumer(type)">{{lp._messageConfig.selectConsumer}}</button>
                  <button class="item_msaage_button mainColor_bg" @click="addTmpConsumer(type)">{{lp._messageConfig.addTmpConsumer}}</button>
                </div>
              </div>

            </el-collapse-item>
          </div>
        </el-collapse>
        <el-collapse :model-value="Object.keys(messageTypes.custom)">
          <div class="item_info" v-for="type in Object.keys(messageTypes.custom)" :key="type">
            <el-collapse-item v-if="type!=='custom'" :name="type">
              <template #title>
                <div style="display: flex; align-items: center; width: 100%;">
                  <div class="item_server_item_slot" style="width: 70%">{{messageTypes.custom[type].description}}(custom_{{type}})</div>
                  <div class="item_module_store_del o2icon-del" @click="deleteTmpMessage($event, type)"></div>
                </div>
              </template>
              <div class="item_info" style="display: flex; align-items: center;">
                <div v-if="messageTypes.custom[type].consumers.length" style="width: 100%">
                  <span v-for="i in messageTypes.custom[type].consumers" :key="i">
                    <el-tag v-if="i.consumer" closable style="margin-right: 5px; margin-bottom: 5px;" @close="closeConsumer(type, i, 'custom')" type="info">
                      {{i.consumer}}
                    </el-tag>
                    <el-tag v-else closable style="margin-right: 5px; margin-bottom: 5px; cursor: pointer" @close="closeConsumer(type, i, 'custom')" @click="editConsumer(type, i, 'custom')">
                      {{i.type}}
                    </el-tag>
                  </span>
                </div>
                <div v-else style="width: 100%">
                  <div class="item_no_consumer">{{lp._messageConfig.noConsumer}}</div>
                </div>
                <div style="min-width: 100px; display: flex; flex-direction: column; align-items: center; justify-content: space-around;">
                  <button class="item_msaage_button mainColor_bg" @click="selectConsumer(type, 'custom')">{{lp._messageConfig.selectConsumer}}</button>
                  <button class="item_msaage_button mainColor_bg" @click="addTmpConsumer(type, 'custom')">{{lp._messageConfig.addTmpConsumer}}</button>
                </div>
              </div>

            </el-collapse-item>
          </div>

          <div class="item_info">
            <div class="item_message_item" @click="addMessageType">
              <div class="item_message_area">
                <div class="item_config_icon o2icon-plus mainColor_bg"></div>
                <div style="margin-left: 0; font-size: 16px; color: #666666">{{lp._messageConfig.addMessageType}}</div>
              </div>
            </div>
          </div>

        </el-collapse>

      </div>



      <div class="item_consumer_editorArea" ref="consumerSelectArea">
        <el-checkbox-group v-model="selectedConsumer" size="small">
          <el-checkbox v-for="i in Object.keys(consumersData)" :key="i" :label="i" size="small" style="width: 80px"/>
        </el-checkbox-group>
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

        <div v-if="currentData.type==='hadoop'">
          <BaseInput label="fsDefaultFS" v-model:value="currentData.fsDefaultFS" :label-style="labelStyle"/>
          <BaseInput label="path" v-model:value="currentData.path" :label-style="labelStyle"/>
          <BaseInput label="username" v-model:value="currentData.username" :label-style="labelStyle"/>
        </div>

      </div>

      <div class="item_consumer_editorArea" ref="messageEditorArea">
        <BaseInput :label="lp._messageConfig.newMessageData.key" v-model:value="newMessageData.key" :label-style="labelStyle"/>
        <BaseInput :label="lp._messageConfig.newMessageData.description" v-model:value="newMessageData.description" :label-style="labelStyle"/>
      </div>

    </div>
</template>

<script setup>
import {lp, o2, component} from '@o2oa/component';
import {ref} from 'vue';
import {delConfig, saveConfig, saveConfigData} from '@/util/acrions';
import BaseSwitch from '@/components/item/BaseSwitch.vue';
import BaseSelect from '@/components/item/BaseSelect.vue';
import BaseInput from '@/components/item/BaseInput.vue';

const props = defineProps({
  message: Object
});

const componentNode = ref();

const messageTypes = ref({});
const consumersData = ref({});
const filterData = ref({});
const loaderData = ref({});

const consumerEditorArea = ref();
const consumerSelectArea = ref();
const messageEditorArea = ref();

const selectedConsumer = ref([]);
const currentData = ref({});
const newMessageData = ref({});
const newConsumerData = {
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
const labelStyle = {
  width: '130px'
}

const saveData = (type, key)=>{
  const path = key ? key+'.'+type : type;
  const value = key ? messageTypes.value[key][type] : messageTypes.value[type];
  saveConfig('messages', path, value);
}

const openEditDlg = (node, cb, title, width, height)=>{
  const container = component.content.getElement('.systemconfig');
  const content = node.value;
  content.show();

  o2.DL.open({
    title,
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

const selectConsumer = (type, key)=>{
  const consumers = [];
  const msgItem = key ? messageTypes.value[key][type] : messageTypes.value[type]
  msgItem.consumers.forEach((c)=>{
    if (c.consumer){
      consumers.push(c.consumer);
    }
  });
  selectedConsumer.value = consumers;
  openEditDlg(consumerSelectArea, (dlg)=>{
    msgItem.consumers = msgItem.consumers.filter((c)=>{
      return !c.consumer
    });
    selectedConsumer.value.forEach((c)=>{
      msgItem.consumers.push({
        consumer: c
      });
    });
    saveData(type, key);
    dlg.close();
  }, lp._messageConfig.selectConsumer, 540, 300);
}

const addTmpConsumer = (type, key)=>{
  currentData.value = Object.clone(newConsumerData);
  openEditDlg(consumerEditorArea, (dlg)=>{

    const d = {
      type: currentData.value.type,
      loader: currentData.value.loader,
      filter: currentData.value.filter,
      enable: true
    }
    if (lp._messageConfig.consumerData[currentData.value.type]){
      lp._messageConfig.consumerData[currentData.value.type].forEach((k)=>{
        d[k] = currentData.value[k];
      });
    }

    const msgItem = key ? messageTypes.value[key][type] : messageTypes.value[type]
    msgItem.consumers.push(d);
    saveData(type, key);

    dlg.close();
  }, lp._messageConfig.selectConsumer, 700, 500);
}

const editConsumer = (type, i, key)=>{
  currentData.value = Object.clone(i);
  openEditDlg(consumerEditorArea, (dlg)=>{

    const d = {
      type: currentData.value.type,
      loader: currentData.value.loader,
      filter: currentData.value.filter,
      enable: true
    }
    if (lp._messageConfig.consumerData[currentData.value.type]){
      lp._messageConfig.consumerData[currentData.value.type].forEach((k)=>{
        d[k] = currentData.value[k];
      });
    }

    const msgItem = key ? messageTypes.value[key][type] : messageTypes.value[type]
    const idx = msgItem.consumers.indexOf(i);
    msgItem.consumers[idx] = d;
    // messageTypes.value[type].consumers.push(d);
    saveData(type, key);

    dlg.close();
  }, lp._messageConfig.editConsumer, 700, 500);
}

const closeConsumer = (type, i, key)=>{
  const msgItem = key ? messageTypes.value[key][type] : messageTypes.value[type]

  msgItem.consumers = msgItem.consumers.filter((c)=>{
    return i !== c;
  })
  saveData(type, key);
}

const addMessageType = ()=>{
  newMessageData.value = {
    key: '',
    consumers: [],
    description: ''
  }
  openEditDlg(messageEditorArea, (dlg)=>{
    if (!newMessageData.value.key){
      component.notice(lp._messageConfig.inputMessageKey, 'error', dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
      return false
    }
    const k = newMessageData.value.key;
    if (messageTypes.value.custom[k]){
      component.notice(lp._messageConfig.hasMessageKey, 'error', dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
      return false
    }

    messageTypes.value.custom[k] = {
      consumers: [],
      description: newMessageData.value.description
    }

    const node = componentNode.value.getParent('.content');
    setTimeout(()=>{
      node.scrollTo(0, node.scrollHeight+100)
    }, 100)

    saveData(k, 'custom');

    dlg.close();
  }, lp._messageConfig.addMessageType, 600, 300);
}

const deleteTmpMessage = (e, type)=>{
  e.stopPropagation();
  const text = lp._messageConfig.deleteTypeInfo.replace('{name}', type)
  component.confirm("warn", e, lp._messageConfig.deleteTypeTitle, text, 350, 170, (dlg)=>{
    delete messageTypes.value.custom[type];
    delConfig('messages', 'custom.'+type);
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);

}

const load = ()=>{
  if (props.message){
    const d = {};
    Object.keys(props.message).forEach((k)=>{
      if (k!=='custom' && k!=='consumers' && k!=='filters' && k!=='loaders' && k!=='clean' && k.substring(0,3)!=='###'){
        d[k] = {};
        d[k].description = props.message[k].description;
        d[k].consumers = props.message[k].consumers.filter((c)=>{
          return c.enable || c.consumer;
        });
      }
    });
    d.custom = props.message.custom || {};

    messageTypes.value = d;
    consumersData.value = props.message.consumers;

    filterData.value = props.message.filters;
    loaderData.value = props.message.loaders;
  }
}
load();

</script>

<style scoped>
.item_message_item{
  height: 50px;
  cursor: pointer;
  border-radius: 15px;
  width: 350px;
  float: left;
}
.item_message_item:hover{
  background-color: #f1f1f1;
}
.item_message_area{
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
.item_no_consumer{
  text-align: center;
  padding: 10px 0;
  color: #999999;
  font-size: 12px;
}
.item_msaage_button{
  padding: 3px 15px;
  font-size: 12px;
  margin-left: 10px;
  margin-top: 5px;
}
.item_consumer_editorArea{
  padding: 20px;
  display: none;
}
.item_module_store_del{
  height: 24px;
  width: 24px;
  line-height: 24px;
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
