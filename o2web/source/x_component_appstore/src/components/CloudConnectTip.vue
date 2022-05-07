<template>
  <div class="appstore-connect-cloud">
    <div style="overflow: hidden">
      <div class="appstore-connect-pic"></div>
      <div class="appstore-connect-button mainColor_bg" @click="openSetting">{{lp.clickToConfig}}</div>
      <div class="appstore-connect-button mainColor_bg" @click="openDoc">{{lp.clickToDoc}}</div>
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
      o2.api.page.openApplication("Setting");
    },
    openDoc(){
      o2.openWindow("https://www.yuque.com/o2oa/course/vwgpbg");
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
  margin: 0 20px 160px 20px;
  border-radius: 100px;
  display: inline-block;
  cursor: pointer;
  font-size: 18px;
}
</style>
