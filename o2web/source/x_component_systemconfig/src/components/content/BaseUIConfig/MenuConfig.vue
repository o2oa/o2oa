<template>
  <div class="item_menu_config" ref="menuConfigNode" :style="computeHeight">
    <div class="item_lnk_area deepColor_bg" @dragover="dragoverLink" @dragenter="dragenterLink" @dragleave="dragleaveLink"  @drop="dropLink">
      <ul class="item_link_ul" v-if="linkList.length">
        <MenuLink v-for="item in linkList" :key="item.uuid" :data="item" @dragItem="dragItem" @dragItemEnd="dragItemEnd" @loaded="recordLinkItems" @ungroup="ungroup" @removeItem="removeLink"></MenuLink>
      </ul>
      <div ref="linkPositionNode" class="item_link_position"></div>
    </div>
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

        <li class="item_menu_refresh_icon" :title="lp._uiConfig.menu.defaultMenu"></li>
      </ul>

      <div class="item_menu_content" v-if="currentList==='app'" @dragover="dragover" @drop="drop">
        <div class="item_menu_content_loading" v-if="!menuList_app.length"></div>
        <ul class="item_menu_ul" v-else>
          <MenuItem v-for="item in menuList_app" :key="item.id" :data="item" @dragItem="dragItem" @dragItemEnd="dragItemEnd" @loaded="recordAppItems" @ungroup="ungroup"></MenuItem>
        </ul>
      </div>
      <div class="item_menu_content" v-if="currentList==='process'" @dragover="dragover" @drop="drop">
        <div class="item_menu_content_loading" v-if="!menuList_process.length"></div>
        <ul class="item_menu_ul" v-else>
          <MenuItem v-for="item in menuList_process" :key="item.id" :data="item" @dragItem="dragItem" @dragItemEnd="dragItemEnd" @loaded="recordAppItems"></MenuItem>
        </ul>
      </div>
      <div class="item_menu_content" v-if="currentList==='infor'" @dragover="dragover" @drop="drop">
        <div class="item_menu_content_loading" v-if="!menuList_infor.length"></div>
        <ul class="item_menu_ul" v-else>
          <MenuItem v-for="item in menuList_infor" :key="item.id" :data="item" @dragItem="dragItem" @dragItemEnd="dragItemEnd" @loaded="recordAppItems"></MenuItem>
        </ul>
      </div>
      <div class="item_menu_content" v-if="currentList==='query'" @dragover="dragover" @drop="drop">
        <div class="item_menu_content_loading" v-if="!menuList_query.length"></div>
        <ul class="item_menu_ul" v-else>
          <MenuItem v-for="item in menuList_query" :key="item.id" :data="item" @dragItem="dragItem" @dragItemEnd="dragItemEnd" @loaded="recordAppItems"></MenuItem>
        </ul>
      </div>

    </div>

    <div class="item_action_area">
      <div class="systemconfig_item_title" :class="currentTitleDefalut">{{lp._uiConfig.defaultMenu}}</div>
      <div class="systemconfig_item_info">{{lp._uiConfig.defaultMenuInfo}}</div>
      <div class="item_button_area">
        <button class="mainColor_bg" @click="saveMenuData('defaultMainMenuData')">{{lp._uiConfig.saveMenu}}</button>
        <button v-if="!!defaultMenuData" @click="clearDefaultMenu">{{lp._uiConfig.clearMenu}}</button>
        <button v-if="!!defaultMenuData && currentMenuType!='default'" @click="loadDefaultMenuData">{{lp._uiConfig.loadMenu}}</button>
      </div>

      <div class="systemconfig_item_title" :class="currentTitleForce">{{lp._uiConfig.forceMenu}}</div>
      <div class="systemconfig_item_info">{{lp._uiConfig.forceMenuInfo}}</div>
      <div class="item_button_area">
        <button class="mainColor_bg" @click="saveMenuData('forceMainMenuData')">{{lp._uiConfig.saveMenu}}</button>
        <button v-if="!!forceMenuData" @click="clearForceMenu">{{lp._uiConfig.clearMenu}}</button>
        <button v-if="!!forceMenuData && currentMenuType!='force'" @click="loadForceMenuData">{{lp._uiConfig.loadMenu}}</button>
      </div>

      <div class="systemconfig_item_title">{{lp._uiConfig.userMenu}}</div>
      <div class="systemconfig_item_info">{{lp._uiConfig.userMenuInfo}}</div>
      <div class="item_button_area">
        <button @click="clearUserMenu">{{lp._uiConfig.clearUserMenu}}</button>
      </div>
    </div>
  </div>

