<template>
  <div class="o2task">

    <h3>{{lp.taskListTitle}}</h3>
    <br/>
    <table align="center" class="taskListTable" border="1">
      <tbody>
        <tr>
          <th>{{lp.taskTitle}}</th>
          <th>{{lp.taskProcess}}</th>
          <th>{{lp.taskTime}}</th>
        </tr>
        <tr v-for="task in taskList">
          <td><a href="#" @click="openTask(task.work)">{{task.title}}</a></td>
          <td>{{task.processName}}</td>
          <td>{{task.startTime}}</td>
        </tr>
      </tbody>
    </table>
    <br>
    <button @click="openCalendar">{{lp.openCalendar}}</button>
    <button @click="openOrganization">{{lp.openOrganization}}</button>
    <button @click="startProcess">{{lp.startProcess}}</button>
    <button @click="createDocument">{{lp.createDocument}}</button>
    <br>
    <button @click="openInBrowser">{{lp.openInBrowser}}</button>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {o2, lp, component} from '@o2oa/component'

const taskList = ref([]);
o2.Actions.load("x_processplatform_assemble_surface").TaskAction.V2ListPaging(1, 5).then((json)=>{
  taskList.value = json.data;
});

function openTask(id){
  o2.api.page.openWork(id);
}
function openCalendar(){
  o2.api.page.openApplication("Calendar");
}
function openOrganization(){
  o2.api.page.openApplication("Org");
}
function openInBrowser() {
  component.openInNewBrowser(true);
}
function startProcess(){
  o2.api.page.startProcess();
}
function createDocument(){
  o2.api.page.createDocument();
}
</script>

<style scoped>
.taskListTable{
  width: 800px;
  box-sizing: border-box;
  border-collapse: collapse;
}
.taskListTable th{
  height: 30px;
  line-height: 30px;
  background-color: #d4e6fb;
}
.taskListTable td{
  height: 24px;
  line-height: 24px;
}
button {
  cursor: pointer;
  font-size: 12px;
  margin: 10px;
  padding: 5px 10px;
  color: #ffffff;
  background-color: #4a90e2;
  border: 1px solid #4a90e2;
  border-radius: 100px;
}
h3 {
  margin: 40px 0 0;
}
a {
  color: #4a90e2;
}
</style>
