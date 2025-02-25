<script setup>
import {ref, onMounted, onUnmounted, inject} from 'vue'
import {EventName} from "../utils/eventBus.js";
import ChatMsg from "../components/ChatMsg.vue";
import {imAction} from "../utils/actions.js";

const {msg} = defineProps(['msg'])
// eventBus
const eventBus = inject('eventBus')

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
</script>

<template>
  <div class="im-dialog-message-history" :id="`historyMsgWin_${msg.id}`">
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