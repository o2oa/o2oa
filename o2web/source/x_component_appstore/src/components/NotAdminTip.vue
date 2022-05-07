<template>
  <div class="appstore-notadmin">
    <div style="overflow: hidden">
      <div class="appstore-notadmin-pic"></div>
      <div class="appstore-notadmin-info">{{lp.notAdmin}}</div>
      <div class="appstore-notadmin-button mainColor_bg" @click="closeApplication">{{lp.close}}</div>
    </div>
  </div>
</template>

<script>
import {lp, component} from '@o2oa/component';

export default {
  name: 'NotAdminTip',
  data(){
    return {
      lp: lp
    }
  },
  methods: {
    closeApplication(){
      component.close();
    }
  }
}
</script>

<style scoped>
.appstore-notadmin{
  height: 100%;
  display: flex;
  justify-content: center;
  align-content: center;
  flex-direction: column;
  background-color: #ffffff;
}
.appstore-notadmin-info{
  color: #666666;
  font-size: 20px;
  height: 40px;
  line-height: 36px;
  margin: 20px 0;
}
.appstore-notadmin-pic{
  width: 60px;
  height: 60px;
  background-image: url("../assets/stop.png");
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  margin: auto;
}
.appstore-notadmin-button{
  padding: 5px 20px;
  margin: 0 20px 160px 20px;
  border-radius: 100px;
  display: inline-block;
  cursor: pointer;
  font-size: 16px;
}
</style>
