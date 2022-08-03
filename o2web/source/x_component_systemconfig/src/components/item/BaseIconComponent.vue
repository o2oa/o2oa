<template>
  <div class="item_input" v-if="value.path && iconJson">
    <div :class="getClass" :style="getIcon"></div>
  </div>
</template>

<script setup>
import {layout} from '@o2oa/component';
import {ref, computed} from 'vue';

const props = defineProps({
  value: Object,
  iconStyle: { type: Object, default: {} },
  iconWidth: { type: String, default: '50px' },
  iconHeight: { type: String, default: '50px' }
});

if (!layout.iconJson){
  layout.iconJson = fetch('../o2_core/o2/xDesktop/$Default/icons.json').then((res)=>{
    return res.json();
  });
}
const iconJson = ref();
layout.iconJson.then((data)=>{
  iconJson.value = data;
});

const getClass = computed(()=>{
  const startWidthUrl = props.value.path.startsWith('@url')
  const path = (startWidthUrl && (!props.value.iconPath || props.value.iconPath==='appicon.png')) ? 'Url' : props.value.path;
  return (iconJson.value[path]) ? 'componentItemIconSystem' : 'componentItemIconCustom';
});

function computeIconStyle(){
  const cmpt = props.value;
  const isUrl = cmpt.path.startsWith('@url');
  const iconObj = iconJson.value[cmpt.path] || ((isUrl && cmpt.iconPath==='appicon.png') ? iconJson.value['Url'] : null);

  const style = (iconObj) ? Object.assign({
    backgroundColor: iconObj.color,
    backgroundImage: `url('../o2_core/o2/xDesktop/$Default/appicons/${iconObj.icon}')`,
    height: props.iconHeight,
    width: props.iconWidth
  }, props.iconStyle) : {
    backgroundImage: (isUrl) ? `url('${cmpt.iconPath}')` : `url('../x_component_${cmpt.path.replace(/\./g, '_')}/$Main/${cmpt.iconPath}')`,
    height: props.iconHeight,
    width: props.iconWidth,
    backgroundSize: `${props.iconWidth} ${props.iconHeight}`
  };
  return style;
}
const getIcon = computed(computeIconStyle);
</script>

<style scoped>
.item_input{

}
.componentItemIconSystem{
  background-size: 26px 26px;
  background-position: center;
  background-repeat: no-repeat;
  border-radius: 40%;
  margin: auto;
}
.componentItemIconCustom{
  background-size: 50px 50px;
  background-position: center;
  background-repeat: no-repeat;
  margin: auto;
}
</style>
