import { component as content } from "@o2oa/oovm";
import { lp, o2, component as c } from "@o2oa/component";
import { configAction } from "../../utils/actions";
import { isInt, setJSONValue } from "../../utils/common";
import template from "./template.html";
import style from "./style.scope.css";
import oInput from "../../components/o-input";
import oTimePicker from "../../components/o-time-picker";
import oSelectorProcess from "../../components/o-selector-process";

export default content({
  style,
  template,
  autoUpdate: true,
  components: { oInput, oSelectorProcess, oTimePicker },
  bind() {
    return {
      lp,
      form: {
        appealEnable: false,
        onDutyFastCheckInEnable: false,
        offDutyFastCheckInEnable: false,
        checkInAlertEnable: false,
        exceptionAlertEnable: false,
        exceptionAlertTime: "09:30",
        appealMaxTimes: 0,
        detailStatisticCronString: "0 0 3 * * ?", //默认凌晨 3 点
        closeOldAttendance: false, // 是否关闭旧考勤
      },
      holidayList: [],
      workDayList: [],
      processSelector: {
        selectorTitle: lp.config.appealProcessTypeProcessLabel,
        value: [], // 流程对象列表
        showValue: "",
        placeholder: lp.config.appealProcessTypeProcessPlaceholder, // 没有数据的时候显示的内容
      },
    };
  },
  async beforeRender() {
    await this.loadConfig();
  },
  afterRender() {
    this.loadDetailStatisticCronClick();// 表达式绑定 click 事件
  },
  // o time picker 控件使用
  setTimeValue(key, value) {
    setJSONValue(key, value, this.bind);
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
      if (!json.detailStatisticCronString) {
        this.bind.form.detailStatisticCronString = "0 0 3 * * ?"; //默认凌晨 3 点
      }
      if (typeof json.appealEnable == "undefined") {
        this.bind.form.appealEnable = false;
      }
      debugger;
      if (json.processId && json.processName) {
        this.bind.processSelector.value = [
          {
            id: json.processId,
            name: json.processName,
          },
        ];
        this.showProcessSelectorValueFun();
      }
    }
  },
  // 保存
  async submit() {
    const form = this.bind.form;
    form.holidayList = this.bind.holidayList;
    form.workDayList = this.bind.workDayList;
    if (form.appealEnable && this.bind.processSelector.value.length < 1) {
      o2.api.page.notice(
        lp.config.appealProcessTypeProcessPlaceholder,
        "error"
      );
      return;
    }
    if (!isInt(form.appealMaxTimes)) {
      o2.api.page.notice(lp.config.appealMaxTimesError, "error");
      return;
    }
    if (form.appealEnable && this.bind.processSelector.value.length > 0) {
      form.processId = this.bind.processSelector.value[0]["id"] || "";
      form.processName = this.bind.processSelector.value[0]["name"] || "";
    } else {
      form.processId = "";
      form.processName = "";
      form.appealMaxTimes = 0;
    }

    const result = await configAction("post", form);
    console.log(result);
    o2.api.page.notice(lp.saveSuccess, "success");
    this.loadConfig();
  },

  // 节假日日期选择器
  loadHolidayDateSelector() {
    MWF.require(
      "MWF.widget.Calendar",
      function () {
        const options = {
          style: "xform",
          secondEnable: false,
          timeSelectType: "select",
          clearEnable: false,
          isTime: false,
          timeOnly: false,
          monthOnly: false,
          yearOnly: false,
          defaultDate: null,
          defaultView: "day",
          target: this.dom,
          baseDate: new Date(),
          onComplate: function (formateDate, date) {
            const year = date.getFullYear();
            const month =
              date.getMonth() + 1 > 9
                ? `${date.getMonth() + 1}`
                : `0${date.getMonth() + 1}`;
            const day =
              date.getDate() > 9 ? `${date.getDate()}` : `0${date.getDate()}`;
            const chooseDate = `${year}-${month}-${day}`;
            if (this.bind.holidayList.indexOf(chooseDate) < 0) {
              this.bind.holidayList.push(chooseDate);
            }
          }.bind(this),
        };
        const bindDom = this.dom.querySelector("#holidaysDateSelector");
        new MWF.widget.Calendar(bindDom, options);
      }.bind(this)
    );
  },
  //   删除一个节假日
  deleteHolidayDateSelector(value) {
    let i = -1;
    for (let index = 0; index < this.bind.holidayList.length; index++) {
      const element = this.bind.holidayList[index];
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
    MWF.require(
      "MWF.widget.Calendar",
      function () {
        const options = {
          style: "xform",
          secondEnable: false,
          timeSelectType: "select",
          clearEnable: false,
          isTime: false,
          timeOnly: false,
          monthOnly: false,
          yearOnly: false,
          defaultDate: null,
          defaultView: "day",
          target: this.dom,
          baseDate: new Date(),
          onComplate: function (formateDate, date) {
            const year = date.getFullYear();
            const month =
              date.getMonth() + 1 > 9
                ? `${date.getMonth() + 1}`
                : `0${date.getMonth() + 1}`;
            const day =
              date.getDate() > 9 ? `${date.getDate()}` : `0${date.getDate()}`;
            const chooseDate = `${year}-${month}-${day}`;
            if (this.bind.workDayList.indexOf(chooseDate) < 0) {
              this.bind.workDayList.push(chooseDate);
            }
          }.bind(this),
        };
        const bindDom = this.dom.querySelector("#workdaysDateSelector");
        new MWF.widget.Calendar(bindDom, options);
      }.bind(this)
    );
  },
  //   删除一个工作日
  deleteWorkdayDateSelector(value) {
    let i = -1;
    for (let index = 0; index < this.bind.workDayList.length; index++) {
      const element = this.bind.workDayList[index];
      if (value === element) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.workDayList.splice(i, 1);
    }
  },
  // 是否启用补卡申请
  clickEnableAppeal() {
    this.bind.form.appealEnable = !this.bind.form.appealEnable;
  },
  clickOnDutyFastCheckInEnable() {
    this.bind.form.onDutyFastCheckInEnable =
      !this.bind.form.onDutyFastCheckInEnable;
  },
  clickOffDutyFastCheckInEnable() {
    this.bind.form.offDutyFastCheckInEnable =
      !this.bind.form.offDutyFastCheckInEnable;
  },
  clickCheckInAlertEnable() {
    this.bind.form.checkInAlertEnable = !this.bind.form.checkInAlertEnable;
  },
  clickExceptionAlertEnable() {
    this.bind.form.exceptionAlertEnable = !this.bind.form.exceptionAlertEnable;
  },
  showProcessSelectorValueFun() {
    if (this.bind.processSelector.value.length > 0) {
      let newShowValue = [];
      for (
        let index = 0;
        index < this.bind.processSelector.value.length;
        index++
      ) {
        const element = this.bind.processSelector.value[index];
        newShowValue.push(element["name"]); // name字段
      }
      this.bind.processSelector.showValue = newShowValue.join(", ");
    }
  },
  loadDetailStatisticCronClick() {
    const cronTarget = this.dom.querySelector("#detailCron");
    o2.requireApp("Template", "widget.CronPicker", () => {
        this.cronPicker = new MWF.xApplication.Template.widget.CronPicker(
          c.content,
          cronTarget,
          c,
          {},
          {
            style: "design",
            position: {
              //node 固定的位置
              x: "right",
              y: "auto",
            },
            onSelect: (value) => {
              this.bind.form.detailStatisticCronString = value;
            },
            onQueryLoad: () => {
              console.log(this.bind.form.detailStatisticCronString);
              if (!this.cronPicker.node) {
                this.cronPicker.options.value = this.bind.form.detailStatisticCronString;
              } else {
                this.cronPicker.setCronValue(this.bind.form.detailStatisticCronString);
              }
            },
          }
        );
    });
  },
  clickCloseOldAttendance() {
    this.bind.form.closeOldAttendance = !this.bind.form.closeOldAttendance;
  },
});
