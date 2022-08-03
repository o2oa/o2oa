<template>
  <li ref="itemNode" @mouseover="overitem" @mouseout="outitem">
    <div class="item_lnk_icon" :style="{backgroundImage: 'url('+linkIcon+')'}" draggable="true" @dragstart="dragstart" @dragend="dragend"></div>
    <div class="item_lnk_text">{{ displayName }}</div>
    <div :title="lp._uiConfig.deleteLink" class="item_link_action icon_off_light" @click="removeLink"></div>
  </li>

</template>

<script setup>
import {onMounted, onUpdated, ref, nextTick} from 'vue';
import {lp, layout, o2} from "@o2oa/component";
import {getComponent} from '@/util/acrions';

const emit = defineEmits(['dragItem', 'dragItemEnd', 'loaded', 'removeItem']);

const props = defineProps({
  data: {
    type: Object,
    default: {}
  }
});
const linkIcon = ref('');
const itemNode = ref();
const displayName = ref('');

function getAppNameCommon(){
  return props.data.title;
}
function getAppNameComponent(){
  if (props.data.name && props.data.name.indexOf("@url") !== 0 ){
    const appNames = props.data.name.split(".");
    let o = o2.xApplication;
    appNames.forEach((name)=>{
      if (!o[name]) o[name] = {};
      o = o[name];
    });
    return new Promise((resolve)=>{
      o2.xDesktop.requireApp(props.data.name, 'lp.' + o2.language, {
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

const getItemType = (data)=>{
  for (const f of Object.keys(itemTypeObj)){
    if (itemTypeObj[f](data)) return f;
  }
}

function getItemNameMethod(){
  if (props.data.appType){
    return getAppNameCommon;
  }else{
    return getAppNameComponent;
  }
}

function getAppName(){
  const name = getItemNameMethod()();
  Promise.resolve(name).then((n)=>{
    displayName.value = n;
  })
}


if (!layout.iconJson){
  layout.iconJson = fetch('../o2_core/o2/xDesktop/$Default/icons.json').then((res)=>{
    return res.json();
  });
}


const getIcon = async () => {
  if (props.data.icon) return props.data.icon;
  const name = props.data.name;

  if (props.data.iconData) return 'data:image/png;base64,' + props.data.iconData;
  if (props.data.iconPath) return props.data.iconPath;

  if (name.substring(0, 4) === '@url') {
    const cmpt = await getComponent(props.data.title);
    if (cmpt.iconData)  return 'data:image/png;base64,' + cmpt.iconData;
    if (cmpt.iconPath)  return cmpt.iconPath;

    return layout.iconJson.then((json)=>{
      if (json['Url'] && json['Url'].icon) return '../o2_core/o2/xDesktop/$Default/appicons/' + json['Url'].icon;
      return layout.path + 'appicons/url.png';
    });
  } else {
    const p = (props.data.appType) ? props.data.appType + 'Default' : props.data.name;

    return layout.iconJson.then((json)=>{
      if (json[p] && json[p].icon)  return '../o2_core/o2/xDesktop/$Default/appicons/' + json[p].icon;
      return '../x_component_' + props.data.name.replace(/\./g, '_') + '/$Main/appicon.png';
    });

  }
}

const overitem = (e)=>{
  const li = e.currentTarget;
  li.addClass('overColor_bg');
  li.querySelector('.item_lnk_text').show();
  li.querySelector('.item_link_action').show();
};

const outitem = (e)=>{
  const li = e.currentTarget;
  li.removeClass('overColor_bg');
  li.querySelector('.item_lnk_text').hide();
  li.querySelector('.item_link_action').hide();
};

const removeLink = ()=>{
  emit('removeItem', props.data);
  // p.removeLink(props.data)
}

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

onUpdated(()=>{
  getAppName();
});
onMounted(()=>{
  getAppName();
  nextTick(async () => {
    linkIcon.value = await getIcon();

    itemNode.value.store('data', (props.data));
    emit('loaded', itemNode.value);

    debugger;
  })
});

</script>

<style scoped>
.item_lnk_icon{
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  margin: auto;
  margin-top: 10px;
  height: 30px;
  width: 30px;
}
.item_lnk_text{
  height: 20px;
  line-height: 20px;
  padding: 0 5px;
  color: #ffffff;
  font-size: 12px;
  display: none;
  /*visibility: hidden;*/
  /*opacity: 0;*/
}
.item_link_action{
  width: 14px;
  height: 14px;
  cursor: pointer;
  position: relative;
  float: right;
  margin-right: 3px;
  top: -62px;
  border-radius: 8px;
  background-position: center;
  background-repeat: no-repeat;
  display: none;
  /*visibility: hidden;*/
  /*opacity: 0;*/
}
li.drag{
  opacity: 0.3
}
</style>

