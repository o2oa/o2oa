<template>
  <div>
    <div>
      <div class="item_title">{{lp._passwordConfig.newPersonPassword}}</div>
      <div class="item_info">{{lp._passwordConfig.newPersonPasswordInfo}}</div>

      <BaseSelect :label="lp._passwordConfig.initialPassword" @change="changePasswordType" v-model:value="initialPasswordType" :options="lp._passwordConfig.initialPasswordTypeOptions"></BaseSelect>

      <div ref="passwordNode" class="item_hide">
        <div class="item_info">{{lp._passwordConfig.initialPasswordType.textInfo}}</div>
        <form>
          <BaseInput :label="lp._passwordConfig.initialPasswordText" input-type="password" :show-password="true" v-model:value="passwordText"></BaseInput>
          <div class="item_info" style="padding-left: 20px">
            <button class="mainColor_bg" style="width: 100px" @click="saveInitialPassword">{{lp.operation.ok}}</button>
          </div>
        </form>

      </div>

      <div ref="scriptNode" class="item_hide">
        <div class="item_info">{{lp._passwordConfig.initialPasswordType.scriptInfo}}</div>
        <BaseScript ref="scriptEditor" :label="lp._passwordConfig.passwordScript" v-model:value="passwordScript"></BaseScript>
        <div class="item_info">
          <button class="mainColor_bg" style="width: 100px" @click="saveInitialPassword">{{lp.operation.ok}}</button>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup>
import {lp, component} from '@o2oa/component';
import {ref, onMounted, nextTick, onUpdated} from 'vue';
import {getConfig, saveConfigData} from '@/util/acrions';
import BaseInput from '@/components/item/BaseInput.vue';
import BaseScript from '@/components/item/BaseScript.vue';
import BaseSelect from '@/components/item/BaseSelect.vue';
import { minify } from "terser";

const initialPasswordType = ref('');
const scriptNode = ref();
const passwordNode = ref();
const scriptEditor = ref();

const personData = ref();
const passwordScript = ref('');
const passwordText = ref('');

const checkInputNode = ()=>{
  console.log(initialPasswordType.value);
  scriptNode.value.hide();
  passwordNode.value.hide();
  if (initialPasswordType.value==='script') scriptNode.value.show();
  if (initialPasswordType.value==='text') passwordNode.value.show();
}
const changePasswordType = ()=>{
  checkInputNode();
  saveInitialPasswordConfig();
}

const transformCode = async (code) => {
  const r = await minify(`this.$pwd = function(){\r\n${code}\r\n}`);
  return r.code;
}
const saveInitialPasswordConfig = async (e) => {
  personData.value.extension.initialPasswordType = initialPasswordType.value;
  switch (initialPasswordType.value) {
    case 'text':
      personData.value.password = passwordText.value;
      personData.value.extension.password = '';
      break;
    case 'script':
      const code = await transformCode(passwordScript.value);
      personData.value.password = `(${code}; return this.$pwd(); )`;
      personData.value.extension.password = passwordScript.value
      break;
    default:
      personData.value.password = '(' + lp._passwordConfig.initialPasswordType[initialPasswordType.value + 'Script'] + ')';
      personData.value.extension.password = lp._passwordConfig.initialPasswordType[initialPasswordType.value + 'Script'];
  }
  getPasswordText(personData.value);

  await saveConfigData('person', personData.value);

}
const saveInitialPassword = async (e) => {
  saveInitialPasswordConfig()
  component.notice(lp._passwordConfig.saveSuccess, "success");
  if (e) e.preventDefault();
}

const getPasswordText = (data)=>{
  if (data.extension && data.extension.password){
    passwordScript.value = data.extension.password;
  }else{
    const password = data.password;
    if (password.startsWith('(')  && password.endsWith(')')){
      passwordScript.value = (password) ? password.substring(1, password.length-1) : lp._passwordConfig.initialPasswordType.mobileScript;
      passwordText.value = '000000';
    }else{
      passwordText.value = password || '000000';
      passwordScript.value = lp._passwordConfig.initialPasswordType.mobileScript;
    }
  }
}


getConfig('person').then((data)=>{
  personData.value = data;
  if (!personData.value.extension) personData.value.extension = {};
  if (personData.value.extension && personData.value.extension.initialPasswordType){
    initialPasswordType.value = personData.value.extension.initialPasswordType;
  }else{
    initialPasswordType.value = (data.password.startsWith('(')  && data.password.endsWith(')')) ? 'script' : 'text'
  }
  getPasswordText(data);
  scriptEditor.value.createEditor();
});

onUpdated(()=>{
  nextTick(()=>{
    checkInputNode();
  });
});
// onMounted()
</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 30px;
  font-size: 14px;
  color: #666666;
  clear: both;
}
.item_radio{
  overflow: hidden;
  padding: 3px 30px;
  font-size: 14px;
  color: #666666;
  clear: both;
  display: inline-flex;
}
.item_hide{
  display: none;
}
</style>
