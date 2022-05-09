<template>
  <div class="appstore-connect-cloud">
    <div style="overflow: hidden">
      <div class="appstore-connect-pic"></div>
      <div class="appstore-connect-button mainColor_bg" @click="openSetting">{{lp.clickToConfig}}</div>
      <div class="appstore-connect-button mainColor_bg" @click="openDoc">{{lp.clickToDoc}}</div>
      <div style="margin-bottom: 200px">
        <div class="appstore-connect-offline">{{lp.offlineInstallInfo}}</div>
        <div class="appstore-connect-button mainColor_bg" @click="offlineInstall">{{lp.offlineInstall}}</div>
        <input type="file" @change="uploadApp" style="display: none"/>
      </div>
    </div>

  </div>
</template>

<script>
import {o2, lp, component} from '@o2oa/component';

export default {
  name: 'CloudConnectTip',
  data(){
    return {
      lp: lp
    }
  },
  methods: {
    openSetting(){
      o2.api.page.openApplication('Setting');
    },
    openDoc(){
      o2.openWindow(lp.configDocUrl);
    },
    uploadApp(e){
      if (e.target.files && e.target.files.length){
        for (let file of e.target.files){
          this.uploadFile(file);
        }
      }
    },
    offlineInstall(){
      const uploadNode = this.$el.querySelector('input');
      uploadNode.click();
    },
    uploadFile(file){
      const formdata = new FormData();
      formdata.append("file", file);
      formdata.append("fileName", file.name);
      o2.Actions.load("x_program_center").MarketAction.installOffline(formdata, file, function(){
        component.notice(lp.installSuccess, 'success');
      });
    }
  }
}
</script>

<style scoped>
.appstore-connect-cloud{
  height: 100%;
  display: flex;
  justify-content: center;
  align-content: center;
  flex-direction: column;
  background-color: #ffffff;
}
.appstore-connect-pic{
  width: 1000px;
  height: 380px;
  background-image: url("../assets/unconnectcloudserver.jpg");
  background-position: center -50px;
  background-repeat: no-repeat;
  background-size: cover;
  margin: auto;
}
.appstore-connect-button{
  padding: 10px 20px;
  margin: 0 20px 30px 20px;
  border-radius: 100px;
  display: inline-block;
  cursor: pointer;
  font-size: 18px;
}
.appstore-connect-offline{
  font-size: 18px;
  line-height: 40px;
  display: inline-block;

}
</style>
