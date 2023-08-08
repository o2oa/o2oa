<template>
  <div>
    <div class="item_title">{{lp._ssoConfig.oauthClientConfig}}</div>
    <div class="item_info">{{lp._ssoConfig.oauthClientConfigInfo}}</div>

    <div class="item_info">
      <div class="item_sso_item" >
        <div class="item_sso_area" @click="addOauthClientConfig">
          <div class="item_sso_icon o2icon-plus mainColor_bg"></div>
          <div class="item_sso_text" style="margin-left: 0; font-size: 16px; width:auto">{{lp._ssoConfig.addOauthClientConfig}}</div>
        </div>
      </div>
    </div>

    <div class="item_info">
      <div v-for="(oauth, index) in oauthClients" :key="oauth.clientId" class="item_sso">
        <div class="item_sso_item" @click="editOauthClientConfig(oauth, index)">
          <div class="item_sso_area">
            <div class="item_sso_icon o2icon-key mainColor_bg"></div>
            <div class="item_sso_text item_bold">{{oauth.clientId}}</div>
            <div class="item_sso_text" style="overflow: hidden;text-overflow: ellipsis;">{{oauth.clientSecret ? "*".repeat(oauth.clientSecret.length) : ""}}</div>
            <div class="item_sso_text" style="display: flex; align-items: center; justify-content: flex-end;">
              <el-switch
                  @click="(e)=>{e.stopPropagation()}"
                  @change="saveConfig('token', 'oauths', oauthClients)"
                  v-model="oauth.enable">
              </el-switch>
            </div>

            <div class="item_sso_text" style="display: flex; align-items: center; justify-content: flex-end;">
              <button class="o2icon-del mainColor_bg item_sso_action" @click="(e)=>{e.stopPropagation(); removeOauthClient(e, index)}"></button>
            </div>
          </div>
        </div>
      </div>
    </div>


    <div class="item_title">{{lp._ssoConfig.oauthServerConfig}}</div>
    <div class="item_info">{{lp._ssoConfig.oauthServerConfigInfo}}</div>

    <div class="item_info">
      <div class="item_sso_item" >
        <div class="item_sso_area" @click="addOauthServerConfig">
          <div class="item_sso_icon o2icon-plus mainColor_bg"></div>
          <div class="item_sso_text" style="margin-left: 0; font-size: 16px; width:auto">{{lp._ssoConfig.addOauthServerConfig}}</div>
        </div>
      </div>
    </div>

    <div class="item_info">
      <div v-for="(oauth, index) in oauthServers" :key="oauth.clientId" class="item_sso">
        <div class="item_sso_item" @click="editOauthServerConfig(oauth, index)">
          <div class="item_sso_area">
            <div class="item_sso_icon o2icon-key mainColor_bg"></div>
            <div class="item_sso_text item_bold">{{oauth.name}}</div>
            <div class="item_sso_text" style="overflow: hidden;text-overflow: ellipsis;">{{oauth.clientId}}</div>
            <div class="item_sso_text" style="display: flex; align-items: center; justify-content: flex-end;">
              <el-switch
                  @click="(e)=>{e.stopPropagation()}"
                  @change="saveConfig('token', 'oauthClients', oauthServers)"
                  v-model="oauth.enable">
              </el-switch>
            </div>

            <div class="item_sso_text" style="display: flex; align-items: center; justify-content: flex-end;">
              <button class="o2icon-del mainColor_bg item_sso_action" @click="(e)=>{e.stopPropagation(); removeOauthServer(e, index)}"></button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div ref="oauthClientEditorArea">
      <div v-if="!!currentOauthClientData" class="item_sso_editorArea">
        <BaseSwitch :label="lp._ssoConfig.isEnable" v-model:value="currentOauthClientData.enable"/>
        <BaseInput :label="lp._ssoConfig.oauth_clientId" v-model:value="currentOauthClientData.clientId"/>
