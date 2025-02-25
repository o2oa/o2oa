<script setup>
import {ref, onMounted, onUnmounted, createApp, nextTick, provide, inject} from 'vue'
import {o2, lp} from "@o2oa/component";
import IMAside from "./components/IMAside.vue";
import IMMain from "./components/IMMain.vue";
import ChooseConversationPage from "./pages/ChooseConversationPage.vue";
import MsgHistoryPage from "./pages/MsgHistoryPage.vue";
import ImConfigPage from "./pages/ImConfigPage.vue";
import MyCollectionPage from "./pages/MyCollectionPage.vue";
import {EventName} from "./utils/eventBus.js";
import {getBaiduMapUrl, getImFileDownloadUrl, imAction} from "./utils/actions.js";
import {imConfig, imGlobalOptions} from './store.js';

const imConfigInstance = imConfig();
const imGlobalOptionsInstance = imGlobalOptions();
// eventBus
const eventBus = inject('eventBus')

// 外部传入的 options
const { options } = defineProps(['options'])

let openChooseConversationEventName = null

onMounted(() => {
  // 根据 options 处理一些特殊情况 比如打开某一个会话
  console.debug('APP =====>  onMounted  options', options)
  // 默认打开的会话 openConversation 是一个 conversation 对象
  if (options && options.openConversation && options.openConversation.id && options.openConversation.personList) {
    openConversationForOutside(options.openConversation)
  } else if (options && options.chatToPerson) {
    chatToPerson(options.chatToPerson)
  }
  if (options) {
    hideSide.value = !!options.hideSide
    imGlobalOptionsInstance.setOptions({
      hideSide: !!options.hideSide
    })
    console.debug('APP =====>  imGlobalOptions', imGlobalOptionsInstance.hideSide)
  }

  // 打开会话选择器 data 里面应该包含 回调的eventName
  eventBus.subscribe(EventName.openChooseConversation, (data) => {
    console.debug("openChooseConversation", data)
    showChooseConversation.value = true
    openChooseConversationEventName = data.eventName
  })
  eventBus.subscribe(EventName.openMsg, (data) => {
    console.debug("openMsg", data)
    openMsg(data)
  })
  eventBus.subscribe(EventName.changeIMConfig, (data) => {
    console.debug("changeIMConfig", data)
    loadImConfig()
  })
  // 读取配置
  loadImConfig()
  // 监听 websocket 消息
  if (layout.desktop && layout.desktop.socket && layout.desktop.socket.addImListener) {
    console.debug('websocket 消息 addImListener => ')
    layout.desktop.socket.addImListener("im_revoke", imWsRevokeMessage);
    layout.desktop.socket.addImListener("im_create", imWsCreateMessage);
    layout.desktop.socket.addImListener("im_conversation", imWsConversationMessage);
  }
  if (layout.desktop) {
    if (!layout.desktop.im) {
      layout.desktop.im = {}
    }
    layout.desktop.im.openChat = openConversationForOutside
    layout.desktop.im.chatToPerson = chatToPerson
  }
})
onUnmounted(() => {
  // eventBus.unsubscribe(EventName.openConversation)
  eventBus.unsubscribe(EventName.openChooseConversation)
  eventBus.unsubscribe(EventName.openMsg)
  layout.desktop.im.openChat = undefined
  layout.desktop.im.chatToPerson = undefined
  console.debug('APP =========== > onUnmounted')
})

const chatToPerson = async (person) => {
  if (!person) {
    return
  }
  console.debug('APP === 》 chatToPerson', person)
  const conv = await imAction('create', {
    type: 'single',
    personList: [person],
  })
  if (conv) {
    openConversationForOutside(conv)
  }
}

// 打开会话 给外部调用
const openConversationForOutside = (conversation) => {
  console.debug('APP === 》 openConversationForOutside', conversation)
  if (!conversation) {
    return
  }
  nextTick(()=> {
    if (layout.desktop.apps && layout.desktop.apps["IMV2"] && layout.desktop.apps["IMV2"].setCurrent) {
      layout.desktop.apps["IMV2"].setCurrent();
    }
    if (conversation.personList && conversation.personList.contains(layout.session.user.distinguishedName)) {
      currentConversation.value = conversation
      eventBus.publish( EventName.initOpenedConversation, conversation)
    } else {
      console.error('当前用户不能打开这个会话', conversation)
    }
  })
}
// 点击会话 打开聊天窗口
const openConversation = (conversation) => {
  console.debug("openConversation", conversation)
  currentConversation.value = conversation
}
// 修改聊天信息对象
const updateOrDeleteConversation = ( conv ) => {
  console.debug("updateOrDeleteConversation", conv)
  currentConversation.value = conv
  eventBus.publish( EventName.refreshMyConversation ) // 刷新列表
}
provide('im-app', {
  openConversation,
  updateOrDeleteConversation
});

