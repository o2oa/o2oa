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
        filter: '',
        startDate: '',
        endDate: ''
      },
      units: [], // 控制组织选择的范围
      filterList: [],
      statisticList: [],
     
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
  afterRender() {
     
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
    return true;
  },
  search() {
    if (this.validateForm()) {this.loadDetailList();}
  },
  async loadDetailList() {
    showLoading(this);
    const form = this.bind.form;
    form.filter = this.bind.filterList[0];
    const json = await detailAction("statistic", form);
    this.bind.statisticList = json || [];
    hideLoading(this);
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
      // var _self = this;
      // o2.api.page.confirm(
      //   "warn",
      //   this.bind.lp.alert,
      //   this.bind.lp.detailExportConfirmMsg,
      //   300,
      //   100,
      //   function () {
      //     _self.exportExcel();
      //     this.close();
      //   },
      //   function () {
      //     this.close();
      //   }
      // );
    }
  },
  async exportExcel() {
    showLoading(this, lp.detailExportConfirmMsg);
    detailAction("statisticExport", this.bind.filterList[0], this.bind.form.startDate, this.bind.form.endDate).then( data => {
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
