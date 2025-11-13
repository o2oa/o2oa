<script setup>
import {ref, onMounted, onUnmounted, inject} from 'vue'
import {lp, o2} from "@o2oa/component";
import {getAvatarUrl, imAction, conversationIconUrl} from "../utils/actions.js";
import {formatPersonName} from "../utils/common.js";
import {imConfig, windowState} from '../store.js';

const imConfigInstance = imConfig();
const windowStateInstance = windowState();

const {conversation} = defineProps(['conversation'])

const emit = defineEmits(['closeChatInfo'])

const quitChatShow = ref(false)
const quitGroupByNormal = ref(false) // 普通成员退出群聊
const isGroupAdmin = ref(false)
const isGroup = ref(false)
const memberList = ref([])
const form = ref({
  title: '',
  note: ''
})

const isTitleEdit = ref(false)
const isNoteEdit = ref(false)
const {updateOrDeleteConversation} = inject('im-app')

onMounted(()=> {
  console.debug(' =====> onMounted ChatInfo', conversation)
  memberList.value =  conversation.personList ?? []
  isGroup.value = conversation.type === 'group'
  isGroupAdmin.value = conversation.adminPerson === layout.session.user.distinguishedName
  form.value.title = conversation.title
  form.value.note = conversation.note
  quitChatShow.value = imConfigInstance.enableClearMsg
  if (quitChatShow.value && isGroup.value && !isGroupAdmin.value) {
    quitChatShow.value = false
  }
  quitGroupByNormal.value = (imConfigInstance.enableGroupMemberQuitSelf && isGroup.value && !isGroupAdmin.value)
})
onUnmounted(()=> {
  console.debug(' =====> onUnmounted ChatInfo')
})

// 关闭
const clickClose = () => {
  emit('closeChatInfo')
}

// 会话名称
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

// 成员
const clickAddMember = () => {
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
            const mergedArray = [...personList, ...memberList.value] // 合并
            const set = new Set(mergedArray) // 去重
            memberList.value = Array.from(set)
            const body = {
              personList: memberList.value
            }
            updateConversationInfo(body)
          }
        }
      }
    })
  });
}

const removeMemberIcon = (person) => {
   return (isGroupAdmin.value && memberList.value.length > 2 && person !==  layout.session.user.distinguishedName)
}

const isGroupAdminMember = (person) => {
  return conversation.adminPerson === person
}
const isSelfMember = (person) => {
  return layout.session.user.distinguishedName === person
}

const clickRemoveMember = (person) => {
  if (isGroupAdmin.value) {
    if (memberList.value.length <= 2) {
      console.error('人数太少 不能再删除')
      return
    }
    if (person ===  layout.session.user.distinguishedName) {
      return;
    }
    const msg = lp.msgDeleteGroupMember + ' 【' + formatPersonName(person) + '】'
    $OOUI.confirm.warn(lp.alert, msg).then((result) => {
      if (result.status === 'ok') {
        result.dlg.close()
        const index = memberList.value.indexOf(person)
        if (index > -1) {
          memberList.value.splice(index, 1)
        }
        const body = {
          personList: memberList.value
        }
        updateConversationInfo(body)
      }
    })
  }
}

// 编辑群名
const clickTitleEdit = (start) => {
  if (!isGroupAdmin.value) {
    return
  }
  form.value.title =  conversation.title
  isTitleEdit.value = start
}
const clickSaveTitle = () => {
  if (!form.value.title) {
    $OOUI.notice.warn(lp.alert, lp.groupNameNotEmpty)
    return
  }
  if (form.value.title.length > 80) {
    $OOUI.notice.warn(lp.alert, lp.groupContentTooLong)
    return
  }
  conversation.title = form.value.title
  updateConversationInfo({title: form.value.title})
  isTitleEdit.value = false
}
// 编辑公告
const clickNoteEdit = (start) => {
  if (!isGroupAdmin.value) {
    return
  }
  form.value.note =  conversation.note
  isNoteEdit.value = start
}

const clickSaveNote = () => {
  // if (!form.value.note) {
  //   $OOUI.notice.warn(lp.alert, lp.groupNoteNotEmpty)
  //   return
  // }
  if (form.value.note.length > 80) {
    $OOUI.notice.warn(lp.alert, lp.groupContentTooLong)
    return
  }
  conversation.note = form.value.note
  updateConversationInfo({note: form.value.note})
  isNoteEdit.value = false
}

