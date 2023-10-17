<template>
  <div>
    <div class="systemconfig_item_title">{{lp._ternaryManagement.securityClearanceEnable}}</div>
    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.securityClearanceEnableInfo"></div>
    <BaseBoolean :value="securityClearanceEnable" @change="(value)=>{saveConfig('ternaryManagement', 'securityClearanceEnable', value); securityClearanceEnable=value}" />

<!--    <div v-if="securityClearanceEnable">-->

    <div class="systemconfig_item_title">{{lp._ternaryManagement.systemSecurityClearance}}</div>
    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.systemSecurityClearanceInfo"></div>
    <BaseItem
        :config="systemSecurityClearance"
        :allowEditor="true"
        type="select"
        :options="objectSecurityClearance.reduce((acc, obj) => { acc[obj.value] = obj.label; return acc; }, {})"
        @changeConfig="(value)=>{systemSecurityClearance = value.toInt(); saveConfig('ternaryManagement', 'systemSecurityClearance', value.toInt()); }"
    ></BaseItem>

    <div class="systemconfig_item_title">{{lp._ternaryManagement.defaultSubjectSecurityClearance}}</div>
    <div class="systemconfig_item_info" v-html="lp._ternaryManagement.defaultSubjectSecurityClearanceInfo"></div>
    <BaseItem
        :config="defaultSubjectSecurityClearance"
        :allowEditor="true"
        type="select"
        :options="subjectSecurityClearance.reduce((acc, obj) => { acc[obj.value] = obj.label; return acc; }, {})"
        @changeConfig="(value)=>{defaultSubjectSecurityClearance = value.toInt(); saveConfig('ternaryManagement', 'defaultSubjectSecurityClearance', value.toInt()); }"
    ></BaseItem>

    <div style="display: none">
      <div class="systemconfig_item_title">{{lp._ternaryManagement.subjectSecurityClearance}}</div>
      <div class="systemconfig_item_info" v-html="lp._ternaryManagement.subjectSecurityClearanceInfo"></div>
      <div class="item_info">
        <el-table :data="subjectSecurityClearance" style="width: 70%">
          <el-table-column prop="label" :label="lp._ternaryManagement.labelName">
            <template #default="scope">
              <el-form v-if="subjectSecurityClearanceEdit" :rules="subjectNameRules" ref="subjectFormNameRef" size="default" :model="scope.row" status-icon>
                <el-form-item prop="label" size="default">
                  <el-input v-model="scope.row.label" size="default"></el-input>
                </el-form-item>
              </el-form>

            </template>
          </el-table-column>
          <el-table-column prop="value" :label="lp._ternaryManagement.labelValue">
            <template #default="scope">
              <el-form v-if="subjectSecurityClearanceEdit" :rules="subjectValueRules" ref="subjectFormValueRef" size="default" :model="scope.row">
                <el-form-item prop="value" size="default">
                  <el-input-number v-model="scope.row.value"></el-input-number>
                </el-form-item>
              </el-form>
            </template>
          </el-table-column>
          <el-table-column width="100">
            <template v-if="subjectSecurityClearanceEdit" #default="scope">
              <div v-if="subjectSecurityClearanceEdit" class="item_module_store_del o2icon-del" @click="deleteSubjectLabel(scope)"></div>
