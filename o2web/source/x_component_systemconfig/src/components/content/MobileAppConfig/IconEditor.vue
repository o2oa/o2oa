<template>
  <div>
    <div class="item_title">{{title}}</div>
    <div class="item_img_area lightColor_bg">
      <img ref="imgNode" :src="'../'+value.path" alt="" v-if="value.path" @load="loadImage"/>
      <img ref="imgNode" :src="'data:image/png;base64,'+value.value" alt="" v-else/>
      <div>
        <div><button class="mainColor_bg" @click="changeImage">{{lp._appConfig.changeImage}}</button></div>
        <div style="margin-top: 20px"><button @click="changeToDefault">{{lp._appConfig.defaultImage}}</button></div>
      </div>
    </div>
    <input type="file" ref="uploadNode" @change="uploadImage" style="display: none"/>
  </div>

</template>

<script setup>
import {ref, computed, onMounted, nextTick} from 'vue';
import {lp, component, o2} from '@o2oa/component';
import {eraseAppStyleImage, uploadAppStyleImage} from '@/util/acrions';

const emit = defineEmits(['change']);

const imgNode = ref();
const imgSize = ref();
const uploadNode = ref();

const props = defineProps({
  value: { type: Object, default: {} }
});

const title = computed(()=>{
  const t = lp._appConfig.imageNames[props.value.name].text;
  return (imgSize.value) ? `${t} ${lp._appConfig.imageSzie}: ${imgSize.value.x} * ${imgSize.value.y}` : t;
});

const changeToDefault = (e)=>{
  const text = lp._appConfig.defaultImageInfo.replace('{name}', lp._appConfig.imageNames[props.value.name].text)
  component.confirm("warn", e, lp._appConfig.defaultImageTitle, text, 500, 170, async (dlg) => {
    await eraseAppStyleImage(lp._appConfig.imageNames[props.value.name].action);
    emit('change', e);
    dlg.close();
  }, (dlg)=>{
    dlg.close();
  }, null, component.content);
}

const changeImage = (e)=>{
  uploadNode.value.click();
}
const uploadImage = (e) => {
  if (uploadNode.value.files && uploadNode.value.files.length) {
    const formdata = new FormData();
    const file = uploadNode.value.files[0];
    formdata.append("file", file);

    uploadAppStyleImage(lp._appConfig.imageNames[props.value.name].action, formdata, file, ()=>{
      emit('change', e);
    });
  }
}
// 在线图片加载完成后计算大小
const loadImage = () => {
  if (imgNode.value){
      imgSize.value = {
        x: imgNode.value.naturalWidth,
        y: imgNode.value.naturalHeight
      }
    }
}

onMounted(()=>{
  nextTick(()=>{
    // base64  图片加载完成后直接读取大小
    if (imgNode.value){
      imgSize.value = {
        x: imgNode.value.naturalWidth,
        y: imgNode.value.naturalHeight
      }
    }
  });
})


// const appStyle = ref();
//
// const saveMobileIndex = (v)=>{
//   if (v==='default'){
//     appStyle.value.indexType = 'default';
//   }else{
//     appStyle.value.indexType = 'portal';
//     appStyle.value.indexPortal = v;
//   }
//   saveAppStyle(appStyle.value);
// }
//
// const load = ()=>{
//   getAppStyle().then((data)=>{
//     appStyle.value = {
//       images: data.images
//     }
//   });
// }
// load();

</script>

<style scoped>
.item_img_area{
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  margin-right: 20px;
  align-items: center;
  flex-wrap: wrap;
  border-radius: 20px;
  padding: 20px 30px;
  background-color: #f2f6fe;
}
</style>
