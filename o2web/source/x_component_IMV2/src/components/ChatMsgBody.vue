<script setup>
import {onMounted, ref} from "vue";
import {lp} from '@o2oa/component'
import {getImFileDownloadUrl, getImFileUrlWithWH} from "../utils/actions.js";
import {contentEscapeBackToSymbol} from "../utils/escapeSymbol.js";
import {fileExtIcon} from "../utils/common.js";

const {msg} = defineProps(['msg'])
const msgBody = ref(null)
onMounted(() => {
  if (msg && msg.body) {
    msgBody.value = JSON.parse(msg.body)
  }
})


const imageUrl = (msgBody) => {
  if (!msgBody) {
    return ''
  }
  let url = getImFileUrlWithWH(msgBody.fileId, 126, 126);
  if (msgBody.fileExtension && msgBody.fileExtension.toLowerCase() === "webp") {
    url = getImFileDownloadUrl(msgBody.fileId);
  }
  return url
}
const isVideo = (msgBody) => {
  return (msgBody.type === 'file' && msgBody.fileExtension  && (msgBody.fileExtension.toLowerCase() === "mp4" || msgBody.fileExtension.toLowerCase() === "avi" || msgBody.fileExtension.toLowerCase() === "ogg"))
}
const fileDownloadUrl = (msgBody) => {
  return getImFileDownloadUrl(msgBody.fileId)
}
const fileIconUrl = (msgBody) => {
  const ext = msgBody.fileExtension?.toLowerCase()
  return fileExtIcon(ext)
}
const locationUrl = (msgBody) => {
  return `https://api.map.baidu.com/marker?location=${msgBody.latitude},${msgBody.longitude}&title=${msgBody.address}&content=${msgBody.address}&output=html&src=net.o2oa.map`
}
const escapeSymbol = (content) => {
  return contentEscapeBackToSymbol(content)
}
const locationPng = () => {
 return new URL( "../assets/location.png", import.meta.url).href
}
const cmsCategoryTitle = (msgBody) => {
  if (!msgBody) {
    return ''
  }
  if (msgBody.categoryAlias) {
    return msgBody.categoryAlias
  }
  if (msgBody.categoryName) {
    return msgBody.categoryName
  }
  return ''
}
const cmsAppTitle = (msgBody) => {
  if (!msgBody) {
    return ''
  }
  if (msgBody.appAlias) {
    return msgBody.appAlias
  }
  if (msgBody.appName) {
    return msgBody.appName
  }
  return ''
}
</script>

<template>
  <div v-if="msgBody">
    <!-- image -->
    <div v-if="msgBody.type === 'image'" class="img-chat">
      <img :src="imageUrl(msgBody)" alt="图片">
    </div>
    <!-- audio -->
    <audio v-if="msgBody.type === 'audio'" :src="getImFileDownloadUrl(msgBody.fileId)" controls preload
           style="max-width: 100%;"></audio>
    <!-- location -->
    <span v-if="msgBody.type === 'location'">
      <img :src="locationPng()" width="24" height="24" alt="location">
      <a :href="locationUrl(msgBody)" target="_blank">
        {{ msgBody.address }}
      </a>
    </span>
    <div v-if="isVideo(msgBody)" class="img-chat">
      <video controls preload  >
        <source :src="fileDownloadUrl(msgBody)"  />
        <a :href="fileDownloadUrl(msgBody)">下载</a>
      </video>
    </div>
    <span v-else-if="msgBody.type === 'file'" style="display: flex;gap: 5px;align-items: center;">
     <img :src="fileIconUrl(msgBody)" width="48" height="48">
     <span  style="word-break: break-all;"> {{ msgBody.fileName }} </span>
    </span>
    <!-- messageCollection -->
    <div v-if="msgBody.type === 'messageHistory'" class="chat-card">
      <div class="chat-card-type">{{ msgBody.messageHistoryTitle }}</div>
      <div class="chat-card-body">{{ msgBody.messageHistoryDesc }}</div>
      <div class="chat-card-bottom">
        <div class="chat-card-bottom-name">{{ lp.msgHistory }}</div>
      </div>
    </div>
    <!-- card -->
    <div v-if="msgBody.type === 'process'" class="chat-card">
      <div class="chat-card-type">{{ `【${msgBody.processName}】` }}</div>
      <div class="chat-card-body">{{ msgBody.title ? msgBody.title : `【${msgBody.processName}】- ${lp.noTitle}` }}</div>
      <div class="chat-card-bottom">
        <div class="chat-card-bottom-name">{{ msgBody.applicationName }}</div>
      </div>
    </div>
    <!-- cms -->
    <div v-if="msgBody.type === 'cms'" class="chat-card">
      <div class="chat-card-type">{{ `【${cmsCategoryTitle(msgBody)}】` }}</div>
      <div class="chat-card-body">{{ msgBody.title ? msgBody.title : `${lp.noTitle}` }}</div>
      <div class="chat-card-bottom">
        <div class="chat-card-bottom-name">{{ cmsAppTitle(msgBody) }}</div>
      </div>
    </div>
    <!-- text -->
    <span style="word-break: break-all;" v-if="msgBody.type === 'text'">{{ escapeSymbol(msgBody.body) }}</span>
    <!--    link-->
    <span style="word-break: break-all;text-decoration: underline; font-weight: 500;" v-if="msgBody.type === 'link'">{{ msgBody.title === msgBody.linkUrl ? msgBody.linkUrl :  `[${msgBody.title}]${msgBody.linkUrl}`}}</span>
  </div>

</template>

<style scoped>

</style>