</template>

<script setup>
import {ref, computed, onUpdated} from 'vue';
import {o2, lp, component} from '@o2oa/component';
import {
  getPublicData,
  clearPublicData,
  loadComponents,
  loadProcessApplication,
  loadPortalApplication,
  loadInforApplication,
  loadQueryApplication } from '@/util/acrions';
import MenuItem from './MenuItem.vue';
import MenuLink from './MenuLink.vue';
import {isOverlap} from '@/util/common';

const menuConfigNode = ref();
const defaultMenuData = ref();
const forceMenuData = ref();

const menuList_app = ref([]);
const menuList_process = ref([]);
const menuList_infor = ref([]);
const menuList_query = ref([]);
const linkList = ref([]);

const currentList = ref('app');
const currentMenuType = ref('force');

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
const currentTitleDefalut = computed(()=>{
  return currentMenuType.value==='default' ? 'mainColor_color' : '';
});
const currentTitleForce = computed(()=>{
  return currentMenuType.value==='force' ? 'mainColor_color' : '';
});

let menuDataPromise = null;
const getMenuData = ()=>{
  if (menuDataPromise) return menuDataPromise;
  menuDataPromise = Promise.all([getPublicData('defaultMainMenuData'), getPublicData('forceMainMenuData')]).then((dataArr)=>{
    defaultMenuData.value = dataArr[0];
    forceMenuData.value = dataArr[1];
    currentMenuType.value = (dataArr[1]) ? 'force' : 'default';
    return dataArr[1] || dataArr[0];
  });
  return menuDataPromise;
}

const reloadDefaultMenuData = ()=>{
  menuDataPromise = null;
  getMenuData().then(()=>{
    loadMenuList('app', defaultMenuData.value);
    currentMenuType.value = 'default';
  });
}
const reloadForceMenuData = ()=>{
  menuDataPromise = null;
  getMenuData().then(()=> {
    loadMenuList('app', forceMenuData.value);
    currentMenuType.value = 'force';
  });
}

