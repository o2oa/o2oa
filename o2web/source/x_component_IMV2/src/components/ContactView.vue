<script setup>
import {lp} from "@o2oa/component";
import {ref, onMounted} from "vue";
import {orgExpressUnitAction, orgIdentityAction, orgPersonAction, orgUnitAction} from "../utils/actions.js";
import OrgNodeView from "./OrgNodeView.vue";
import {debounce} from "../utils/common.js";

const emit = defineEmits(['chatWithPerson'])

const searchContent = ref('')
const searchMode = ref(false)
const searchPersonList = ref([])
const treeData = ref([])

const inputSearch = (e) => {
  searchContent.value = e.target.value
  _searchConversation(e.target.value)
}

const _searchConversation = async (content) => {
  if (!content) {
    clearSearch()
    return
  }
  searchMode.value = true
  // 搜索
  searchOnlineData(content)
}
const searchOnlineData = debounce(async (val) => {
  if (!val) return
  const body = {
    key: val
  }
  const result = await orgIdentityAction('listLike', body)
  searchPersonList.value = result || []
}, 300)

const clearSearch = () => {
  searchContent.value = ''
  searchMode.value = false
  searchPersonList.value = []
}

onMounted(async () => {
  const topList = await listTop()

  for (const org of topList) {
    const node = {
      unique: org.unique,
      name: org.name,
      distinguishedName: org.distinguishedName,
      expanded: true,   // 默认展开
      loaded: false,
      children: [],
      persons: []
    }

    await loadChildren(node) // 默认加载第二层
    treeData.value.push(node)
  }
})

const clickPersonIdentity = async (identity) => {
  console.debug('contactView clickPersonIdentity ====>', identity)
  if (identity && identity.person) {
    const person = await orgPersonAction('get', identity.person)
    console.debug(person)
    if (person &&  person.distinguishedName) {
      emit('chatWithPerson', person.distinguishedName)
    }
  } else {
    console.error('错误的对象', identity)
  }
}
// 点击展开
const handleToggle = async (node) => {
  node.expanded = !node.expanded
  if (node.expanded) {
    await loadChildren(node)
  }
}
// 加载组织人员
const loadChildren = async (node) => {
  if (node.loaded) return

  const [orgs, persons] = await Promise.all([
    listByParent(node.unique),
    personListByUnit(node.distinguishedName)
  ])

  node.children = orgs.map(org => ({
    unique: org.unique,
    name: org.name,
    distinguishedName: org.distinguishedName,
    expanded: false,
    loaded: false,
    children: [],
    persons: []
  }))

  node.persons = persons
  node.loaded = true
}
const listTop = async () => {
  const topList = await orgUnitAction('listTop')
  return topList || []
}
// 查询下级组织
const listByParent = async (parent) => {
  const body = {
    countSubDirectIdentity: true,
    countSubDirectUnit: true,
    unitList: [parent]
  }
  const unitList = await orgExpressUnitAction('listWithUnitSubDirectObject', body)
  return unitList || []
}
// 查询组织下人员
const personListByUnit = async (unit) => {
  const identityList = await orgIdentityAction('listWithUnit', unit)
  return identityList || []
}
</script>

<template>
  <div class="im-conversation-view">
    <div class="im-conversation-search">
      <oo-input style="width: 100%;" type="text" leftIcon="search" :value="searchContent"
                :placeholder="lp.conversationSearchPlaceholder" @input="inputSearch">
        <span v-if="searchContent!==''" class="arrow" slot="after-inner-after" @click="clearSearch"><i class="ooicon-error"></i></span>
      </oo-input>
    </div>
    <!--    搜索结果列表-->
    <div class="im-conversation-list" v-if="searchMode">
      <div class="org-tree">
        <div class="org-children">
          <!-- 人员 -->
          <div
              v-for="p in searchPersonList"
              :key="p.id"
              class="person"
              @click="clickPersonIdentity(p)"
          >
          <span class="arrow">
            <i class="ooicon-person"></i>
          </span>
            <span class="name">{{ p.name }}({{ p.unitLevelName }})</span>
          </div>
        </div>
      </div>
    </div>
    <!--    组织树 -->
    <div class="im-conversation-list" v-else>
      <div class="org-tree">
        <OrgNodeView
            v-for="node in treeData"
            :key="node.unique"
            :node="node"
            @toggle="handleToggle"
            @clickPersonIdentity="clickPersonIdentity"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.org-tree {
  font-size: 1rem;
  margin: 0.8rem 0.8rem 0 0.8rem;
}

.person {
  cursor: pointer;
  user-select: none;
  display: flex;
  align-items: center;
  padding: var(--oo-default-radius) 0;
}

.org-title i {
  font-size: 1.2rem;
}

.arrow {
  width: 2rem;
  text-align: center;
}

.name {
  font-weight: 500;
  flex: 1;
}
</style>