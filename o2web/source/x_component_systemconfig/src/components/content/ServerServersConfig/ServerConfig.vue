<template xmlns="">
  <div v-if="nodeData">
<!--    <button class="" @click="load">{{lp._serversConfig.reloadServerConfig}}</button>-->

    <div class="systemconfig_item_title">{{lp._serversConfig.sameConfig}}</div>
    <div class="systemconfig_item_info">{{lp._serversConfig.sameConfigInfo}}</div>
    <BaseBoolean v-model:value="sameConfig"></BaseBoolean>

    <div v-if="sameConfig">
      <div class="systemconfig_item_title">{{lp._serversConfig.serverConfig}}</div>
      <div class="systemconfig_item_info" v-html="lp._serversConfig.serverConfigInfo"></div>

      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.serverPortInfo}}</label>
        <div class="item_input_area">
          <BaseInput v-model:value="nodeData.application.port" input-type="number"/>
        </div>
      </div>

      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.serverProxyHost}}</label>
        <div class="item_input_area">
          <BaseInput v-model:value="nodeData.application.proxyHost"/>
        </div>
      </div>

      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.serverProxyPort}}</label>
        <div class="item_input_area">
          <BaseInput v-model:value="nodeData.application.proxyPort" input-type="number"/>
        </div>
      </div>

      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.httpProtocol}}</label>
        <div class="item_input_area">
          <BaseSelect v-model:value="nodeData.application.httpProtocol" :options="{http: 'http', https: 'https'}" />
        </div>
      </div>
    </div>

    <div v-else>
<!--      <el-form ref="formRef" :rules="rules" label-width="120px" label-position="left">-->

        <div class="systemconfig_item_info">
        <el-collapse :model-value="['center', 'application', 'web']">
          <el-collapse-item name="center">
            <template #title>
              <div class="item_server_item_slot">{{lp._systemInfo.centerServer}}</div>
              <div class="systemconfig_item_info" v-html="lp._serversConfig.serverConfigInfo"></div>
            </template>
            <div>
<!--              <div class="systemconfig_item_info">{{lp._serversConfig.serverConfigInfo}}</div>-->

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverPortInfo}}</label>
                <div class="item_input_area" :class="{'el-form-item is-error': !portCheck}">
                  <BaseInput v-model:value="nodeData.center.port" input-type="number" @change="checkPort"/>
                  <div class="el-form-item__error" v-if="!portCheck">{{lp._serversConfig.saveServerConfigPortError}}</div>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverProxyHost}}</label>
                <div class="item_input_area">
                  <BaseInput v-model:value="nodeData.center.proxyHost"/>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverProxyPort}}</label>
                <div class="item_input_area">
                  <BaseInput v-model:value="nodeData.center.proxyPort" input-type="number"/>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.httpProtocol}}</label>
                <div class="item_input_area">
                  <BaseSelect v-model:value="nodeData.center.httpProtocol" :options="{http: 'http', https: 'https'}" />
                </div>
              </div>
            </div>
          </el-collapse-item>

          <el-collapse-item name="application">
            <template #title>
              <div class="item_server_item_slot">{{lp._systemInfo.appServer}}</div>
              <div class="systemconfig_item_info" v-html="lp._serversConfig.serverConfigInfo"></div>
            </template>
            <div>
<!--              <div class="systemconfig_item_info">{{lp._serversConfig.serverConfigInfo}}</div>-->

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverPortInfo}}</label>
                <div class="item_input_area" :class="{'el-form-item is-error': !portCheck}">
                  <BaseInput v-model:value="nodeData.application.port" input-type="number" @change="checkPort"/>
                  <div class="el-form-item__error" v-if="!portCheck">{{lp._serversConfig.saveServerConfigPortError}}</div>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverProxyHost}}</label>
                <div class="item_input_area">
                  <BaseInput v-model:value="nodeData.application.proxyHost"/>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverProxyPort}}</label>
                <div class="item_input_area">
                  <BaseInput v-model:value="nodeData.application.proxyPort" input-type="number"/>
                </div>
              </div>
            </div>
          </el-collapse-item>

          <el-collapse-item name="web">
            <template #title>
              <div class="item_server_item_slot">{{lp._systemInfo.webServer}}</div>
              <div class="systemconfig_item_info" v-html="lp._serversConfig.serverConfigInfo"></div>
            </template>
            <div>
