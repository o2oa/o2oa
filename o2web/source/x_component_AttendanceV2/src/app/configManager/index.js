import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { configAction } from "../../utils/actions";
import template from "./template.html";
import style from './style.scope.css';

export default content({
  style,
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
      form: {},
      holidayList: [],
      workDayList: [],
    };
  },
  beforeRender() {
    
  },
  afterRender() {
    this.loadConfig();
    this.loadHolidayDateSelector();
    this.loadWorkdayDateSelector();
  },
  // 获取配置对象
  async loadConfig() {
    const json = await configAction("get");
    if (json) {
      this.bind.form = json || {};
      if (json.holidayList) {
        this.bind.holidayList = json.holidayList;
      }
      if (json.workDayList) {
        this.bind.workDayList = json.workDayList;
      }
    }
  },
  // 保存
  async submit() {
    const form = this.bind.form;
    form.holidayList = this.bind.holidayList;
    form.workDayList = this.bind.workDayList;
    const result = await configAction("post", form);
    console.log(result);
    o2.api.page.notice(lp.saveSuccess, 'success');
    this.loadConfig();
  },

  // 节假日日期选择器
  loadHolidayDateSelector() {
    MWF.require("MWF.widget.Calendar", function(){
      const options = {
          "style": "xform",
          "secondEnable" : false,
          "timeSelectType" : "select",
          "clearEnable": false,
          "isTime": false,
          "timeOnly": false,
          "monthOnly" : false,
          "yearOnly" : false,
          "defaultDate": null,
          "defaultView" : "day",
          "target":  this.dom,
          "baseDate": new Date(),
          "onComplate": function(formateDate, date){
            const year = date.getFullYear();
            const month = (date.getMonth() + 1) > 9 ? `${date.getMonth() + 1}`:`0${(date.getMonth() + 1)}`;
            const day = (date.getDate()) > 9 ? `${date.getDate()}`:`0${date.getDate()}`;
            const chooseDate = `${year}-${month}-${day}`;
            if (this.bind.holidayList.indexOf(chooseDate) < 0) {
              this.bind.holidayList.push(chooseDate);
            }
          }.bind(this),
      };
      const bindDom = this.dom.querySelector("#holidaysDateSelector");
      new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
   //   删除一个节假日
   deleteHolidayDateSelector(value) {
    let i = -1;
    for (let index = 0; index <  this.bind.holidayList.length; index++) {
      const element =  this.bind.holidayList[index];
      if (value === element) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.holidayList.splice(i, 1);
    }
  },
   // 工作日日期选择器
   loadWorkdayDateSelector() {
    MWF.require("MWF.widget.Calendar", function(){
      const options = {
          "style": "xform",
          "secondEnable" : false,
          "timeSelectType" : "select",
          "clearEnable": false,
          "isTime": false,
          "timeOnly": false,
          "monthOnly" : false,
          "yearOnly" : false,
          "defaultDate": null,
          "defaultView" : "day",
          "target":  this.dom,
          "baseDate": new Date(),
          "onComplate": function(formateDate, date){
            const year = date.getFullYear();
            const month = (date.getMonth() + 1) > 9 ? `${date.getMonth() + 1}`:`0${(date.getMonth() + 1)}`;
            const day = (date.getDate()) > 9 ? `${date.getDate()}`:`0${date.getDate()}`;
            const chooseDate = `${year}-${month}-${day}`;
            if (this.bind.workDayList.indexOf(chooseDate) < 0) {
              this.bind.workDayList.push(chooseDate);
            }
          }.bind(this),
      };
      const bindDom = this.dom.querySelector("#workdaysDateSelector");
      new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
  //   删除一个工作日
  deleteWorkdayDateSelector(value) {
    let i = -1;
    for (let index = 0; index <  this.bind.workDayList.length; index++) {
      const element =  this.bind.workDayList[index];
      if (value === element) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.workDayList.splice(i, 1);
    }
  },

});
