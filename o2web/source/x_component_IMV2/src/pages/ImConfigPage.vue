<script setup>
import {ref, onMounted, inject} from 'vue'
import {lp} from '@o2oa/component'
import {imAction} from "../utils/actions.js";
import {EventName} from "../utils/eventBus.js";
import {imConfig, windowState} from '../store.js';

const windowStateInstance = windowState();
const imConfigInstance = imConfig();
// eventBus
const eventBus = inject('eventBus')

onMounted(() => {
  loadConfig()
})

// im配置文件
const imConfigRef = ref({
  enableClearMsg: false,
  enableRevokeMsg: false,
  enableOnlyOfficePreview: false,
  enableGroupMemberQuitSelf: false,
  revokeOutMinute: 2,
  conversationCheckInvoke: '',
})
// im配置文件
const loadConfig = async () => {
  imConfigRef.value = {
    enableClearMsg: imConfigInstance.enableClearMsg,
    enableRevokeMsg: imConfigInstance.enableRevokeMsg,
    enableOnlyOfficePreview: imConfigInstance.enableOnlyOfficePreview,
    enableGroupMemberQuitSelf: imConfigInstance.enableGroupMemberQuitSelf,
    revokeOutMinute: imConfigInstance.revokeOutMinute,
    conversationCheckInvoke: imConfigInstance.conversationCheckInvoke,
  }
  console.debug('config==> ', imConfigRef.value)
  enableClearMsg.value = imConfigRef.value.enableClearMsg ?? false
  enableRevokeMsg.value = imConfigRef.value.enableRevokeMsg ?? false
  enableOnlyOfficePreview.value = imConfigRef.value.enableOnlyOfficePreview ?? false
  enableGroupMemberQuitSelf.value = imConfigRef.value.enableGroupMemberQuitSelf ?? false
  revokeOutMinute.value = imConfigRef.value.revokeOutMinute ?? 2
  conversationCheckInvoke.value = imConfigRef.value.conversationCheckInvoke ?? ''
}
// 清除消息
const enableGroupMemberQuitSelf = ref(false)
const changeEnableGroupMemberQuitSelf = (e) => {
  if (e && e.target) {
    enableGroupMemberQuitSelf.value = e.target.booleanValue
    imConfigRef.value.enableGroupMemberQuitSelf = enableGroupMemberQuitSelf.value
    saveConfig(imConfigRef.value)
  }
}
// 清除消息
const enableClearMsg = ref(false)
const changeEnableClearMsg = (e) => {
  if (e && e.target) {
    enableClearMsg.value = e.target.booleanValue
    imConfigRef.value.enableClearMsg = enableClearMsg.value
    saveConfig(imConfigRef.value)
  }
}
// 撤回
const enableRevokeMsg = ref(false)
const changeEnableRevokeMsg = (e) => {
  if (e && e.target) {
    enableRevokeMsg.value = e.target.booleanValue
    imConfigRef.value.enableRevokeMsg = enableRevokeMsg.value
    saveConfig(imConfigRef.value)
  }
}
// onlyOffice 预览
const enableOnlyOfficePreview = ref(false)
const changeEnableOnlyOfficePreview = (e) => {
  if (e && e.target) {
    enableOnlyOfficePreview.value = e.target.booleanValue
    imConfigRef.value.enableOnlyOfficePreview = enableOnlyOfficePreview.value
    saveConfig(imConfigRef.value)
  }
}
// 撤回时限
const revokeOutMinute = ref(2)
const inputRevokeOutMinute = (e) => {
  if (e.keyCode === 13) {
    e.preventDefault()
    revokeOutMinute.value = e.target.value
    imConfigRef.value.revokeOutMinute = e.target.value
    saveConfig(imConfigRef.value)
  }
}
// 会话检查脚本
const conversationCheckInvoke = ref('')
const inputConversationCheckInvoke = (e) => {
  if (e.keyCode === 13) {
    e.preventDefault()
    conversationCheckInvoke.value = e.target.value
    imConfigRef.value.conversationCheckInvoke = e.target.value
    saveConfig(imConfigRef.value)
  }
}

// 保存配置文件
const saveConfig = async (config) => {
  console.debug('save config==== > ', config)
  const res = await imAction('config', config)
  console.info('保存配置', res)
  if (res) {
    eventBus.publish(EventName.changeIMConfig)
    $OOUI.notice.success(lp.alert, lp.msgSettingsSaveSuccess)
  }
}
const styleCalc = () => {
  if (windowStateInstance.isMobile) {
    return 'width: calc( ' + windowStateInstance.windowWidth + 'px - 3em )!important;'
  }
  return undefined
}
</script>

<template>
  <div class="im-config-dialog" :style=" styleCalc() ">
    <div class="im-config-form-line">
      <div class="left">{{ lp.settingsClearMsg }}</div>
      <oo-switch :value="enableClearMsg" @change="changeEnableClearMsg"></oo-switch>
    </div>

    <div class="im-config-form-line">
      <div class="left">{{ lp.settingsGroupMemberQuitSelf }}</div>
      <oo-switch :value="enableGroupMemberQuitSelf" @change="changeEnableGroupMemberQuitSelf"></oo-switch>
    </div>

    <div class="im-config-form-line">
      <div class="left">{{ lp.settingsRevokeMsg }}</div>
      <oo-switch :value="enableRevokeMsg" @change="changeEnableRevokeMsg"></oo-switch>
    </div>
    <div class="im-config-form-line">
      <div class="left">{{ lp.settingsEnableOnlyOfficePreviewMsg }}</div>
      <oo-switch :value="enableOnlyOfficePreview" @change="changeEnableOnlyOfficePreview"></oo-switch>
    </div>
    <div class="im-config-form-label">
      <span>{{ lp.settingsRevokeOutMinuteMsg }}</span>
    </div>
    <div class="im-config-form-line2">
      <oo-input style="width: 100%;" type="number" :value="revokeOutMinute" @keydown="inputRevokeOutMinute"></oo-input>
    </div>
    <div class="im-config-form-label">
      <span>{{ lp.settingsConversationCheckInvokeMsg }}</span>
    </div>
    <div class="im-config-form-line2">
      <oo-input style="width: 100%;" type="text" :value="conversationCheckInvoke"
                @keydown="inputConversationCheckInvoke"></oo-input>
    </div>

  </div>
</template>

<style scoped>

</style>