<!--              <div class="systemconfig_item_info">{{lp._serversConfig.serverConfigInfo}}</div>-->

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverPortInfo}}</label>
                <div class="item_input_area" :class="{'el-form-item is-error': !portCheck}">
                  <BaseInput v-model:value="nodeData.web.port" input-type="number" @change="checkPort"/>
                  <div class="el-form-item__error" v-if="!portCheck">{{lp._serversConfig.saveServerConfigPortError}}</div>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverProxyHost}}</label>
                <div class="item_input_area">
                  <BaseInput v-model:value="nodeData.web.proxyHost"/>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.serverProxyPort}}</label>
                <div class="item_input_area">
                  <BaseInput v-model:value="nodeData.web.proxyPort" input-type="number"/>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.proxyCenterEnable}}</label>
                <div class="item_input_area">
                  <BaseBoolean v-model:value="nodeData.web.proxyCenterEnable"/>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.proxyApplicationEnable}}</label>
                <div class="item_input_area">
                  <BaseBoolean v-model:value="nodeData.web.proxyApplicationEnable"/>
                </div>
              </div>

              <div class="item_info">
                <label class="item_label">{{lp._serversConfig.proxyTimeOut}}</label>
                <div class="item_input_area">
                  <BaseInput v-model:value="nodeData.web.proxyTimeOut" input-type="number"/>
                </div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

