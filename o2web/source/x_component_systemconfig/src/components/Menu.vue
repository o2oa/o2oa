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
<!--          <input :placeholder="lp.searchKey">-->
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

    <div class="menuBottom">
      <div class="menuTitleText" @click="(e)=>{layout.openApplication(e,'ConfigDesigner')}">JSON配置</div>
    </div>

<!--    <div class="menuItem" @click="(e)=>{layout.openApplication(e,'ConfigDesigner')}">-->
<!--      <div class="menuItemIcon"><i class="o2icon-log"></i></div>-->
<!--      <div class="menuItemText">JSON配置</div>-->
<!--    </div>-->


  </div>
</template>

<script setup>
import {getMenuJson} from '@/util/menu';
import {ref} from 'vue';
import {lp, layout} from '@o2oa/component';

const menuJson = ref(getMenuJson());
let currentItem = ref(menuJson.value[0].children[0]);

const emit = defineEmits(['changeItem']);

emit("changeItem", currentItem.value.component);

function selectedItem(item){
  currentItem.value = item;
  emit("changeItem", item.component);
}

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
  padding-left: 20px;
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
.menuBottom{
  margin: 0 20px;
  height: 60px;
  font-size: 18px;
  color: #333333;
  line-height: 60px;
  border-top: 1px solid #cccccc;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
</style>
