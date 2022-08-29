<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{ lp.cloudConfig }}</div>
    <div class="systemconfig_item_info" v-html="lp._cloudConfig.info"></div>

    <div class="item_info">
      <div class="item_connect">
        <div class="item_connect_img" :class="{item_connect_img_connected: connected, item_connect_img_disconnect: !connected}"></div>
        <div class="item_connect_text">{{connected ? lp._cloudConfig.connected : lp._cloudConfig.disconnect}}</div>
      </div>

      <div class="item_connect">
        <div class="item_connect_img" :class="{item_connect_img_connected: validated, item_connect_img_disconnect: !validated}"></div>
        <div class="item_connect_text">{{validated ? lp._cloudConfig.validated : lp._cloudConfig.notValidated}}</div>
        <button class="mainColor_bg" @click="checkConnect">{{lp._cloudConfig.recheck}}</button>
      </div>
    </div>

    <div class="item_info" v-if="!connected">
      <div class="item_connect_info mainColor_color">{{lp._cloudConfig.disconnectInfo}}</div>
    </div>

    <div class="item_info" v-if="connected && !validated">
      <div class="item_connect_info mainColor_color">{{lp._cloudConfig.notValidatedInfo}}</div>
      <div class="item_line">
        <label>{{lp._cloudConfig.loginInfo}}</label>
        <button class="mainColor_bg" @click="openLoginCollect()">{{lp._cloudConfig.loginButtonText}}</button>
      </div>
      <div class="item_line">
        <label>{{lp._cloudConfig.registerInfo}}</label>
        <button class="mainColor_bg" @click="openRegisterCollect">{{lp._cloudConfig.registerButtonText}}</button>
      </div>
      <div class="item_line">
        <label>{{lp._cloudConfig.forgotPasswordInfo}}</label>
        <button class="mainColor_bg" @click="openResetPasswordCollect">{{lp._cloudConfig.forgotPasswordButtonText}}</button>
      </div>
    </div>

    <div class="item_info" v-if="connected && validated">
      <div class="item_connect_info mainColor_color" v-html="lp._cloudConfig.validatedInfo.replace('{name}', collectName)"></div>

      <div class="item_line">
        <button class="mainColor_bg" @click="logoutCollect">{{lp._cloudConfig.logoutCollect}}</button>
        <button class="mainColor_bg" @click="openResetPasswordCollect()">{{lp._cloudConfig.modifyCollectPassword}}</button>
        <button class="mainColor_bg" @click="openDeleteCollect()">{{lp._cloudConfig.deleteCollect}}</button>
        <button class="mainColor_bg" @click="openLoginCollect()">{{lp._cloudConfig.reloginCollect}}</button>
      </div>
    </div>

    <div ref="loginNode" style="display: none">
      <LoginCloudDlg v-if="openLogin" @login="()=>{closeLoginDlg(); checkConnect(); }" :collect-data="collectData"></LoginCloudDlg>
    </div>

    <div ref="registerNode" style="display: none">
      <RegisterCloudDlg v-if="openRegister" @register="()=>{closeRegisterDlg(); checkConnect(); }"></RegisterCloudDlg>
    </div>

    <div ref="deleteNode" style="display: none">
      <DeleteCloudDlg v-if="openDelete" @deleted="()=>{closeDeleteDlg(); checkConnect(); }" :collect-data="collectData"></DeleteCloudDlg>
    </div>

    <div ref="resetPasswordNode" style="display: none">
      <ResetPasswordCloudDlg v-if="openResetPassword" @reset="()=>{closeResetPasswordDlg(); checkConnect(); }" :collect-name="collectData.name"></ResetPasswordCloudDlg>
    </div>

  </div>


</template>

<script setup>
import {ref} from 'vue';
import {component, lp, o2} from '@o2oa/component';
import {connectCollect, getConfigData, validateCollect, disconnectCollect} from "@/util/acrions";
import LoginCloudDlg from '@/components/content/ServerCloudConfig/LoginCloudDlg.vue';
import RegisterCloudDlg from '@/components/content/ServerCloudConfig/RegisterCloudDlg.vue';
import DeleteCloudDlg from '@/components/content/ServerCloudConfig/DeleteCloudDlg.vue';
import ResetPasswordCloudDlg from '@/components/content/ServerCloudConfig/ResetPasswordCloudDlg.vue';


