<template>
  <li ref="itemNode">
    <div class="item_menu_icon">
      <BaseIcon  draggable="true" @dragstart="dragstart" @dragend="dragend" @click="openGroup"
          :value="data" :item-style="{padding: '0'}" icon-width="40px" icon-height="40px" :icon-style="{width:'40px', height: '40px', backgroundSize: '22px 22px'}"></BaseIcon>
      <div ref="iconMask" class="item_icon_mask"></div>
    </div>
    <div class="item_menu_text" >{{displayName}}</div>
    <div class="item_group_content grayColor_bg" v-if="data.type==='group'" ref="groupArea" @dragover="dragover" @dragleave="dragleave" @dragenter="dragenter">
      <div class="item_group_content_title" @click="editorTitle" ref="titleNode">
        <span>{{data.name}}</span>
        <input class="item_group_title_input" v-model="data.name" @blur="readTitle"/>
      </div>
      <ul class="item_menu_ul">
        <MenuItem v-for="item in data.itemDataList" :key="item.id" :data="item" @dragItem="dragItem" @dragItemEnd="dragItemEnd" @loaded="recordAppItems"></MenuItem>
      </ul>
    </div>
  </li>

</template>

<script setup>
import {onMounted, ref, nextTick, onUpdated} from 'vue';
import {o2} from "@o2oa/component";
import BaseIcon from '@/components/item/BaseIcon.vue';
import {isOverlap} from "@/util/common";

const emit = defineEmits(['dragItem', 'dragItemEnd', 'loaded', 'ungroup']);

const props = defineProps({
  data: {
    type: Object,
    default: {}
  }
});

const itemType = ref('component');
const displayName = ref('');
const itemNode = ref();
const iconMask = ref();

function getAppNameCommon(){
  return props.data.name;
}
function getAppNameInfor(){
  return props.data.appName;
}
function getAppNameComponent(){
  if (props.data.path && props.data.path.indexOf("@url") !== 0 ){
    const appNames = props.data.path.split(".");
    let o = o2.xApplication;
    appNames.forEach((name)=>{
      if (!o[name]) o[name] = {};
      o = o[name];
    });
    return new Promise((resolve)=>{
      o2.xDesktop.requireApp(props.data.path, 'lp.' + o2.language, {
        onSuccess: function(){
          resolve(o.LP.title || props.data.title);
        },
        onFailure: function () {
          resolve(props.data.title);
        }
      });
    });
  }else{
    return props.data.title;
  }
}
function getItemNameType(){
  if (props.data.type==='group') return 'group';
  if (props.data.hasOwnProperty('portalCategory')) return 'portal';
  if (props.data.type==='system' || props.data.type==='custom') return 'component';
  if (props.data.hasOwnProperty('documentType')) return 'infor';
  if (props.data.hasOwnProperty('queryCategory')) return 'query';
  if (props.data.hasOwnProperty('applicationCategory')) return 'process';
}

function getItemNameMethod(){
  switch (itemType.value){
    case 'component':
      return getAppNameComponent;
    case 'infor':
      return getAppNameInfor;
    default:
      return getAppNameCommon;
  }
}

function getAppName(){
  const name = getItemNameMethod()();
  Promise.resolve(name).then((n)=>{
    displayName.value = n;
  })
}

onUpdated(()=>{
  getAppName();
});

function dragstart(e){
  const iconNode = e.currentTarget;
  const node = iconNode.getParent('li');

  node.addClass('drag');
  node.isDragging = true;
  e.dataTransfer.effectAllowed = 'move';

  const p = iconNode.getPosition();
  const size = iconNode.getSize();
  const offset = {
    x: p.x-e.clientX,
    y: p.y-e.clientY
  };
  emit('dragItem', {node, offset, size});
}
function dragend(e){
  const iconNode = e.currentTarget;
  const node = iconNode.getParent('li');
  node.isDragging = false;
  node.removeClass('drag');
  emit('dragItemEnd', e);
}


//group
let morph;
const maskNode = function(node){
  node.setStyle('filter', 'blur(5px)');
  new Mask(node, {
    destroyOnHide: true,
    onClick: ()=>{ closeGroup(); }
  });
  node.mask();
}
const unmaskNode = function(node){
  node.setStyle('filter', '');
  node.unmask();
}

const groupArea = ref();
const openGroup = ()=>{
  if (!groupArea.value) return false;
  const areaNode = itemNode.value.getParent('.item_menu_area');
  maskNode(areaNode);

  groupArea.value.setStyle('display', 'block');

  const size = areaNode.getSize();
  const height = size.y*0.8;
  const width = size.x-20;
  let y = (size.y*0.2)/2;
  const x = 85;

  return new Promise((resolve)=>{
    morph.start({
      top: y+'px',
      left: x+'px',
      width: width+'px',
      height: height+'px'
    }).chain(()=>{
      resolve();
    });
  });

}
const closeGroup = ()=>{
  const areaNode = itemNode.value.getParent('.item_menu_area');
  const iconNode = itemNode.value.querySelector('.item_menu_icon');
  const p = iconNode.getPosition(groupArea.value.getOffsetParent());

  return new Promise((resolve)=>{
    morph.start({
      top: p.y+'px',
      left: p.x+'px',
      width: '40px',
      height: '40px'
    }).chain(()=>{
      groupArea.value.setStyle('display', 'none');
      unmaskNode(areaNode);
      resolve();
    });
  })
}