// websocket 创建消息
const imWsCreateMessage = (msg) => {
  console.debug('websocket 消息 创建=====> ', msg)
  eventBus.publish( EventName.wsAddMsg, msg )
  eventBus.publish( EventName.refreshMyConversation ) // 刷新列表
}
// websocket 撤回消息
const imWsRevokeMessage = (msg) => {
  console.debug('websocket 消息 撤回=====> ', msg)
  eventBus.publish( EventName.wsRevokeMsg, msg )
}
// websocket 会话更新消息
const imWsConversationMessage = (conversation) => {
  console.debug('websocket 消息 会话更新=====> ', conversation)
  eventBus.publish( EventName.refreshMyConversation ) // 刷新列表
}

const hideSide = ref(false)

const currentConversation = ref(null)

const showChooseConversation = ref(false)

const closeChooseConversation = (conversation) => {
  console.debug('APP === 》 closeChooseConversation', conversation)
  showChooseConversation.value = false
  if (conversation && openChooseConversationEventName) {
    eventBus.publish(openChooseConversationEventName, conversation)
  }
}
const loadImConfig = async () => {
  const config = await imAction('getImConfig')
  imConfigInstance.setImConfig(config)
}
// 打开消息
const openMsg = (msg) => {
  const body = JSON.parse(msg.body)
  if (body.type === 'messageHistory') {
    openMsgHistory(msg)
  } else if (body.type === "image") {
    window.open(getImFileDownloadUrl(body.fileId))
  } else if (body.type === "process") {
    o2.api.form.openWork(body.work, "", "");
  } else if (body.type === "file") {
    // 有安装 onlyOffice
    if (layout.serviceAddressList["x_onlyofficefile_assemble_control"]
        && imConfigInstance.enableOnlyOfficePreview && body.fileExtension
        && (body.fileExtension.toLowerCase() === "docx" || body.fileExtension.toLowerCase() === "doc"
            || body.fileExtension.toLowerCase() === "xls" || body.fileExtension.toLowerCase() === "xlsx"
            || body.fileExtension.toLowerCase() === "ppt" || body.fileExtension.toLowerCase() === "pptx"
            || body.fileExtension.toLowerCase() === "pdf" || body.fileExtension.toLowerCase() === "csv"
            || body.fileExtension.toLowerCase() === "txt")) {
      const onlyOfficeUrl = "../o2_lib/onlyoffice/index.html?fileName=" + body.fileName + "&file=" + getImFileDownloadUrl(body.fileId)
      window.open(onlyOfficeUrl)
      return;
    } else if (body.fileExtension && (body.fileExtension.toLowerCase() === "mp4" || body.fileExtension.toLowerCase() === "avi" || body.fileExtension.toLowerCase() === "ogg")) {
      console.log('视频文件无需下载！')
      return;
    }
    window.open(getImFileDownloadUrl(body.fileId));
  } else if (body.type === "location") {
    const url = getBaiduMapUrl(body.latitude, body.longitude, body.address, body.addressDetail);
    window.open(url);
  }
}
// 打开聊天记录类型的消息
const openMsgHistory = async (msg) => {
    const container = document.querySelector('.im-container')
    const fragment = document.createDocumentFragment()
    const msgHistoryPageComponent = createApp(MsgHistoryPage, {
      msg: msg
    }).mount(fragment)
    $OOUI.dialog(lp.msgHistory, msgHistoryPageComponent.$el, container, {buttons: ''})
}
// 打开设置页面
const openImConfigPage = () => {
  const container = document.querySelector('.im-container')
  const fragment = document.createDocumentFragment()
  const imConfigPage = createApp(ImConfigPage, {}).mount(fragment)
  $OOUI.dialog(lp.setting, imConfigPage.$el, container, {buttons: ''})
}
// 打开我的收藏页码
const openMyCollectionPage = () => {
  const container = document.querySelector('.im-container')
  const fragment = document.createDocumentFragment()
  const myCollectionPage = createApp(MyCollectionPage, {}).mount(fragment)
  $OOUI.dialog(lp.msgCollectionTitle, myCollectionPage.$el, container, {buttons: ''})
}
</script>

<template>
  <div class="im-app im-container">
    <div class="im-aside" v-if="!hideSide">
      <IMAside @clickImConfig="openImConfigPage" @clickMyCollectionPage="openMyCollectionPage"/>
    </div>
    <div class="im-main">
      <IMMain :conv="currentConversation"/>
    </div>
  </div>
  <ChooseConversationPage v-if="showChooseConversation"
                          @closeChooseConversation="closeChooseConversation"></ChooseConversationPage>
</template>

<style scoped></style>
