import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { isEmpty } from "../../../utils/common";
import { groupAction, attendanceWorkPlaceV2Action, attendanceShiftAction } from "../../../utils/actions";
import style from "./style.scope.css";
import template from "./temp.html";
import oInput from "../../../components/o-input";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import selectShift from "../../shiftManager/selectShift";
import addressSelector from "../../addressManager/addressSelector";

export default content({
  style,
  template,
  components: { oInput, oOrgPersonSelector, selectShift, addressSelector },
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.groupAdd,
      form: {
        groupName: "",
        checkType: "1", // 1 固定班制 2 自由打卡 3 排班制
        participateList: [],
        assistAdminList: [], // 协助管理员
        unParticipateList: [],
        workPlaceIdList: [],
        allowFieldWork: false,  // 是否允许外勤打卡.
        requiredFieldWorkRemarks: false, // 外勤打卡备注是否必填.
        fieldWorkMarkError: false, // 外勤打卡是否作为异常数据处理.
        requiredCheckInDateList: [], // 必须打卡的日期，如 2023-01-01.
        noNeedCheckInDateList: [], // 无需打卡的日期，如 2023-01-01.
      },
      // 班次选择器使用
      shiftSelectorOpen: false,
      shiftSelector: {
        key: "all", // 当前给那个地方选择班次
        shiftSelected: {},
      },
      // 工作场所选择器使用
      workPlaceSelectorOpen: false,
      workPlaceSelector: {
        workAddressSelected: []
      },
      // 固定班制 选择班次和打卡日子
      workDay: {
        monday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        tuesday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        wednesday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        thursday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        friday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        saturday: {
          checked: false,
          shiftId: "",
          shiftSelected: {},
        },
        sunday: {
          checked: false,
          shiftId: "",
          shiftSelected: {},
        },
      },
      // 自由工时 选择打卡日子
      workDateList: [], // 选中的days
      days: [
        {
          name: lp.day.Monday,
          value: "1",
        },
        {
          name: lp.day.Tuesday,
          value: "2",
        },
        {
          name: lp.day.Wednesday,
          value: "3",
        },
        {
          name: lp.day.Thursday,
          value: "4",
        },
        {
          name: lp.day.Friday,
          value: "5",
        },
        {
          name: lp.day.Saturday,
          value: "6",
        },
        {
          name: lp.day.Sunday,
          value: "0",
        },
      ],
      dateCycleList: [
        {name: lp.dateCycle.none, value: "none"},
        {name: lp.dateCycle.week, value: "week"}, 
        {name: lp.dateCycle.twoWeek, value: "twoWeek"}, 
        {name: lp.dateCycle.month, value: "month"}, 
      ],
      requiredCheckInDateForTableList: [], // 必须打卡的日期, 里面存对象 date, shift, cycle
      noNeedCheckInDateForTableList: [], // 无需打卡的日期，如, 里面存对象 date, cycle
    };
  },
  // 先查询数据
  async beforeRender() {
    if (this.bind.updateId) {
      const group = await groupAction("get", this.bind.updateId);
      if (group) {
        this.bind.form = group;
        if (group.workDateProperties) {
          // 处理成前端使用的对象 
          const dayList = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"];
          for (let key of dayList) {
            if (group.workDateProperties[key]) {
              let day = group.workDateProperties[key];
              if (day && day.shift) {
                day.shiftSelected = day.shift;
                group.workDateProperties[key] = day;
              }
            } else {
              group.workDateProperties[key] = {
                checked: false
              }
            }
          }
          this.bind.workDay = group.workDateProperties;
        }
      }
    }
  },
  // 
  afterRender() {
    if (this.bind.form && this.bind.form.id && this.bind.form.id !== "") { // 修改
      this.bind.fTitle = lp.groupUpdate;
      // 回查工作场所对象
      this.loadWorkPlaceObjectsByIds(this.bind.form.workPlaceIdList);
      // 日期列表分拆
      if (this.bind.form.workDateList) {
        const dateList = this.bind.form.workDateList.split(",");
        this.bind.workDateList= dateList || [];
      }
      // 必须打卡日期前端数据转化
      this.dealRequiredCheckInDate();
      // 无需打卡日期的前端数据转化
      this.dealNoNeedCheckInDate();
    }
    // 时间选择器
    this.loadRequiredDateSelector();
    this.loadNoNeedDateSelector();
  },
  async dealRequiredCheckInDate() {
    let list = [];
    for (let index = 0; index < this.bind.form.requiredCheckInDateList.length; index++) {
      const element = this.bind.form.requiredCheckInDateList[index];
      const dArray = element.split("|");
      const dateObj = {
        date: dArray[0]
      };
      if (dArray[1]) {
        const shift = await attendanceShiftAction("get", dArray[1]);
        dateObj.shift = shift;
      }
      if(dArray[2]) {
        const cycle = this.bind.dateCycleList.filter( c => c.value === dArray[2]);
        dateObj.cycle = cycle[0];
      }
      list.push(dateObj);
    }
    this.bind.requiredCheckInDateForTableList = list;
  },
  dealNoNeedCheckInDate() {
    this.bind.noNeedCheckInDateForTableList = this.bind.form.noNeedCheckInDateList.map(d => {
      const dArray = d.split("|");
      if (dArray[1]) {
        const cycle = this.bind.dateCycleList.filter( c => c.value === dArray[1]);
        return {
          date: dArray[0],
          cycle: cycle[0]
        };
      }
      return {
        date: dArray[0]
      };
    });
  },
  // 选择工作日
  changeWorkDayChecked(key) {
    if (key && this.bind.workDay[key]) {
      this.bind.workDay[key].checked = !this.bind.workDay[key].checked;
      if (!this.bind.workDay[key].checked) {
        this.bind.workDay[key].shiftSelected = {}; 
      }
    }
  },
  // 打开班次选择器 批量设置 已经选择的
  openShiftSelect(key) {
    this.bind.shiftSelector.key = key || "all";
    this.bind.shiftSelectorOpen = true;
  },
  // 班次选择器调用 返回选中的班次对象
  setShiftData(shift) {
    if (!shift) {
      return;
    }
    // 新对象 清除绑定信息
    const newShift =  {
      id: shift.id,
      shiftName: shift.shiftName,
    };
    // requiredCheckIn- 开头的key 表示在必需打卡日期的配置
    if (this.bind.shiftSelector.key.startsWith('requiredCheckIn-')) {
      const date = this.bind.shiftSelector.key.substring('requiredCheckIn-'.length);
      for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
        const element = this.bind.requiredCheckInDateForTableList[index];
        if (element.date === date) {
          element.shift = newShift;
          this.bind.requiredCheckInDateForTableList[index] = element;
        }
      }
    } else if (this.bind.shiftSelector.key  === "all") {
      this.bind.shiftSelector.shiftSelected = newShift;
      // 其它班次
      for (let prop in this.bind.workDay) {
        if (this.bind.workDay[prop] && this.bind.workDay[prop].checked) {
          this.bind.workDay[prop].shiftSelected = newShift;
        }
      }
    } else {
      if (this.bind.workDay[this.bind.shiftSelector.key]) {
        this.bind.workDay[this.bind.shiftSelector.key].shiftSelected = newShift;
      }
    }
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
  // 切换考勤类型
  clickChangeType(type) {
    this.bind.form.checkType = type;
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
    if (i > -1) {
      this.bind.workPlaceSelector.workAddressSelected.splice(i, 1);
    }
  },
  // 是否允许外勤
  clickAllowFieldWork() {
    this.bind.form.allowFieldWork = !this.bind.form.allowFieldWork;
  },
  // 外勤是否必填说明
  clickRequiredFieldWorkRemarks() {
    this.bind.form.requiredFieldWorkRemarks = !this.bind.form.requiredFieldWorkRemarks;
  },
  // 外勤是否作为异常数据处理
  clickFieldWorkMarkError() {
    this.bind.form.fieldWorkMarkError = !this.bind.form.fieldWorkMarkError;
  },
  // 必须打卡日期
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
            let isIn = false;
            for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
              const element = this.bind.requiredCheckInDateForTableList[index];
              if (element.date === chooseDate) {
                isIn = true;
                break;
              }
            }
            if (!isIn) {
              this.bind.requiredCheckInDateForTableList.push({
                date: chooseDate,
              });
            }
          }.bind(this),
      };
      let bindDom = this.dom.querySelector("#requiredDateSelector");
      new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
  // 必须打卡日期删除
  deleteRequiredDate(date) {
    let i = -1;
    for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
      const element = this.bind.requiredCheckInDateForTableList[index];
      if (date === element.date) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.requiredCheckInDateForTableList.splice(i, 1);
    }
  },
  showRequiredDateCycleSelector(id) {
    const selector = this.dom.querySelector("#o2-selector-"+id);
    const brother = this.dom.querySelector("#o2-required-date-"+id);
    const x = brother.getBoundingClientRect().left + document.documentElement.scrollLeft;
    const y = brother.getBoundingClientRect().top + document.documentElement.scrollTop;
    selector.style.left = x + "px";
    selector.style.top = (y+40)+"px";
    selector.style.position = "fixed";
    selector.show();
  },
  chooseDateCycleItem(date, cycle) {
    for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
      const element = this.bind.requiredCheckInDateForTableList[index];
      if (element.date === date) {
        element.cycle = cycle;
        this.bind.requiredCheckInDateForTableList[index] = element;
      }
    }
    this.dom.querySelector("#o2-selector-"+date).hide();
  },
  // 必须打卡的日期 修改周期
  chooseDateCycleItem2(e) {
    const id = e.target.id;
    const value = e.target.value;
    const date = id.substring('o2-selector-'.length);
    const cycle = this.bind.dateCycleList.filter(c => c.value === value);
    for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
      const element = this.bind.requiredCheckInDateForTableList[index];
      if (element.date === date) {
        element.cycle = cycle[0];
        this.bind.requiredCheckInDateForTableList[index] = element;
      }
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

            let isIn = false;
            for (let index = 0; index < this.bind.noNeedCheckInDateForTableList.length; index++) {
              const element = this.bind.noNeedCheckInDateForTableList[index];
              if (element.date === chooseDate) {
                isIn = true;
                break;
              }
            }
            if (!isIn) {
              const c = this.bind.dateCycleList[0]
              this.bind.noNeedCheckInDateForTableList.push({
                date: chooseDate,
                cycle: c
              });
            }
          }.bind(this),
      };
      let bindDom = this.dom.querySelector("#noNeedDateSelector");
      new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
   // 无需打卡日期 修改周期
   noNeedDateChangeCycle(e) {
    const id = e.target.id;
    const value = e.target.value;
    const date = id.substring('o2-noNeed-'.length);
    const cycle = this.bind.dateCycleList.filter(c => c.value === value);
    for (let index = 0; index < this.bind.noNeedCheckInDateForTableList.length; index++) {
      const element = this.bind.noNeedCheckInDateForTableList[index];
      if (element.date === date) {
        element.cycle = cycle[0];
        this.bind.noNeedCheckInDateForTableList[index] = element;
      }
    }
  },
  // 无需打卡日期删除
  deleteNoNeedDateSelector(value) {
    let i = -1;
    for (let index = 0; index <  this.bind.noNeedCheckInDateForTableList.length; index++) {
      const element =  this.bind.noNeedCheckInDateForTableList[index];
      if (value === element.date) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.noNeedCheckInDateForTableList.splice(i, 1);
    }
  },
  // 开始排班前 先保存数据
  startSchedule() {
    this.submit((result)=>{
      console.log(result);
      this.openScheduleWindow({bind: {trueParticipantList: result.trueParticipantList || [], groupId: result.id}})
    })
    
  },
  // 打开排班窗口
  async openScheduleWindow(data) {
    const bind = data || {};
    console.log(bind);
    const c = (await import('../schedule/index.js')).default;
    this.scheduleVm = await c.generate("#scheduleFom", bind, this);
  },
  // 关闭当前窗口
  close() {
    this.$topParent.publishEvent('group', {});
    this.$parent.closeFormVm();
  },
  async submit(callback) {
    debugger;
    const myForm = this.bind.form;
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
    // 根据考勤类型判断
    if (myForm.checkType === "1") {
        let flag = false;
        let isBreak = false;
        let workDateProperties = {};
        let baseShiftId = "";
        for (let key in this.bind.workDay) {
          const day = this.bind.workDay[key];
          if (day.checked && (!day.shiftSelected || isEmpty(day.shiftSelected.id))) {
            isBreak = true;
            break;
          }
          if (day.checked && day.shiftSelected && !isEmpty(day.shiftSelected.id)) {
            flag = true;
            workDateProperties[key] = {
              shiftId: day.shiftSelected.id,
              checked: day.checked,
            };
            if (baseShiftId === "") {
              baseShiftId = day.shiftSelected.id;
            }
          }
          if (!day.checked) {
            workDateProperties[key] = {
              checked: false,
            };
          }
        }
        if (isBreak) {
          o2.api.page.notice(lp.groupForm.shiftErrorNotEmpty, 'error');
          return;
        }
        if (!flag) {
          o2.api.page.notice(lp.groupForm.timeErrorNotEmpty, 'error');
          return ;
        }
        myForm.workDateProperties = workDateProperties;
        myForm.shiftId = baseShiftId;
    } else if (myForm.checkType === "2") {
        // 工作日期 day
        if (this.bind.workDateList.length < 1) {
          o2.api.page.notice(lp.groupForm.timeErrorNotEmpty, 'error');
          return ;
        }
        myForm.workDateList = this.bind.workDateList.join(",");
    } else if (myForm.checkType === "3"){
      // 排班校验
    } else {
      o2.api.page.notice(lp.groupForm.checkTypeError, 'error');
      return;
    }
    
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
    // 必须打卡的日期
    if (myForm.checkType === "1") { // 固定班制 一定要配置班次
      const rCheckInDateList = this.bind.requiredCheckInDateForTableList.map((d) => {
        const date = d.date;
        const shiftId = (d.shift && d.shift.id) ? d.shift.id : "";
        const cycle = (d.cycle && d.cycle.value) ?  d.cycle.value :  "";
        if (shiftId === "") {
          return "";
        }
        return date+"|"+shiftId+"|"+cycle;
      }).filter(str => !!str);
      
      if (this.bind.requiredCheckInDateForTableList.length > 0 && rCheckInDateList.length < 1) {
        o2.api.page.notice("必须打卡的日期得配置班次！", 'error');
        return;
      }
      myForm.requiredCheckInDateList = rCheckInDateList;
    } else if (myForm.checkType === "2") { // 自由工时 不需要班次
      const rCheckInDateList = this.bind.requiredCheckInDateForTableList.map((d) => {
        const date = d.date;
        const shiftId =  "";
        const cycle = (d.cycle && d.cycle.value) ?  d.cycle.value :  "";
        return date+"|"+shiftId+"|"+cycle;
      });
      myForm.requiredCheckInDateList = rCheckInDateList;
     }

    // 无需打卡的日期
    const noNeedCheckInDateList = this.bind.noNeedCheckInDateForTableList.map((d) => {
      const date = d.date;
      const cycle = (d.cycle && d.cycle.value) ?  d.cycle.value :  "";
      return date+"|"+cycle;
    });
    myForm.noNeedCheckInDateList = noNeedCheckInDateList;
    if (callback && callback instanceof Function && myForm.status  !== 1) {
      myForm.status = 2;
    } else {
      myForm.status = 1;
    }

    const result = await groupAction("createOrUpdate", myForm);
    console.log(result);
    if (callback && callback instanceof Function) {
      this.bind.form.id = result.id;
      callback(result);
    } else {
      o2.api.page.notice(lp.saveSuccess, 'success');
      this.close();
    }
    
  },
});
