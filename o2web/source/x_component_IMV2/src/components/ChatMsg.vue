<script setup>
import {onMounted, ref} from "vue";
import {lp} from '@o2oa/component'
import {chatMsgShowTimeFormat, formatPersonName} from "../utils/common.js";
import {getAvatarUrl, getImFileDownloadUrl, getImFileUrlWithWH} from "../utils/actions.js";
import {contentEscapeBackToSymbol} from "../utils/escapeSymbol.js";
import ChatMsgBody from "./ChatMsgBody.vue";

const {msg, enableLeftSide} = defineProps(['msg', 'enableLeftSide'])
const isSender = ref(false)
const msgType = ref('')
const senderMsgWhiteBg = ref(false)
const quoteMessageBody = ref(null)

onMounted(() => {
  if (enableLeftSide) {
    isSender.value = false
  } else {
    isSender.value = (msg.createPerson === layout.session.user.distinguishedName)
  }
  const msgBody = JSON.parse(msg.body)
  msgType.value = msgBody.type ?? ''
  senderMsgWhiteBg.value = (msgBody.type === 'file' || msgBody.type === 'process' || msgBody.type === 'cms' || msgBody.type === 'audio' || msgBody.type === 'messageHistory')
  if (msg.quoteMessage && msg.quoteMessage.body) {
    quoteMessageBody.value = JSON.parse(msg.quoteMessage.body)
  }

})

const emit = defineEmits([ "clickOpenMsg", "clickOpenQuoteMsg" ]);

const clickOpenMsg = (msg) => {
  console.debug('open msg', msg)
  emit('clickOpenMsg', msg)
}

const clickOpenQuoteMsg = (msg) => {
  console.debug('open quote msg', msg)
  emit('clickOpenQuoteMsg', msg)
}

const fileUrl = (msgBody) => {
  if (!msgBody) {
    return ''
  }
  let url = getImFileUrlWithWH(msgBody.fileId, 48, 48);
  if (msgBody.fileExtension && msgBody.fileExtension.toLowerCase() === "webp") {
    url = getImFileDownloadUrl(msgBody.fileId);
  }
  return url
}

const quoteMsgContent = (quoteMessage, msgBody) => {
  if (!msgBody) {
    return
  }
  let name = formatPersonName(quoteMessage.createPerson)
  name += ": ";
  let lastMessage = msgBody.body;
  if (msgBody.type) {
    // convData.lastMessageType = mBody.type;
    if (msgBody.type === "process") {
      let title = msgBody.title;
      if (!title) {
        title = "【" + msgBody.processName + "】- " + lp.noTitle;
      }
      lastMessage = title;
    } else if (msgBody.type === "cms") {
      lastMessage = msgBody.title || "";
    }
  }
  if (msgBody.type !== "emoji" && msgBody.type !== "image") {
    name += contentEscapeBackToSymbol(lastMessage)
    if (msgBody.type === "file") {
      name += " " + msgBody.fileName;
    }
  }

  return name
}

const sendBoxClass = () => {
  if (msgType.value === 'image') {
    return  'chat-sender-box'
  } else if (senderMsgWhiteBg.value) {
    return 'chat-sender-box chat-sender-box-bg-white'
  }  else  {
    return 'chat-sender-box chat-sender-box-bg'
  }
}

const receiverBoxClass = () => {
  if (msgType.value === 'image') {
    return  'chat-receiver-box'
  } else if (enableLeftSide) {
    return 'chat-receiver-box chat-receiver-box-bg-f7'
  }  else  {
    return 'chat-receiver-box chat-receiver-box-bg'
  }
}


</script>

<template>
  <div class="chat-msg-time">{{ chatMsgShowTimeFormat(msg.createTime) }}</div>
  <div v-if="isSender" class="chat-sender">
    <div class="chat-sender-content">
      <div class="chat-sender-name">{{ formatPersonName(msg.createPerson) }}</div>

      <div :class="sendBoxClass()" @click="clickOpenMsg(msg)">
        <ChatMsgBody :msg="msg"></ChatMsgBody>
      </div>

      <div class="im-chat-quote-message-box chat-sender-quote-msg" v-if="msg.quoteMessage"
           @click="clickOpenQuoteMsg(msg.quoteMessage)">
        <div class="im-chat-quote-message-desc">{{ quoteMsgContent(msg.quoteMessage, quoteMessageBody) }}</div>
        <div class="im-chat-quote-message-image" v-if=" quoteMessageBody && quoteMessageBody.type === 'image' ">
          <img :src="fileUrl(quoteMessageBody)" alt="图片">
        </div>
      </div>
    </div>
    <div class="chat-sender-avatar">
      <img :src=" getAvatarUrl(msg.createPerson) " alt="avatar">
    </div>
  </div>
  <div v-else class="chat-receiver">
    <div class="chat-receiver-avatar">
      <img :src=" getAvatarUrl(msg.createPerson) " alt="avatar">
    </div>
    <div class="chat-receiver-content">
      <div class="chat-receiver-name">{{ formatPersonName(msg.createPerson) }}</div>
      <div :class="receiverBoxClass()" @click="clickOpenMsg(msg)">
<!--        <div :class="enableLeftSide ? 'chat-left_triangle-f7':'chat-left_triangle'"></div>-->
        <ChatMsgBody :msg="msg"></ChatMsgBody>
      </div>
      <div class="im-chat-quote-message-box chat-receiver-quote-msg" v-if="msg.quoteMessage"
           @click="clickOpenQuoteMsg(msg.quoteMessage)">
        <div class="im-chat-quote-message-desc">{{ quoteMsgContent(msg.quoteMessage, quoteMessageBody) }}</div>
        <div class="im-chat-quote-message-image" v-if=" quoteMessageBody && quoteMessageBody.type === 'image' ">
          <img :src="fileUrl(quoteMessageBody)" alt="图片">
        </div>
      </div>
    </div>

  </div>

</template>

<style scoped>

</style>