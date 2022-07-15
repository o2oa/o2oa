<template>
  <div class="item_input">
    <div :class="getClass()" :style="getIcon()"></div>
  </div>
</template>

<script setup>
const props = defineProps({
  value: Object,
  iconStyle: { type: Object, default: {} },
  iconWidth: { type: String, default: '50px' },
  iconHeight: { type: String, default: '50px' }
});

function getClass(){
  return (props.value.appIcon) ? 'componentItemIcon' : 'componentItemIconDefault';
}

function getIcon(){
  return (props.value.appIcon) ? {
    backgroundImage: `url('data:image/png;base64,${props.value.appIcon}')`,
    height: props.iconHeight,
    width: props.iconWidth,
    backgroundSize: `${props.iconWidth} ${props.iconHeight}`
  } : Object.assign({
    height: props.iconHeight,
    width: props.iconWidth
  }, props.iconStyle);
}
</script>

<style scoped>
.item_input{

}
.componentItemIconDefault{
  background-color: #bbbbbb;
  background-image: url(../../assets/cmsDefault.png);
  background-position: center;
  background-repeat: no-repeat;
  width: 40px;
  height: 40px;
  border-radius: 40%;
  background-size: 22px 22px;
}
.componentItemIcon{
  background-position: center;
  background-repeat: no-repeat;
  margin: auto;
}
</style>
