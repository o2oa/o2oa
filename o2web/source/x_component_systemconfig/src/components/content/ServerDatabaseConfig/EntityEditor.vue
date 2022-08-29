<template>
  <div class="item">
    <div class="item_info">
      <div class="item_database_item lightColor_bg">
        <div class="item_database_area">
          <div class="o2icon-database item_config_icon mainColor_bg"></div>
          <div v-if="databaseType==='external'" class="item_server_item_slot item_bold" style="min-width: 340px">{{database.url.substring(0, database.url.lastIndexOf('/'))}}</div>
          <div v-if="databaseType==='inner'" class="item_server_item_slot item_bold" style="min-width: 340px">{{address}}:{{database.tcpPort}}</div>
        </div>
      </div>

      <BaseInput :label="lp._databaseServer.includeEntity" input-type="textarea" v-model:value="database.includes"
                 :options="{rows: 3, spellcheck: false, style: 'word-break: break-all;'}"
                 :label-style="{width: '100px'}"/>
      <div class="item_info_action">
        <div>{{lp._databaseServer.includeEntityInfo}}</div>
        <div style="min-width: 80px"><button @click="selectEntityList(database, 'includes')">{{lp.select}}</button></div>
      </div>

      <BaseInput :label="lp._databaseServer.excludeEntity" input-type="textarea" v-model:value="database.excludes"
                 :options="{rows: 3, spellcheck: false, style: 'word-break: break-all;'}"
                 :label-style="{width: '100px'}"/>
      <div class="item_info_action">
        <div>{{lp._databaseServer.excludeEntityInfo}}</div>
        <div style="min-width: 80px"><button @click="selectEntityList(database, 'excludes')">{{lp.select}}</button></div>
      </div>
    </div>

    <div class="item_entity_area" ref="entitySelectArea">

      <el-transfer
          filterable
          :filter-method="filterMethod"
          :filter-placeholder="lp._databaseServer.findClass"
          :titles="[lp._databaseServer.entityList, lp._databaseServer.selectedEntityList]"
          :data="entity"
          :left-default-checked="selectedItems"
          @left-check-change="selectList"
      />

    </div>
  </div>
</template>

<script setup>
import {component, lp, o2} from '@o2oa/component';
import {ref, onUpdated} from 'vue';
import BaseInput from '@/components/item/BaseInput.vue';

const emit = defineEmits(['update:value', 'change']);

const includes = ref();
const excludes = ref();
const selectedItems = ref([]);
const entitySelectArea = ref();

const props = defineProps({
  database:  {
    type: Object,
    default: null
  },
  databaseType: String,
  address: String,
  entity: Array
});

const selectEntityList = (db, item)=>{
  openEditDlg(db[item].split(/\s*[,\n\r]\s*/g), entitySelectArea, (dlg)=>{
    db[item] = selectedItems.value.join('\n');
    dlg.close();
  }, 780, 660);
}
const openEditDlg = (data, node, cb, width, height)=>{
  selectedItems.value = data;
  const container = component.content;
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

const selectList = (selected, changed)=>{
  selectedItems.value = selected;
}
const unselectList = (selected, changed)=>{
  const i = selectedItems.value.indexOf(selected[0]);
  selectedItems.value.splice(i, 1);
}

const filterMethod = (query, item) => {
  return item.label.toLowerCase().includes(query.toLowerCase())
}

function changeValue(e){
  const v = (props.inputType==='number') ? e.toFloat() : e;
  emit('update:value', v);
  emit('change', v);
}

</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 30px 3px 30px;
  font-size: 14px;
  color: #666666;
  clear: both;
  align-items: center;
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
.item_info_action{
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  margin-left: 130px;
  margin-right: 30px;
  margin-top: 5px;
}

.el-transfer {
  --el-transfer-border-color: var(--el-border-color-lighter);
  --el-transfer-border-radius: 10px;
  --el-transfer-panel-width: 700px;
  --el-transfer-panel-header-height: 40px;
  --el-transfer-panel-header-bg-color: var(--el-fill-color-light);
  --el-transfer-panel-footer-height: 40px;
  --el-transfer-panel-body-height: 478px;
  --el-transfer-item-height: 30px;
  --el-transfer-filter-height: 32px;
}
.el-transfer>:nth-child(3){
  display: none!important;
}
.item_entity_area{
  display: none;
  padding: 20px 10px 10px 10px
}
</style>
