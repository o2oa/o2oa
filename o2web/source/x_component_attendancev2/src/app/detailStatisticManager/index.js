import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { formatDate, isEmpty , convertMinutesToHoursAndMinutes, showLoading, hideLoading} from "../../utils/common";
import { detailAction } from "../../utils/actions";
import oOrgPersonSelector from "../../components/o-org-person-selector";
import oDatePicker from "../../components/o-date-picker";
import template from "./template.html";

export default content({
  template,
  components: { oOrgPersonSelector, oDatePicker },
  autoUpdate: true,
  bind() {
    return {
      lp,
      // 搜索表单
      form: {
        filterList: [],
        startDate: '',
        endDate: ''
      },
      units: [], // 控制组织选择的范围
      filterList: [],
      statisticList: [],
      tableHeaderList:[] // detail 

    };
  },
  beforeRender() {
    const today = new Date();
    // 计算本周第一天的日期
    const firstDayOfWeek = new Date(today.setDate(today.getDate() - today.getDay() + 1));
    // 计算本周最后一天的日期
    const lastDayOfWeek = new Date(today.setDate(firstDayOfWeek.getDate() + 6));
    this.bind.form.startDate = formatDate(firstDayOfWeek);
    this.bind.form.endDate = formatDate(lastDayOfWeek);
    // 当前用户的选择范围
    if (content.myDutyList && content.myDutyList.length > 0) {
      let units = [];
      for (let i = 0; i < content.myDutyList.length; i++) {
        const duty = content.myDutyList[i];
        if (duty.woUnit && duty.woUnit.distinguishedName) {
          units.push(duty.woUnit.distinguishedName);
        }
      }
      this.bind.units = units;
    }
  },
  // yyyy-MM-dd 
  _toDate(dateString) {
    var dateParts = dateString.split("-"); // 将字符串拆分为年、月和日部分
    var year = parseInt(dateParts[0], 10); // 将年份部分解析为整数
    var month = parseInt(dateParts[1], 10) - 1; // 将月份部分解析为整数（月份从0开始）
    var day = parseInt(dateParts[2], 10); // 将日部分解析为整数
    return new Date(year, month, day); // 创建Date对象
  },
  // 间隔天数
  _dateDiffInDays(date1, date2) {
    const timeDiff = date2.getTime() - date1.getTime();
    const daysDiff = Math.floor(timeDiff / (1000 * 3600 * 24));
    return Math.abs(daysDiff); // 如果需要考虑日期的顺序，请移除 Math.abs
  },
  // 日期列表
  _dateRangeToStringList(startDate, endDate) {
    const dateList = [];
    let currentDate = new Date(startDate);
    while (currentDate <= endDate) {
      dateList.push(formatDate(currentDate));
      currentDate.setDate(currentDate.getDate() + 1); // 增加一天
    }
    return dateList;
  },
  validateForm() {
    if (this.bind.filterList.length < 1) {
      o2.api.page.notice(lp.detailStatisticList.filterEmptyPlaceholder, 'error');
      return false;
    }
    if (isEmpty(this.bind.form.startDate)) {
      o2.api.page.notice(lp.detailStatisticList.startDateEmptyPlaceholder, 'error');
      return false;
    }
    if (isEmpty(this.bind.form.endDate)) {
      o2.api.page.notice(lp.detailStatisticList.endDateEmptyPlaceholder, 'error');
      return false;
    }
    const start = this._toDate(this.bind.form.startDate);
    const end = this._toDate(this.bind.form.endDate);
    if (end < start) {
      o2.api.page.notice(lp.detailStatisticList.endDateCannotSmaller, 'error');
      return false;
    }
    if (this._dateDiffInDays(start, end) > 31) {
      o2.api.page.notice(lp.detailStatisticList.startDateAndEndDateMoreThan, 'error');
      return false;
    }
    return true;
  },
  search() {
    if (this.validateForm()) {this.loadDetailList();}
  },
  async loadDetailList() {
    await showLoading(this);
    this._showTableHeader();
    const form = this.bind.form;
    form.filterList = this.bind.filterList;
    try {
      const json = await detailAction("statistic", form);
      const list =  json || [];
      this.bind.statisticList = list;
    } catch (e) {
      console.error(e);
    }
    await hideLoading(this);
  },
  _showTableHeader() {
    const start = this._toDate(this.bind.form.startDate);
    const end = this._toDate(this.bind.form.endDate);
    this.bind.tableHeaderList = this._dateRangeToStringList(start, end);
  },
  async openRecordList(date, staticItem) {
    const list = staticItem.detailList || [];
    let detail = null;
    for (let index = 0; index < list.length; index++) {
      const element = list[index];
      if (element.recordDateString && element.recordDateString == date) {
        detail = element;
        break;
      }
    }
    if (detail) {
      const recordList = detail.recordList || [];
      this.$topParent.openRecordListVm({bind: { recordList:  recordList }})
    }
  },
  // 是否有 detail
  checkDateDetail(date, staticItem) {
    const list = staticItem.detailList || [];
    for (let index = 0; index < list.length; index++) {
      const element = list[index];
      if (element.recordDateString && element.recordDateString == date) {
        return true;
      }
    }
    return false;
  },
  formatRecordList(date, staticItem){
    let result = "";
    const list = staticItem.detailList || [];
    let detail = null;
    for (let index = 0; index < list.length; index++) {
      const element = list[index];
      if (element.recordDateString && element.recordDateString == date) {
        detail = element;
        break;
      }
    }
    if (detail && detail.workDay) {
      const recordList = detail.recordList || [];
      for (let index = 0; index < recordList.length; index++) {
        const element = recordList[index];
        result +=  (element.checkInType === 'OnDuty' ? lp.onDuty : lp.offDuty) + ": "+ this._formatRecordResult(element);
        if (index != recordList.length-1) {
          result += " ";
        }
      }
    }
    return result;
  },
  _formatRecordResult(record) {
    let span = "";
    if (record.fieldWork) {
      span = lp.appeal.fieldWork;
    } else if(record.leaveData) {
      span = record.leaveData.leaveType;
    } else {
      const result = record.checkInResult;
      if (result === "PreCheckIn") {
        span = "";
      } else if (result === "NotSigned") {
        span = lp.appeal.notSigned;
      } else if (result === "Normal") {
        span = lp.appeal.normal;
      } else if (result === "Early") {
        span = lp.appeal.early;
      } else if (result === "Late") {
        span = lp.appeal.late;
      } else if (result === "SeriousLate") {
        span = lp.appeal.seriousLate;
      } else {
        span = "";
      }
    }
    return span;
  },
  // 格式化用户姓名
  formatName(person) {
    if (person && person.indexOf("@") > -1) {
      return person.split("@")[0];
    }
    return person;
  },
  // 格式化工作时长
  formatWorkTimeDuration(workTime) {
    return convertMinutesToHoursAndMinutes(workTime);
  },
  // 导出 
  statisticExport() {
    if (this.validateForm()) {
      this.exportExcel();
    }
  },
  async exportExcel() {
    await showLoading(this, lp.detailExportConfirmMsg);
    const form = this.bind.form;
    form.filterList = this.bind.filterList;
    detailAction("statisticExport", form).then( data => {
      if (data ) {
        this.downloadExcelConfirm(data);
      }
    })
    .catch( err => {
      console.error(err);
    })
    .finally(() => {
      hideLoading(this);
    });
  },
  downloadExcelConfirm(result) {
    if (result) {
      var _self = this;
      o2.api.page.confirm(
        "info",
        lp.alert,
        lp.detailExportExcelFileSuccess, //lpFormat(lp,  "", {number: result.errorRows}),
        300,
        100,
        function () {
          _self.downloadExportExcel(result.flag);
          this.close();
        },
        function () {
          this.close();
        }
      );
    }
  },
  // 下载统计结果
  downloadExportExcel(resultFlag) {
    if (resultFlag) {
      const dAction = o2.Actions.load("x_attendance_assemble_control").LeaveAction.action;
      let url =  dAction.getAddress() + dAction.actions.getResult.uri;
      url = url.replace("{flag}", encodeURIComponent(resultFlag));
      console.debug(url);
      window.open(o2.filterUrl(url));
    }
  },
  
});
