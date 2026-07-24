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
        exceptionAlertDateNumber: 1,
        exceptionAlertTime: "09:30",
        appealMaxTimes: '0',
        detailStatisticCronString: "0 0 3 * * ?", //默认凌晨 3 点
        closeOldAttendance: true, // 是否关闭旧考勤
        aliFaceControlEnable: false, // 阿里云人脸扩展是否启用
        faceDetectionEnable: false, // 打卡前是否启用人脸比对
        properties: {
          checkInAlertOnDutyBeforeMinutes: 10, // 默认上班前 10 分钟
          checkInAlertOffDutyAfterMinutes: 10, // 默认下班后 10 分钟 
          statisticUnitDutyName:"", // 考勤统计管理员的职务名称
        }
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
    console.debug('snapshot', JSON.parse(JSON.stringify(json)));
console.debug('fields', json.appealMaxTimes, json.properties && json.properties.statisticUnitDutyName);
    if (json) {
      this.bind.form = json || {};
      if (!this.bind.form.appealMaxTimes) {
        this.bind.form.appealMaxTimes = '0';
      }
      if (!json.properties) {
        this.bind.form.properties = {};
      }
      if (!json.properties.checkInAlertOnDutyBeforeMinutes) {
        this.bind.form.properties.checkInAlertOnDutyBeforeMinutes = 10;
      }
      if (!json.properties.checkInAlertOffDutyAfterMinutes) {
        this.bind.form.properties.checkInAlertOffDutyAfterMinutes = 10;
      }
      if (!json.properties.statisticUnitDutyName) {
        this.bind.form.properties.statisticUnitDutyName = "考勤管理员";
      }
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
      if (typeof json.exceptionAlertDateNumber == "undefined" || json.exceptionAlertDateNumber === null) {
        this.bind.form.exceptionAlertDateNumber = 1;
      }
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
    console.debug('load', this.bind.form);
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
    console.debug('submit', form);
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
    if (form.checkInAlertEnable === true) {
      const onDutyBefore = form.properties.checkInAlertOnDutyBeforeMinutes;
      // 上班前提醒分钟数必须为整数且大于等于0 小于等于60
      if (!isInt(onDutyBefore) || onDutyBefore < 0 || onDutyBefore > 60) {
        o2.api.page.notice("上班前提醒分钟数必须为整数且大于等于0 小于等于60", "error");
        return;
      }
      const offDutyAfter = form.properties.checkInAlertOffDutyAfterMinutes;
      // 下班后提醒分钟数必须为整数且大于等于0 小于等于60
      if (!isInt(offDutyAfter) || offDutyAfter < 0 || offDutyAfter > 60) {
        o2.api.page.notice("下班后提醒分钟数必须为整数且大于等于0 小于等于60", "error");
        return;
      }
    }
    form.exceptionAlertDateNumber = Number(form.exceptionAlertDateNumber) === 0 ? 0 : 1;
    if (form.exceptionAlertEnable === true && form.exceptionAlertDateNumber === 0) {
      const alertTimeMinutes = this.getTimeMinutes(form.exceptionAlertTime);
      if (alertTimeMinutes < 18 * 60) {
        o2.api.page.notice("异常打卡提醒选择当天时，提醒时间必须为18:00或之后", "error");
        return;
      }
    }
    form.closeOldAttendance = true
    const result = await configAction("post", form);
    console.debug('submit result', result);
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
  clickToAliface() {
    layout.openApplication(null, 'aliface')
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
  changeExceptionAlertDateNumber(e) {
    this.bind.form.exceptionAlertDateNumber = Number(e.target.value) === 0 ? 0 : 1;
  },
  getTimeMinutes(time) {
    if (!time || !time.includes(":")) {
      return 0;
    }
    const values = time.split(":");
    return (Number(values[0]) || 0) * 60 + (Number(values[1]) || 0);
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
  clickFaceDetectionEnable() {
    this.bind.form.faceDetectionEnable = !this.bind.form.faceDetectionEnable;
  }
});
