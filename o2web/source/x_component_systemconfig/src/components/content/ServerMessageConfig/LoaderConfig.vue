<template>
    <div v-if="loaders" ref="componentNode">
      <div class="item_title">{{lp._messageConfig.loaderConfigTitle}}</div>
      <div class="item_info">{{lp._messageConfig.loaderConfigInfo}}</div>
      <div class="item_info" v-html="lp._messageConfig.consumerInfo2"></div>

      <div class="item_info">
        <div class="item_message_item" @click="addLoader">
          <div class="item_message_area">
            <div class="item_config_icon o2icon-plus mainColor_bg"></div>
            <div style="margin-left: 0; font-size: 16px; color: #666666">{{lp._messageConfig.addLoader}}</div>
          </div>
        </div>
      </div>

      <div class="item_message_module">
        <el-collapse>
          <div class="item_info" v-for="type in Object.keys(loaders)" :key="type">
            <el-collapse-item :name="type">
              <template #title>
                <div style="display: flex; align-items: center; width: 100%;">
                  <div class="item_server_item_slot" style="width:80%">{{type}}</div>
                  <div class="item_module_store_del o2icon-del" @click="deleteLoader($event, type)"></div>
                </div>
              </template>
              <div class="item_info">
                <BaseScript :value="loaders[type]" :inputType="{type: 'service'}" @blur="(v)=>{loaders[type]=v; saveLoader();}" @save="(v)=>{loaders[type]=v; saveLoader();}"></BaseScript>
              </div>

            </el-collapse-item>

          </div>
        </el-collapse>
      </div>

      <div class="item_consumer_editorArea" ref="loaderEditorArea">
        <BaseInput :label="lp._messageConfig.loaderKey" v-model:value="newLoaderData.key"/>
      </div>

    </div>
</template>

<script setup>
import {lp, o2, component} from '@o2oa/component';
import {ref} from 'vue';
import {delConfig, saveConfig} from '@/util/acrions';
import BaseScript from '@/components/item/BaseScript.vue';
import BaseInput from '@/components/item/BaseInput.vue';

const props = defineProps({
  message: Object
});

const loaderEditorArea = ref();
const newLoaderData = ref({});
const loaders = ref({});

const saveLoader = ()=>{
  saveConfig('messages', 'loaders', loaders.value);
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
const addLoader = ()=>{
  newLoaderData.value={key: ''};
  openEditDlg(loaderEditorArea, (dlg)=>{
    if (!newLoaderData.value.key){
      component.notice(lp._messageConfig.inputLoaderKey, 'error', dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
      return false
    }
    const k = newLoaderData.value.key;
    if (loaders.value.hasOwnProperty(k)){
      component.notice(lp._messageConfig.hasLoaderKey, 'error', dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
      return false
    }
    loaders.value[k] = lp._messageConfig.loaderComment;
    saveLoader();
    dlg.close()
  }, lp._messageConfig.addLoader, 500, 200);
}

const deleteLoader = (e, key)=>{
  e.stopPropagation();
  const text = lp._messageConfig.deleteLoaderInfo.replace('{name}', key)
  component.confirm("warn", e, lp._messageConfig.deleteLoaderTitle, text, 350, 170, (dlg)=>{
    delete loaders.value[key];
    delConfig('messages', 'loaders.'+key);
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);
}

const load = ()=>{
  if (props.message){
    loaders.value = props.message.loaders || {};
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
