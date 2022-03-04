<template>
  <div class="appNode">
    <div class="menuArea">
      <Menu @changeItem="loadContent"/>
    </div>

    <div class="contentArea">
      <div class="content">
        <div is="vue:contentComponent"></div>
<!--        <SystemInfor />-->
      </div>
    </div>
  </div>
</template>

<script setup>
import Menu from './components/Menu.vue';
import { provide, ref, defineAsyncComponent } from 'vue';

let contentType = ref("SystemInfor");
function loadContent(type){
  console.log('loadContent: ' + type);
  contentType.value = type;
}
function contentComponent() {
  // const comp = defineAsyncComponent(() =>
  //     import('./components/content/'+contentType.value+'.vue')
  // );
  // debugger;
  // //component(contentType.value, comp)
  console.log('./components/content/' + contentType.value + '.vue');
  return contentType.value;
  //return import('./components/content/SystemInfor.vue')
};


// function contentComponent() {
//   // const module = await import('./components/content/' + contentType.value + '.vue');
//   // return module;
//   return contentType.value;
// }

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