<!--      </el-form>-->
    </div>

    <div class="item_info" style="padding-left: 20px; display: flex;">
      <button class="mainColor_bg" @click="saveServerConfig">{{lp._serversConfig.saveServerConfig}}</button>
    </div>
    <el-divider />

    <div class="systemconfig_item_title">{{lp._serversConfig.dumpData}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.dumpDataInfo"></div>

    <div class="item_info" style="display: block">
      <BaseBoolean :label="lp._serversConfig.dumpEnable" v-model:value="dumpData.enable"/>
    </div>
    <div class="item_info" style="display: block">
      <BaseCron :label="lp._serversConfig.dumpCron" v-model:value="dumpData.cron"/>
    </div>
    <div class="item_info" style="display: block">
      <BaseInput :label="lp._serversConfig.dumpSize" v-model:value="dumpData.size" input-type="number"/>
    </div>
    <div class="item_info" style="display: block">
      <BaseInput :label="lp._serversConfig.dumpPath" v-model:value="dumpData.path"/>
    </div>
    <div class="item_info" style="padding-left: 20px; display: flex;">
      <button class="mainColor_bg" @click="saveDumpConfig">{{lp._serversConfig.saveDump}}</button>
    </div>

    <el-divider />

    <div class="systemconfig_item_title">{{lp._serversConfig.restoreData}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.restoreDataInfo"></div>

    <div class="item_info" style="display: block">
      <BaseBoolean :label="lp._serversConfig.restoreEnable" v-model:value="restoreData.enable"/>
    </div>
    <div class="item_info" style="display: block">
      <BaseCron :label="lp._serversConfig.restoreCron" v-model:value="restoreData.cron"/>
    </div>
    <div class="item_info" style="display: block">
      <BaseInput :label="lp._serversConfig.restorePath" v-model:value="restoreData.path"/>
    </div>
    <div class="item_info" style="padding-left: 20px; display: flex;">
      <button class="mainColor_bg" @click="saveRestoreConfig">{{lp._serversConfig.saveRestore}}</button>
    </div>

    <el-divider />


    <div class="systemconfig_item_title">{{lp._serversConfig.includes}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.includesInfo"></div>

    <BaseRadio :group-style="{flexDirection: 'row', alignItems: 'flex-start'}"
               :options="[
                   {label: 'all', value: 'all', text: lp._serversConfig.includesAll},
                   {label: 'select', value: 'select', text: lp._serversConfig.includesSelect}
                   ]"
               v-model:value="applicationIncludesType"
               @change="(v)=>{if (v==='all'){includes = []; includesCustom = ''; saveIncludes()}}"
    />

    <div v-if="applicationIncludesType==='select'">
      <div class="item_info" v-html="lp._serversConfig.includesInfo2"></div>
      <div class="item_info item_checkbox_group" style="margin-top: 10px;">
          <el-checkbox-group v-model="includes" size="large">
            <el-checkbox v-for="m in Object.keys(applicationModules)" :key="m" :label="m" style="border-color: #cccccc; overflow: hidden;margin-right: 20px; height: 45px;">

              <el-tag size="large" round :effect="(includes.includes(m)) ? 'light' : 'plain'" style="height: 40px">
                <div style="font-weight: 600; font-size: 14px;">{{applicationModules[m]}}</div>
                <div :title="m" style="margin-top: 3px; font-size: 12px; opacity: 0.5; width: 160px; overflow: hidden; text-overflow: ellipsis">{{m.substring(m.lastIndexOf('.')+1)}}</div>
              </el-tag>

<!--              <div style="font-weight: 600">{{applicationModules[m]}}</div>-->
<!--              <div :title="m" style="margin-top: 3px; font-size: 12px; opacity: 0.5; width: 160px; overflow: hidden; text-overflow: ellipsis">{{m.substring(m.lastIndexOf('.')+1)}}</div>-->
            </el-checkbox>
          </el-checkbox-group>
      </div>
      <div class="item_info" v-html="lp._serversConfig.includesInfo3"></div>
      <div class="item_info" style="display: flex">
        <el-input type="textarea" v-model="includesCustom"></el-input>
      </div>

      <div class="item_info" style="padding-left: 20px; display: flex;">
        <button class="mainColor_bg" @click="saveIncludes">{{lp._serversConfig.saveIncludes}}</button>
      </div>

    </div>

    <el-divider />

    <div class="systemconfig_item_title">{{lp._serversConfig.excludes}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.excludesInfo"></div>

    <BaseRadio :group-style="{flexDirection: 'row', alignItems: 'flex-start'}"
               :options="[
                   {label: 'none', value: 'none', text: lp._serversConfig.excludesNone},
                   {label: 'select', value: 'select', text: lp._serversConfig.excludesSelect}
                   ]"
               v-model:value="applicationExcludesType"
               @change="(v)=>{if (v==='none'){excludes = []; excludesCustom = ''; saveExcludes()}}"
    />

    <div v-if="applicationExcludesType==='select'">
      <div class="item_info" v-html="lp._serversConfig.excludesInfo2"></div>
      <div class="item_info item_checkbox_group" style="margin-top: 10px;">
        <el-checkbox-group v-model="excludes" size="large">
          <el-checkbox v-for="m in Object.keys(applicationModules)" :key="m" :label="m" style="border-color: #cccccc; overflow: hidden;margin-right: 20px; height: 45px;">

            <el-tag type="danger" size="large" round :effect="(excludes.includes(m)) ? 'light' : 'plain'" style="height: 40px">
              <div style="font-weight: 600; font-size: 14px;">{{applicationModules[m]}}</div>
              <div :title="m" style="margin-top: 3px; font-size: 12px; opacity: 0.5; width: 160px; overflow: hidden; text-overflow: ellipsis">{{m.substring(m.lastIndexOf('.')+1)}}</div>
            </el-tag>


          </el-checkbox>
        </el-checkbox-group>
      </div>
      <div class="item_info" v-html="lp._serversConfig.excludesInfo3"></div>
      <div class="item_info" style="display: flex">
        <el-input type="textarea" v-model="excludesCustom"></el-input>
      </div>

      <div class="item_info" style="padding-left: 20px; display: flex;">
        <button class="mainColor_bg" @click="saveExcludes">{{lp._serversConfig.saveExcludes}}</button>
      </div>

    </div>

    <el-divider />

    <div class="systemconfig_item_title">{{lp._serversConfig.sslConfig}}</div>
    <div class="systemconfig_item_info" v-html="lp._serversConfig.sslInfo"></div>

    <div class="item_info">
      <label class="item_label">{{lp._serversConfig.sslEnable}}</label>
      <div class="item_input_area" style="padding-left: 10px">
        <BaseBoolean v-model:value="sslEnable" />
      </div>
    </div>

    <div v-if="!!sslEnable">
      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.sslKeyStorePassword}}</label>
        <div class="item_input_area" style="padding-left: 10px">
          <BaseInput v-model:value="sslData.sslKeyStorePassword" input-type="password" show-password/>
        </div>
      </div>
      <div class="item_info">
        <label class="item_label">{{lp._serversConfig.sslKeyManagerPassword}}</label>
        <div class="item_input_area" style="padding-left: 10px">
          <BaseInput v-model:value="sslData.sslKeyManagerPassword" input-type="password" show-password/>
        </div>
      </div>
    </div>

    <div class="item_info" style="padding-left: 20px">
      <button class="mainColor_bg" @click="saveServerSSLConfig">{{lp._serversConfig.saveServerSSLConfig}}</button>
    </div>

  </div>
