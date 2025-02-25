<script setup>
import {lp} from '@o2oa/component'
import O2Task from './O2Task.vue'

import { ref } from 'vue'

defineProps({
  msg: String,
})

const count = ref(0)

</script>

<template>
  <h1>{{ msg }}</h1>

  <div class="card">
    <button type="button" @click="count++">count is {{ count }}</button>
    <p>
      Edit
      <code>components/HelloWorld.vue</code> to test HMR
    </p>
  </div>

  <h2>{{ lp.welcome }} </h2>
  <p>
    For more O2OA development document,<br>
    check out the
    <a href="https://www.o2oa.net/develop.html" target="_blank" rel="noopener">O2OA develop documentation</a>.
  </p>
  <p>
    Learn more about IDE Support for Vue in the
    <a
      href="https://vuejs.org/guide/scaling-up/tooling.html#ide-support"
      target="_blank"
      >Vue Docs Scaling up Guide</a
    >.
  </p>
  <p class="read-the-docs">Click on the Vite Vue and O2OA logos to learn more</p>
  <O2Task/>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
