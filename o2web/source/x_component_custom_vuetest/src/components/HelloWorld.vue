<template>
  <img class="logo" alt="O2OA logo" src="../assets/o2logo.png">
  <div class="hello">
    <h1>{{ lp.welcome }} </h1>
    <p>
      For more O2OA development document,<br>
      check out the
      <a href="https://www.o2oa.net/develop.html" target="_blank" rel="noopener">O2OA develop documentation</a>.
    </p>
    <p>
      For a guide and recipes on how to configure / customize this project,<br>
      check out the
      <a href="https://cli.vuejs.org" target="_blank" rel="noopener">vue-cli documentation</a>.
    </p>
  </div>
  <O2Task/>
</template>

<script setup>
import {lp} from '@o2oa/component'
import O2Task from './O2Task.vue'

defineProps({
  msg: String
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
.logo {
  width: 200px;
  height: 200px;
}
</style>
