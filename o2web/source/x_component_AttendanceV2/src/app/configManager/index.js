import { component as content } from "@o2oa/oovm";
import { lp, o2, component as c } from "@o2oa/component";
import { configAction } from "../../utils/actions";
import template from "./template.html";
import style from "./style.scope.css";
import oInput from "../../components/o-input";
import oSelectorProcess from "../../components/o-selector-process";

export default content({
  style,
  template,
  autoUpdate: true,
  components: { oInput, oSelectorProcess },
  bind() {
    return {
      lp,
      form: {
        appealEnable: true,
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
  beforeRender() {},
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
      //console.debug(this.bind);
    }
  },
  // 保存
  async submit() {
    console.debug(this.bind);
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
    if (this.bind.processSelector.value.length > 0) {
      form.processId = this.bind.processSelector.value[0]["id"] || "";
      form.processName = this.bind.processSelector.value[0]["name"] || "";
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
});