const updateConversationInfo = async (conv) => {
  conv.id = conversation.id
  const result = await imAction('update', conv)
  if (result) {
    console.debug(`修改会话对象成功 `, result)
    await loadConversationOnline()
  }
}
const loadConversationOnline = async () => {
  const result = await imAction('conversation', conversation.id)
  if (result) {
    updateOrDeleteConversation(result)
  }
}

// 删除聊天
const clickQuit = () => {
  const msg = isGroup.value ? lp.messageDeleteGroupConversationAlert : lp.messageDeleteSingleConversationAlert
  $OOUI.confirm.warn(lp.alert, msg).then((result) => {
    if (result.status === 'ok') {
      result.dlg.close()
      quitChat()
    }
  })
}

// 单聊删除会话、群聊管理员删除会话
const quitChat = async () => {
  if (isGroup.value && isGroupAdmin.value) {
    const result = await imAction('deleteGroupConversation', conversation.id)
    if (result) {
      console.info(`解散群聊成功 `, result)
      updateOrDeleteConversation(null)
    }
  } else if (!isGroup.value) {
    const result = await imAction('deleteSingleConversation', conversation.id)
    if (result) {
      console.info(`删除单聊成功 `, result)
      updateOrDeleteConversation(null)
    }
  }
}

const clickQuitGroup = () => {
  $OOUI.confirm.warn(lp.alert, lp.messageQuitGroupAlert).then((result) => {
    if (result.status === 'ok') {
      result.dlg.close()
      quitGroup()
    }
  })
}
// 普通成员 退出群聊
const quitGroup = async () => {
   if (isGroup.value && !isGroupAdmin.value) { // 群聊并且不是管理员
     const result = await imAction('quitGroupSelf', conversation.id)
     if (result) {
       console.info(`普通成员退出群聊成功 `, result)
       updateOrDeleteConversation(null)
     }
   }
}


</script>

<template>
  <div class="im-chat-info" :class="{   w100:  windowStateInstance.isMobile}">
    <div class="im-chat-info-header">
      <div class="title">{{lp.chatInfo}}</div>
      <div class="im-icon-btn pointer"  @click="clickClose" >
        <i class=" ooicon-close icon"></i>
      </div>
    </div>
    <div class="im-divider-v"></div>
    <div class="im-chat-info-body">
      <!--  头像和名称    -->
      <div class="im-chat-info-avatar-name">
        <div class="avatar">
          <img :src=" conversationIconUrl(conversation.id) " alt="avatar">
        </div>
        <div  v-if="!isTitleEdit" :class=" isGroupAdmin ? 'name pointer' : 'name' "  @click="clickTitleEdit(true)" >{{ conversationName(conversation)  }} <i v-if="isGroupAdmin" class="ooicon-edit"></i> </div>
        <div v-if="isTitleEdit" class="name" >
          <oo-input type="text" :placeholder="lp.groupNamePlaceholder" :value="form.title" @input="form.title = $event.target.value" ></oo-input>
          <div   style="display: flex;flex-direction: row;align-items: center;margin-top: 0.7rem;">
            <oo-button  @click="clickSaveTitle">{{lp.ok}}</oo-button>
            <oo-button  type="cancel" style=" margin-left: 0.7rem;" @click="clickTitleEdit(false)">{{lp.cancel}}</oo-button>
          </div>
        </div>


      </div>
      <!--        成员列表-->
      <div class="im-chat-info-item" v-if="isGroup">
        <div class="title-line">
          <div class="tag"></div>
          <div class="label">
            {{ lp.groupMember }}
          </div>
        </div>
        <div class="im-chat-info-members"  >
          <div :class="isGroupAdmin ? 'im-chat-info-members-item pointer': 'im-chat-info-members-item' " v-for="person in memberList" :key="person" @click="clickRemoveMember(person)">
            <div class="chat-sender-avatar">
              <img :src=" getAvatarUrl(person) " alt="avatar">
            </div>

            <div class="im-chat-info-member-name">
              <div>{{ formatPersonName(person) }}</div>
              <div class="im-chat-info-member-remove" v-if="removeMemberIcon(person)">
                <i class="ooicon-error"></i>
              </div>
            </div>

            <div class="im-chat-info-member-tag-admin" v-if="isGroupAdminMember(person)">群主</div>
            <div class="im-chat-info-member-tag-self"  v-if="isSelfMember(person)">本人</div>

          </div>
          <div class="im-chat-info-members-item pointer"  v-if="isGroupAdmin" @click="clickAddMember">
            <div class="chat-sender-avatar im-chat-info-member-add">
              <i class="ooicon-add-circle icon"></i>
            </div>
            <div class="im-chat-info-member-name">
              {{ lp.groupAddMember }}
            </div>
          </div>
        </div>
      </div>
      <div class="margin-top-10 margin-bottom-10"></div>
      <!--        群名-->
