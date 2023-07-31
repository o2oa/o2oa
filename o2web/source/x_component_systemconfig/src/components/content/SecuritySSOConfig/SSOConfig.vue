<template>
  <div>
    <div class="item_title">{{lp._ssoConfig.ssoConfig}}</div>
    <div class="item_info">{{lp._ssoConfig.ssoConfigInfo}}</div>
    <div class="item_info">{{lp._ssoConfig.ssoConfigInfo2}}</div>
    <div class="item_info">
      <div class="item_sso_item" >
        <div class="item_sso_area" @click="addSSOConfig">
          <div class="item_sso_icon o2icon-plus mainColor_bg"></div>
          <div class="item_sso_text" style="margin-left: 0; font-size: 16px">{{lp._ssoConfig.addSSOConfig}}</div>
        </div>
      </div>

<!--      <button style="margin-left:0" @click="addSSOConfig">添加鉴权配置</button>-->
    </div>

    <div class="item_info">
      <div v-for="(sso, index) in ssos" :key="sso.client" class="item_sso">
        <div class="item_sso_item" @click="editSSOConfig(sso, index)">
          <div class="item_sso_area">
            <div class="o2icon-key item_sso_icon mainColor_bg"></div>
            <div class="item_sso_text item_bold">{{sso.client}}</div>
            <div class="item_sso_text" style="overflow: hidden;text-overflow: ellipsis;">{{sso.key ? "*".repeat(sso.key.length) : ""}}</div>
            <div class="item_sso_text" style="display: flex; align-items: center; justify-content: flex-end;">
              <el-switch
                  @click="(e)=>{e.stopPropagation()}"
                  @change="saveConfig('token', 'ssos', ssos)"
                  v-model="sso.enable">
              </el-switch>
            </div>

            <div class="item_sso_text" style="display: flex; align-items: center; justify-content: flex-end;">
              <button class="o2icon-del mainColor_bg item_sso_action" @click="(e)=>{e.stopPropagation(); removeSSO(e, index)}"></button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="item_title">{{lp._ssoConfig.useSSOConfig}}</div>
    <div class="item_info">{{lp._ssoConfig.useSSOConfigInfo}}</div>
    <div class="item_info">{{lp._ssoConfig.useSSOConfigInfo1}}</div>
    <div class="item_info">{{lp._ssoConfig.useSSOConfigInfo2}}</div>
    <div class="item_info" style="line-height: 24px" v-html="lp._ssoConfig.useSSOConfigInfo3"></div>

    <div class="item_info" style="line-height: 24px" v-html="lp._ssoConfig.useSSOConfigInfo4"></div>

<!--    <div class="item_title">{{lp._ssoConfig.ssoTokenTools}}</div>-->
<!--    <div class="item_info" style="margin-top: 20px; padding-left: 20px">-->
<!--      <button>{{lp._ssoConfig.ssoTokenCode}}</button>-->
<!--      <button>{{lp._ssoConfig.ssoTokenCheck}}</button>-->
<!--    </div>-->

    <div class="item_sso_editorArea" ref="ssoEditorArea">
      <BaseSwitch :label="lp._ssoConfig.isEnable" v-model:value="currentSSOData.enable"/>
      <BaseInput :label="lp._ssoConfig.ssoConfigName" v-model:value="currentSSOData.client"/>
<!--      <BaseInput :label="lp._ssoConfig.ssoConfigKey" v-model:value="currentSSOData.key"/>-->
      <BaseInput :label="lp._ssoConfig.ssoConfigKey" input-type="password" :show-password="true" v-model:value="currentSSOData.key" v-if="showKeyInput"></BaseInput>
      <div style="color: #999999; margin-left: 120px">{{lp._ssoConfig.ssoConfigKeyInfo}}</div>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import BaseSwitch from '@/components/item/BaseSwitch.vue';
import BaseInput from '@/components/item/BaseInput.vue';

import {getConfigData, saveConfig} from '@/util/acrions';

const ssos = ref([]);
const currentSSOData = ref({});
const ssoEditorArea = ref();
const showKeyInput = ref(false);

const removeSSO = (e, idx)=>{
  const text = lp._ssoConfig.removeSSOConfig.replace(/{name}/, ssos.value[idx].client);
  const item = e.currentTarget.getParent('.item_sso_item');
  item.addClass('item_sso_item_del');
  component.confirm("warn", e, lp._ssoConfig.removeSSOConfigTitle, text, 500, 170, function(){
    ssos.value.splice(idx, 1);
    saveConfig('token', 'ssos', ssos.value);
    this.close();
  }, function(){
    item.removeClass('item_sso_item_del');
    this.close();
  }, null, component.content);


}
const editSSOConfig = (data, idx)=>{
  showKeyInput.value = true;
  currentSSOData.value = Object.clone(data);
  const container = component.content.getElement('.systemconfig');
  const content = ssoEditorArea.value;
  content.show();

  o2.DL.open({
    title: lp._ssoConfig.editSSOConfig,
    container,
    maskNode: container,
    width: 580,
    height: 320,
    content,
    onQueryClose: () => {
      content.hide();
      content.inject(container);
      showKeyInput.value = false;
    },
    buttonList: [{
      text: lp.operation.ok,
      type: 'ok',
      action: async (dlg) => {
        if (currentSSOData.value.client && currentSSOData.value.key){
          const sameName = (sso, i)=>{
            return (i!==idx && sso.client===currentSSOData.value.client);
          }
          if (ssos.value.some(sameName)){
            const info = lp._ssoConfig.ssoSameNameError.replace('{name}', currentSSOData.value.client);
            component.notice( info, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
          }else if (currentSSOData.value.key.length % 8 !== 0 ){
            const info = lp._ssoConfig.ssoKeyLengthError;
            component.notice( info, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
          }else{
            if (idx || idx===0){
              ssos.value[idx] = currentSSOData.value;
            }else{
              ssos.value.push(currentSSOData.value);
            }
            saveConfig('token', 'ssos', ssos.value);
            dlg.close();
          }
        }else{
          component.notice( lp._ssoConfig.ssoDataError, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
        }
      }
    }, {
      text: lp.operation.cancel,
      type: 'cancel',
      action: dlg => dlg.close()
    }]
  })
}
const addSSOConfig = ()=>{
  const data = {
    enable: true,
    client: '',
    key: ''
  }
  editSSOConfig(data);
}

const load = async () => {
  getConfigData('token').then((data)=>{
    ssos.value = data.ssos || [];
  });
}

load();
</script>

<style scoped>
.item_input_area{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
}
.item_sso{

}
.item_sso_item{
  height: 60px;
  cursor: pointer;
  border-radius: 20px;
}
.item_sso_item:hover{
  background-color: #f1f1f1;
}
.item_sso_item_del{
  background-color: #ffecec;
}
.item_sso_area{
  padding: 10px;
  display: inline-flex;
}
.item_sso_text{
  line-height: 40px;
  color: #666666;
  font-size: 14px;
  margin-left: 10px;
  width: 100px;
}
.item_sso_icon{
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
.item_sso_editorArea{
  display: none;
  padding: 20px;
}
.item_sso_action{
  line-height: 20px;
  padding: 0;
  width: 34px;
  height: 34px;
  opacity: 0;
  border-radius: 40%;
  transition: opacity 0.5s;
}
.item_sso_item:hover .item_sso_action{
  opacity: 1;
}
</style>
