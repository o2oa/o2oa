<template>
  <div class="menu">
    <div class="menuTitle">
      <div class="menuTitleLogo" style="background-image: url('../x_component_systemconfig/$Main/default/setting.png');"></div>
      <div class="menuTitleText">{{lp.title}}</div>
    </div>
    <div class="menuContent">
      <div v-for="menu in menuJson">
        <div class="menuItem" @click="toggle">
          <div class="menuItemIcon"><i :class="'o2icon-'+menu.icon"></i></div>
          <div class="menuItemText">{{menu.title}}</div>
        </div>

        <div class="menuItemSubArea">

            <div class="menuItem subItem" v-for="item in menu.children">
              <div class="menuItemIcon subItemIcon"><i :class="'o2icon-'+item.icon"></i></div>
              <div class="menuItemCount o2icon-forward"></div>
              <div class="menuItemText subItemText">{{item.title}}</div>
            </div>

        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import {getMenuJson} from '../util/menu.js';
import {ref} from 'vue';
import {o2, lp, component} from '@o2oa/component';

const menuJson = getMenuJson();

function toggle(e){
  const subNode = e.currentTarget.getNext();
  debugger;
  if (subNode.getStyle("display") === "none"){
    subNode.show();
  }else{
    subNode.hide();
  }
}

// const taskList = ref([]);
// o2.Actions.load("x_processplatform_assemble_surface").TaskAction.V2ListPaging(1, 5).then((json)=>{
//   taskList.value = json.data;
// });
//
// function openTask(id){
//   o2.api.page.openWork(id);
// }
// function openCalendar(){
//   o2.api.page.openApplication("Calendar");
// }
// function openOrganization(){
//   o2.api.page.openApplication("Org");
// }
// function openInBrowser() {
//   component.openInNewBrowser(true);
// }
// function startProcess(){
//   o2.api.page.startProcess();
// }
// function createDocument(){
//   o2.api.page.createDocument();
// }
</script>

<style scoped>
.menuItemSubArea{
  display: none;
}
.menu{
  margin: 0;
  user-select: none;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.menuTitle{
  margin: 0 20px;
  height: 60px;
  font-size: 18px;
  color: #333333;
  line-height: 60px;
  border-bottom: 1px solid #cccccc;
}
.menuTitleLogo{
  background-repeat: no-repeat;
  background-size: 18px;
  background-position: center;
  background-color: #758790;
  width: 32px;
  height: 32px;
  float: left;
  border-radius: 20px;
  margin-top: 14px;
  margin-right: 10px;
}
.menuTitleText{
  float: left;
}
.menuContent{
  height: inherit;
  overflow: auto;
}
.menuItem{
  height: 50px;
  cursor: pointer;
  line-height: 50px;
  font-size: 16px;
}
.menuItem_current{
  background-color: #EBF1F7;
}
.menuItem_over{
  background-color: #F7F7F7;
}

.menuItemIcon {
  margin-left: 20px;
  /*height: 16px;*/
  /*width: 16px;*/
  /*padding: 12px 5px 12px 0;*/
  float: left;
  color: #999999;
}
.menuItemText{
  margin: 0 30px 0 46px;
  color: #999999;
  text-align: left;
}
.menuItemCount{
  margin-right: 20px;
  float: right;
  width: 20px;
  font-size: 1px;
  color: #999999;
  line-height: 40px!important;
  text-align: right;
}
.subItem {
  height: 40px;
  line-height: 40px;
  padding-left: 10px;
  font-size: 14px;
}
.subItemIcon {
  margin-left: 20px;
  color: #666666;
}
.subItemText{
  margin: 0 30px 0 40px;
  color: #333333;
  text-align: left;
}
</style>
