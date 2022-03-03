<template>
  <div class="appNode">
    <div class="menuArea">
      <Menu />
    </div>

    <div class="contentArea">
      <div class="content">

      </div>
    </div>
  </div>
</template>

<script setup>
import Menu from './components/Menu.vue'
//import {lp} from '@o2oa/component'


</script>

<style scoped>
.appNode{
  overflow: hidden;
  height: 100%;
}
.menuArea{
  max-width: 300px;
  min-width: 160px;
  width: 15%;
  height: 100%;
  background: #FFFFFF;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.25);
  float: left;

}
.contentArea {
  height: 100%;
  background: #F0F0F0;
  display: flex;
  flex-direction:column;
}
.content{
  height: 100%;
  margin: 10px;
  background: #FFFFFF;
  border-radius: 4px;
  overflow: auto;
  display: flex;
  flex-direction: column;
}
</style>
