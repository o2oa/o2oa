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
  flex-direction: column;
  text-align: left;
}
</style>
<style>
.title{
  height: 40px;
  padding: 20px 30px 0;
  font-size: 24px;
  color: rgb(51, 51, 51);
  font-weight: bold;
  line-height: 40px;
}
.item_title{
  height: 30px;
  line-height: 30px;
  padding: 20px 0 0 30px;
  font-size: 18px;
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
</style>