<!--              <div v-if="subjectSecurityClearanceEdit" class="item_module_store_del o2icon-plus" @click="deleteSubjectLabel(scope)"></div>-->
            </template>
          </el-table-column>
        </el-table>

        <div style="padding: 10px 0; width: 70%">
          <button v-if="!subjectSecurityClearanceEdit" class="mainColor_bg" @click="subjectSecurityClearanceEdit=true">{{lp.operation.edit}}</button>
          <div v-if="subjectSecurityClearanceEdit" style="display: flex; justify-content: space-between;">
            <button class="mainColor_bg" @click="addSubjectLabel">{{lp.operation.add}}</button>
            <div>
            <button class="mainColor_bg" @click="saveSubjectLabel">{{lp.operation.ok}}</button>
            <button class="grayColor_bg" @click="cancelSubjectLabel">{{lp.operation.cancel}}</button>
              </div>
          </div>
        </div>
      </div>


      <div class="systemconfig_item_title">{{lp._ternaryManagement.objectSecurityClearance}}</div>
      <div class="systemconfig_item_info" v-html="lp._ternaryManagement.objectSecurityClearanceInfo"></div>
      <div class="item_info">
        <el-table :data="objectSecurityClearance" style="width: 70%">
          <el-table-column prop="label" :label="lp._ternaryManagement.labelName">
            <template #default="scope">

              <el-form v-if="objectSecurityClearanceEdit" :rules="objectNameRules" ref="objectFormNameRef" size="default" :model="scope.row">
                <el-form-item prop="label" size="default">
                  <el-input v-model="scope.row.label" size="default" popper-class="systemconfig"></el-input>
                </el-form-item>
              </el-form>

            </template>
          </el-table-column>
          <el-table-column prop="value" :label="lp._ternaryManagement.labelValue">
            <template #default="scope">

              <el-form v-if="objectSecurityClearanceEdit" :rules="objectValueRules" ref="objectFormValueRef" size="default" :model="scope.row">
                <el-form-item prop="value" size="default">
                  <el-input-number v-model="scope.row.value"></el-input-number>
                </el-form-item>
              </el-form>

            </template>
          </el-table-column>
          <el-table-column width="100">
            <template #default="scope">
              <div v-if="objectSecurityClearanceEdit" class="item_module_store_del o2icon-del" @click="deleteObjectLabel(scope)"></div>
            </template>
          </el-table-column>
        </el-table>

        <div style="padding: 10px 0; width: 70%">
          <button v-if="!objectSecurityClearanceEdit" class="mainColor_bg" @click="objectSecurityClearanceEdit=true">{{lp.operation.edit}}</button>
          <div v-if="objectSecurityClearanceEdit" style="display: flex; justify-content: space-between;">
            <button class="mainColor_bg" @click="addObjectLabel">{{lp.operation.add}}</button>
            <div>
            <button class="mainColor_bg" @click="saveObjectLabel">{{lp.operation.ok}}</button>
            <button class="grayColor_bg" @click="cancelObjectLabel">{{lp.operation.cancel}}</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue';
import {lp} from '@o2oa/component';
import {getConfigData, saveConfig} from "@/util/acrions";
import BaseBoolean from '@/components/item/BaseBoolean.vue';
import BaseItem from '@/components/item/BaseItem.vue';

const subjectSecurityClearance = ref([]);
const objectSecurityClearance = ref([]);
const securityClearanceEnable = ref(false);
const defaultSubjectSecurityClearance = ref();
const systemSecurityClearance = ref();

const subjectSecurityClearanceEdit = ref(false);
const objectSecurityClearanceEdit = ref(false);

let subjectSecurityClearanceData = null;
let objectSecurityClearanceData = null;

const subjectFormNameRef = ref();
const subjectFormValueRef = ref();
const objectFormNameRef = ref();
const objectFormValueRef = ref();

getConfigData('ternaryManagement').then((data)=>{
  securityClearanceEnable.value = data.securityClearanceEnable;
  defaultSubjectSecurityClearance.value = (data.defaultSubjectSecurityClearance ?? '200').toString();
  systemSecurityClearance.value = (data.systemSecurityClearance ?? '300').toString();

  subjectSecurityClearanceData = data.subjectSecurityClearance ?? {};
  subjectSecurityClearance.value = Object.keys(subjectSecurityClearanceData).map((key)=>{
    return {label: key, value: subjectSecurityClearanceData[key]}
  });

  objectSecurityClearanceData = data.objectSecurityClearance ?? {};
  objectSecurityClearance.value = Object.keys(objectSecurityClearanceData).map((key)=>{
    return {label: key, value: objectSecurityClearanceData[key]}
  });

});

const deleteSubjectLabel = (scope)=>{
  subjectSecurityClearance.value.splice(scope.$index,1)
}
const deleteObjectLabel = (scope)=>{
  objectSecurityClearance.value.splice(scope.$index,1)
}

