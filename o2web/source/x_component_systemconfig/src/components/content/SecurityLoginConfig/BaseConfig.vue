<template>
  <div>
    <div class="item_title">{{lp._loginConfig.captchaLogin}}</div>
    <div class="item_info">{{lp._loginConfig.captchaLoginInfo}}</div>
    <div class="item_info">
      <el-switch
          @change="saveConfig('person', 'captchaLogin', captchaLogin)"
          v-model="captchaLogin"
          :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>

    <div class="item_title">{{lp._loginConfig.codeLogin}}</div>
    <div class="item_info">{{lp._loginConfig.codeLoginInfo}}</div>
    <div class="item_info">
      <el-switch
          @change="saveConfig('person', 'codeLogin', codeLogin)"
          v-model="codeLogin"
          :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>

    <div class="item_title">{{lp._loginConfig.bindLogin}}</div>
    <div class="item_info">{{lp._loginConfig.bindLoginInfo}}</div>
    <div class="item_info">
      <el-switch
          @change="saveConfig('person', 'bindLogin', bindLogin)"
          v-model="bindLogin"
          :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>

    <BaseItem
        :title="lp._loginConfig.register"
        :info="lp._loginConfig.registerInfo"
        :config="register"
        :allowEditor="true"
        :options="lp._loginConfig.registerValues"
        type="select"
        @changeConfig="(value)=>{register = value; saveConfig('person', 'register', register)}"></BaseItem>

    <div class="item_title">{{lp._loginConfig.loginPage}}</div>
    <div class="item_info">{{lp._loginConfig.loginPageInfo}}</div>
    <div class="item_info">
      <el-switch
          @change="saveConfig('portal', 'loginPage.enable', loginPage)"
          v-model="loginPage"
          :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>
    <div v-if="!!loginPage">
      <div class="item_info" style="display: inline-flex; align-items: center;">
        <label class="item_label">{{lp._loginConfig.loginPagePortal}}</label>

        <div class="item_input_area">
          <el-select v-model="loginPortal" size="default" :placeholder="lp._loginConfig.selectPortal" @change="saveConfig('portal', 'loginPage.portal', loginPortal)">
            <el-option value="default" :label="lp.default"></el-option>
            <el-option v-for="portal in portalList" :key="portal.id" :value="portal.id" :label="portal.name"></el-option>
          </el-select>
        </div>
      </div>
    </div>

    <div class="item_title">{{lp._loginConfig.indexPage}}</div>
    <div class="item_info">{{lp._loginConfig.indexPageInfo}}</div>
    <div class="item_info">
      <el-switch
          @change="saveConfig('portal', 'indexPage.enable', indexPage)"
          v-model="indexPage"
          :active-text="lp.operation.enable" :inactive-text="lp.operation.disable">
      </el-switch>
    </div>
    <div v-if="!!indexPage">
      <div class="item_info" style="display: inline-flex; align-items: center;">
        <label class="item_label">{{lp._loginConfig.indexPagePortal}}</label>

        <div class="item_input_area">
          <el-select v-model="indexPortal" size="default" :placeholder="lp._loginConfig.selectPortal" @change="saveConfig('portal', 'indexPage.portal', indexPortal)">
            <el-option value="default" :label="lp.default"></el-option>
            <el-option v-for="portal in portalList" :key="portal.id" :value="portal.id" :label="portal.name"></el-option>
          </el-select>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import {ref} from 'vue';
import {lp} from '@o2oa/component';
import BaseItem from '@/components/item/BaseItem.vue';
import {getConfigData, loadPortals, saveConfig} from '@/util/acrions';

const captchaLogin = ref(false);
const codeLogin = ref(true);
const bindLogin = ref(true);
const faceLogin = ref(false);
const register = ref('disable');
const loginPage = ref(false);
const loginPortal = ref('default');
const indexPage = ref(false);
const indexPortal = ref('');
const portalList = ref([]);

const load = async () => {
  getConfigData('person').then((data)=>{
    captchaLogin.value = !!data.captchaLogin;
    codeLogin.value = !!data.codeLogin;
    bindLogin.value = !!data.bindLogin;
    faceLogin.value = !!data.faceLogin;
    if (data.register) register.value = data.register;
  });
  getConfigData('portal').then((data)=>{
    loginPage.value = !!data.loginPage.enable;
    loginPortal.value = data.loginPage.portal || 'default';
    indexPage.value = !!data.indexPage.enable;
    indexPortal.value = data.indexPage.portal;
  });
  loadPortals().then((data)=>{
    portalList.value = data;
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

</style>
