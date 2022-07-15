<template>
  <div class="appNode">
    <div class="menuArea">
      <Menu @changeItem="loadContent"/>
    </div>

    <div class="contentArea">
      <div class="content">
        <div v-if="!!contentType" is="vue:contentComponent"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Menu from './components/Menu.vue';
import { ref, defineAsyncComponent, computed } from 'vue';

let contentType = ref("");
function loadContent(type){
  contentType.value = type;
}
const contentComponent = computed(() => {
  const compName = contentType.value;
  return defineAsyncComponent(() => import('./components/content/'+compName+'.vue') );
});

</script>

<style scoped>
.appNode{
  overflow: hidden;
  height: 100%;
}
.menuArea{
  max-width: 300px;
  min-width: 180px;
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
  text-align: left;
}
</style>
<style>
.systemconfig_title{
  height: 40px;
  padding: 20px 30px 0;
  font-size: 20px;
  color: rgb(51, 51, 51);
  font-weight: bold;
  line-height: 40px;
}
.systemconfig_item_title{
  height: 30px;
  line-height: 30px;
  padding: 20px 0 0 30px;
  font-size: 16px;
  font-weight: bold;
  color: rgb(102, 102, 102);
}
.systemconfig_item_info{
  overflow: hidden;
  padding: 5px 30px;
  font-size: 14px;
  line-height: 24px;
  color: rgb(153, 153, 153);
}
.systemconfig_tab_area{
  padding: 20px 30px;
  width: 80%;
  min-width: 960px;
  max-width: 2000px;
  margin-right: 100px;
}
.systemconfig_area{
  display: flex;
  flex-direction: column;
  width: 100%;
}
.item_title{
  height: 30px;
  line-height: 30px;
  padding: 20px 0 0 30px;
  font-size: 16px;
  font-weight: bold;
  clear: both;
  color: rgb(102, 102, 102);
}
.item_info{
  overflow: hidden;
  padding: 5px 30px;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
}
.item_value{
  overflow: hidden;
  padding-right: 30px;
  font-size: 14px;
  color: rgb(153, 153, 153);
  clear: both;
  display: inline;
}

button{
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}
*::-webkit-scrollbar {
  width:8px;
  height: 8px;
  border-radius: 8px;
  background-color: #dddddd;
}
*::-webkit-scrollbar-thumb{
  width: 8px;
  border-radius: 8px;
  background-color: #bbbbbb;
  cursor: pointer;
}
*::-webkit-scrollbar-thumb:hover{
  width: 8px;
  border-radius: 8px;
  background-color: #666666;
  cursor: pointer;
}
* {
  scrollbar-color: #bbbbbb #dddddd;
  scrollbar-width: thin;
}
</style>