<!--      <div class="im-chat-info-item" v-if="isGroup">-->
<!--        <div class="label">{{lp.groupName}}</div>-->
<!--        <div v-if="!isTitleEdit" :class=" isGroupAdmin ? 'value pointer' : 'value' "  @click="clickTitleEdit(true)">{{ form.title ? form.title : lp.groupNamePlaceholder}} <i class="ooicon-edit"></i> </div>-->
<!--        <div v-if="isTitleEdit" class="value" >-->
<!--          <oo-input type="text" :placeholder="lp.groupNamePlaceholder" :value="form.title" @input="form.title = $event.target.value" ></oo-input>-->
<!--        </div>-->
<!--        <div v-if="isTitleEdit"   style="display: flex;flex-direction: row;align-items: center;margin-top: 0.7rem;">-->
<!--          <oo-button  @click="clickSaveTitle">{{lp.ok}}</oo-button>-->
<!--          <oo-button  type="cancel" style=" margin-left: 0.7rem;" @click="clickTitleEdit(false)">{{lp.cancel}}</oo-button>-->
<!--        </div>-->
<!--      </div>-->
      <!--        群公告-->
      <div class="im-chat-info-item" v-if="isGroup">
        <div class="title-line">
          <div class="tag"></div>
          <div class="label">
            {{ lp.groupNote }}
          </div>
        </div>

        <div v-if="!isNoteEdit"   :class=" isGroupAdmin ? 'value pointer' : 'value' "  @click="clickNoteEdit(true)">{{ form.note ? form.note : lp.groupNotePlaceholder}} <i v-if="isGroupAdmin" class="ooicon-edit"></i></div>
        <div v-if="isNoteEdit"  class="value" >
          <oo-input type="text" :placeholder="lp.groupNotePlaceholder" :value="form.note" @input="form.note = $event.target.value" ></oo-input>
        </div>
        <div v-if="isNoteEdit" style="display: flex;flex-direction: row;align-items: center;margin-top: 0.7rem;">
          <oo-button  @click="clickSaveNote">{{lp.ok}}</oo-button>
          <oo-button  type="cancel" style=" margin-left: 0.7rem;" @click="clickNoteEdit(false)">{{lp.cancel}}</oo-button>
        </div>
      </div>
      <!--        置顶-->
<!--      <div class="im-chat-info-item">-->
<!--        <div class="label row">-->
<!--          <div>-->
<!--            {{lp.chatTop}}-->
<!--          </div>-->
<!--          <oo-checkbox @change="changeTop" checked="{{$.isTop ? 'true' : 'false'}}" ></oo-checkbox>-->
<!--        </div>-->
<!--      </div>-->

      <!--        退出-->
      <div  v-if="quitChatShow">
        <div class="im-divider-v margin-top-10 margin-bottom-10"></div>
        <div class="im-chat-info-item" @click="clickQuit">
          <div class="danger pointer">{{ lp.deleteChat }}</div>
        </div>
        <div class="im-divider-v"></div>
      </div>
      <!--   退出群聊 普通成员   -->
      <div  v-if="quitGroupByNormal">
        <div class="im-divider-v margin-top-10 margin-bottom-10"></div>
        <div class="im-chat-info-item" @click="clickQuitGroup">
          <div class="danger pointer">{{ lp.quitGroup }}</div>
        </div>
        <div class="im-divider-v"></div>
      </div>

    </div>
  </div>
</template>

<style scoped>

</style>