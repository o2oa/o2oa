<template>
    <div class="app-layout-form">
        <el-form label-position="top">
            <el-form-item>
                <el-radio-group v-model="pageItem.isNative">
                    <el-radio :label="true">原生页面</el-radio>
                    <el-radio :label="false">门户页面</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="原生页面" v-if="pageItem.isNative === true">
                <div class="al-checkbox-group">
                    <div v-for="item in s.defaultAppHomePageList"
                        :class="item.nativeKey === pageItem.nativeKey ? 'al-checkbox-button mainColor_bg' : 'al-checkbox-button'"
                        :key="item.nativeKey" @click="chooseNative(item)">{{ item.name }}</div>
                </div>
            </el-form-item>
            <el-form-item label="门户选择" v-else>
                <div style="display: flex;flex-direction: column; gap: 8px;padding: 0 12px;">
                    <el-select v-model="portal.portalId" size="default" placeholder="请选择门户" @change="(p) => {
                        console.debug('选择了 ', p)
                        if (p === 'default') {
                            portal.portalId = ''
                            pageItem.name = ''
                        } else {
                            portal.portalId = p;
                            const pObj = portalList.find((r) => r.id === p)
                            console.debug('选择了 ', pObj)
                            if (pObj) {
                                pageItem.name = pObj.name
                            }
                        }

                    }" popper-class="systemconfig">
                        <el-option value="default" :label="lp.default"></el-option>
                        <el-option v-for="portal in portalList" :key="portal.id" :value="portal.id"
                            :label="portal.name"></el-option>
                    </el-select>
                    <!-- <el-input type="text" v-model="portal.pageId" placeholder="请输入页面名称" class="item_input" size="default" /> -->
                    <el-input type="text" v-model="portal.portalParameters" placeholder="请输入门户参数" class="item_input"
                        size="default" />
                    <el-switch v-model="portal.hiddenAppBar" active-text="隐藏标题栏">
                    </el-switch>
                </div>

            </el-form-item>
            <el-form-item label="页面名称">
                <div style="display: flex;flex-direction: column; gap: 8px;padding: 0 12px;">
                    <el-input type="text" v-model="pageItem.name" placeholder="请输入页面名称" class="item_input"
                        size="default" />
                </div>
            </el-form-item>
            <el-form-item label="页面图标">
                <div class="icon-btn">
                    <i :class="pageItem.iconClass + ' icon'"></i>
                </div>
            </el-form-item>
            <el-form-item label="是否主页">
                <el-switch v-model="pageItem.isMain">
                </el-switch>
            </el-form-item>

            <el-form-item>
                <button class="mainColor_bg" @click.prevent="onSubmit()">保存</button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { lp, component } from '@o2oa/component';
import { ref, watch, onMounted } from 'vue';
import staticData from '../../../util/data'
import { loadPortals } from '../../../util/acrions'
import BaseItem from "@/components/item/BaseItem.vue";


const s = staticData()
const props = defineProps({
    item: {
        type: Object,
        default: {}
    },
});
const pageItem = ref((props.item ?? {}));

const emit = defineEmits(['savePageItem']);

const portal = ref({
    portalId: '',
    pageId: '',
    portalParameters: '',
    hiddenAppBar: false,
});
const portalList = ref([]);
onMounted(() => {
    if (pageItem.value.portal) {
        portal.value.portalId = pageItem.value.portal.portalId || ''
        portal.value.pageId = pageItem.value.portal.pageId || ''
        portal.value.portalParameters = pageItem.value.portal.portalParameters || ''
        portal.value.hiddenAppBar = pageItem.value.portal.hiddenAppBar || false
    }
    loadPortals().then((data) => {
        portalList.value = data;
    });
})

const chooseNative = (item) => {
    item.id = pageItem.value.id // 不改id
    pageItem.value = item
}

const onSubmit = () => {
    if (!pageItem.value.name) {
        component.notice("页面名称不能为空！", "error");
        return
    }
    if (pageItem.value.isNative !== true) {
        if (!portal.value.portalId) {
            component.notice("请选择门户！", "error");
            return
        }
        pageItem.value.portal = portal.value
    }

    save()
}

const save = () => {
    emit('savePageItem', pageItem.value);
}

</script>
<style scoped>
.app-layout-form {
    flex: 1;
    margin: 0 1rem;
    padding: 1rem;
    border-width: 1px;
    border-style: solid;
    border-color: #ebebeb;
    border-image: initial;
    border-radius: 3px;
    transition: 0.2s;
}

.app-layout-form .item_input {
    /* width: 450px; */
    margin-right: 30px;
}

.app-layout-form .icon-btn {
    margin-left: 10px;
    width: 36px;
    height: 36px;
    border: solid 1px #ebebeb;
    border-radius: 3px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.al-checkbox-group {}

.al-checkbox-button {
    position: relative;
    display: inline-block;
    line-height: 1;
    font-weight: 500;
    vertical-align: middle;
    cursor: pointer;
    color: rgb(96, 98, 102);
    appearance: none;
    text-align: center;
    box-sizing: border-box;
    user-select: none;
    font-size: 14px;
    white-space: nowrap;
    background: rgb(255, 255, 255);
    border-width: 1px 1px 1px 0px;
    border-style: solid solid solid;
    border-color: rgb(220, 223, 230) rgb(220, 223, 230) rgb(220, 223, 230);
    border-image: initial;
    border-left: 0px;
    outline: none;
    margin: 0px;
    transition: 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    padding: 12px 20px;
    border-radius: 0px;
}

.al-checkbox-button:first-child {
    box-shadow: none !important;
    border-left: 1px solid rgb(220, 223, 230);
    border-radius: 4px 0px 0px 4px;
}

.al-checkbox-button:last-child {
    border-radius: 0px 4px 4px 0px;
}
</style>