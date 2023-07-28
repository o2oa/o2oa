import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { convertMinutesToHoursAndMinutes } from "../../utils/common";
import { detailActionListByPaging } from "../../utils/actions";
import oPager from "../../components/o-pager";
import oOrgPersonSelector from "../../components/o-org-person-selector";
import oDatePicker from "../../components/o-date-picker";
import template from "./template.html";

export default content({
  template,
  components: { oPager, oDatePicker, oOrgPersonSelector },
  autoUpdate: true,
  bind() {
    return {
      lp,
      // 搜索表单
      form: {
        userId: '',
        startDate: '',
        endDate: ''
      },
      filterList: [],
      units: [], // 控制组织选择的范围
      detailList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 每页条目数
      },
    };
  },
  beforeRender() {
    // 前一天 当天没有数据
    const today = new Date();
    let year = today.getFullYear();
    let m =  today.getMonth(); 
    let before = today.getDate() - 1;
    if (before < 1) {
      before = 1;
      m = m - 1;
      if (m < 0) {
        m = 0;
        year -= 1;
      }
    }
    const month = (m + 1) > 9 ? `${m + 1}`:`0${(m + 1)}`;
    const day = (before) > 9 ? `${before}`:`0${before}`;
    const chooseDate = `${year}-${month}-${day}`;
    this.bind.form.startDate = chooseDate;
    this.bind.form.endDate = chooseDate;
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
  search() {
    this.bind.pagerData.page = 1;
    this.loadDetailList();
  },
  loadData(e) {
    if (
      e &&
      e.detail &&
      e.detail.module &&
      e.detail.module.bind
    ) {
      this.bind.pagerData.page = e.detail.module.bind.page || 1;
      this.loadDetailList();
    }
  },
  async loadDetailList() {
    let form = this.bind.form;
    if (this.bind.filterList && this.bind.filterList.length>0) {
      form.userId = this.bind.filterList[0];
    } else {
      if (this.bind.units.length > 0) {
        o2.api.page.notice(lp.detailStatisticList.filterEmptyPlaceholder, 'error');
        return;
      }
      form.userId = "";
    }
    const json = await detailActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      form
    );
    if (json) {
      this.bind.detailList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  dateClassName(detail) {
    if (detail && detail.workDay === false) {
      return "color-holiday";
    }
    return "";
  },
  formatDate(detail) {
    if (detail && detail.recordDateString) {
      if (detail.recordDay) {
        if (detail.recordDay === '0') {
          return detail.recordDateString + "("+lp.day.Sunday+")";
        } else if (detail.recordDay === '1') {
          return detail.recordDateString + "("+lp.day.Monday+")";
        } else if (detail.recordDay === '2') {
          return detail.recordDateString + "("+lp.day.Tuesday+")";
        } else if (detail.recordDay === '3') {
          return detail.recordDateString + "("+lp.day.Wednesday+")";
        } else if (detail.recordDay === '4') {
          return detail.recordDateString + "("+lp.day.Thursday+")";
        } else if (detail.recordDay === '5') {
          return detail.recordDateString + "("+lp.day.Friday+")";
        } else if (detail.recordDay === '6') {
          return detail.recordDateString + "("+lp.day.Saturday+")";
        }
      }
      return detail.recordDateString;
    }
    return "";
  },
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
  async openRecordList(detail) {
    // const content = (await import(`./recordList/index.js`)).default;
    // this.recordListVm = await content.generate(".form", {bind: { recordList: detail.recordList||[] }}, this);
    this.$topParent.openRecordListVm({bind: { recordList: detail.recordList||[] }})
  }
});
