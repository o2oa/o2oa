import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { lpFormat } from "../../utils/common";
import { detailActionListByPaging } from "../../utils/actions";
import oPager from "../../components/o-pager";
import oDatePicker from "../../components/o-date-picker";
import template from "./template.html";

export default content({
  template,
  components: { oPager, oDatePicker },
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
      detailList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
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
  },
  afterRender() {
    this.loadDetailList();
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
      e.detail.module.bind &&
      e.detail.module.bind.pagerData
    ) {
      this.bind.pagerData.page = e.detail.module.bind.pagerData.page;
      this.loadDetailList();
    }
  },
  async loadDetailList() {
    const json = await detailActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      this.bind.form
    );
    if (json) {
      this.bind.detailList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  formatName(person) {
    if (person && person.indexOf("@") > -1) {
      return person.split("@")[0];
    }
    return person;
  },
  async openRecordList(detail) {
    const content = (await import(`./recordList/index.js`)).default;
    this.recordListVm = await content.generate(".form", {bind: { recordList: detail.recordList||[] }}, this);
  },
  closeRecordList() {
    if (this.recordListVm ) {
      this.recordListVm.destroy();
    }
  }
});
