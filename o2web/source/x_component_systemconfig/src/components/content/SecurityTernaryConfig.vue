<template>
  <div class="systemconfig_area">
    <div class="systemconfig_title">{{lp.ternaryManagement}}</div>

    <div class="systemconfig_tab_area">
      <el-tabs tab-position="right">
        <el-tab-pane :label="lp._ternaryManagement.ternary" lazy>
          <TernaryConfig></TernaryConfig>
        </el-tab-pane>
        <el-tab-pane :label="lp._ternaryManagement.label" name="second" lazy>
          <LabelConfig></LabelConfig>
        </el-tab-pane>

      </el-tabs>
    </div>

<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.enable}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.enableInfo"></div>-->
<!--    <BaseBoolean :value="ternaryManagementEnable" @change="(value)=>{saveConfig('ternaryManagement', 'enable', value)}" />-->


<!--&lt;!&ndash;    @changeConfig="(value)=>{failureCount = value.toInt(); saveConfig('person', 'failureCount', value.toInt())}"&ndash;&gt;-->

<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.logRetainDays}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.logRetainDaysInfo"></div>-->
<!--    <div>-->
<!--      <div class="item_info" style="display: inline-flex; align-items: center;">-->
<!--        <label class="item_label">{{lp._ternaryManagement.logRetainDays}}</label>-->
<!--        <div class="item_input_area">-->
<!--          <BaseItem-->
<!--              :config="logRetainDays"-->
<!--              :allowEditor="true"-->
<!--              type="number"-->
<!--              @changeConfig="(value)=>{logRetainDays=value.toInt(); saveConfig('general', 'requestLogRetainDays', value.toInt())}"-->
<!--          ></BaseItem>-->

<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.logBodyEnable}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.logBodyEnableInfo"></div>-->
<!--    <BaseBoolean :value="logBodyEnable" @change="(value)=>{saveConfig('general', 'requestLogBodyEnable', value)}" />-->


<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.securityClearanceEnable}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.securityClearanceEnableInfo"></div>-->
<!--    <BaseBoolean :value="securityClearanceEnable" @change="(value)=>{saveConfig('ternaryManagement', 'securityClearanceEnable', value)}" />-->


<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.subjectSecurityClearance}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.subjectSecurityClearanceInfo"></div>-->
<!--    <div class="item_info">-->
<!--      <el-table :data="subjectSecurityClearance" style="width: 100%">-->
<!--        <el-table-column prop="label" :label="lp._ternaryManagement.labelName">-->
<!--          <template #default="scope">-->
<!--            <el-input v-model="scope.row.label" size="default" popper-class="systemconfig"></el-input>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column prop="value" :label="lp._ternaryManagement.labelValue">-->
<!--          <template #default="scope">-->
<!--            <el-input-number v-model="scope.row.value"></el-input-number>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column width="100">-->
<!--          <template #default="scope">-->
<!--            <div class="item_module_store_del o2icon-del"></div>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--      </el-table>-->
<!--&lt;!&ndash;      <div style="display: flex;justify-content: space-between;">&ndash;&gt;-->
<!--&lt;!&ndash;        &lt;!&ndash;                  <button class="mainColor_bg" style="margin-top: 10px; margin-left: 0" @click="saveStoreData(type)">{{lp._storageServer.saveStore}}</button>&ndash;&gt;&ndash;&gt;-->
<!--&lt;!&ndash;        <button style="margin-top: 10px; margin-left: 0" @click="addStore(type)">{{lp._storageServer.addStore}}</button>&ndash;&gt;-->
<!--&lt;!&ndash;      </div>&ndash;&gt;-->

<!--    </div>-->


<!--    <div class="systemconfig_item_title">{{lp._ternaryManagement.objectSecurityClearance}}</div>-->
<!--    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.objectSecurityClearanceInfo"></div>-->
<!--    <div class="item_info">-->
<!--      <el-table :data="objectSecurityClearance" style="width: 100%">-->
<!--        <el-table-column prop="label" :label="lp._ternaryManagement.labelName">-->
<!--          <template #default="scope">-->
<!--            <el-input v-model="scope.row.label" size="default" popper-class="systemconfig"></el-input>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column prop="value" :label="lp._ternaryManagement.labelValue">-->
<!--          <template #default="scope">-->
<!--            <el-input-number v-model="scope.row.value"></el-input-number>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column width="100">-->
<!--          <template #default="scope">-->
<!--            <div class="item_module_store_del o2icon-del"></div>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--      </el-table>-->
<!--    </div>-->

  </div>
</template>

<script setup>
import {lp} from '@o2oa/component';
import TernaryConfig from "@/components/content/SecurityTernaryConfig/TernaryConfig.vue";
import LabelConfig from "@/components/content/SecurityTernaryConfig/LabelConfig.vue";
</script>

<style scoped>
.item_module_store_del{
  height: 30px;
  width: 30px;
  line-height: 30px;
  background-color: #f1f1f1;
  border-radius: 15px;
  cursor: pointer;
  text-align: center;
  color: #555555;
}
.item_module_store_del:hover{
  background-color: #e34141;
  color: #ffffff;
  transition: background-color 1s;
}
</style>
