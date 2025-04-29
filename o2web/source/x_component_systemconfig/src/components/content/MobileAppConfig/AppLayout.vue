<template>
    <div class="systemconfig_area">
        <div class="app-layout">
            <div class="app-layout-preview">
                <div class="top-bar mainColor_bg" v-if="!hiddenAppBar && currentItem?.isNative !== true">{{
                    currentItem?.name || '' }}</div>
                <div class="content">
                    <div class="content-container" id="app_content"></div>
                    <div class="content-mask"></div>
                    <div class="content-operation-button">
                        <button class="mainColor_bg" v-if="currentItem" @click="updatePageItem">修改</button>
                        <button class="mainColor_bg" v-if="currentItem" @click="deletePageItem">删除</button>
                    </div>
                </div>
                <div class="bar">
                    <div class="item" v-for="(item, index) in ev" @click="clickItem(item)" :draggable="true"
                        @dragstart="onDragStart($event, index)" @dragover="onDragOver($event)"
                        @drop="onDrop($event, index)" @dragend="onDragEnd">
                        <div v-if="item.isMain" class="main">
                            <img v-if="item.id === currentItem?.id" :src="'/' + mainImageFocusUrl" alt="main">
                            <img v-else :src="'/' + mainImageBlurUrl" alt="main">
                        </div>
                        <div v-else class="normal">
                            <div :class="item.id === currentItem?.id ? ` icon-box mainColor_color ` : `icon-box `">
                                <i :class="item.iconClass + ' icon'"></i>
                            </div>
                            <div :class="item.id === currentItem?.id ? `text-box mainColor_color` : `text-box`">{{
                                item.name }}</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="app-layout-form">
                <button class="mainColor_bg" style="margin-left: 100px;" v-if="ev.length < 5" @click="newPageItem">新增</button>
            </div>
        </div>
        <el-dialog v-model="dialogVisible" :title="dialogTitle" :close-on-click-modal="false" @closed="closedDialog">
            <AppLayoutForm v-if="dialogVisible" :item="dialogPageItem" @savePageItem="(item) => {
                console.debug('保存数据', item)
                const index = ev.findIndex((p) => p.id === item.id)
                if (index > -1) { // 修改
                    ev.splice(index, 1, item)
                } else { // 新增 TODO 校验相同页面
                    ev.push(item)
                }
                if (item.isMain === true) {
                    updateMainItem(item)
                }
                save()
                clickItem(item)
                nextTick(() => dialogVisible = false)
            }"></AppLayoutForm>
        </el-dialog>

    </div>
</template>
<script setup>
import { lp } from '@o2oa/component';
import { ref, watch, onMounted, nextTick } from 'vue';
import staticData from '../../../util/data'
import { copyObject } from '../../../util/common'
import AppLayoutForm from './AppLayoutForm.vue';

const s = staticData()
const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogPageItem = ref(null);
const newPageItem = () => {
    const newItem = s.defaultAppHomePageList[0];
    newItem.id = o2.uuid();
    dialogPageItem.value = newItem;
    dialogTitle.value = '新增页面';
    dialogVisible.value = true;
}
// 更新页面
const updatePageItem = () => {
    if (!currentItem.value) {
        return
    }
    console.debug('=====> 打开更新页面')
    dialogPageItem.value = copyObject(currentItem.value);
    dialogTitle.value = '修改页面';
    dialogVisible.value = true;
}
const closedDialog = () => {
    console.debug('=====> closedDialog')
    dialogPageItem.value = null;
    dialogTitle.value = '';
}
// 删除页面
const deletePageItem = () => {
    if (!currentItem.value) {
        return
    }
    const index = ev.value.findIndex((p) => p.id === currentItem.value.id)
    console.debug('删除', index)
    if (index > -1) {
        const item = ev.value[index]
        ev.value.splice(index, 1)
        if (item.isMain === true && ev.value.length > 0) { // 如果删了主页，第一页作为主页
            updateMainItem(ev.value[0])
        }
        save()
    }
    const firstItem = ev.value.length > 0 ? ev.value[0] : null
    clickItem(firstItem)
}
// 修改 isMain 参数
const updateMainItem = (mainItem) => {
    ev.value = ev.value.map(item => ({
        ...item,
        isMain: item.id === mainItem.id
    }));
}

