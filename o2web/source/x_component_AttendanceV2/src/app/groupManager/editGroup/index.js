import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { isEmpty } from "../../../utils/common";
import { groupAction } from "../../../utils/actions";
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
        
        shiftId: "",
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
    if (this.bind.form && this.bind.form.id && this.bind.form.id !== "") {
      this.bind.fTitle = this.bind.lp.groupUpdate;
    }
    this.loadRequiredDateSelector();
    this.loadNoNeedDateSelector();
  },
  // 打开班次选择器
  async openShiftSelect() {
    // const content = (await import("../../shiftManager/selectShift/index.js"))
    //   .default;
    // const dm = document.body.querySelector("#app-attendance-v2");
    // this.shiftSelectVm = await content.generate(dm, {}, this);
    this.bind.shiftSelectorOpen = true;
  },
  // 关闭班次选择器
  closeShiftSelect() {
    // if (this.shiftSelectVm) {
    //   this.shiftSelectVm.destroy();
    // }
    this.bind.shiftSelectorOpen = false;
  },
  // 班次选择 返回结果
  // reciveShiftSelect(value) {
  //   // value是一个shift的对象
  //   this.bind.form.shift = value || {};
  //   this.bind.form.shiftId = this.bind.form.shift.id || "";
  // },
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
  // 打开工作场所选择器
  async openWorkPlaceSelector() {
    // const content = (await import("../../addressManager/addressSelector/index.js"))
    //   .default;
    // const dm = document.body.querySelector("#app-attendance-v2");
    // this.workPlaceSelectVm = await content.generate(dm, {bind: {workAddressSelected: this.bind.form.workPlaceList}}, this);
    this.bind.workPlaceSelectorOpen = true;
  },
  // 关闭班次选择
  closeSelectWorkPlace() {
    // if (this.workPlaceSelectVm) {
    //   this.workPlaceSelectVm.destroy();
    // }
    this.bind.workPlaceSelectorOpen = false;
  },

  // reciveWorkPlaceSelect(value) {
  //   console.debug(value);
  //   this.bind.form.workPlaceList = value;
  // },
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

  close() {
    this.$parent.closeGroup();
  },
  async submit() {
    debugger;
    console.debug(this.bind);
    // this.close();
  },
});
