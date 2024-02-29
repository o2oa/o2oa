<template>
  <div>
    <div>
      <div class="item_title">{{lp._passwordConfig.newPersonPassword}}</div>
      <div class="item_info">{{lp._passwordConfig.newPersonPasswordInfo}}</div>

      <BaseSelect :label="lp._passwordConfig.initialPassword" :label-style="{width: '100px'}" @change="changePasswordType" v-model:value="initialPasswordType" :options="lp._passwordConfig.initialPasswordTypeOptions"></BaseSelect>

      <div ref="passwordNode" class="item_hide">
        <div class="item_info">{{lp._passwordConfig.initialPasswordType.textInfo}}</div>
        <form>
          <BaseInput :label="lp._passwordConfig.initialPasswordText" :label-style="{width: '100px'}" input-type="password" :show-password="true" v-model:value="passwordText"></BaseInput>
          <div class="item_info" style="padding-left: 20px">
            <button class="mainColor_bg" style="width: 100px" @click="saveInitialPassword($event, 'text')">{{lp.operation.ok}}</button>
          </div>
        </form>

      </div>

      <div ref="scriptNode" class="item_hide">
        <div class="item_info">{{lp._passwordConfig.initialPasswordType.scriptInfo}}</div>
        <BaseScript ref="scriptEditor" :label="lp._passwordConfig.passwordScript" :value="passwordScript" @blur="(v)=>{passwordScript = v}"  @save="(v)=>{passwordScript = v}"></BaseScript>
        <div class="item_info">
          <button class="mainColor_bg" style="width: 100px" @click="saveInitialPassword($event, 'script')">{{lp.operation.ok}}</button>
        </div>
      </div>
    </div>

    <div>
      <BaseItem :title="lp._passwordConfig.passwordPeriod"
                :info="lp._passwordConfig.passwordPeriodInfo"
                :config="passwordPeriod"
                :allowEditor="true"
                type="number"
                @changeConfig="(value)=>{passwordPeriod=value.toInt(); saveConfig('person', 'passwordPeriod', value.toInt())}"
      />
    </div>

    <div>
      <div class="item_title">{{lp._passwordConfig.passwordRegex}}</div>
      <div class="item_info">{{lp._passwordConfig.passwordRegexInfo}}</div>
      <div class="item">
        <label class="item_label">{{lp._passwordConfig.passwordRegexLength}}</label>
        <div class="item_input_area">
          <el-slider ref="passwordLengthNode" class="item_slider" tooltip-class="systemconfig" v-model="passwordLength" range :max="30" :min="1" @input="recordPasswordLength" @change="savePasswordRuleConfig"></el-slider>
        </div>
      </div>
      <div class="item">
        <label class="item_label">{{lp._passwordConfig.passwordRule}}</label>
        <div class="item_input_area">
          <el-checkbox @change="savePasswordRuleConfig" v-model="passwordRuleValues.useLowercase">{{lp._passwordConfig.passwordRuleValue.useLowercase}}</el-checkbox><br/>
          <el-checkbox @change="savePasswordRuleConfig" v-model="passwordRuleValues.useNumber">{{lp._passwordConfig.passwordRuleValue.useNumber}}</el-checkbox><br/>
          <el-checkbox @change="savePasswordRuleConfig" v-model="passwordRuleValues.useUppercase">{{lp._passwordConfig.passwordRuleValue.useUppercase}}</el-checkbox><br/>
          <el-checkbox @change="savePasswordRuleConfig" v-model="passwordRuleValues.useSpecial">{{lp._passwordConfig.passwordRuleValue.useSpecial}}</el-checkbox>
        </div>
      </div>

