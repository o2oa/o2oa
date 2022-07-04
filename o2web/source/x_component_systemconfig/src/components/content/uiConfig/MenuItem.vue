<template>
  <li ref="itemNode" draggable="true" @dragstart="dragstart" @dragenter="dragenter" @dragover="dragover" @dragend="dragend">
    <div class="item_menu_icon">
      <BaseIcon :value="data" :item-style="{padding: '0'}" icon-width="40px" icon-height="40px" :icon-style="{width:'40px', height: '40px', backgroundSize: '22px 22px'}"></BaseIcon>
      <div ref="iconMask" class="item_icon_mask"></div>
    </div>
    <div class="item_menu_text" >{{displayName}}</div>
  </li>

</template>

<script setup>
import {defineProps, onMounted, ref, defineEmits, nextTick, toRaw, onUpdated} from 'vue';
import {o2} from "@o2oa/component";
import BaseIcon from '@/components/item/BaseIcon.vue';

const emit = defineEmits(['dragItem', 'loaded']);

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


function dragstart(e){
  const node = e.currentTarget;
  node.addClass('drag');
  node.isDragging = true;
  e.dataTransfer.effectAllowed = 'move';

  const iconNode = node.querySelector('.item_menu_icon');
  const p = iconNode.getPosition();
  const size = iconNode.getSize();
  const offset = {
    x: p.x-e.clientX,
    y: p.y-e.clientY
  };
  emit('dragItem', {node, offset, size});
}
function dragend(e){
  const node = e.target;
  node.isDragging = false;
  node.removeClass('drag');
}

onMounted((e)=>{
  itemType.value = getItemNameType();
  getAppName();

  nextTick((e)=>{
    itemNode.value.store('data', toRaw(props.data));
    emit('loaded', itemNode.value);
  })
});

onUpdated(()=>{
  console.log(props.data);
})


// function dragenter(e){
//   // e.target.style.backgroundColor = '#999999'
// }
// function dragover(e){
//   //e.dataTransfer.dropEffect = 'move'
//   e.preventDefault();
//   // Set the dropEffect to move
//   // e.dataTransfer.dropEffect = "move";
// }
</script>

<style scoped>
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
</style>
