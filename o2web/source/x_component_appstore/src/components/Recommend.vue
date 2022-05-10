<template>
  <div class="recommend-area">
    <div class="recommend-left">
      <div class="recommend-item" :style="{backgroundImage: 'url('+broadcastPic0+')'}" @click="openApplication(recommend[0])">
        <div class="recommend-item-mask"></div>
        <div class="recommend-item-title">{{(recommend[0]) ? recommend[0].name : ""}}</div>
      </div>
    </div>
    <div class="recommend-right">
      <div class="recommend-right-top">
        <div class="recommend-item" :style="{backgroundImage: 'url('+broadcastPic1+')'}" @click="openApplication(recommend[1])">
          <div class="recommend-item-mask"></div>
          <div class="recommend-item-title">{{(recommend[1]) ? recommend[1].name : ""}}</div>
        </div>
      </div>
      <div class="recommend-right-bottom">
        <div class="recommend-item" :style="{backgroundImage: 'url('+broadcastPic2+')'}" @click="openApplication(recommend[2])">
          <div class="recommend-item-mask"></div>
          <div class="recommend-item-title">{{(recommend[2]) ? recommend[2].name : ""}}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {o2, component} from '@o2oa/component';

export default {
  name: 'Recommend',
  data(){
    return {
      recommend: []
    }
  },
  computed: {
    broadcastPic0: function(){
      return (this.recommend[0]) ? this.recommend[0].broadcastPic : "";
    },
    broadcastPic1: function(){
      return (this.recommend[1]) ? this.recommend[1].broadcastPic : "";
    },
    broadcastPic2: function(){
      return (this.recommend[2]) ? this.recommend[2].broadcastPic : "";
    }
  },
  created(){
    o2.Actions.load('x_program_center').MarketAction.	listTopThree().then((json)=>{
      this.recommend = json.data;
    });
  },
  methods: {
    openApplication(item){
      o2.api.page.openApplication('appstore.application', {
        appId: item.id,
        appName: item.name
      });
    }
  }
}
</script>

<style scoped>
.recommend-area{
  height: 100%;
  display: flex;
}
.recommend-left{
  width: 63%;
  padding: 10px;
}
.recommend-right{
  width: 37%;
  display: flex;
  flex-direction: column;
}
.recommend-right-top{
  height: 50%;
  padding: 10px;
}
.recommend-right-bottom{
  height: 50%;
  padding: 10px;
}
.recommend-item{
  height: 100%;
  border-radius: 16px;
  cursor: pointer;
  position: relative;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.recommend-item-mask{
  background: linear-gradient(180deg,rgba(0,0,0,0) 0%,rgba(0,0,0,0.5) 100%);
  border-radius: 16px;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  position: absolute;
}
.recommend-item-title{
  width: 100%;
  height: 36px;
  line-height: 36px;
  font-size: 24px;
  color: #FFFFFF;
  bottom: 10px;
  position: absolute;
}
</style>
