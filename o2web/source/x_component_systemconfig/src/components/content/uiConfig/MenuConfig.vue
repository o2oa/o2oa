<template>
  <div class="item_menu_config" ref="menuConfigNode" :style="computeHeight">
    <div class="item_lnk_area deepColor_bg"></div>
    <div class="item_menu_area">
      <ul class="item_menu_title">
        <li :class="(currentList==='app') ? 'mainColor_bg' : ''"
            @click="loadMenuList('app')">{{lp._uiConfig.menu.application}}</li>

        <li :class="(currentList==='process') ? 'mainColor_bg' : ''"
            @click="loadMenuList('process')">{{lp._uiConfig.menu.process}}</li>

        <li :class="(currentList==='infor') ? 'mainColor_bg' : ''"
            @click="loadMenuList('infor')">{{lp._uiConfig.menu.cms}}</li>

        <li :class="(currentList==='query') ? 'mainColor_bg' : ''"
            @click="loadMenuList('query')">{{lp._uiConfig.menu.query}}</li>

        <li class="item_menu_refresh_icon" :title="lp._uiConfig.menu.defaultMenu" @click="test"></li>
      </ul>

      <div class="item_menu_content" v-if="currentList==='app'" @dragover="dragover" @drop="drop">
        <div class="item_menu_content_loading" v-if="!menuList_app.length"></div>
        <ul class="item_menu_ul" v-else>
          <MenuItem v-for="item in menuList_app" :data="item" @dragItem="dragItem" @loaded="recordAppItems"></MenuItem>
        </ul>
      </div>
      <div class="item_menu_content" v-if="currentList==='process'">
        <div class="item_menu_content_loading" v-if="!menuList_process.length"></div>
        <ul class="item_menu_ul" v-else>
          <MenuItem v-for="item in menuList_process" key="item.id" :data="item"></MenuItem>
        </ul>
      </div>
      <div class="item_menu_content" v-if="currentList==='infor'">
        <div class="item_menu_content_loading" v-if="!menuList_infor.length"></div>
        <ul class="item_menu_ul" v-else>
          <MenuItem v-for="item in menuList_infor" key="item.id" :data="item"></MenuItem>
        </ul>
      </div>
      <div class="item_menu_content" v-if="currentList==='query'">
        <div class="item_menu_content_loading" v-if="!menuList_query.length"></div>
        <ul class="item_menu_ul" v-else>
          <MenuItem v-for="item in menuList_query" key="item.id" :data="item"></MenuItem>
        </ul>
      </div>


    </div>

    <div class="item_action_area">
      <div class="systemconfig_item_title">{{lp._uiConfig.defaultMenu}}</div>
      <div class="systemconfig_item_info">{{lp._uiConfig.defaultMenuInfo}}</div>
      <div class="item_button_area">
        <button class="mainColor_bg">{{lp._uiConfig.saveMenu}}</button>
        <button>{{lp._uiConfig.clearMenu}}</button>
        <button>{{lp._uiConfig.loadMenu}}</button>
      </div>

      <div class="systemconfig_item_title">{{lp._uiConfig.forceMenu}}</div>
      <div class="systemconfig_item_info">{{lp._uiConfig.forceMenuInfo}}</div>
      <div class="item_button_area">
        <button class="mainColor_bg">{{lp._uiConfig.saveMenu}}</button>
        <button>{{lp._uiConfig.clearMenu}}</button>
        <button>{{lp._uiConfig.loadMenu}}</button>
      </div>


      <div class="systemconfig_item_title">{{lp._uiConfig.userMenu}}</div>
      <div class="systemconfig_item_info">{{lp._uiConfig.userMenuInfo}}</div>
      <div class="item_button_area">
        <button>{{lp._uiConfig.clearUserMenu}}</button>
      </div>
    </div>
  </div>

</template>

<script setup>
import {ref, computed, unref} from 'vue';
import {lp, layout} from '@o2oa/component';
import {getForceMenuData,
  getDefaultMenuData,
  loadComponents,
  loadProcessApplication,
  loadPortalApplication,
  loadInforApplication,
  loadQueryApplication} from '@/util/acrions';
import MenuItem from './MenuItem.vue'
import {isOverlap} from '@/util/common';

const menuConfigNode = ref();
const defaultMenuData = ref();
const forceMenuData = ref();
const menuData = ref( layout.userLayout.menuData || {
  "appList": [],
  "processList": [],
  "inforList": [],
  "queryList": []
});

let menuList_app = ref([]);
const menuList_process = ref([]);
const menuList_infor = ref([]);
const menuList_query = ref([]);

// const menuList_app = markRaw({value: []});
// const menuList_process = {value: []};
// const menuList_infor = {value: []};
// const menuList_query = {value: []};

const currentList = ref('app');

const computeHeight = computed(() => {
  if (menuConfigNode.value){
    var pNode = menuConfigNode.value.getParent('.systemconfig_area');
    if (pNode){
      const titleNode = pNode.querySelector('.systemconfig_title');
      const size = pNode.getSize();
      const titleSize = (titleNode) ? titleNode.getSize() : {y:0};
      const height = size.y - titleSize.y-40;
      return {height: height+'px'}
    }
  }
});

function test(){
  let list = [];
  list.push(menuList_app.value[0]);
  list.push(menuList_app.value[2]);
  list.push(menuList_app.value[1]);
  list = list.concat(menuList_app.value.slice(3));

  console.log(list);
  menuList_app.value = list;
}

