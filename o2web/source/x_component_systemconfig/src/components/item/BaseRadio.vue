<template>
  <div class="item">
    <label class="item_label" v-if="label" :style="labelStyle">{{label}}</label>
    <div class="item_input">
      <el-radio-group v-model="value" @change="changeValue($event)" :style="groupStyle">
        <el-radio v-for="op in options" key="op.value" :label="op.label">{{op.text}}</el-radio>
      </el-radio-group>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(['update:value', 'change']);

const props = defineProps({
  label: String,
  value: String,
  labelStyle: {
    type: Object,
    default: {}
  },
  options: {
    type: Object,
    default: {}
  },
  groupStyle: {
    type: Object,
    default: {}
  }
});

function changeValue(e){
  emit('change', e);
  emit('update:value', e);
}

</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 20px;
  font-size: 14px;
  color: #666666;
  clear: both;
  display: flex;
  flex-direction: row;
  align-items: center;
}
.item_label{
  text-align: left;
  overflow: hidden;
  font-size: 14px;
  color: #333333;
  clear: both;
  display: block;
  float: left;
  width: 80px;
  height: 32px;
  line-height: 32px;
}
.item_input{
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
}

button {
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