//drag
let dragging;
let overItem = null;
const menuItems = [];

const dragItem = (e)=>{
  dragging = e;
}
const dragItemEnd = (e)=>{
  e.preventDefault();
  e.stopPropagation();
  if (dragging){
    const nodeList = groupArea.value.querySelectorAll('li');
    const list = [];
    nodeList.forEach((node, i)=>{
      const d = node.retrieve('data');
      list.push(d);
    });
    props.data.itemDataList = list;
    if (!dragging.node.parentNode){
      const item = dragging.node.retrieve('data');
      const group = props.data;
      emit('ungroup', {group, item, closeGroup})
    }
    dragging = null;
  }
}
const recordAppItems = (node)=>{
  const iconNode = node.querySelector('.item_menu_icon');
  const rect = iconNode.getBoundingClientRect();
  const maskNode = node.querySelector('.item_icon_mask');
  menuItems.push({node, rect, maskNode, iconNode});
};


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

const getCurrentMenuList = ()=>{
  switch (currentList.value){
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

const dragover = (e)=>{
  e.preventDefault();
  e.stopPropagation();
  if (dragging){
    const rect = {
      x: e.clientX+dragging.offset.x+18,
      y: e.clientY+dragging.offset.y+18,
      width: dragging.size.x-36,
      height: dragging.size.y-36,
    }
    checkDargPosition(rect);
  }
}

const dragleave = (e)=>{
  e.preventDefault();
  e.stopPropagation();
  if (dragging){
    if (!e.currentTarget.contains(e.relatedTarget)){
      dragging.node.dispose();
    }
  }
}
const dragenter = (e)=>{
  e.preventDefault();
  e.stopPropagation();
  if (dragging){
    const ul = e.currentTarget.querySelector('.item_menu_ul');
    if (!ul.contains(dragging.node)) dragging.node.inject(ul);
  }
}

//editor title
const titleNode = ref();
const editorTitle = ()=>{
  const node = titleNode.value;
  node.firstChild.hide();
  node.lastChild.show().focus();
}
const readTitle = (e)=>{
  const node = e.currentTarget;
  node.hide();
  node.previousSibling.show();
  getAppName();
  //emit('changeTitle', props.data);
}

onMounted((e)=>{
  itemType.value = getItemNameType();
  getAppName();

  nextTick((e)=>{
    itemNode.value.store('data', (props.data));
    emit('loaded', itemNode.value);

    if (groupArea.value){
      morph = new Fx.Morph(groupArea.value, {
        "duration": 200,
        "transition": Fx.Transitions.Quart.easeOut
      });

      const iconNode = itemNode.value.querySelector('.item_menu_icon');
      const areaNode = itemNode.value.getParent('.item_menu_area');
      groupArea.value.inject(areaNode, 'after');
      groupArea.value.position({
        relativeTo: iconNode,
        position: 'upperLeft',
        edge: 'upperLeft'
      });

      if (props.data.isNew){
        openGroup().then(()=>{
          editorTitle()
        });
        props.data.isNew = false;
      }
    }

  })
});
</script>

<style scoped>
.item_menu_ul{
  padding: 0;
  margin: 0;
  list-style: none;
}
li{
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
li.drag{
  opacity: 0.3
}
.item_menu_text {
  padding: 5px 0;
  height: 32px;
  font-size: 12px;
  color: #333333;
}
.item_menu_icon {
  width: 40px;
  height: 40px;
  border-radius: 40%;
  background-size: 22px 22px;
  margin: auto;
  position: relative;
}
.item_icon_mask{
  width: 60px;
  height: 60px;
  display: none;
  position: absolute;
  opacity: 0.3;
  background-color: #999999;
  border-radius: 40%;
  top: -10px;
  left: -10px;
}
.item_group_content{
  position: absolute;
  width: 40px;
  height: 40px;
  border-radius: 20px;
  background-size: 22px 22px;
  overflow: hidden;
  padding: 5px 0;
  display: none;
}
.item_group_content_title{
  height: 60px;
  padding: 8px 0 12px 0;
  line-height: 40px;
  font-size: 16px;
  text-align: center;
  overflow: hidden;
}
.item_group_title_input{
  border: 0;
  height: 40px;
  line-height: 40px;
  padding: 0 10px;
  font-size: 16px;
  text-align: center;
  width: 90%;
  margin: auto;
  border-radius: 20px;
  display: none;
}
</style>

