<template>
  <div class="item_input">
    <div class="componentGroupIcon" :style="Object.assign(getIcon(value), iconStyle)">
      <div style="padding: 6px 0 0 6px; overflow: hidden;">
        <BaseIcon v-for="item in value.itemDataList.slice(0,4)" key="item.id"
                  :value="item"
                  :item-style="{padding: '0', float:'left',clear: 'none'}" icon-width="12px" icon-height="12px"
                  :icon-style="{width:'12px', height: '12px', backgroundSize: '8px 8px', float: 'left', margin: '0 4px 4px 0'}"></BaseIcon>
      </div>
    </div>
  </div>
</template>

<script setup>
import BaseIcon from './BaseIcon.vue'

const props = defineProps({
  value: Object,
  iconStyle: { type: Object, default: {} },
  iconWidth: { type: String, default: '50px' },
  iconHeight: { type: String, default: '50px' }
});

function getIcon(){
  return {
    height: props.iconHeight,
    width: props.iconWidth
  };
}
</script>

<style scoped>
.item_input{

}
.componentGroupIcon{
  width: 50px;
  height: 50px;
  border-radius: 40%;
  margin: auto;
  background-color: #e6e6e6;
}
</style>