async function loadMenuList(listType, menuType) {
  currentList.value = listType;

  const getMenuData = (menuType === 'force') ? [getForceMenuData()] : [getDefaultMenuData()];
  const dataPromise = {
    appList: [loadComponents, loadPortalApplication],
    processList: [loadProcessApplication],
    inforList: [loadInforApplication],
    queryList: [loadQueryApplication],
  }[listType+'List'].reduce((p, v)=>{
    p.push(v());
    return p;
  }, getMenuData);

  const dataArr = await Promise.all(dataPromise);
  const mData = dataArr.shift();
  if (mData) menuData.value = mData;
  const olist = dataArr.flat();
  const orderMenu = menuData.value[listType+'List'];

  const list = [];
  orderMenu.forEach((item)=>{
    if (item.type==='group'){
      list.push(item);
      item.itemDataList.forEach(m=>{
        const i = olist.findIndex(e => e.id===m.id);
        if (i!==-1){
          olist.splice(i,1);
        }
      });
    }else{
      const i = olist.findIndex(e => e.id===item.id);
      if (i!==-1){
        list.push(olist[i]);
        olist.splice(i,1);
      }
    }
  });

  switch (listType){
    case 'app':
      menuList_app.value = list.concat(olist);
      break;
    case 'process':
      menuList_process.value = list.concat(olist);
      break;
    case 'infor':
      menuList_infor.value = list.concat(olist);
      break;
    case 'query':
      menuList_query.value = list.concat(olist);
      break;
  }
  console.log(menuList_app)
  // menuList_app = unref(menuList_app);
}

loadMenuList('app');

const menuItems = [];
const recordAppItems = (node)=>{
  const iconNode = node.querySelector('.item_menu_icon');
  const rect = iconNode.getBoundingClientRect();
  const maskNode = node.querySelector('.item_icon_mask');
  menuItems.push({node, rect, maskNode, iconNode});
};


let dragging;

function checkDargOverItem(rect){
  var overItem = null;
  menuItems.forEach(function(item){
    if (!item.node.isDragging){
      if (isOverlap(rect, item.rect)){
        item.maskNode.style.display='block';
        overItem = item;
      }else{
        item.maskNode.style.display='none';
      }
    }
  });
  return overItem;
}
function checkDargPosition(rect){
  const p = {
    x: rect.x+rect.width/2,
    y: rect.y+rect.height/2
  }
  for (let i = 0; i<menuItems.length; i++){
    const item = menuItems[i];
    if (!item.node.isDragging){
      if (p.y>=item.rect.y && p.y<=item.rect.y+item.rect.height){
        if (p.x<item.rect.x && p.x>item.rect.x-20){
          dragging.node.inject(item.node, "before");
        }else if(p.x>item.rect.x+item.rect.width && p.x<item.rect.x+item.rect.width+20){
          dragging.node.inject(item.node, "after");
        }
      }
    }
    item.rect = item.iconNode.getBoundingClientRect();
  }
}

function dragover(e){
  e.preventDefault();
  const rect = {
    x: e.clientX+dragging.offset.x+18,
    y: e.clientY+dragging.offset.y+18,
    width: dragging.size.x-36,
    height: dragging.size.y-36,
  }
  const overItem = checkDargOverItem(rect);
  if (!overItem){
    checkDargPosition(rect)
  }
}
function drop(e){
  const nodeList = e.currentTarget.querySelectorAll('li');
  const list = [];
  nodeList.forEach((node)=>{
    list.push(node.retrieve('data'));
  });
  console.log(list);
  menuList_app.value = list;
}
function dragItem(e){
  dragging = e;
}

</script>

<style scoped>
.item_lnk_area{
  width: 80px;
  height: 100%;
  float: left;
}
.item_menu_area {
  width: 390px;
  height: 100%;
  background: #ffffff;
  box-shadow: 4px 0 6px 0 rgb(0 0 0 / 25%);
  opacity: 1;
  user-select: none;
  -webkit-user-select: none;
  float: left;
  border: 1px solid #cccccc;
  box-sizing: border-box !important;
  display: flex;
  flex-direction: column;
}
.item_action_area{
  height: 100%;
  margin-left: 470px;
}
.item_button_area{
  padding: 10px 10px 30px 20px;
}
.item_menu_title{
  font-size: 14px;
  font-weight: bold;
  color: #333333;
  text-align: left;
  padding: 20px;
  height: 30px;
  line-height: 30px;
  margin: 0;
}
.item_menu_title li{
  width: 60px;
  height: 28px;
  line-height: 28px;
  border-radius: 14px;
  text-align: center;
  float: left;
  margin-right: 6px;
  margin-left: 6px;
  cursor: pointer;
  list-style: none;
  padding: 0;
}
.item_menu_content{
  overflow: auto;
  height: 100%;
  padding: 10px 0;
}

li.item_menu_refresh_icon{
  height: 28px;
  width: 28px;
  float: right;
  background-position: center;
  background-repeat: no-repeat;
  cursor: pointer;
  background-image: url("../../../assets/refresh_menu.png");
}
.item_menu_content_loading{
  background-position: center;
  background-repeat: no-repeat;
  background-image: url("../../../assets/loading.gif");
  height: 300px;
}

.item_menu_ul{
  padding: 0;
  margin: 0;
  list-style: none;
}
.item_menu_ul li{
  padding: 0;
  margin: 0;
  width: 72px;
  height: 90px;
  margin-left: 18px;
  margin-bottom: 10px;
  float: left;
  text-align: center;
  cursor: pointer;
}
.item_menu_div{

}
.item_menu_div_text {
  padding: 5px 0;
  height: 32px;
  font-size: 12px;
  color: #333333;
}
</style>
