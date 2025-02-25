<script setup>
import {onMounted, onUnmounted, ref, inject} from 'vue'
import {EventName} from "../utils/eventBus.js";
import ChatMsg from "../components/ChatMsg.vue";
import {imAction} from "../utils/actions.js";
import {lp} from "@o2oa/component";

// eventBus
const eventBus = inject('eventBus')
onMounted(()=> {
  console.debug('onMounted ====>  MyCollectionPage ')
  refreshData()
})
onUnmounted(() => {
  console.debug('onUnmounted ====> MyCollectionPage ')
})
// 是否选择模式
const chooseMode = ref(false)
// 消息列表
const collectionMsgList = ref([])
// 选择的消息列表
const selectCollectionMsgList = ref([])
// 页码
const page = ref(1)
// 是否在加载数据中
const isLoading = ref(false)
// 是否有更多数据
const hasMoreData = ref(true)

const refreshData = () => {
  page.value = 1
  loadCollectionMsgList()
}
const loadCollectionMsgList = async ()=> {
  if (isLoading.value) {
    return
  }
  isLoading.value = true
  const list = await imAction('collectionListByPaging', page.value, 20)
  if (list && list.length > 0) {
    if (page.value > 1) {
      collectionMsgList.value.push(...list)
    } else {
      collectionMsgList.value = list
    }
    hasMoreData.value = list.length >= 20
  } else {
    hasMoreData.value = false
  }
  isLoading.value = false
}
const clickSelectCollection = (c) => {
  const i = selectCollectionMsgList.value.findIndex((m) => m.id === c.id)
  if (i > -1) {
    selectCollectionMsgList.value.splice(i, 1)
  } else {
    selectCollectionMsgList.value.push(c)
  }
}
// 打开消息
const clickOpenMsg = (msg) => {
  console.debug('open msg  ==== MyCollectionPage', msg)
  if (chooseMode.value) {
    const collection = collectionMsgList.value.find((m) => m.message.id === msg.id)
    console.debug(collection)
    if (collection) {
      clickSelectCollection(collection)
    }
  } else {
    // 打开消息
    eventBus.publish(EventName.openMsg, msg)
  }
}
const clickOpenQuoteMsg = (msg) => {
  if (chooseMode.value) {
    return
  }
  console.debug('open quote msg  ==== MyCollectionPage', msg)
  // 打开消息
  eventBus.publish(EventName.openMsg, msg)
}
const clickCancelChooseMode = () => {
  chooseMode.value = false
  selectCollectionMsgList.value = []
}
const clickDeleteSelected = () => {
  if (selectCollectionMsgList.value.length < 1) {
    $OOUI.notice.warn(lp.alert, lp.msgNeedSelectMessage)
    return
  }
  $OOUI.confirm.warn(lp.alert, lp.msgDeleteCollection).then((result) => {
    console.info(result)
    if (result.status === 'ok') {
      result.dlg.close()
      deleteCollectionList()
    }
  })
}
const  deleteCollectionList = async () => {
  const deleteList = selectCollectionMsgList.value.map((m)=> m.id)
  const body = {
    'msgIdList': deleteList
  }
  const result = await imAction('msgCollectionRemove', body)
  if (result) {
    console.info(`删除收藏成功 `, result)
    // 清除列表数据
    collectionMsgList.value = collectionMsgList.value.filter((c) => !deleteList.includes(c.id))
  }

  clickCancelChooseMode()
}
</script>

<template>
    <div class="im-dialog-message-history">
      <div class="im-dialog-header" style="justify-content: right;" v-if="collectionMsgList.length > 0">
        <oo-button  v-if="!chooseMode"  type="simple" style="color: var(--oo-color-main);border: 1px solid var(--oo-color-main); width: 4.28rem;" @click="chooseMode = true">{{lp.choose}}</oo-button>
<!--        <oo-button  v-if="chooseMode"  style=" margin-left: 0.7rem;" @click="clickDeleteSelected">{{lp.delete}}</oo-button>-->
<!--        <oo-button  v-if="chooseMode" type="cancel" style=" margin-left: 0.7rem;" @click="clickCancelChooseMode">{{lp.cancel}}</oo-button>-->
      </div>
      <div class="im-dialog-body">
        <div class="chat-no-more-data" v-if="collectionMsgList.length === 0">
          {{ lp.msgLoadNoMoreData }}
        </div>
        <div v-for="c in collectionMsgList" :key="c.id">
          <div class="im-chat-msg-body">
            <div class="im-chat-msg-select" v-if="chooseMode" @click="clickSelectCollection(c)">
              <i :class=" selectCollectionMsgList.findIndex( m => m.id === c.id) > -1 ? 'ooicon-checkbox-checked mainColor_color icon':'ooicon-checkbox-unchecked icon' "></i>
            </div>
            <div class="im-chat-msg-body-content">
              <ChatMsg :msg="c.message" :enable-left-side="true" @clickOpenQuoteMsg="clickOpenQuoteMsg" @clickOpenMsg="clickOpenMsg"></ChatMsg>
            </div>
          </div>
        </div>
      </div>
      <div class="im-dialog-header" style="justify-content: center;" v-if="collectionMsgList.length > 0 && chooseMode">
        <div style="background: #2E82F7;border-radius: 2px;"></div>
        <oo-button  v-if="chooseMode" style="width: 12rem;height: 2.57rem;"   @click="clickDeleteSelected">{{lp.delete}}</oo-button>
        <oo-button  v-if="chooseMode" type="cancel" style="width: 10rem;height: 2.57rem; margin-left: 0.7rem;" @click="clickCancelChooseMode">{{lp.cancel}}</oo-button>
      </div>
    </div>
</template>

<style scoped>

</style>