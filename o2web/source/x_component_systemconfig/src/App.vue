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
  console.log(contentType.value)
  const compName = contentType.value;
  return defineAsyncComponent(() => import('./components/content/'+compName+'.vue') );
  // console.log('./components/content/' + contentType.value + '.vue');
  // return comp;
  //return await import('./components/content/' + contentType.value + '.vue');
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
}
</style>
