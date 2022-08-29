<template>
  <div style="padding: 30px 20px;">

    <div class="item_warn">{{lp._cloudConfig.deleteCollectUnitInfo.replace('{name}', collectData.name)}}</div>

    <el-form ref="formRef" :model="collectData" :rules="loginRules" size="large" status-icon>
      <el-form-item prop="name">
        <el-input v-model="collectData.name" size="large" :placeholder="lp._cloudConfig.collectUsername" style="border-color:red">
          <template #prefix>
            <i class="o2icon-user" style="font-size: 18px"></i>
          </template>
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
          <button class="mainColor_bg" style="width: 140px; height:26px; line-height: 26px" @click="sendMobileCode($event)"
                  :class="{grayColor_bg: !codeButtonEnable, item_color_black: !codeButtonEnable}">{{getCodeText}}</button>
        </div>
      </el-form-item>

      <div class="item_div_button mainColor_bg" @click="deleteCollectUnit($event)">{{lp._cloudConfig.deleteCollectUnit}}</div>

      <div class="item_login_error">{{errorText}}</div>
    </el-form>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue';
import {lp} from '@o2oa/component';
import {
  checkCollectName,
  getConfigData,
  deleteCollect,
  sendCode
} from "@/util/acrions";

const emit = defineEmits(['deleted']);

const formRef = ref();
const mobileInputRef = ref();

const props = defineProps({
  collectData: { type: Object, default: null },
})

// const collectData = reactive({
//   name: '',
//   mobile: '',
//   code: ''
// });

const errorText = ref('');
const getCodeText = ref(lp._cloudConfig.getCode);
const codeButtonEnable = ref(true);

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
const mobileReg = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
const validateMobile = (rule, value, callback) => {
  if (value && !mobileReg.test(value)){
    callback(new Error(lp._cloudConfig.mobileError))
  }else{
    callback()
  }
}

const loginRules = reactive({
  name: [
    { required: true, message: lp._cloudConfig.inputCollectUsername, trigger: 'blur' },
    { validator: validateName, trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: lp._cloudConfig.inputCollectMobile, trigger: 'blur' },
    { validator: validateMobile, trigger: 'blur' }
  ],
  code: [{ required: true, message: lp._cloudConfig.inputCollectCode, trigger: 'blur' }],
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

const sendMobileCode = (e)=>{
  e.preventDefault();
  errorText.value = '';
  if (codeButtonEnable.value){
    mobileInputRef.value.focus();
    setTimeout(()=>{
      mobileInputRef.value.blur();
    }, 20)
    if (props.collectData.mobile){
      codeButtonEnable.value = false;
      getCodeText.value = lp._cloudConfig.regetCode+'('+wait+')';
      countdown();

      sendCode(props.collectData.mobile).then((data)=>{
        if (!data.value){
          wait = 0;
          countdown();
          errorText.value = data.value;
        }
      });
    }
  }
}

const deleteCollectUnit = async (e) => {
  errorText.value = '';
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const result = await deleteCollect(props.collectData.name, props.collectData.mobile, props.collectData.code);
      if (result.value){
        emit('deleted', e);
      }else{
        errorText.value = lp._cloudConfig.deleteError;
      }
    }
  });
}

// getConfigData('collect', true).then((data)=>{
//   collectData.name = data.name;
// });

</script>

<style scoped>
.item_warn{
  color: red;
  margin-bottom: 20px;
  font-size: 16px;
}
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
