import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { formatDate, isEmpty , convertMinutesToHoursAndMinutes} from "../../utils/common";
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
      //
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
  // 导出 
  statisticExport() {
    if (this.validateForm()) {
      var _self = this;
      o2.api.page.confirm(
        "warn",
        this.bind.lp.alert,
        this.bind.lp.detailExportConfirmMsg,
        300,
        100,
        function () {
          _self.exportExcel();
          this.close();
        },
        function () {
          this.close();
        }
      );
     
    }
  },
  exportExcel() {
    const dAction = o2.Actions.load("x_attendance_assemble_control").DetailAction.action;
    let url =  dAction.getAddress() + dAction.actions.statisticExport.uri;
    console.debug(url);
    url = url.replace("{filter}", encodeURIComponent(this.bind.filterList[0]));
    url = url.replace("{start}", encodeURIComponent(this.bind.form.startDate));
    url = url.replace("{end}", encodeURIComponent(this.bind.form.endDate));
    console.debug(url);
    window.open(o2.filterUrl(url));
  },
   
  async loadDetailList() {
    const form = this.bind.form;
    form.filter = this.bind.filterList[0];
    const json = await detailAction("statistic", form);
    this.bind.statisticList = json || [];
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
  }
  
});
