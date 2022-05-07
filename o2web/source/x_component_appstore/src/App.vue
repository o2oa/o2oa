<template>
  <div class="appstore-content appstore-root">
      <Content></Content>
  </div>
</template>

<script>
import Content from './components/Content.vue'

export default {
  name: 'AppStore',
  components: {
    Content
  }
}
</script>

<style>
.appstore-root{
  overflow: auto;
}
.appstore-content{
  height: 100%;
  box-sizing: border-box;
}
.appstore-content *{
  box-sizing: border-box !important;
}
.vipColor_bg{
  background-color: #FDAD0A;
  color: #ffffff !important;
}
.appstore-contactus{
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  background-image: url("./assets/pic_qiyeweixin@2x.png");
  width: 200px;
  height: 200px;
  margin: auto;
}
.appstore-contactus-text{
  padding: 10px 20px;
  text-align: center;
}
.appstore-contactus-phone{
  padding: 10px 20px;
  text-align: center;
  color: #F24949;
  font-size: 20px;
}
</style>
