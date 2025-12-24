<script setup>
import {ref, inject } from 'vue'
import { lp,o2 } from '@o2oa/component'
import MyConversation from "./MyConversation.vue";
import ContactView from "./ContactView.vue";
import {imAction} from "../utils/actions.js";
import {EventName} from "../utils/eventBus.js";
import { useLoadingStore } from '../store.js';

const emit = defineEmits(['clickImConfig', 'clickMyCollectionPage'])

// eventBus
const eventBus = inject('eventBus')

const {openConversation} = inject('im-app')

const loadingStore = useLoadingStore();

const clickImConfig = ()=> {
  emit('clickImConfig')
}
const clickMyCollectionPage = ()=> {
  emit('clickMyCollectionPage')
}

const isAdmin = ref(o2.AC.isAdministrator())

const clickChoosePersonCreateChat = () => {

  o2.requireApp("Selector","package", () => {
    const container = document.querySelector('.im-container')
    new o2.O2Selector(container,  {
      "type": 'identity',
      "count": 0,
      "style": "v10",
      "title": '通讯录',
      "firstLevelSelectable": true,
      "resultType": "person",
      "onPostLoadContent": function () {
        this.titleTextNode.set("text", '通讯录')
      },
      "onComplete":  (items) => {
        console.log(items)
        if (items && items.length > 0) {
          let personList = items.map(i => i.data.distinguishedName)
          const me = layout.session.user.distinguishedName;
          personList = personList.filter(p => p !== me)
          if (personList.length === 0 ) {
            $OOUI.notice.warn(lp.alert, lp.msgNeedChoosePerson)
          } else {
            newConversation(personList, personList.length === 1 ? "single" : "group")
          }
        }
      }
    })
  });
}

const newConversation = async (personList, type) => {
  console.debug('====> 创建聊天 ', personList, type)
  debugger
  loadingStore.showLoading()
  const res = await imAction('create', {
    type: type,
    personList: personList,
  })
  loadingStore.hideLoading()
  if (res) {
    // 打开添加到会话列表
    eventBus.publish(EventName.addConversationToList, res)
    // 打开会话
    openConversation(res)
  }
}

const currentTab = ref(0)
const toggleTab = (tab) => {
  if (tab !== 0 && tab !== 1) {
    currentTab.value = currentTab.value === 0 ? 1 : 0
  } else {
    currentTab.value = tab
  }
}

const chatWithPerson =  (personDn) => {
  if (!personDn || personDn === layout.session.user.distinguishedName) {
    console.error('传入参数错误', personDn)
    return
  }
  toggleTab(0) // 切换到聊天列表
  newConversation([personDn],"single")
}

const showContact = () => {
  toggleTab(1) // 切换到通讯录
}

</script>

<template>
  <div class="im-conversation">
      <div class="im-conversation-tab">
        <div :class="currentTab === 0 ? 'im-conversation-tab-item active' : 'im-conversation-tab-item'" @click="toggleTab"><i class="ooicon-chat"></i></div>
        <div :class="currentTab === 1 ? 'im-conversation-tab-item active' : 'im-conversation-tab-item'" @click="toggleTab"><i class="ooicon-personnel"></i></div>
      </div>
    <div class="im-conversation-container" v-if="currentTab === 0">
      <div class="im-conversation-header">
        <div class="im-conversation-header-menus">
          <div class="menu-icon " >
            <i class="ooicon-create"></i>
          </div>
          <div class="menu" @click="clickChoosePersonCreateChat">{{ lp.choosePersonCreate }}</div>
        </div>
        <div class="im-btn-icon " @click="clickMyCollectionPage">
          <i class="ooicon-pentagram"></i>
        </div>
        <div class="im-btn-icon " @click="clickImConfig" v-if="isAdmin">
          <i class="ooicon-config"></i>
        </div>
      </div>
      <!-- 会话列表 -->
      <div class="im-conversation-content">
        <MyConversation @showContact="showContact"/>
      </div>
    </div>
    <ContactView  v-if="currentTab === 1" @chatWithPerson="chatWithPerson"/>
  </div>
</template>

<style scoped>

</style>