const addSubjectLabel = ()=>{
  subjectSecurityClearance.value.push({
    label: "",
    value: 0
  });
}
const addObjectLabel = ()=>{
  objectSecurityClearance.value.push({
    label: "",
    value: 0
  });
}

const cancelSubjectLabel = ()=>{
  subjectSecurityClearance.value = Object.keys(subjectSecurityClearanceData).map((key)=>{
    return {label: key, value: subjectSecurityClearanceData[key]}
  });
  subjectSecurityClearanceEdit.value = false;
}

const cancelObjectLabel = ()=>{
  objectSecurityClearance.value = Object.keys(objectSecurityClearanceData).map((key)=>{
    return {label: key, value: objectSecurityClearanceData[key]}
  });
  objectSecurityClearanceEdit.value = false;
}

const saveSubjectLabel = async () => {
  // objectSecurityClearance.value;
  let isValid = true;

  await Promise.all([subjectFormNameRef.value.validate((valid) => {
    isValid = isValid && valid;
  }), subjectFormValueRef.value.validate((valid) => {
    isValid = isValid && valid;
  })]);

  if (isValid){
    subjectSecurityClearanceData = subjectSecurityClearance.value.reduce((labels, obj)=>{
      labels[obj.label] = obj.value;;
      return labels;
    }, {});
    saveConfig('ternaryManagement', 'subjectSecurityClearance', subjectSecurityClearanceData)
    subjectSecurityClearanceEdit.value = false;
  }
}

const saveObjectLabel = async () => {
  let isValid = true;

  await Promise.all([objectFormNameRef.value.validate((valid) => {
    isValid = isValid && valid;
  }), objectFormValueRef.value.validate((valid) => {
    isValid = isValid && valid;
  })]);

  if (isValid) {
    objectSecurityClearanceData = objectSecurityClearance.value.reduce((labels, obj)=>{
      labels[obj.label] = obj.value;;
      return labels;
    }, {});
    saveConfig('ternaryManagement', 'objectSecurityClearance', objectSecurityClearanceData)
    objectSecurityClearanceEdit.value = false;
  }
}

const validateValue = (labels, rule, value, callback) => {
  const o = labels.filter((v)=>{
    return v.value===value;
  })

  if (o.length > 1) {
    callback(new Error(lp._ternaryManagement.labelValueSame));
  } else {
    callback();
  }
}

const validateName = (labels, rule, value, callback) => {
  const o = labels.filter((v)=>{
    return v.label===value;
  })

  if (o.length > 1) {
    callback(new Error(lp._ternaryManagement.labelValueSame));
  } else {
    callback();
  }
}

const validateSubjectValue = (rule, value, callback)=>{
  validateValue(subjectSecurityClearance.value, rule, value, callback)
}
const validateObjectValue = (rule, value, callback)=>{
  validateValue(objectSecurityClearance.value, rule, value, callback)
}
const validateSubjectName = (rule, value, callback)=>{
  validateName(subjectSecurityClearance.value, rule, value, callback)
}
const validateObjectName = (rule, value, callback)=>{
  validateName(objectSecurityClearance.value, rule, value, callback)
}


const subjectNameRules = reactive({
  label: [
    {required: true, message: lp._ternaryManagement.labelNameEmpty, trigger: 'blur'},
    {validator: validateSubjectName, trigger: 'blur' }
  ]
});
const objectNameRules = reactive({
  label: [
    {required: true, message: lp._ternaryManagement.labelNameEmpty, trigger: 'blur'},
    {validator: validateObjectName, trigger: 'blur' }
  ]
});

const subjectValueRules = reactive({
  value: [
    {required: true, message: lp._ternaryManagement.labelValueEmpty, trigger: 'blur'},
    {validator: validateSubjectValue, trigger: 'blur' }
  ]
});
const objectValueRules = reactive({
  value: [
    {required: true, message: lp._ternaryManagement.labelValueEmpty, trigger: 'blur'},
    {validator: validateObjectValue, trigger: 'blur' }
  ]
});


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
  margin-bottom: 18px;
}
.item_module_store_del:hover{
  background-color: #e34141;
  color: #ffffff;
  transition: background-color 1s;
}
</style>