const emit = defineEmits(['update:value', 'saveLayout']);

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
// 页面数组
const ev = ref((props.value ?? []));
watch(
    () => props.value,
    (v) => ev.value = v
);

// 主页按钮图片
const mainImageFocusUrl = ref('x_desktop/img/app/default/index_bottom_menu_logo_focus.png');
const mainImageBlurUrl = ref('x_desktop/img/app/default/index_bottom_menu_logo_blur.png');
watch(
    () => props.images,
    (images) => {
        setImageUrl(images)
    }
);
onMounted(() => {
    console.debug(s)
    const c = ev.value.filter((e) => e.isMain === true)
    if (c && c.length > 0) {
        clickItem(c[0])
    }
    setImageUrl(props.images)
})
const setImageUrl = (images) => {
    const focus = images.filter((s) => s.name === 'index_bottom_menu_logo_focus')
    if (focus && focus.length > 0) {
        mainImageFocusUrl.value = focus[0].path
    }
    const blur = images.filter((s) => s.name === 'index_bottom_menu_logo_blur')
    if (blur && blur.length > 0) {
        mainImageBlurUrl.value = blur[0].path
    }
}


// 当前选中的页面对象
const currentItem = ref(null);
// 是否隐藏头部
const hiddenAppBar = ref(false);
// 点击底部Item
const clickItem = (item) => {
    currentItem.value = item;
    setContentHtml(item)
}
// 页面操作
const setContentHtml = (item) => {
    const divElement = document.getElementById('app_content');
    divElement.innerHTML = ''
    hiddenAppBar.value = false
    if (!item) {
        return
    }
    if (item.isNative === true) {
        let url = ''
        switch (item.nativeKey) {
            case 'home':
                url = s.homePageUrl;
                break;
            case 'im':
                url = s.imPageUrl;
                break;
            case 'contact':
                url = s.contactPageUrl;
                break;
            case 'app':
                url = s.appPageUrl;
                break;
            case 'settings':
                url = s.settingPageUrl;
                break;
        }
        if (url) {
            divElement.innerHTML = `<img style="width:100%;height:100%;" src="${url}" alt="${item.name}">`
        }
    } else if (item.portal) {
        const portalId = item.portal.portalId;
        hiddenAppBar.value = item.portal.hiddenAppBar || false;
        const pageId = item.portal.pageId;
        const portalParameters = item.portal.portalParameters;
        if (portalId) {
            let url = `../x_desktop/portalmobile.html?id=${portalId}`
            if (pageId) {
                url += `&page=${pageId}`
            }
            if (portalParameters) {
                url += `&parameters=${portalParameters}`
            }
            divElement.innerHTML = `<iframe src="${url}" width="100%" height="100%" frameborder="0"></iframe>`;
        }
    }
}
// 底部 Item 拖拽
let draggedIndex = null;
const onDragStart = (event, index) => {
    draggedIndex = index;
    event.dataTransfer.effectAllowed = 'move';
};
const onDragOver = (event) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
};
const onDrop = (event, dropIndex) => {
    event.preventDefault();
    if (draggedIndex !== null && draggedIndex !== dropIndex) {
        const draggedItem = ev.value[draggedIndex];
        ev.value.splice(draggedIndex, 1); // Remove the dragged item
        ev.value.splice(dropIndex, 0, draggedItem); // Insert it at the new position
        save()
    }
};
const onDragEnd = () => {
    draggedIndex = null;
};

const save = () => {
    emit('update:value', ev.value);
    emit('saveLayout', ev.value);
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
    width: 298px;
    height: 648px;
    border: 1px solid #e7e7eb;
    display: flex;
    flex-direction: column;
}

.app-layout-preview .top-bar {
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
}

.app-layout-preview .content {
    flex: 1;
    height: calc(100% - 99px);
    position: relative;
}

.app-layout-preview .content .content-container {
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
}

.app-layout-preview .content .content-mask {
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    background-color: #00000000;
}

.app-layout-preview .content .content-operation-button {
    position: absolute;
    top: 0;
    left: 298px;
    right: -80px;
    bottom: 0;
    display: flex;
    flex-direction: column;
    gap: 10px;
    
}
.app-layout-preview .content .content-operation-button button {
    border-radius: 0px 100px 100px 0px!important;
    margin-left: 0px!important;
}

.app-layout-preview .content .content-container img {
    width: 100%;
    height: 100%;
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
    height: 36px;
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