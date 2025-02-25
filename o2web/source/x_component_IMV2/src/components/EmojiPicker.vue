<script setup>
import {onMounted, ref} from "vue";

onMounted(()=> {
  loadEmojiJson()
})

const emit = defineEmits(["chooseEmoji", "closeEmojiPicker"]);

const chooseEmoji = (emoji) => {
  emit('chooseEmoji', emoji)
}
const closeEmojiPicker = () => {
  emit('closeEmojiPicker')
}

const emojiData = ref(null)
const emojiV2TypeList = ref(["emote", "animals", "emoji-food", "emoji-activities", "emoji-travel", "emoji-clothes","emoji-prompt"])
const currentEmojiType = ref('emote')
const currentEmojiList = ref([])

const loadEmojiJson = () => {
  const url = new URL( "../assets/emoji.json", import.meta.url).href
  fetch(url)
      .then(response => {
        if (!response.ok) {
          console.error('网络响应错误, emoji.json读取错误');
        }
        return response.json(); // 解析为 JSON
      })
      .then(data => {
        emojiData.value = data
        currentEmojiType.value = 'emote'
        currentEmojiList.value = data[ currentEmojiType.value]
      })
      .catch(error => {
        console.error('请求失败:', error);
      });
}
const clickStopClose = (e) => {
  e.stopPropagation()
}
const clickChooseEmoji = (e, emoji) => {
  e.stopPropagation()
  console.debug('===========> 点击了 emoji ', emoji)
  chooseEmoji(emoji)
}
const clickChooseEmojiType = (e, emojiType) => {
  e.stopPropagation()
  console.debug('===========> 点击了 emoji type ', emojiType)
  currentEmojiType.value = emojiType
  currentEmojiList.value = emojiData.value[emojiType]
}
</script>

<template>
  <div class="im-chat-emoji-mask" @click="closeEmojiPicker">
  <div class="im-chat-emoji" @click="clickStopClose">
    <div class="im-chat-emoji-triangle"></div>
    <div class="im-chat-emoji-container">
      <div class="im-chat-emoji-type-container">
        <div class="im-chat-emoji-item pointer" v-for="emo in currentEmojiList" :key="emo"  @click="clickChooseEmoji($event, emo)">
          {{emo}}
        </div>
      </div>
      <div class="im-divider-v"></div>
      <div class="im-chat-emoji-type-list">
        <div class="im-chat-emoji-item pointer" v-for="eType in emojiV2TypeList"  :key="eType">
          <div :class="  eType === currentEmojiType ? 'btn active':'btn' " @click="clickChooseEmojiType($event, eType)">
            <i :class="`ooicon-${eType} icon`"></i>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<style scoped>

</style>