<template>
<!--  <div class="systemconfig_item_title">{{lp._resource.componentResource}}</div>-->
  <div class="systemconfig_item_info" v-html="lp._resource.componentResourceInfo"></div>
  <div class="componentsArea">
    <div class="componentItem" v-for="(componentItem, index) in components" :key="componentItem.id">
      <div class="componentItemTitle" :title="componentItem.title">{{componentItem.title}}</div>
      <div class="componentItemName">{{componentItem.name.substring(componentItem.name.indexOf(".")+1, componentItem.name.length)}}</div>
      <div class="componentItemIcon">
        <BaseIcon :value="componentItem"/>
      </div>
      <div>
        <button class="grayColor_bg" @mouseover="highlighting" @mouseout="unhighlighting" v-if="componentItem.visible" @click="openApplication(componentItem)">{{lp._component.open}}</button>
        <button class="grayColor_bg" @mouseover="highlighting" @mouseout="unhighlighting" @click="()=>{editComponent(componentItem, index)}">{{lp._component.edit}}</button>
        <button class="grayColor_bg" @mouseover="highlighting" @mouseout="unhighlighting" v-if="componentItem.type!=='system'" @click="(e)=>{uninstallApplication(e, componentItem)}">{{lp._component.uninstall}}</button>
      </div>
    </div>

    <div class="componentItem" style="background-color: #fcfcfc" @mouseenter="(e)=>{e.target.addClass('deployOver')}" @mouseleave="(e)=>{e.target.removeClass('deployOver')}" @click="createComponent">
      <div class="componentItemTitle">{{lp._component.deploy}}</div>
      <div class="componentItemName"></div>
      <div class="componentItemDeploy" style="cursor: pointer">
        <div class="componentItemIconDeploy" style=""></div>
      </div>
    </div>

    <div class="componentEditorArea" ref="componentEditorNode">
      <div v-if="currentComponent">
        <BaseInput :label="lp._component.name" v-model:value="currentComponent.name"/>
        <BaseInput :label="lp._component.title" v-model:value="currentComponent.title"/>
        <BaseInput :label="lp._component.path" v-model:value="currentComponent.path" @change="changePath"/>
        <div class="editorPathInfo">{{lp._component.urlPathInfo}}</div>
        <BaseBoolean :label="lp._component.visible" v-model:value="currentComponent.visible"/>
        <BasePerson :label="lp._component.allowList" v-model:value="currentComponent.allowList"/>
        <BasePerson :label="lp._component.denyList" v-model:value="currentComponent.denyList"/>
        <BaseIcon :label="lp._component.icon" v-model:value="currentComponent"
                  :icon-style="{marginLeft:'10px'}"
                  :can-change="!!(currentComponent.path && currentComponent.path.startsWith('@url'))"
                  :upload-text="lp._component.selectIcon"
                  :clear-text="lp._component.clearIcon"/>
        <BaseUpload v-if="currentComponent.type!=='system' &&  (!currentComponent.path || !currentComponent.path.startsWith('@url')) && general.deployResourceEnable"
                    :label="lp._component.upload"
                    :warn="lp._component.uploadWarn"
                    :upload-files="currentComponent.componentFile"
                    accept=".zip"
                    @upload="uploadComponentFile"
                    @remove="removeComponentFile"/>
      </div>
    </div>
  </div>
</template>

<script setup>
import {component, lp, o2} from '@o2oa/component';
import {loadComponents, removeComponent, saveComponent, dispatchComponentFile, getConfigData} from '@/util/acrions';
import {ref, reactive} from "vue";
import BaseInput from '@/components/item/BaseInput.vue';
import BaseBoolean from '@/components/item/BaseBoolean.vue';
import BasePerson from '@/components/item/BasePerson.vue';
import BaseIcon from '@/components/item/BaseIcon.vue';
import BaseUpload from '@/components/item/BaseUpload.vue';

const components = ref([]);
const currentComponent = ref();
const componentEditorNode = ref();

loadComponents().then((data)=>{
  components.value = data;
});

const general = ref({});
getConfigData('general').then((data)=>{
  general.value = data;
});

