<template>
    <div style="height: 100%;display: flex;flex-direction: column;">
        <div class="app-layout-icon-header">
            <div class="app-layout-icon-item app-layout-close " @click="close">
                <div class="btn"><i class="ooicon-close icon"></i></div>
            </div>
        </div>
        <div class="app-layout-icon-container">
            <div class="app-layout-icon-list">
                <div class="app-layout-icon-item" v-for="icon in iconList" :key="icon">
                    <div :class="icon === currentIcon ? 'btn active' : 'btn'" @click="choose(icon)">
                        <i :class="`${icon} icon`"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>
<script setup>
import { lp, component } from '@o2oa/component';
import { ref } from 'vue';
import staticData from '../../../util/data'

const emit = defineEmits(['picked']);


const s = staticData()
const props = defineProps({
    current: {
        type: String,
        default: ''
    },
});
const currentIcon = ref((props.current ?? ''));
const iconList = ref([])
iconList.value = s.iconClassNameList

const choose = (icon) => {
    emit('picked', icon)
}
const close = () => {
    emit('picked', null)
}



</script>
<style scoped>
.app-layout-icon-container {
    padding: 1rem;
    overflow: auto;
}

.app-layout-icon-header {
    display: flex;
    height: 36px;
    justify-content: end;
}

.app-layout-icon-list {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 0.6rem;
}

.app-layout-close {
    width: 2.5rem!important;
    height: 2.5rem!important;
    font-size: 1.2rem!important;
    text-align: center;
}
.app-layout-icon-item {
    width: 3.5rem;
    height: 2.5rem;
    font-size: 1.8rem;
    text-align: center;
    cursor: pointer;
}

.app-layout-icon-item .btn {
    width: 100%;
    height: 100%;
}

.app-layout-icon-item .btn i {
    width: 100%;
    height: 100%;
}

.app-layout-icon-item .active {
    border-radius: var(--oo-area-radius);
    border: 1px solid var(--el-color-primary);
}
</style>