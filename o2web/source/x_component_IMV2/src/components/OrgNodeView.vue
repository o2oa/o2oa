<script setup>
const props = defineProps({
  node: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['toggle'])

function toggle() {
  debugger
  emit('toggle', props.node)
}
</script>

<template>
  <div class="org-node">
    <div class="org-title" @click="toggle">
      <span class="arrow"><i class="ooicon-workcenter"></i></span>
      <span class="name">{{ node.name }}</span>
      <span class="arrow">
      <i :class="node.expanded ? 'ooicon-drop_down':'ooicon-arrow_forward'"></i>
    </span>
    </div>

    <div v-show="node.expanded" class="org-children">
      <!-- 人员 -->
      <div
          v-for="p in node.persons"
          :key="p.id"
          class="person"
      >
      <span class="arrow">
        <i class="ooicon-person"></i>
      </span>
        <span class="name">{{ p.name }}</span>
      </div>

      <!-- 子组织 -->
      <OrgNodeView
          v-for="child in node.children"
          :key="child.unique"
          :node="child"
          @toggle="$emit('toggle', $event)"
      />
    </div>
  </div>
</template>

<style scoped>
.org-title,.person {
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

.org-children {
  margin-left: 1.29rem;
}

</style>