<!--      <button style="margin-left: 30px; margin-top: 10px" class="mainColor_bg" @click="savePasswordRuleConfig">{{lp._passwordConfig.savePasswordRule}}</button>-->
    </div>

    <div>
      <div class="item_title">{{lp._passwordConfig.passwordRsa}}</div>
      <div class="item_info">{{lp._passwordConfig.passwordRsaInfo}}</div>
      <div class="item">
        <label class="item_label">{{lp._passwordConfig.passwordRsa}}</label>
        <div class="item_input_area">
          <el-switch
              @change="saveRsaEnableConfig"
              v-model="rsaEnable"
              :active-text="lp.operation.enable"
              :inactive-text="lp.operation.disable">
          </el-switch>
        </div>
      </div>
    </div>

    <div>
      <div class="item_title">{{lp._passwordConfig.passwordCheck}}</div>
      <div class="item_info">{{lp._passwordConfig.passwordCheckInfo}}</div>
      <div class="item">
        <label class="item_label">{{lp._passwordConfig.passwordCheck}}</label>
        <div class="item_input_area">
          <el-switch
              @change="saveFirstLoginModifyPwdConfig"
              v-model="firstLoginModifyPwd"
              :active-text="lp.operation.enable"
              :inactive-text="lp.operation.disable">
          </el-switch>
        </div>
      </div>
    </div>

<!--    <div>-->
<!--      <div class="item_title">{{lp._passwordConfig.tokenEncryptType}}</div>-->
<!--      <div class="item_info" v-html="lp._passwordConfig.tokenEncryptTypeInfo"></div>-->

<!--      <BaseSelect :label="lp._passwordConfig.tokenEncryptTypeLabel"-->
<!--                  v-model:value="encryptType"-->
<!--                  :options="lp._passwordConfig.encryptTypeOptions"></BaseSelect>-->
<!--      <button class="mainColor_bg" @click="changeEncryptType" style="margin-left: 120px; margin-top: 10px">{{lp._passwordConfig.tokenEncryptTypeButton}}</button>-->
<!--      <div class="item_info" v-html="lp._passwordConfig.tokenEncryptTypeInfo3"></div>-->
<!--    </div>-->

  </div>
</template>

<script setup>
import {lp, component} from '@o2oa/component';
import {ref, nextTick, onUpdated} from 'vue';
import {getConfigData, saveConfigData, getConfig, saveConfig} from '@/util/acrions';
import BaseInput from '@/components/item/BaseInput.vue';
import BaseScript from '@/components/item/BaseScript.vue';
import BaseSelect from '@/components/item/BaseSelect.vue';
import BaseItem from '@/components/item/BaseItem.vue';
import { minify } from "terser";

const initialPasswordType = ref('');
const scriptNode = ref();
const passwordNode = ref();
const scriptEditor = ref();
const passwordLengthNode = ref();

const personData = ref();
const passwordScript = ref('');
const passwordText = ref('');
const passwordPeriod = ref(0);
const passwordLength = ref([6,30]);
const passwordRuleValues = ref({
  useLowercase: true,
  useNumber: true,
  useUppercase: false,
  useSpecial: false
});
const rsaEnable = ref(false);
const firstLoginModifyPwd = ref(false);
const encryptType = ref('');


const recordPasswordLength = (e)=>{
  const node = passwordLengthNode.value.$el;
  const sliderNodes = node.querySelectorAll('.el-slider__button');
  e.forEach((v, i)=>{
    sliderNodes[i].set('text', v);
  })
}

const checkInputNode = (type)=>{
  scriptNode.value.hide();
  passwordNode.value.hide();

  const t = type || initialPasswordType.value;
  if (t==='script') scriptNode.value.show();
  if (t==='text') passwordNode.value.show();
}
const changePasswordType = (type)=>{
  checkInputNode(type);
  saveInitialPasswordConfig(type);
}

const transformCode = async (code) => {
  const r = await minify(`this.$pwd = function(){\r\n${code}\r\n}`);
  return r.code;
}

const saveRsaEnableConfig = async ()=>{
  await saveConfig('token', 'rsaEnable', rsaEnable.value);
}

const saveFirstLoginModifyPwdConfig = async ()=>{
  personData.value.firstLoginModifyPwd = firstLoginModifyPwd.value;
  await saveConfigData('person', personData.value);
}

