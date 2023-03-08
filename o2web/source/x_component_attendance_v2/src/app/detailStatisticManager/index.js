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
  search() {
    if (this.bind.filterList.length < 1) {
      o2.api.page.notice(lp.detailStatisticList.filterEmptyPlaceholder, 'error');
      return ;
    }
    if (isEmpty(this.bind.form.startDate)) {
      o2.api.page.notice(lp.detailStatisticList.startDateEmptyPlaceholder, 'error');
      return ;
    }
    if (isEmpty(this.bind.form.endDate)) {
      o2.api.page.notice(lp.detailStatisticList.endDateEmptyPlaceholder, 'error');
      return ;
    }
    this.loadDetailList();
  },
   
  async loadDetailList() {
    const form = this.bind.form;
    form.filter = this.bind.filterList[0];
    const json = await detailAction("statistic", form);
    console.debug(json);
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