</template>

<script setup>
import {component, lp} from '@o2oa/component';
import {getConfigData, saveConfigData, getApplicationModules, saveConfig} from "@/util/acrions";
import {ref, reactive} from 'vue';
import BaseBoolean from "@/components/item/BaseBoolean";
import BaseInput from "@/components/item/BaseInput";
import BaseSelect from "@/components/item/BaseSelect";
import BaseRadio from "@/components/item/BaseRadio";
import BaseCron from "@/components/item/BaseCron";

const props = defineProps({
  server: Object,
  nodeName: String
});

const nodeData = ref(null);
const dumpData = ref({});
const restoreData = ref({});

const sameConfig = ref(false);
const sslEnable = ref(false);
const sslData = ref({});
const applicationModules = ref([]);
const applicationIncludesType = ref('all');
const applicationExcludesType = ref('none');

const includes = ref([]);
const includesCustom = ref('');

const excludes = ref([]);
const excludesCustom = ref('');

const formRef = ref();
const portCheck = ref(true);

const checkPort = ()=>{
  portCheck.value = true;
  if (!checkSameConfig(nodeData.value) && !checkDifferentPort(nodeData.value)){
    portCheck.value = false;
    return false;
  }
  return true;
}

const saveDumpConfig = async () => {
  await saveConfig(props.nodeName, 'dumpData', dumpData.value);
  component.notice(lp._serversConfig.saveDumpSuccess, "success");
}
const saveRestoreConfig = async () => {
  await saveConfig(props.nodeName, 'restoreData', restoreData.value);
  component.notice(lp._serversConfig.saveRestoreSuccess, "success");
}


const saveServerConfig = async () => {
  if (sameConfig.value) {
    Object.keys(nodeData.value.application).forEach((k)=>{
      if (k!=='includes' && k!=='excludes'){
        nodeData.value.center[k] = nodeData.value.application[k];
        nodeData.value.web[k] = nodeData.value.application[k];
      }
    });
  }else{
    if (!checkPort()){
      component.notice(lp._serversConfig.saveServerConfigPortError, "error");
      return false;
    }
  }

  await saveConfigData(props.nodeName, nodeData.value);
  component.notice(lp._serversConfig.saveServerConfigSuccess, "success");
  //await Promise.all([saveConfigData(props.nodeName, nodeData.value), saveConfigData('token', sslData.value)])
}

const saveIncludes = async () => {
  await getConfigData(props.nodeName, true).then((data) => {
    const customMobile = includesCustom.value.split(/\s*,\s*/g);
    customMobile.forEach((m)=>{
      if (m && !includes.value.includes(m)){
        includes.value.push(m)
      }
    });
    includes.value = includes.value.filter((v)=>{ return v && v.trim();})
    data.application.includes = includes.value;
    data.application.includes = includes.value;

    delete data.center.includes;
    delete data.web.includes;

    nodeData.value.application.includes = includes.value;
    return saveConfigData(props.nodeName, data);
  });
  component.notice(lp._serversConfig.saveServerIncludesSuccess, "success");
}
const saveExcludes = async () => {
  await getConfigData(props.nodeName, true).then((data) => {
    const customModule = excludesCustom.value.split(/\s*,\s*/g);
    customModule.forEach((m)=>{
      if (m && !includes.value.includes(m)){
        excludes.value.push(m)
      }
    });
    excludes.value = excludes.value.filter((v)=>{ return v && v.trim();})
    data.application.excludes = excludes.value;

    delete data.center.excludes;
    delete data.web.excludes;

    nodeData.value.application.excludes = excludes.value;
    return saveConfigData(props.nodeName, data);
  });
  component.notice(lp._serversConfig.saveServerExcludesSuccess, "success");
}