<!--        <BaseInput :label="lp._ssoConfig.oauth_clientSecret" v-model:value="currentOauthClientData.clientSecret"/>-->
        <BaseInput :label="lp._ssoConfig.oauth_clientSecret" input-type="password" :show-password="true" v-model:value="currentOauthClientData.clientSecret"></BaseInput>
        <div style="color: #999999; margin-left: 120px">{{lp._ssoConfig.ssoConfigKeyInfo}}</div>
        <BaseMap ref="mapEditor" :label="lp._ssoConfig.oauth_mapping" v-model:value="currentOauthClientData.mapping"/>
      </div>
    </div>

    <div ref="oauthServerEditorArea">
      <div v-if="!!currentOauthServerData" class="item_sso_editorArea">
        <BaseSwitch :label="lp._ssoConfig.isEnable" v-model:value="currentOauthServerData.enable" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_name" v-model:value="currentOauthServerData.name" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_displayName" v-model:value="currentOauthServerData.displayName" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_icon" v-model:value="currentOauthServerData.icon" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_clientId" v-model:value="currentOauthServerData.clientId" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_clientSecret" v-model:value="currentOauthServerData.clientSecret" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_authAddress" v-model:value="currentOauthServerData.authAddress" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_authParameter" v-model:value="currentOauthServerData.authParameter" :label-style="{width:'100px'}"/>
        <BaseSelect :label="lp._ssoConfig.oauth_authMethod" v-model:value="currentOauthServerData.authMethod"  :label-style="{width:'100px'}" :options="{'GET': 'GET', 'POST':'POST'}"/>

        <BaseInput :label="lp._ssoConfig.oauth_tokenAddress" v-model:value="currentOauthServerData.tokenAddress" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_tokenParameter" v-model:value="currentOauthServerData.tokenParameter" :label-style="{width:'100px'}"/>
        <BaseSelect :label="lp._ssoConfig.oauth_tokenMethod" v-model:value="currentOauthServerData.tokenMethod"  :label-style="{width:'100px'}" :options="{'GET': 'GET', 'POST':'POST'}"/>
        <BaseSelect :label="lp._ssoConfig.oauth_tokenType" v-model:value="currentOauthServerData.tokenType"  :label-style="{width:'100px'}" :options="{'json': 'JSON', 'form':'FORM'}"/>

        <BaseInput :label="lp._ssoConfig.oauth_infoAddress" v-model:value="currentOauthServerData.infoAddress" :label-style="{width:'100px'}"/>
        <BaseInput :label="lp._ssoConfig.oauth_infoParameter" v-model:value="currentOauthServerData.infoParameter" :label-style="{width:'100px'}"/>
        <BaseSelect :label="lp._ssoConfig.oauth_infoMethod" v-model:value="currentOauthServerData.infoMethod"  :label-style="{width:'100px'}" :options="{'GET': 'GET', 'POST':'POST'}"/>
        <BaseSelect :label="lp._ssoConfig.oauth_infoType" v-model:value="currentOauthServerData.infoType"  :label-style="{width:'100px'}" :options="{'json': 'JSON', 'form':'FORM', 'other': 'OTHER'}"/>

        <div v-if="currentOauthServerData.infoType!=='json' && currentOauthServerData.infoType!=='form'">
          <div class="item_info" v-html="lp._ssoConfig.infoScriptTextInfo"></div>
          <div class="item_script">
            <label class="item_script_label">{{lp._ssoConfig.oauth_infoScriptText}}</label>
            <div class="item_script_area">
              <BaseScript ref="scriptEditor" :label="lp._ssoConfig.oauth_infoScriptText" v-model:value="currentOauthServerData.infoScriptText"/>
            </div>
          </div>
        </div>

        <BaseInput :label="lp._ssoConfig.oauth_infoCredentialField" v-model:value="currentOauthServerData.infoCredentialField" :label-style="{width:'100px'}"/>

        <BaseSelect :label="lp._ssoConfig.oauth_bindingField" v-model:value="currentOauthServerData.bindingField"  :label-style="{width:'100px'}"
                    :options="{'none': 'none', 'open1Id': 'open1Id', 'open2Id':'open2Id', 'open3Id':'open3Id', 'open4Id':'open4Id', 'open5Id':'open5Id'}"/>

      </div>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import BaseSwitch from '@/components/item/BaseSwitch.vue';