const savePasswordRuleConfig = async ()=>{
  personData.value.extension.passwordRuleValues = passwordRuleValues.value;
  personData.value.extension.passwordLength = passwordLength.value;

  const regexs = [];
  const regexRules = [];
  if (passwordRuleValues.value.useLowercase){
    regexs.push(lp._passwordConfig.passwordRuleRegex.useLowercase);
    regexRules.push(lp._passwordConfig.passwordRuleValue.useLowercase);
  }
  if (passwordRuleValues.value.useNumber){
    regexs.push(lp._passwordConfig.passwordRuleRegex.useNumber);
    regexRules.push(lp._passwordConfig.passwordRuleValue.useNumber);
  }
  if (passwordRuleValues.value.useUppercase){
    regexs.push(lp._passwordConfig.passwordRuleRegex.useUppercase);
    regexRules.push(lp._passwordConfig.passwordRuleValue.useUppercase);
  }
  if (passwordRuleValues.value.useSpecial){
    regexs.push(lp._passwordConfig.passwordRuleRegex.useSpecial);
    regexRules.push(lp._passwordConfig.passwordRuleValue.useSpecial);
  }
  const regexStr = `^${regexs.join('')}.{${passwordLength.value[0]},${passwordLength.value[1]}}$`;
  const regexText = lp._passwordConfig.passwordLengthText
      .replace('{n}', passwordLength.value[0]+'-'+passwordLength.value[1])
      .replace('{text}', regexRules.join(', '))

  personData.value.passwordRegex = regexStr;
  personData.value.passwordRegexHint = regexText;

  await saveConfigData('person', personData.value);
}

const saveInitialPasswordConfig = async (type) => {
  debugger;
  initialPasswordType.value = type;
  personData.value.extension.initialPasswordType = type || initialPasswordType.value;
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
const saveInitialPassword = async (e, type) => {
  saveInitialPasswordConfig(type)
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

// const changeEncryptType = (e)=>{
//   const html = lp._passwordConfig.tokenEncryptTypeInfo3+'<br>'+lp._passwordConfig.changeTokenEncryptTypeInfo
//   component.confirm('warn', e, lp._passwordConfig.tokenEncryptTypeButton, {html}, 750, 300, ()=>{
//     saveConfig('person', 'encryptType', encryptType.value);
//   }, (dlg)=>{dlg.close();})
// }

getConfigData('person').then((data)=>{
  if (!data.extension) data.extension = {};
  if (!data.extension.passwordLength){
    data.extension.passwordLength = [6, 30];
  }
  if (!data.extension.passwordRuleValues){
    data.extension.passwordRuleValues = {
      useLowercase: true,
      useNumber: true,
      useUppercase: false,
      useSpecial: false
    }
  }
  personData.value = data;

  if (personData.value.extension && personData.value.extension.initialPasswordType){
    initialPasswordType.value = personData.value.extension.initialPasswordType;
  }else{
    initialPasswordType.value = (data.password.startsWith('(')  && data.password.endsWith(')')) ? 'script' : 'text'
  }
  passwordPeriod.value = data.passwordPeriod;
  getPasswordText(data);
  // scriptEditor.value.createEditor();

  passwordLength.value = personData.value.extension.passwordLength;
  passwordRuleValues.value = personData.value.extension.passwordRuleValues;
  encryptType.value = data.encryptType || 'default';

  firstLoginModifyPwd.value = personData.value.firstLoginModifyPwd;
});
getConfig('token', 'rsaEnable').then((data)=>{
  rsaEnable.value = !!data;
});


onUpdated(()=>{
  nextTick(()=>{
    checkInputNode();
    recordPasswordLength(passwordLength.value);
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
.item_label{
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
.item_pass_input{
  width: 100px;
  text-align: center;
}
.item_input_area{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
  margin-left: 80px;
}
.item_slider{
  width: 300px;
}
.item_hide{
  display: none;
}
</style>
