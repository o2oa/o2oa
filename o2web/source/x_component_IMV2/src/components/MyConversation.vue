<script setup>
import {ref, onMounted, onUnmounted, inject,} from 'vue'
import {lp} from '@o2oa/component'
import {imAction, conversationIconUrl} from "../utils/actions.js"
import {formatPersonName, friendlyTime, toDate} from "../utils/common.js";
import {EventName} from "../utils/eventBus.js";
import {imGlobalOptions} from "../store.js";

const emit = defineEmits(["closeChooseConversation", "showContact"]);
const {openConversation} = inject('im-app')
// eventBus
const eventBus = inject('eventBus')

const imGlobalOptionsInstance = imGlobalOptions();

onMounted(() => {
  console.debug('myConversation mounted', eventBus)
  loadMyConversation()
  if (!chooseMode) {
    eventBus.subscribe(EventName.initOpenedConversation, initOpenedConversationEventFun)
    eventBus.subscribe(EventName.refreshMyConversation, refreshMyConversationEventFun)
    eventBus.subscribe(EventName.addConversationToList, addConversationToListEventFun)
  }
})
onUnmounted(() => {
  console.debug('myConversation unmounted', eventBus)
  if (!chooseMode) {
    eventBus.unsubscribe(EventName.initOpenedConversation)
    eventBus.unsubscribe(EventName.refreshMyConversation)
    eventBus.unsubscribe(EventName.addConversationToList)
  }
})

const initOpenedConversationEventFun = (conv) => {
  console.debug("initOpenedConversation", conv)
  if (conv) {
    clickConversation(conv)
  }
}
const refreshMyConversationEventFun = () => {
  console.debug("refreshMyConversation")
  loadMyConversation()
}
const addConversationToListEventFun = (data) => {
  console.debug("addConversationToList")
  if (data) {
    const index = myConversationList.value.findIndex((item) => item.id === data.id)
    if (index < 0) { // 会话不存在 添加
      myConversationList.value.unshift(data)
    }
    currentConversation.value = data
  }
}

// ref
const {chooseMode} = defineProps(['chooseMode']) // 选择模式
const myConversationList = ref([])
const currentConversation = ref(null)
const searchContent = ref('')
const searchMode = ref(false)
const searchConversationResultList = ref([])


// function

const loadMyConversation = async () => {
  const list = await imAction('myConversationList')
  if (list) {
    myConversationList.value = list
    if (!chooseMode) { // 被踢出群之类的特殊情况
      if (currentConversation.value && list.findIndex(item => item.id === currentConversation.value.id) < 0) {
        currentConversation.value = null
        // eventBus.publish(EventName.openConversation, null)
        openConversation(null)
      }
    }
    if (imGlobalOptionsInstance.firstOpenConversation && list.length <= 0) {
      emit('showContact')
      imGlobalOptionsInstance.loadedConversation()
    }
  }
}

const inputSearch = (e) => {
  if (e.keyCode === 13) {
    e.preventDefault()
    searchContent.value = e.target.value
    console.debug('搜索', searchContent.value)
    _searchConversation(e.target.value)
  }
}

const _searchConversation = (content) => {
  if (!content) {
    _clearSearch()
    return
  }
  const list = myConversationList.value
  searchMode.value = true
  searchConversationResultList.value = list.filter((item) => item.title.includes(content) || item.personList.filter((p) => p.includes(content)).length > 0)
}
const _clearSearch = () => {
  searchContent.value = ''
  searchMode.value = false
  searchConversationResultList.value = []
}

const conversationName = (conversation) => {
  if (conversation.type === "single") {
    let chatPerson = "";
    if (conversation.personList && conversation.personList instanceof Array) {
      for (let j = 0; j < conversation.personList.length; j++) {
        const person = conversation.personList[j];
        if (person !== layout.session.user.distinguishedName) {
          chatPerson = person;
        }
      }
      if (chatPerson) {
        return formatPersonName(chatPerson)
      }
    }
  }
  return conversation.title
}
const conversationLastMessageTime = (conversation) => {
  if (conversation.lastMessage && conversation.lastMessage.createTime) {
    return friendlyTime(toDate(conversation.lastMessage.createTime));
  }
  return ''
}
const conversationLastMessage = (conversation) => {
  if (conversation.lastMessage && conversation.lastMessage.body) {
    const mBody = JSON.parse(conversation.lastMessage.body);
    let lastMessage = mBody.body;
    // if (conversation.lastMessage.createTime) {
    //   var time = this.main._friendlyTime(o2.common.toDate(this.data.lastMessage.createTime));
    //   convData.time = time;
    // }
    if (mBody.type) {
      // convData.lastMessageType = mBody.type;
      if (mBody.type === "process") {
        let title = mBody.title;
        if (!title) {
          title = "【" + mBody.processName + "】- " + lp.noTitle;
        }
        lastMessage = title;
      } else if (mBody.type === "cms") {
        lastMessage = mBody.title || "";
      }
    }
    return lastMessage
  }
  return ''
}
const clickConversation = (conversation) => {
  if (searchMode.value) {
    _clearSearch()
  }
  if (chooseMode) {
    emit('closeChooseConversation', conversation)
  } else {
    currentConversation.value = conversation
    // eventBus.publish(EventName.openConversation, conversation)
    openConversation(conversation)
  }

}
</script>

<template>
  <div class="im-conversation-view">
    <div class="im-conversation-search">
      <oo-input style="width: 100%;" type="text" leftIcon="search" :value="searchContent"
                :placeholder="lp.conversationSearchPlaceholder"
                @keydown="inputSearch"></oo-input>
    </div>
    <!--      会话列表 -->
    <div class="im-conversation-list" v-if=" !searchMode ">
      <div v-for="item in myConversationList" :key="item.id"
           :class=" item.id === currentConversation?.id ? 'im-conv-item active': 'im-conv-item' "
           @click="clickConversation(item)">
        <div class="avatar">
          <img :src="conversationIconUrl(item.id)" alt="avatar">
        </div>
        <div class="body">
          <div class="body-line">
            <div class="title"> {{ conversationName(item) }}</div>
            <div class="time">{{ conversationLastMessageTime(item) }}</div>
          </div>
          <div class="body-line margin-top-10">
            <div class="msg">
              {{ conversationLastMessage(item) }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <!--      搜索结果 -->
    <div class="im-conversation-list" v-if=" searchMode ">
      <div v-for="item in searchConversationResultList" :key="item.id"
           :class=" item.id === currentConversation?.id ? 'im-conv-item active': 'im-conv-item' "
           @click="clickConversation(item)">
        <div class="avatar">
          <img :src="conversationIconUrl(item.id)" alt="avatar">
        </div>
        <div class="body">
          <div class="body-line">
            <div class="title"> {{ conversationName(item) }}</div>
            <div class="time">{{ conversationLastMessageTime(item) }}</div>
          </div>
          <div class="body-line margin-top-10">
            <div class="msg">
              {{ conversationLastMessage(item) }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<style scoped>

</style>