import BaseInput from '@/components/item/BaseInput.vue';
import BaseMap from '@/components/item/BaseMap.vue';
import BaseSelect from '@/components/item/BaseSelect.vue';
import BaseScript from '@/components/item/BaseScript.vue';



import {getConfigData, saveConfig} from '@/util/acrions';

const oauthClients = ref([]);
const oauthServers = ref([]);
const currentOauthServerData = ref();
const currentOauthClientData = ref();

const oauthClientEditorArea = ref();
const oauthServerEditorArea = ref();
const mapEditor = ref();
const scriptEditor = ref();

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
  currentSSOData.value = Object.clone(data);
  const container = component.content.getElement('.systemconfig');
  const content = ssoEditorArea.value;
  content.show();

  o2.DL.open({
    title: '编辑鉴权配置',
    container,
    maskNode: container,
    width: 580,
    height: 320,
    content,
    onQueryClose: () => {
      content.hide();
      content.inject(container);
    },
    buttonList: [{
      text: lp.operation.ok,
      type: 'ok',
      action: async (dlg) => {
        if (currentSSOData.value.client && currentSSOData.value.key){
          const sameName = (sso, i)=>{
            return (i!==idx && sso.client===currentSSOData.value.client);
          }
          if (ssos.value.some(sameName)) {
            const info = lp._ssoConfig.ssoSameNameError.replace('{name}', currentSSOData.value.client);
            component.notice(info, 'error', dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
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

const removeOauth = (e, idx, info, title, name, path, oauthData)=>{
  const text = lp._ssoConfig[info].replace(/{name}/, oauthClients.value[idx][name]);
  const item = e.currentTarget.getParent('.item_sso_item');
  item.addClass('item_sso_item_del');
  component.confirm("warn", e, lp._ssoConfig[title], text, 500, 170, function(){
    oauthData.value.splice(idx, 1);
    saveConfig('token', path, oauthData.value);
    this.close();
  }, function(){
    item.removeClass('item_sso_item_del');
    this.close();
  }, null, component.content);
}
const removeOauthClient = (e, idx)=>{
  removeOauth(e, idx, 'removeOauthConfig', 'removeOauthConfigTitle', 'clientId', 'oauths', oauthClients);
}
const removeOauthServer = (e, idx)=>{
  removeOauth(e, idx, 'removeOauthConfig', 'removeOauthConfigTitle', 'name', 'oauthClients', oauthServers);
}

const editOauthConfig = (idx, node, title, width, height, ok)=>{
  const container = component.content.getElement('.systemconfig');
  const content = node.value;
  content.show();

  o2.DL.open({
    title: lp._ssoConfig[title],
    container,
    maskNode: container,
    width,
    height,
    content,
    onQueryClose: () => {
      content.hide();
      content.inject(container);
      currentOauthClientData.value = null;
      currentOauthServerData.value = null;
      if (mapEditor.value) mapEditor.value.destroyEditor();
      if (scriptEditor.value) scriptEditor.value.destroyEditor();
    },
    buttonList: [{
      text: lp.operation.ok,
      type: 'ok',
      action: ok
    }, {
      text: lp.operation.cancel,
      type: 'cancel',
      action: (dlg) => {
        dlg.close()
      }
    }]
  })
}

const editOauthClientConfig = (data, idx)=>{
  currentOauthClientData.value = Object.clone(data);
  editOauthConfig(idx, oauthClientEditorArea, 'editOauthClientConfig', 680, 500, (dlg)=>{
    if (currentOauthClientData.value.clientId && currentOauthClientData.value.clientSecret){
      const sameName = (sso, i)=>{
        return (i!==idx && sso.clientId===currentOauthClientData.value.clientId);
      }
      if (oauthClients.value.some(sameName)){
        const info = lp._ssoConfig.oauthClientSameNameError.replace('{name}', currentOauthClientData.value.clientId);
        component.notice( info, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
      }else if (currentOauthClientData.value.clientSecret.length % 8 !== 0 ){
        const info = lp._ssoConfig.ssoKeyLengthError;
        component.notice( info, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
      }else{
        if (idx || idx===0){
          oauthClients.value[idx] = currentOauthClientData.value;
        }else{
          oauthClients.value.push(currentOauthClientData.value);
        }
        saveConfig('token', 'oauths', oauthClients.value);
        dlg.close();
      }
    }else{
      component.notice( lp._ssoConfig.oauthClientDataError, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
    }
  })
}
const addOauthClientConfig = ()=>{
  const data = {
    "enable": true,
    "clientId": "",
    "clientSecret": "",
    "mapping": {}
  }
  editOauthClientConfig(data);
}

const editOauthServerConfig = (data, idx)=>{
  currentOauthServerData.value = Object.clone(data);
  editOauthConfig(idx, oauthServerEditorArea, 'editOauthServerConfig', 800, 800, (dlg)=>{
    if (currentOauthServerData.value.clientId && currentOauthServerData.value.name){
      const sameName = (sso, i)=>{
        return (i!==idx && sso.name===currentOauthServerData.value.name);
      }
      if (oauthServers.value.some(sameName)){
        const info = lp._ssoConfig.oauthServerSameNameError.replace('{name}', currentOauthServerData.value.name);
        component.notice( info, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
      }else{
        if (idx || idx===0){
          oauthServers.value[idx] = currentOauthServerData.value;
        }else{
          oauthServers.value.push(currentOauthServerData.value);
        }
        saveConfig('token', 'oauthClients', oauthServers.value);
        dlg.close();
      }
    }else{
      component.notice( lp._ssoConfig.oauthServerDataError, 'error',  dlg.node, {x: 'left', y: 'top'}, {x: 10, y: 10});
    }
  })
  if (scriptEditor.value) scriptEditor.value.createEditor();
}
const addOauthServerConfig = ()=>{
  const data = {
    "enable": false,
    "name": "",
    "displayName": "",
    "icon": "",
    "clientId": "",
    "clientSecret": "",
    "authAddress": "",
    "authParameter": "client_id={$client_id}&redirect_uri={$redirect_uri}",
    "authMethod": "GET",
    "tokenAddress": "",
    "tokenParameter": "client_id={$client_id}&client_secret={$client_secret}&redirect_uri={$redirect_uri}&grant_type=authorization_code&code={$code}",
    "tokenMethod": "POST",
    "tokenType": "json",
    "infoAddress": "",
    "infoParameter": "access_token={$access_token}",
    "infoMethod": "GET",
    "infoType": "json",
    "infoCredentialField": "openId",
    "infoScriptText": "",
    "bindingEnable": false,
    "bindingField": "",
  }
  editOauthServerConfig(data);
}


const load = async () => {
  getConfigData('token').then((data)=>{
    oauthClients.value = data.oauths || [];
    oauthServers.value = data.oauthClients || [];
  });
}

load();
</script>

<style scoped>
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
  line-height: 40px;
  border-radius: 40%;
  margin-right: 10px;
}
.item_bold{
  font-weight: bold;
  color: #333333;
}
.item_sso_editorArea{
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
.item_script_area{
  padding: 0;
  font-size: 14px;
  margin-right: 20px;
  width: calc(100% - 80px);
}
.item_script{
  overflow: hidden;
  padding: 10px 30px;
  font-size: 14px;
  color: #666666;
  clear: both;
  display: flex;
  justify-content: flex-start;
}
.item_script_label{
  text-align: left;
  overflow: hidden;
  font-size: 14px;
  color: #333333;
  clear: both;
  display: block;
  float: left;
  width: 80px;
  height: 32px;
  line-height: 32px;
}

</style>
