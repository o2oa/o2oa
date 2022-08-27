<template>
  <div style="padding: 30px 20px;">
    <el-form ref="loginFormRef" :model="collectData" :rules="loginRules" size="large" status-icon>
      <el-form-item prop="name">
        <el-input v-model="collectData.name" size="large" :placeholder="lp._cloudConfig.collectUsername" style="border-color:red">
          <template #prefix>
            <i class="o2icon-user" style="font-size: 18px"></i>
          </template>
        </el-input>
      </el-form-item>
      <div class="item_validate_info"></div>
      <el-form-item prop="password">
        <el-input v-model="collectData.password" type="password" size="large" :placeholder="lp._cloudConfig.collectPassword" show-password>
          <template #prefix>
            <i class="o2icon-lock" style="font-size: 18px"></i>
          </template>
        </el-input>
      </el-form-item>
      <div class="item_validate_info"></div>
      <div class="item_div_button mainColor_bg" @click="loginCollect($event)">{{lp._cloudConfig.loginButtonText}}</div>
<!--      <div class="item_dlg_link">-->
<!--        <div class="item_dlg_link_text mainColor_color">{{lp._cloudConfig.registerCollect}}</div>-->
<!--        <div class="item_dlg_link_text mainColor_color">{{lp._cloudConfig.forgotPassword}}</div>-->
<!--      </div>-->
      <div class="item_login_error" v-if="loginError">{{lp._cloudConfig.loginError}}</div>
    </el-form>
  </div>
</template>

<script setup>
import {ref, reactive, getCurrentInstance} from 'vue';
import {lp} from '@o2oa/component';
import {getConfigData, loginToCollect} from "@/util/acrions";

const emit = defineEmits(['login']);

const props = defineProps({
  collectData: { type: Object, default: null },
})

const loginFormRef = ref();

const loginError = ref(false);
// const collectData = reactive({
//   name: '',
//   password: ''
// });


const loginRules = reactive({
  name: [{ required: true, message: lp._cloudConfig.inputCollectUsername, trigger: 'change' }],
  password: [{ required: true, message: lp._cloudConfig.inputCollectPassword, trigger: 'change' }]
});

// getConfigData('collect', true).then((data)=>{
//   collectData.name = data.name;
//   collectData.password = data.password;
// });

const loginCollect = async (e) => {
  const vm = getCurrentInstance();
  debugger;
  loginError.value = false;
  await loginFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      const result = await loginToCollect({
        name: props.collectData.name,
        password: props.collectData.password
      });
      if (result.value){
        emit('login', e);
      }else{
        loginError.value = true;
      }
    }
  });
}

</script>

<style scoped>
.item_dlg_link{
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 30px;
  padding: 0 10px;
}
.item_dlg_link_text{
  cursor: pointer;
}
.item_dlg_link_text:hover{
  text-decoration: underline;
}
.item_login_error{
  padding-top: 10px;
  display: flex;
  justify-content: space-around;
  font-size: 16px;
  color: red;
}
</style>