const connected = ref(false);
const validated = ref(false);

const loginNode = ref();
const openLogin = ref(false);
const registerNode = ref();
const openRegister = ref(false);
const deleteNode = ref();
const openDelete = ref(false);
const resetPasswordNode = ref();
const openResetPassword = ref(false);

const loginCloudDlgRef = ref();
// const loginCloudDlgRef = ref();
// const loginCloudDlgRef = ref();
// const loginCloudDlgRef = ref();

const collectData = ref(null);
const collectName = ref('');

const openDlg = (content, width, height, title, buttonList)=>{
  const container = component.content.getElement('.systemconfig');
  content.show();
  return o2.DL.open({
    title,
    container,
    maskNode: container,
    width,
    height,
    content,
    onQueryClose: () => {
      content.hide();
      content.inject(container);
      checkCollectData();
    },
    buttonList
  });
}



let loginDlg;
const openLoginCollect = ()=>{
  openLogin.value = true;
  loginDlg = openDlg(loginNode.value, 600, 370, lp._cloudConfig.loginButtonText);
}
const closeLoginDlg = ()=>{
  if (loginDlg) loginDlg.close();
  loginDlg = null;
}

let registerDlg;
const openRegisterCollect = ()=>{
  openRegister.value = true;
  registerDlg = openDlg(registerNode.value, 600, 620, lp._cloudConfig.registerCollect);
}
const closeRegisterDlg = ()=>{
  if (registerDlg) registerDlg.close();
  registerDlg = null;
}

let deleteDlg;
const openDeleteCollect = ()=>{
  openDelete.value = true;
  deleteDlg = openDlg(deleteNode.value, 600, 450, lp._cloudConfig.deleteCollectUnit);
}
const closeDeleteDlg = ()=>{
  if (deleteDlg) deleteDlg.close();
  deleteDlg = null;
}

let resetPasswordDlg;
const openResetPasswordCollect = ()=>{
  openResetPassword.value = true;
  resetPasswordDlg = openDlg(resetPasswordNode.value, 600, 560, lp._cloudConfig.resetPasswordCollect);
}
const closeResetPasswordDlg = ()=>{
  if (resetPasswordDlg) resetPasswordDlg.close();
  resetPasswordDlg = null;
}


const logoutCollect = ()=>{
  disconnectCollect();
  checkConnect();
}

const checkCollectData = ()=>{
  getConfigData('collect', true).then((data)=>{
    collectData.value = data;
    collectName.value = data.name;
  });
};
const checkConnect = ()=>{
  connected.value = false;
  validated.value = false;
  connectCollect().then((data) => {
    connected.value = data.value;
  });
  validateCollect().then((data) => {
    validated.value = data.value;
  });
  checkCollectData();
}
checkConnect();

</script>

<style scoped>
.item_connect{
  height: 80px;
  padding: 0 20px;
  width: 660px;
  overflow: hidden;
  background: rgb(247, 247, 247);
  border-radius: 20px;
  margin-top: 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.item_connect_info{
  margin: 20px 0 30px 0;
  font-size: 16px
}
.item_connect_img{
  width: 228px;
  height: 80px;
  float: left;
  background-position: 50% 50%;
  background-size: contain;
  background-repeat: no-repeat;
}
.item_connect_img_connected {
  background-image: url("../../assets/pic_connected.png");
}
.item_connect_img_disconnect {
  background-image: url("../../assets/pic_disconnect.png");
}
.item_connect_text{
  padding-left: 30px;
  height: 80px;
  line-height: 80px;
  color: rgb(102, 102, 102);
  font-size: 18px;
}
.item_line{
  margin: 20px 0;
}
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
  padding: 0;
  display: flex;
  justify-content: space-around;
  font-size: 16px;
  color: red;
}
</style>