const saveServerSSLConfig = async () => {
  const saveServerPromise = getConfigData(props.nodeName, true).then((data)=>{
    data.center.sslEnable = sslEnable.value;
    data.application.sslEnable = sslEnable.value;
    data.web.sslEnable = sslEnable.value;

    nodeData.value.center.sslEnable = sslEnable.value;
    nodeData.value.application.sslEnable = sslEnable.value;
    nodeData.value.web.sslEnable = sslEnable.value;

    return saveConfigData(props.nodeName, data);
  });
  await Promise.all([saveServerPromise, saveConfigData('token', sslData.value)]);
  component.notice(lp._serversConfig.saveServerSSLConfigSuccess, "success");
}

const checkDifferentPort = (data)=>{
  const appPort = data.application.port || 80;
  const cenPort = data.center.port || 80;
  const webPort =data.web.port || 80;
  return (appPort!==cenPort && appPort!==webPort && cenPort!==webPort);
}

const checkSameConfig = (data)=>{
  const appPort = data.application.port || 80;
  const cenPort = data.center.port || 80;
  const webPort =data.web.port || 80;

  return (appPort===cenPort && appPort===webPort);
}
const load = ()=>{
  const nodePromise = getConfigData(props.nodeName, true).then((d)=>{
    const data = {
      application: Object.clone(d.application),
      web: Object.clone(d.web),
      center: Object.clone(d.center)
    }
    nodeData.value = data;
    dumpData.value = Object.clone(d.dumpData);
    restoreData.value = Object.clone(d.restoreData);

    if (!nodeData.value.web.hasOwnProperty('proxyCenterEnable')){
      nodeData.value.web.proxyCenterEnable = true;
    }
    if (!nodeData.value.web.hasOwnProperty('proxyApplicationEnable')){
      nodeData.value.web.proxyApplicationEnable = true;
    }
    if (!nodeData.value.web.hasOwnProperty('proxyTimeOut')){
      nodeData.value.web.proxyTimeOut = 300;
    }
    applicationIncludesType.value = (!nodeData.value.application.includes || !nodeData.value.application.includes.length) ? 'all': 'select';
    applicationExcludesType.value = (!nodeData.value.application.excludes || !nodeData.value.application.excludes.length) ? 'none': 'select';

    sameConfig.value = checkSameConfig(data);
    sslEnable.value = data.application.sslEnable;
    includes.value = data.application.includes;
    excludes.value = data.application.excludes;
    return data;
  });

  const modulePromise = getApplicationModules().then((modulesData)=>{
    const modules = {};
    modulesData.forEach((m)=>{
      modules[m.value] = m.name;
    });
    applicationModules.value = modules;
    return modulesData;
  });

  Promise.all([nodePromise, modulePromise]).then(()=>{
    const customModule = [];
    const eCustomModule = [];
    includes.value.forEach((m)=>{
      if (!applicationModules.value[m]){
        customModule.push(m)
      }
    });
    excludes.value.forEach((m)=>{
      if (!applicationModules.value[m]){
        eCustomModule.push(m)
      }
    });
    excludesCustom.value = eCustomModule.join(',');


    excludes
  });

  getConfigData("token").then((data)=>{
    sslData.value = {
      sslKeyStorePassword: data.sslKeyStorePassword,
      sslKeyManagerPassword: data.sslKeyManagerPassword
    };
  });

  // getApplicationModules().then((data)=>{
  //   const modules = {};
  //   const includeModules = {};
  //   const excludeModules = {};
  //   data.forEach((m)=>{
  //     modules[m.value] = m.name;
  //
  //   });
  //   applicationModules.value = modules;
  //   includeApplicationModules.value
  // });

}
load();

</script>

<style scoped>
.item_label{
  width: 100px;
  margin-bottom: 18px;
}
.item{
  padding: 0 30px;
}
.item_info{
  display: flex;
  align-items: center;
}
.item_input_area{
  width: 80%;
  margin-bottom: 18px;
  position: relative;
}
.item_server_item_slot {
  font-size: 16px;
  font-weight: bold;
  color: rgb(102, 102, 102);
}
.el-form-item{
  display: block;
  margin-bottom: 18px;
}
.el-form-item__error {
  top: 32px;
  left: 30px;
}
.item_checkbox_group label{
  margin: 0 8px 8px 0;
}


</style>
