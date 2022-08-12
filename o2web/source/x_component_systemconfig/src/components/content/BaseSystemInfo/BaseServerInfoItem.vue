<template>
  <div ref="jsonNode" style="width: 98%">
    <el-descriptions class="margin-top" :column="2" size="small" border>
      <div v-for="key in Object.keys(data)" :key="key">
      <el-descriptions-item v-if="itemLp[key]" width="80px" :label="itemLp[key]" label-class-name="item_descriptions_label" class-name="item_descriptions_content">
        {{data[key]}}
      </el-descriptions-item>
      </div>
    </el-descriptions>


  </div>
</template>

<script setup>
import {onMounted, defineProps, ref} from 'vue';
import {lp, o2} from '@o2oa/component';

const props = defineProps({
  data: { type: Object, default: {} },
  itemLp: { type: Object, default: lp._systemInfo.serverData },
});
const jsonNode = ref();

// onMounted(()=>{
//   o2.require('o2.widget.JsonParse', ()=>{
//     (new o2.widget.JsonParse(props.data, jsonNode.value)).load();
//   })
// });

</script>

<style scoped>
.margin-top {
  margin-top: 20px;
}
</style>
