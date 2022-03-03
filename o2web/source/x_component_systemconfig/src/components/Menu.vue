<template>
  <div>
    <div class="menu">
      <div class="menuTitle">
        <div class="menuTitleText">{{lp.title}}</div>
      </div>
      <div v-for="menu in menuJson">
        <div class="menuItem">
          <div class="menuItemText">{{menu.title}}</div>
        </div>
        <div class="menuItemSubArea" v-for="item in menu.children">
          <div class="menuItem subItem">
            <div class="menuItemIcon"><icon :class="'o2icon-'+item.icon"></icon></div>
            <div class="menuItemCount o2icon-forward"></div>
            <div class="menuItemText subItemText">{{item.title}}</div>
          </div>
        </div>
      </div>


<!--      <div class="menuItem">-->
<!--        <div class="menuItemText">{{lp.baseConfig}}</div>-->
<!--      </div>-->
<!--      <div class="menuItemSubArea">-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-config"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.systemInfor}}</div>-->
<!--        </div>-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-ui"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.uiConfig}}</div>-->
<!--        </div>-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-component"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.componentDeploy}}</div>-->
<!--        </div>-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-webres"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.resourceDeploy}}</div>-->
<!--        </div>-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-service"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.serviceDeploy}}</div>-->
<!--        </div>-->
<!--      </div>-->
<!--      <div class="menuItem">-->
<!--        <div class="menuItemText">{{lp.securityConfig}}</div>-->
<!--      </div>-->
<!--      <div class="menuItemSubArea">-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-config"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.systemInfor}}</div>-->
<!--        </div>-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-ui"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.uiConfig}}</div>-->
<!--        </div>-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-component"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.componentDeploy}}</div>-->
<!--        </div>-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-webres"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.resourceDeploy}}</div>-->
<!--        </div>-->
<!--        <div class="menuItem subItem">-->
<!--          <div class="menuItemIcon"><icon class="o2icon-service"></icon></div>-->
<!--          <div class="menuItemCount o2icon-forward"></div>-->
<!--          <div class="menuItemText subItemText">{{lp.serviceDeploy}}</div>-->
<!--        </div>-->
<!--      </div>-->


    </div>
  </div>
</template>

<script setup>
import {getMenuJson} from '../util/menu.js';
import {ref} from 'vue';
import {o2, lp, component} from '@o2oa/component';

const menuJson = getMenuJson();

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
.menu{
  margin: 0;
  user-select: none;
}

.menuTitle{
  margin: 0 20px;
  height: 40px;
  font-size: 16px;
  color: #333333;
  line-height: 40px;
  border-bottom: 1px solid #E6E6E6;
}
.menuTitleText{
  float: left;
}
.menuTitleAction {
  text-align: right;
  width: 40px;
  height: 40px;
  float: right;
  cursor: pointer;
  color: #4A90E2;
}
.menuItem{
  height: 40px;
  cursor: pointer;
  line-height: 40px;
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
  margin: 0 30px 0 20px;
  color: #666666;
  text-align: left;
}
.menuItemCount{
  margin-right: 20px;
  float: right;
  width: 30px;
  font-size: 1px;
  color: #999999;
  line-height: 30px!important;
  text-align: right;
}
.subItem {
  height: 30px;
  line-height: 30px;
  padding-left: 10px;
  font-size: 14px;
}
.subItemText{
  margin: 0 30px 0 38px;
  color: #666666;
  text-align: left;
}
</style>
