<script setup>
import {onMounted} from 'vue'
import { lp } from '@o2oa/component'
import {dom} from '@o2oa/util'
import MyConversation from "../components/MyConversation.vue";

const emit = defineEmits([ "closeChooseConversation", "clickOpenQuoteMsg" ]);

onMounted(()=> {
  calcChooseWinPosition()
})

const closeChooseConversation = (conversation) => {
  console.debug('ChooseConversationPage === 》 closeChooseConversation')
  emit('closeChooseConversation', conversation)
}

const closeWindow = () => {
  emit('closeChooseConversation')
}
const calcChooseWinPosition = () => {
  debugger
  const node = document.querySelector('#chooseConversationWindowNode')
  const rect = node.parentElement.getBoundingClientRect()
  const selfRect = node.getBoundingClientRect()
  const left = (rect.width - selfRect.width) / 2
  const top = (rect.height - selfRect.height) / 2
  dom.setStyles(node, {
    position: 'absolute',
    left: left + 'px',
    top: top + 'px'
  })
}
</script>

<template>
<div class="im-cover-bg">
  <div class="im-dialog-window" id="chooseConversationWindowNode">
    <div class="im-conversation-header">
      <div class="im-conversation-header-menus">
        {{lp.chooseConversation}}
      </div>
      <div class="im-btn-icon ooicon-close" @click="closeWindow"></div>
    </div>
    <div class="im-conversation-content">
      <MyConversation :choose-mode="true" @closeChooseConversation="closeChooseConversation" />
    </div>
  </div>
</div>
</template>

<style scoped>

</style>