<template>
  <div style="padding: 30px 20px;">
    <el-form ref="registerFormRef" :model="collectData" :rules="rules" size="large" status-icon>
      <el-form-item prop="name">
        <el-input v-model="collectData.name" :placeholder="lp._cloudConfig.collectUsername">
          <template #prefix><i class="o2icon-user" style="font-size: 18px"></i></template>
        </el-input>
      </el-form-item>

      <el-form-item prop="mobile">
        <el-input ref="mobileInputRef" v-model="collectData.mobile" :placeholder="lp._cloudConfig.collectMobile">
          <template #prefix><i class="o2icon-mobile" style="font-size: 18px"></i></template>
        </el-input>
      </el-form-item>

      <el-form-item prop="code">
        <div class="item_code_layout">
          <el-input v-model="collectData.code" :placeholder="lp._cloudConfig.collectCode">
            <template #prefix><i class="o2icon-security" style="font-size: 18px"></i></template>
          </el-input>
          <button class="mainColor_bg" style="width: 140px; height:26px; line-height: 26px" @click="sendRegisterCode($event)"
                  :class="{grayColor_bg: !codeButtonEnable, item_color_black: !codeButtonEnable}">{{getCodeText}}</button>
        </div>
      </el-form-item>

      <el-form-item prop="password">
        <el-input v-model="collectData.password" type="password" :placeholder="lp._cloudConfig.collectPassword" show-password>
          <template #prefix><i class="o2icon-lock" style="font-size: 18px"></i></template>
        </el-input>
      </el-form-item>

      <el-form-item prop="confirm">
        <el-input v-model="collectData.confirm" type="password" :placeholder="lp._cloudConfig.collectConfirm" show-password>
          <template #prefix><i class="o2icon-lock" style="font-size: 18px"></i></template>
        </el-input>
      </el-form-item>

      <div class="item_validate_info"></div>
      <div class="item_div_button mainColor_bg" @click="registerCollect($event)">{{lp._cloudConfig.resetPasswordCollect}}</div>
      <div class="item_login_error">{{errorText}}</div>
    </el-form>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue';
import {lp} from '@o2oa/component';
import {checkCollectName, checkCollectPass, sendCode, resetPasswordCollect, getConfigData} from "@/util/acrions";

const emit = defineEmits(['reset']);

const registerFormRef = ref();
const mobileInputRef = ref();

const errorText = ref();

const props = defineProps({
  collectName: '',
})

const collectData = reactive({
  name: props.collectName,
  mobile: '',
  code: '',
  password: '',
  confirm: ''
});

const getCodeText = ref(lp._cloudConfig.getCode);

const codeButtonEnable = ref(true);

const mobileReg = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;

const validateName = (rule, value, callback) => {
  if (value){
    checkCollectName(value).then((data)=>{
      if (!data.value){
        callback(new Error(lp._cloudConfig.collectUsernameNotExist))
      }else{
        callback()
      }
    });
  }else{
    callback()
  }
}
const validatePass = (rule, value, callback) => {
  if (value){
    checkCollectPass(value).then((data)=>{
      if (data.value){
        callback(new Error(data.value))
      }else{
        callback()
      }
    });
  }else{
    callback()
  }
}
const validateConfirm = (rule, value, callback) => {
  if (value && collectData.password && value !== collectData.password){
      callback(new Error(lp._cloudConfig.passwordDisagree))
  }else{
    callback()
  }
}
const validateMobile = (rule, value, callback) => {
  if (value && !mobileReg.test(value)){
    callback(new Error(lp._cloudConfig.mobileError))
  }else{
    callback()
  }
}


const rules = reactive({
  name: [
    { required: true, message: lp._cloudConfig.inputCollectUsername, trigger: 'blur' },
    { validator: validateName, trigger: 'blur' }
  ],
  password: [
    { required: true, message: lp._cloudConfig.inputCollectPassword, trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: lp._cloudConfig.inputCollectMobile, trigger: 'blur' },
    { validator: validateMobile, trigger: 'blur' }
  ],
  code: [{ required: true, message: lp._cloudConfig.inputCollectCode, trigger: 'blur' }],
  confirm: [
    { required: true, message: lp._cloudConfig.inputCollectConfirm, trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
});

let wait = 60;
const countdown = ()=>{
  const check = ()=>{
    wait--;
    if (wait<=0){
      codeButtonEnable.value = true;
      getCodeText.value = lp._cloudConfig.getCode;
      wait = 60;
    }else{
      getCodeText.value = lp._cloudConfig.regetCode+'('+wait+')';
      setTimeout(check, 1000);
    }
  }
  setTimeout(check, 1000);
}

const sendRegisterCode = (e)=>{
  e.preventDefault();
  errorText.value = '';
  if (codeButtonEnable.value){
    mobileInputRef.value.focus();
    setTimeout(()=>{
      mobileInputRef.value.blur();
    }, 20)
    if (collectData.mobile){
      codeButtonEnable.value = false;
      getCodeText.value = lp._cloudConfig.regetCode+'('+wait+')';
      countdown();

      sendCode(collectData.mobile).then((data)=>{
        if (!data.value){
          wait = 0;
          countdown();
          errorText.value = data.value;
        }
      });
    }
  }

}

const registerCollect = async (e) => {
  errorText.value = '';
  await registerFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      const result = await resetPasswordCollect({
        mobile: collectData.mobile,
        codeAnswer: collectData.code,
        name: collectData.name,
        password: collectData.password
      });

      if (result.value){
        emit('reset', e);
      }else{
        errorText.value = lp._cloudConfig.resetPasswordError;
      }
    }
  });
}

// getConfigData('collect', true).then((data)=>{
//   collectData.name = data.name;
// });

</script>

<style scoped>
.item_code_layout{
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.item_color_black{
  color: #999999!important;
}
.item_login_error{
  padding-top: 10px;
  display: flex;
  justify-content: space-around;
  font-size: 16px;
  color: red;
}
</style>
