<template>
  <div>
    <div class="item_title">{{lp._passwordConfig.adminPassword}}</div>
    <div class="item_info">{{lp._passwordConfig.adminPasswordInfo}}</div>
    <button @click="changeAdminPassword(lp._passwordConfig.modifyAdminPassword, 'xadmin')"
            style="margin-left: 30px; margin-top: 20px" class="mainColor_bg">{{lp._passwordConfig.modifyAdminPassword}}</button>

    <div class="item_title">{{lp._passwordConfig.ternaryPassword}}</div>
    <div class="item_info">{{lp._passwordConfig.ternaryPasswordInfo}}</div>
    <button @click="changeAdminPassword(lp._passwordConfig.modifySystemManagerPassword, 'systemManager')"
            style="margin-left: 30px; margin-top: 20px" class="mainColor_bg">{{lp._passwordConfig.modifySystemManagerPassword}}</button>

    <button @click="changeAdminPassword(lp._passwordConfig.modifySecurityManagerPassword, 'securityManager')"
            style="margin-left: 30px; margin-top: 20px" class="mainColor_bg">{{lp._passwordConfig.modifySecurityManagerPassword}}</button>

    <button @click="changeAdminPassword(lp._passwordConfig.modifyAuditManagerPassword, 'auditManager')"
            style="margin-left: 30px; margin-top: 20px" class="mainColor_bg">{{lp._passwordConfig.modifyAuditManagerPassword}}</button>


    <div class="item_hide" ref="changePasswordNode">
      <form>
        <BaseInput :label="lp._passwordConfig.oldPassword" input-type="password" :labelStyle="{width: '100px'}" :show-password="true" v-model:value="oldPassword"></BaseInput>
        <BaseInput :label="lp._passwordConfig.newPassword" input-type="password" :labelStyle="{width: '100px'}" :show-password="true" v-model:value="newPassword"></BaseInput>
        <BaseInput :label="lp._passwordConfig.confirmPassword" input-type="password" :labelStyle="{width: '100px'}" :show-password="true" v-model:value="confirmPassword"></BaseInput>
      </form>
    </div>




  </div>
</template>

<script setup>
import {lp, component, o2, layout} from '@o2oa/component';
import {ref} from 'vue';
import {changePassword} from '@/util/acrions';
import BaseInput from '@/components/item/BaseInput.vue';

const changePasswordNode = ref();

const oldPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');

const openDlg = (title, action)=>{
  const container = component.content.getElement('.systemconfig');
  const content = changePasswordNode.value;
  content.show();

  o2.DL.open({
    title: title,
    container,
    maskNode: container,
    width: 600,
    height: 350,
    content,
    onQueryClose: () => {
      oldPassword.value = '';
      newPassword.value = '';
      confirmPassword.value = '';
      content.hide();
      content.inject(container);
    },
    buttonList: [{
      text: lp.operation.ok,
      type: 'ok',
      action: async (dlg) => {
        if (action) action(dlg);
      }
    }, {
      text: lp.operation.cancel,
      type: 'cancel',
      action: dlg => dlg.close()
    }]
  })
}

const encryptPassword = (password)=>{
  return new Promise((resolve, reject)=>{
    o2.load("../o2_lib/jsencrypt/jsencrypt.js", ()=>{
      var encrypt = new JSEncrypt();
      encrypt.setPublicKey("-----BEGIN PUBLIC KEY-----"+layout.config.publicKey+"-----END PUBLIC KEY-----");
      resolve(encrypt.encrypt(password));
    });
  });
}

const checkPassword = (dlg)=>{
  if (!oldPassword.value || !newPassword.value || !confirmPassword.value) {
    component.notice(lp._passwordConfig.passwordEmpty, "error", dlg.node, {x: 'right', y: 'top'}, {x: 10, y: 10});
    return false;
  }
  if (newPassword.value !== confirmPassword.value) {
    component.notice(lp._passwordConfig.passwordDisaccord, "error", dlg.node, {x: 'right', y: 'top'}, {x: 10, y: 10});
    return false;
  }
  return true;
}

const getOldPassword = async () => {
  return (layout.config.publicKey) ? (await encryptPassword(oldPassword.value)) : oldPassword.value;
}
const getNewPassword = async () => {
  return (layout.config.publicKey) ? (await encryptPassword(newPassword.value)) : newPassword.value;
}

const commitChangePassword = async (credential) => {
  const opd = await getOldPassword();
  const npd = await getNewPassword();
  await changePassword(credential, opd, npd);
}

const changeAdminPassword = (title, credential)=>{
  openDlg(title, async (dlg) => {
    if (checkPassword(dlg)){
      await commitChangePassword(credential);
      dlg.close();
    }
  })
}

</script>

<style scoped>
.item_hide{
  display: none;
  padding: 30px;
}
</style>
