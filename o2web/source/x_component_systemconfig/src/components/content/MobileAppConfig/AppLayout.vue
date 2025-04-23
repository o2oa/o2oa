<template>
    <div class="systemconfig_area">
        <div class="app-layout">
            <div class="app-layout-preview">
                <div class="content"></div>
                <div class="bar">
                    <div class="item" v-for="item in ev" @click="clickItem(item)">
                        <div v-if="item.isMain" class="main">
                            <img v-if=" item.id ===  currentItem?.id " :src="'/'+mainImageFocusUrl" alt="main">
                            <img v-else :src="'/'+mainImageBlurUrl" alt="main">
                        </div>
                        <div v-else class="normal">
                            <div :class=" item.id ===  currentItem?.id  ?  ` icon-box mainColor_color ` : `icon-box ` ">
                                <i :class="item.iconClass + ' icon'"></i>
                            </div>
                            <div :class=" item.id ===  currentItem?.id  ? `text-box mainColor_color` : `text-box` ">{{ item.name }}</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="app-layout-form"></div>
        </div>
    </div>
</template>
<script setup>
import { lp } from '@o2oa/component';
import { ref, watch, onMounted } from 'vue';

const emit = defineEmits(['update:value']);

const props = defineProps({
    value: {
        type: Array,
        default: []
    },
    images: {
        type: Array,
        default: []
    },
});
const ev = ref((props.value ?? []));
watch(
    () => props.value,
    (v) => ev.value = v
);

const mainImageFocusUrl = ref('x_desktop/img/app/default/index_bottom_menu_logo_focus.png');
const mainImageBlurUrl = ref('x_desktop/img/app/default/index_bottom_menu_logo_blur.png');
watch(
    () => props.images,
    (images) => {
        setImageUrl(images)
    }
);
onMounted(()=> {
    const c = ev.value.filter( (e) => e.isMain === true)
    if (c && c.length > 0) {
        currentItem.value = c[0]
    }
    setImageUrl(props.images)
})

const setImageUrl = (images) => {
    const focus = images.filter((s)=> s.name === 'index_bottom_menu_logo_focus')
    if (focus && focus.length > 0) {
        mainImageFocusUrl.value = focus[0].path
    }
    const blur = images.filter((s)=> s.name === 'index_bottom_menu_logo_blur')
    if (blur && blur.length > 0) {
        mainImageBlurUrl.value = blur[0].path
    }
}
// 当前选中的
const currentItem = ref(null);
 
const clickItem = (item) => {
    currentItem.value = item;
    console.debug('c', currentItem.value)
}

const save = () => {
    emit('update:value', ev.value);
}

</script>
<style scoped>
.app-layout {
    display: flex;
    flex-direction: row;
    align-items: start;
    justify-content: start;
    padding: 20px 0 0 30px;
}
.app-layout-preview {
    position: relative;
    width: 317px;
    height: 580px;
    border: 1px solid #e7e7eb;
    display: flex;
    flex-direction: column;
}
.app-layout-preview .content {
    flex: 1;
}
.app-layout-preview .bar {
    height: 50px;
    border-top: 1px solid #e7e7eb;
    background-color: #ededed;
    display: flex;
    flex-direction: row;
}
.app-layout-preview .bar .item {
    flex: 1;
    height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
}
.app-layout-preview .bar .item .main {
    width: 42px;
    height: 42px;
}
.app-layout-preview .bar .item .main img {
    width: 42px;
    height: 42px;
 }
.app-layout-preview .bar .item .normal {
    display: flex;
    flex-direction: column;
    height: 42px;
    gap: 5px;
    align-items: center;
    justify-content: center;
}
.app-layout-preview .bar .item .normal .icon-box {
    width: 36px;
    height: 36x;
    display: flex;
    align-items: center;
    justify-content: center;
}
.app-layout-preview .bar .item .normal .text-box {
    color: #666;
    font-size: 12px;
}
.app-layout-form {
    flex: 1;
}
</style>