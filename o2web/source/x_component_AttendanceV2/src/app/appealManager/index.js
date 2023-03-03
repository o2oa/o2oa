import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { lpFormat } from "../../utils/common";
import { appealInfoActionListByPaging } from "../../utils/actions";
import oPager from "../../components/o-pager";
import template from "./template.html";

export default content({
  template,
  components: { oPager },
  autoUpdate: true,
  bind() {
    return {
      lp,
      appealList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        size: 15, // 每页条目数
      },
    };
  },
  afterRender() {
    this.loadAppealList();
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
      this.loadAppealList();
    }
  },
  async loadAppealList() {
    const json = await appealInfoActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      {}
    );
    if (json) {
      this.bind.appealList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  formatRecordResult(record) {
    if (record && record.checkInResult) {
      if (record.checkInResult == 'Absenteeism') {
        return "旷工迟到";
      } else if (record.checkInResult == 'Early') {
        return "早退";
      } else if (record.checkInResult == 'Late') {
        return "迟到";
      } else if (record.checkInResult == 'SeriousLate') {
        return "严重迟到";
      } else if (record.checkInResult == 'NotSigned') {
        return "未打卡";
      }
    }
    return "";
  },
  formatAppealStatus(appeal) {
    if (appeal) {
      if (appeal.status === 0) {
        return "待处理";
      } else if (record.checkInResult === 1) {
        return "审批中";
      } else if (record.checkInResult === 2) {
        return "审批通过";
      } else if (record.checkInResult === 3) {
        return "审批不通过";
      }  
    }
    return "";
  },
  startProcess(id) {
    console.debug(id);
  },
  // 关闭表单页面
  closeGroup() {
    // if (this.addGroupVm) {
    //   this.addGroupVm.destroy();
    // }
    this.loadAppealList();
  },
});
