<script setup>
import {ref, onMounted, onUnmounted, inject} from 'vue'
import {EventName} from "../utils/eventBus.js";
import ChatMsg from "../components/ChatMsg.vue";
import {imAction} from "../utils/actions.js";
import {windowState} from "../store.js";

const {msg} = defineProps(['msg'])
// eventBus
const eventBus = inject('eventBus')
const windowStateInstance = windowState();

onMounted(() => {
  console.debug('onMounted ====>  MsgHistory 消息', msg)
  loadMsgList()
})
onUnmounted(() => {
  console.debug('onUnmounted ====> MsgHistory')
})
// 消息列表
const msgList = ref([])

const loadMsgList = async () => {
  const msgBody = JSON.parse(msg.body)
  const body = {"msgIdList": msgBody.messageHistoryIds ?? []}
  const res = await imAction('msgListObject', body)
  if (res) {
    msgList.value = res
  }
}
// 打开消息
const clickOpenMsg = (msg) => {
  console.debug('open msg  ==== msgHistory', msg)
  // 打开消息
  eventBus.publish(EventName.openMsg, msg)
}

const styleCalc = () => {
  if (windowStateInstance.isMobile) {
    return 'width: calc( '+windowStateInstance.windowWidth+'px - 3em )!important;'
  }
  return undefined
}

</script>

<template>
  <div class="im-dialog-message-history"  :style=" styleCalc() "  :id="`historyMsgWin_${msg.id}`">
    <div class="im-dialog-body">
      <div v-for="msg in msgList" :key="msg.id">
        <div class="im-chat-msg-body">
          <div class="im-chat-msg-body-content">
            <ChatMsg :msg="msg" :enable-left-side="true" @clickOpenQuoteMsg="clickOpenMsg"
                     @clickOpenMsg="clickOpenMsg"></ChatMsg>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>