const loadDefaultMenuData = ()=>{
  loadMenuList('app', defaultMenuData.value);
  currentMenuType.value = 'default';
}
const loadForceMenuData = ()=>{
  loadMenuList('app', forceMenuData.value);
  currentMenuType.value = 'force';
}
const clearDefaultMenu = (e)=>{
  component.confirm("warn", e, lp._uiConfig.clearDefaultMenuDataTitle, lp._uiConfig.clearDefaultMenuData, 350, 170, (dlg)=>{
    clearPublicData('defaultMainMenuData').then(()=>{
      defaultMenuData.value = null;
      component.notice(lp._uiConfig.clearDefaultMenuDataSuccess, "success");
      if (currentMenuType.value==='force'){
        reloadForceMenuData();
      }else{
        reloadDefaultMenuData();
      }
    });
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);
}
const clearForceMenu = (e)=>{
  component.confirm("warn", e, lp._uiConfig.clearForceMenuDataTitle, lp._uiConfig.clearForceMenuData, 350, 170, (dlg)=>{
    clearPublicData('forceMainMenuData').then(()=>{
      forceMenuData.value = null;
      component.notice(lp._uiConfig.clearForceMenuDataSuccess, "success");
      if (currentMenuType.value==='force'){
        reloadForceMenuData();
      }else{
        reloadDefaultMenuData();
      }
    });
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);
}
const clearUserMenu = (e)=>{
  component.confirm("warn", e, lp._uiConfig.clearUserMenuData, lp._uiConfig.clearUserMenuDataConfirm, 380, 120, (dlg)=>{
    const id = o2.uuid();

    o2.UD.putPublicData("clearCustomMenuDataFlag", {"id": id.toString()}, (dlg)=>{
      component.notice(lp._uiConfig.clearForceMenuDataSuccess, "success");
    });

    dlg.close();
  }, dlg=>dlg.close());
}

const retrieveMenuData = (type)=>{
  const list = getCurrentMenuList(type);
  return list.value.map((item)=>{
    return (item.type==='group') ? item : {
      id: item.id,
      name: item.name,
      type: item.type
    }
  });
}

const saveMenuData = (name)=>{
  const menuData = {
    "appList": retrieveMenuData('app'),
    "processList": retrieveMenuData('process'),
    "inforList": retrieveMenuData('infor'),
    "queryList": retrieveMenuData('query'),
    "linkList": linkList.value,
  };
  const info = (name === 'forceMainMenuData') ? lp._uiConfig.saveForceMenuDataSuccess : lp._uiConfig.saveDefaultMenuDataSuccess
  o2.UD.putPublicData(name, menuData, ()=>{
    component.notice(info, "success");
    if (name === 'forceMainMenuData'){
      reloadForceMenuData();
    }else{
      reloadDefaultMenuData();
    }
  });
}

const loadMenuList = async (listType, menuData)=>{
  currentList.value = listType;
  const dataPromise = {
    appList: [loadComponents, loadPortalApplication],
    processList: [loadProcessApplication],
    inforList: [loadInforApplication],
    queryList: [loadQueryApplication],
  }[listType+'List'].reduce((p, v)=>{
    p.push(v());
    return p;
  }, [menuData || getMenuData()]);

  const dataArr = await Promise.all(dataPromise);
  const mData = dataArr.shift() || {
    "appList": [],
    "processList": [],
    "inforList": [],
    "queryList": []
  };
  const olist = dataArr.flat();
  const orderMenu = mData[listType+'List'];

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
  if (!mData.linkList){
    o2.JSON.get('../o2_core/o2/xDesktop/$Default/defaultLnk.json', (data)=>{
      linkList.value = data;
      mData.linkList = linkList.value;
    });
  }else{
    linkList.value = mData.linkList
  }
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
let overItem = null;
function checkDargOverItem(rect){
  overItem = null;
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

const getCurrentMenuList = (type)=>{
  switch (type || currentList.value){
    case 'app':
      return menuList_app;
    case 'process':
      return menuList_process;
    case 'infor':
      return menuList_infor;
    case 'query':
      return menuList_query;
  }
}

const checkDargPosition = (rect)=>{
  const p = {
    x: rect.x+rect.width/2,
    y: rect.y+rect.height/2
  }
  for (const item of menuItems){
    //const item = menuItems[i];
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
  e.stopPropagation();
  if (dragging){
    const draggingData = dragging.node.retrieve('data');
    if (draggingData.id){
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
    }else{
      e.dataTransfer.dropEffect = "none";
    }
  }
}
function drop(e){
  e.preventDefault();
  e.stopPropagation();
  if (dragging){
    const draggingData = dragging.node.retrieve('data');
    const overItemData = (overItem) ? overItem.node.retrieve('data') : null;

    const nodeList = e.currentTarget.querySelectorAll('li');
    const list = [];
    nodeList.forEach((node, i)=>{
      const d = node.retrieve('data');
      if (d===overItemData){
        if (d.type==='group'){
          d.itemDataList.push(draggingData);
          list.push(d);
        }else{
          list.push({
            id: o2.uuid(),
            name: 'Group',
            type: 'group',
            visible: true,
            isNew: true,
            itemDataList: [d, draggingData]
          });
        }
      }else{
        if (!overItemData  || d!==draggingData) list.push(d);
      }
    });
    if (overItem) overItem.maskNode.style.display='none';
    getCurrentMenuList().value = list;
  }
}
const dragItem = (e)=>{
  dragging = e;
}
const dragItemEnd = (e)=>{
  dragging = null;
}
const ungroup = (e)=>{
  const menuList = getCurrentMenuList();
  menuList.value.push(e.item);
  if (!e.group.itemDataList.length){
    e.closeGroup().then(()=>{
      menuList.value.splice(menuList.value.indexOf(e.group), 1);
    });
  }
}

//link
const linkPositionNode = ref();
const linkItems = [];
const recordLinkItems = (node)=>{
  const iconNode = node.querySelector('.item_lnk_icon');
  const rect = iconNode.getBoundingClientRect();
  linkItems.push({node, rect, iconNode});
};

const checkDargLinkPosition = (rect)=>{
  const p = {
    x: rect.x+rect.width/2,
    y: rect.y+rect.height/2
  }
  linkPositionNode.value.show();
  for (const item of linkItems){
    if (p.y<item.rect.y && p.y>item.rect.y-20){
      linkPositionNode.value.inject(item.node, "before");
      break;
    }else if(p.y>item.rect.y+item.rect.width && p.y<item.rect.y+item.rect.width+20){
      linkPositionNode.value.inject(item.node, "after");
      break;
    }
  }
}

const dragenterLink = ()=>{
  // linkPositionNode.value.show();
  // linkPositionNode.value.inject(menuConfigNode.value.querySelector('.item_link_ul'));
}

const dragleaveLink = (e)=>{
  // if (!e.currentTarget.contains(e.target)){
  //   linkPositionNode.value.hide();
  // }
}

const dragoverLink = (e)=>{
  e.preventDefault();
  e.stopPropagation();
  if (dragging){
    const draggingData = dragging.node.retrieve('data');
    if (draggingData.type==='group' ){
      e.dataTransfer.dropEffect = "none";
    }else{
      e.dataTransfer.dropEffect = "move";

      const rect = {
        x: e.clientX+dragging.offset.x+18,
        y: e.clientY+dragging.offset.y+18,
        width: dragging.size.x-36,
        height: dragging.size.y-36,
      }
      checkDargLinkPosition(rect)
    }
  }
}

const itemTypeObj = {
  group: data=>data.type==='group',
  portal: data=>data.hasOwnProperty('portalCategory'),
  component: data=>data.type==='system' || data.type==='custom',
  infor: data=>data.hasOwnProperty('documentType'),
  query: data=>data.hasOwnProperty('queryCategory'),
  process: data=>data.hasOwnProperty('applicationCategory')
}

const getItemType = (data)=>{
  for (const f of Object.keys(itemTypeObj)){
    if (itemTypeObj[f](data)) return f;
  }
}
const createLinkData = (data)=>{
  const type = getItemType(data);
  switch (type){
    case 'component':
      return {
        name: data.path,
        title: data.title,
        uuid: o2.uuid()
      };
    case 'portal':
      return {
        name: 'portal.Portal',
        title: data.name,
        iconData: data.icon,
        appType: 'portal',
        options: {
          portalId: data.id,
          appId: 'portal.Portal'+data.id
        },
        uuid: o2.uuid()
      };
    case 'infor':
      return {
        name: 'cms.Module',
        title: data.appName,
        iconData: data.appIcon,
        appType: 'cms',
        options: {
          columnData: {
            id: data.id
          },
          id: data.id,
          appId: 'cms.Modulebe'+data.id
        },
        uuid: o2.uuid()
      };
    case 'query':
      return {
        name: 'query.Query',
        title: data.name,
        iconData: data.icon,
        appType: 'query',
        options: {
          id: data.id,
          appId: 'query.Query'+data.id
        },
        uuid: o2.uuid()
      };
    case 'process':
      return {
        name: 'process.Application',
        title: data.name,
        iconData: data.icon,
        appType: 'process',
        options: {
          id: data.id,
          appId: 'process.Application'+data.id
        },
        uuid: o2.uuid()
      }
  }
}

const checkLinkList = ()=>{
  linkItems.splice(0, linkItems.length);
  const ul = menuConfigNode.value.querySelector('.item_link_ul');
  if (ul){
    const nodeList = menuConfigNode.value.querySelector('.item_link_ul').querySelectorAll('li');
    nodeList.forEach((node)=>{
      const iconNode = node.querySelector('.item_lnk_icon');
      const rect = iconNode.getBoundingClientRect();
      linkItems.push({node, rect, iconNode});
    });
    linkPositionNode.value.inject(ul);
  }
}

const dropLink = (e)=>{
  e.preventDefault();
  e.stopPropagation();
  if (dragging){
    const draggingData = dragging.node.retrieve('data');
    const linkData = (draggingData.id) ? createLinkData(draggingData) : draggingData;

    const idx = linkList.value.indexOf(linkData);
    if (idx!==-1) linkList.value.splice(idx, 1);

    const positionNode = linkPositionNode.value.getPrevious();
    if (positionNode){
      const postionData = positionNode.retrieve('data');
      linkList.value.splice(linkList.value.indexOf(postionData)+1, 0, linkData);
    }else{
      linkList.value.unshift(linkData);
    }
    linkPositionNode.value.hide();
  }
}

const removeLink = (linkItem)=>{
  linkList.value.erase(linkItem);
}

onUpdated(checkLinkList);

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
.item_link_ul{
  padding: 0;
  margin: 0;
  list-style: none;
}
.item_link_ul li{
  height: 60px;
  cursor: pointer;
  padding: 5px 0;
  text-align: center;
  user-select: none;
  overflow: hidden;
}
.item_menu_div{

}
.item_menu_div_text {
  padding: 5px 0;
  height: 32px;
  font-size: 12px;
  color: #333333;
}
.item_link_position{
  height: 2px;
  background-color: #ffffff;
  border-radius: 2px;
  width: 70px;
  margin: auto;
  display: none;
  position: absolute;
  margin-left: 5px;
}
</style>
