import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { isEmpty } from "../../../utils/common";
import { groupAction, attendanceWorkPlaceV2Action } from "../../../utils/actions";
import template from "./temp.html";
import oInput from "../../../components/o-input";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import selectShift from "../../shiftManager/selectShift";
import addressSelector from "../../addressManager/addressSelector";

export default content({
  template,
  components: { oInput, oOrgPersonSelector, selectShift, addressSelector },
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.groupAdd,
      form: {
        groupName: "",
        participateList: [],
        unParticipateList: [],
        workPlaceIdList: [],
        allowFieldWork: false,  // 是否允许外勤打卡.
        requiredFieldWorkRemarks: false, // 外勤打卡备注是否必填.
        requiredCheckInDateList: [], // 必须打卡的日期，如 2023-01-01.
        noNeedCheckInDateList: [], // 无需打卡的日期，如 2023-01-01.
      },
      // 班次选择器使用
      shiftSelectorOpen: false,
      shiftSelector: {
        shiftSelected: {},
      },
      // 工作场所选择器使用
      workPlaceSelectorOpen: false,
      workPlaceSelector: {
        workAddressSelected: []
      },
      workDateList: [], // 选中的days
      days: [
        {
          name: "周日",
          value: "0",
        },
        {
          name: "周一",
          value: "1",
        },
        {
          name: "周二",
          value: "2",
        },
        {
          name: "周三",
          value: "3",
        },
        {
          name: "周四",
          value: "4",
        },
        {
          name: "周五",
          value: "5",
        },
        {
          name: "周六",
          value: "6",
        },
      ],
    };
  },
  afterRender() {
    if (this.bind.form && this.bind.form.id && this.bind.form.id !== "") { // 修改
      this.bind.fTitle = this.bind.lp.groupUpdate;
      // 班次对象
      this.bind.shiftSelector.shiftSelected = this.bind.form.shift;
      // 回查工作场所对象
      this.loadWorkPlaceObjectsByIds(this.bind.form.workPlaceIdList);
      // 日期列表分拆
      const dateList = this.bind.form.workDateList.split(",");
      this.bind.workDateList= dateList || [];
    }
    // 时间选择器
    this.loadRequiredDateSelector();
    this.loadNoNeedDateSelector();
  },
  // 打开班次选择器
  async openShiftSelect() {
    this.bind.shiftSelectorOpen = true;
  },
  // 关闭班次选择器
  closeShiftSelect() {
    this.bind.shiftSelectorOpen = false;
  },
  // 点击days的checkbox
  selectDay(value) {
    if (value.value) {
      const index = this.bind.workDateList.indexOf(value.value);
      if (index > -1) {
        this.bind.workDateList.splice(index, 1);
      } else {
        this.bind.workDateList.push(value.value);
      }
    }
  },
  async loadWorkPlaceObjectsByIds(ids) {
    const list = await attendanceWorkPlaceV2Action("listWithWorkPlaceObject", {"idList": ids});
    this.bind.workPlaceSelector.workAddressSelected = list || [];
    
  },
  // 打开工作场所选择器
  async openWorkPlaceSelector() {
    this.bind.workPlaceSelectorOpen = true;
  },
  // 关闭工作场所选择器
  closeSelectWorkPlace() {
    this.bind.workPlaceSelectorOpen = false;
  },
  // 工作场所
  deleteWorkPlace(value) {
    let i = -1;
    for (let index = 0; index < this.bind.workPlaceSelector.workAddressSelected.length; index++) {
      const element = this.bind.workPlaceSelector.workAddressSelected[index];
      if (value.id === element.id) {
        i = index;
        break
      }
    }
    console.debug(i);
    if (i > -1) {
      this.bind.workPlaceSelector.workAddressSelected.splice(i, 1);
    }
  },
  clickAllowFieldWork() {
    this.bind.form.allowFieldWork = !this.bind.form.allowFieldWork;
  },
  clickRequiredFieldWorkRemarks() {
    this.bind.form.requiredFieldWorkRemarks = !this.bind.form.requiredFieldWorkRemarks;
  },
  // 特殊打卡日期
  loadRequiredDateSelector() {
    MWF.require("MWF.widget.Calendar", function(){
      var defaultView = "day";
      var options = {
          "style": "xform",
          "secondEnable" : false,
          "timeSelectType" : "select",
          "clearEnable": false,
          "isTime": false,
          "timeOnly": false,
          "monthOnly" : false,
          "yearOnly" : false,
          "defaultDate": null,
          "defaultView" : defaultView,
          "target":  this.dom,
          "baseDate": new Date(),
          "onComplate": function(formateDate, date){
            let year = date.getFullYear();
            let month = (date.getMonth() + 1) > 9 ? `${date.getMonth() + 1}`:`0${(date.getMonth() + 1)}`;
            let day = (date.getDate()) > 9 ? `${date.getDate()}`:`0${date.getDate()}`;
            const chooseDate = `${year}-${month}-${day}`;
            if (this.bind.form.requiredCheckInDateList.indexOf(chooseDate) < 0) {
              this.bind.form.requiredCheckInDateList.push(chooseDate);
            }
          }.bind(this),
      };
      let bindDom = this.dom.querySelector("#requiredDateSelector");
      new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
  // 必须打卡日期删除
  deleteRequiredDate(value) {
    let i = -1;
    for (let index = 0; index <  this.bind.form.requiredCheckInDateList.length; index++) {
      const element =  this.bind.form.requiredCheckInDateList[index];
      if (value === element) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.form.requiredCheckInDateList.splice(i, 1);
    }
  },
  loadNoNeedDateSelector() {
    MWF.require("MWF.widget.Calendar", function(){
      var defaultView = "day";
      var options = {
          "style": "xform",
          "secondEnable" : false,
          "timeSelectType" : "select",
          "clearEnable": false,
          "isTime": false,
          "timeOnly": false,
          "monthOnly" : false,
          "yearOnly" : false,
          "defaultDate": null,
          "defaultView" : defaultView,
          "target":  this.dom,
          "baseDate": new Date(),
          "onComplate": function(formateDate, date){
            let year = date.getFullYear();
            let month = (date.getMonth() + 1) > 9 ? `${date.getMonth() + 1}`:`0${(date.getMonth() + 1)}`;
            let day = (date.getDate()) > 9 ? `${date.getDate()}`:`0${date.getDate()}`;
            const chooseDate = `${year}-${month}-${day}`;
            if (this.bind.form.noNeedCheckInDateList.indexOf(chooseDate) < 0) {
              this.bind.form.noNeedCheckInDateList.push(chooseDate);
            }
          }.bind(this),
      };
      let bindDom = this.dom.querySelector("#noNeedDateSelector");
      new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
  // 无需打卡日期删除
  deleteNoNeedDateSelector(value) {
    let i = -1;
    for (let index = 0; index <  this.bind.form.noNeedCheckInDateList.length; index++) {
      const element =  this.bind.form.noNeedCheckInDateList[index];
      if (value === element) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.form.noNeedCheckInDateList.splice(i, 1);
    }
  },
  // 关闭当前窗口
  close() {
    this.$parent.closeGroup();
  },
  async submit() {
    debugger;
    let myForm = this.bind.form;
    // 考勤组名称
    if (isEmpty(myForm.groupName)) {
      o2.api.page.notice(lp.groupForm.titleErrorNotEmpty, 'error');
      return ;
    }
    // 考勤人员
    if (myForm.participateList.length < 1) {
      o2.api.page.notice(lp.groupForm.participatesErrorNotEmtpy, 'error');
      return ;
    }
    // 班次选择
    const shiftSelected = this.bind.shiftSelector.shiftSelected;
    if (!shiftSelected || isEmpty(shiftSelected.id)) {
      o2.api.page.notice(lp.groupForm.shiftErrorNotEmpty, 'error');
      return ;
    }
    myForm.shiftId = shiftSelected.id; // 班次
    // 工作日期 day
    if (this.bind.workDateList.length < 1) {
      o2.api.page.notice(lp.groupForm.timeErrorNotEmpty, 'error');
      return ;
    }
    myForm.workDateList = this.bind.workDateList.join(",");
    // 工作地址
    if ( this.bind.workPlaceSelector.workAddressSelected.length < 1) {
      o2.api.page.notice(lp.groupForm.workPlaceErrorNotEmpty, 'error');
      return ;
    }
    let workPlaceIdList = [];
    for (let index = 0; index < this.bind.workPlaceSelector.workAddressSelected.length; index++) {
      const element = this.bind.workPlaceSelector.workAddressSelected[index];
      workPlaceIdList.push(element.id);
    }
    myForm.workPlaceIdList = workPlaceIdList;
    const result = await groupAction("createOrUpdate", myForm);
    console.log(result);
    o2.api.page.notice(lp.saveSuccess, 'success');
    this.close();
  },
});
