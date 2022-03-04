<template>
  <div class="menu">
    <div class="menuTitle">
      <div class="menuTitleLogo" style="background-image: url('../x_component_systemconfig/$Main/default/setting.png');"></div>
      <div class="menuTitleText">{{lp.title}}</div>
    </div>
<!--    <div class="searchArea">-->
<!--      <div class="searchInputArea">-->
<!--        <i class="o2icon-search"></i>-->
<!--        <div>-->
<!--          <input :placeholder="lp.searchkey">-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
    <div class="menuContent">
      <div v-for="menu in menuJson">
        <div class="menuItem" @click="menu.expand = !menu.expand">
          <div class="menuItemIcon"><i :class="'o2icon-'+menu.icon"></i></div>
          <div class="menuItemText">{{menu.title}}</div>
        </div>

        <div class="menuItemSubArea" v-show="!!menu.expand" >

            <div class="menuItem subItem" v-for="item in menu.children" :key="item" :class="{'menuItem_current mainColor_color': currentItem===item}"
                 @click="selectedItem(item)"
                 @mouseover="$event.currentTarget.addClass('menuItem_over')"
                 @mouseout="$event.currentTarget.removeClass('menuItem_over')"
            >
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

const menuJson = ref(getMenuJson());
let currentItem = ref(menuJson.value[0].children[0]);

const emit = defineEmits(['changeItem']);

emit("changeItem", currentItem.value.component);
// setContentType(currentItem.value.component);

// function subItemClass(item){
//   if (item.expand) currentItem=item;
//   return {'menuItem_current mainColor_color' : item.expand};
// }

function selectedItem(item){
  currentItem.value = item;
  emit("changeItem", item.component);
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
  /*display: none;*/
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
.searchArea{
  height: 30px;
  padding: 15px 20px;
}
.searchInputArea{
  border: 1px solid #f1f1f1;
  height: 30px;
  border-radius: 15px
}
.searchInputArea i{
  width: 30px;
  height: 30px;
  display: inline-block;
  float: left;
}
.searchInputArea div{
  height: 30px;
  margin-left: 30px;
}
.searchInputArea input{
  border: 0;
  line-height: 28px;
  display: inline-block;
  width: 99%;
  background: transparent;
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
  color: #999999;
}
.menuItem_current{
  background-color: #EBF1F7!important;
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
}
.menuItemText{
  margin: 0 30px 0 46px;
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
  color: #666666;
}
.subItemIcon {
  margin-left: 20px;
}
.subItemText{
  margin: 0 30px 0 40px;
  text-align: left;
}
</style>