function highlighting(e){
  e.currentTarget.addClass('mainColor_bg');
  e.currentTarget.removeClass('grayColor_bg');
}
function unhighlighting(e){
  e.currentTarget.removeClass('mainColor_bg');
  e.currentTarget.addClass('grayColor_bg');
}
function openApplication(cmpt){
  if (cmpt.visible) o2.api.page.openApplication(cmpt.path);
}
function uninstallApplication(e, cmpt){
  const text = lp._component.removeComponent.replace(/{name}/, cmpt.title);
  component.confirm("warn", e, lp._component.removeComponentTitle, text, 500, 170, function(){
    uninstallComponent(cmpt);
    this.close();
  }, function(){
    this.close();
  }, null, component.content);
}
async function uninstallComponent(cmpt) {
  await removeComponent(cmpt.id)
  component.notice(lp._component.removeComponentOk, "success");
  components.value.erase(cmpt);
}

async function editComponent(cmpt, index) {
  if (cmpt) currentComponent.value = Object.clone(cmpt);
  const container = component.content.getElement('.systemconfig');
  const content = componentEditorNode.value;
  content.show();

  const size = component.content.getSize();
  const width = Math.min(size.x * 0.9, 800);
  const height = Math.min(size.y * 0.9, 720);

  o2.DL.open({
    title: '编辑组件',
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
        if (currentComponent.value.name && currentComponent.value.title && currentComponent.value.path){
          if (currentComponent.value.componentFile && currentComponent.value.componentFile.length){
            await dispatchComponentFile(currentComponent.value.componentFile[0]);
            currentComponent.value.componentFile = null;
          }
          const d = await saveComponent(currentComponent.value);
          if (d.id) currentComponent.value.id = d.id;
          component.notice( lp._component.deploySuccess, "success");
          if (index || index===0){
            components.value[index] = currentComponent.value;
          }else{
            components.value.push(currentComponent.value);
          }
          dlg.close();
        }else{
          component.notice( lp._component.componentDataError, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
        }
      }
    }, {
      text: lp.operation.cancel,
      type: 'cancel',
      action: dlg => dlg.close()
    }]
  })
}
function createComponent(){
  const cmpt = {
    allowList: [],
    denyList: [],
    iconPath: "appicon.png",
    name: "",
    path: "",
    title: "",
    type: "custom",
    visible: true
  };
  // components.value.push(cmpt)
  currentComponent.value = cmpt;
  editComponent();
}

function uploadComponentFile(file){
  currentComponent.value.componentFile = [file];
}
function removeComponentFile(){
  currentComponent.value.componentFile = [];
}
function changePath(e){
  if (e.startsWith('@url')){
    removeComponentFile();
  }
}

</script>

<style scoped>
.componentsArea{
  padding: 20px;
  text-align: center;
}
.componentItem{
  width: 140px;
  height: 180px;
  padding: 10px;
  background-color: #f6f6f6;
  border-radius: 8px;
  margin: 5px;
  float: left;
  border: 1px solid #dddddd;
}
.componentItemTitle{
  height: 30px;
  line-height: 30px;
  font-weight: bold;
  font-size: 18px;
  word-break: keep-all;
  overflow: hidden;
}
.componentItemName{
  height: 20px;
  line-height: 20px;
  color: #999999;
  font-size: 14px;
}
.componentItemIcon{
  height: 70px;
  padding: 15px 0;
}

button{
  margin: auto 2px;
  width: 28px;
  height: 20px;
  border-radius: 20px;
  border: 1px solid #cccccc;
  font-size: 12px;
  cursor: pointer;
  padding: 2px 5px;
}
.componentItemDeploy{
  height: 90px;
  padding: 5px 0;
}
.componentItemIconDeploy{
  width: 90px;
  height: 90px;
  background-size: 60px 60px;
  background-position: center;
  background-repeat: no-repeat;
  border-radius: 10%;
  background-color: #f3f3f3;
  border: 2px solid #cccccc;
  margin: auto;
  background-image: url('../../../assets/add.png');
}
.deployOver{
  color: #347ddb;
}
.deployOver .componentItemIconDeploy{
  background-image: url('../../../assets/add_over.png');
  border: 2px solid #347ddb;
}
.componentEditorArea{
  display: none;
  padding: 20px;
}
.editorPathInfo{
  padding: 5px 20px;
  text-align: left;
  margin-left: 90px;
}
</style>
