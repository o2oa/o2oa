import { component as content } from "@o2oa/oovm";
import { lp, component as app } from "@o2oa/component";
import { appealInfoActionManagerListByPaging, appealInfoAction } from "../../utils/actions";
import oOrgPersonSelector from "../../components/o-org-person-selector";
import oDatePicker from "../../components/o-date-picker";
import oPager from "../../components/o-pager";
import template from "./template.html";

export default content({
  template,
  components: { oPager, oDatePicker, oOrgPersonSelector },
  autoUpdate: true,
  bind() {
    return {
      lp,
      appealList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 每页条目数
      },
      // 搜索表单
      form: {
        users: [],
        startDate: '',
        endDate: ''
      },
      filterList: [],
      units: [], // 控制组织选择的范围
    };
  },
  beforeRender() {
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
  loadData(e) {
    if (
      e &&
      e.detail &&
      e.detail.module &&
      e.detail.module.bind
    ) {
      this.bind.pagerData.page = e.detail.module.bind.page || 1;
      this.loadAppealList();
    }
  },
  search() {
    this.bind.pagerData.page = 1;
    this.loadAppealList();
  },
  async loadAppealList() {
    let form = this.bind.form;
    if (this.bind.filterList && this.bind.filterList.length>0) {
      form.users = this.bind.filterList;
    } else {
      if (this.bind.units.length > 0) {
        o2.api.page.notice(lp.detailStatisticList.filterEmptyPlaceholder, 'error');
        return;
      }
      form.users = [];
    }
    const json = await appealInfoActionManagerListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      form
    );
    if (json) {
      this.bind.appealList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  formatPersonName(person) {
    if (person && person.indexOf("@") > -1) {
      return person.split("@")[0];
    }
    return person;
  },
  formatRecordResultClass(record) {
    let span = "";
    if (record.fieldWork) {
      span = "color-fieldWork";
    } else {
      const result = record.checkInResult;
      if (result === 'PreCheckIn') {
        span = "";
      } else if (result === 'NotSigned') {
        span =   "color-nosign";
      }  else if (result === 'Normal') {
        span =   "color-normal";
      } else if (result === 'Early') {
        span =    "color-early";
      } else if (result === 'Late') {
        span =    "color-late";
      } else if (result === 'SeriousLate') {
        span =    "color-serilate";
      } else { 
        span = "" ;
      }
    }
    return span;
  },
  formatRecordResult(record) {
    let span = "";
    if (record.fieldWork) {
      span = lp.appeal.fieldWork;
    } else {
      const result = record.checkInResult;
      if (result === 'PreCheckIn') {
        span = "";
      } else if (result === 'NotSigned') {
        span =  lp.appeal.notSigned;
      }  else if (result === 'Normal') {
        span =   lp.appeal.normal;
      } else if (result === 'Early') {
        span =   lp.appeal.early;
      } else if (result === 'Late') {
        span =   lp.appeal.late;
      } else if (result === 'SeriousLate') {
        span =   lp.appeal.seriousLate;
      } else { 
        span = "" ;
      }
    }
    return span;
  },
  formatAppealStatus(appeal) {
    if (appeal) {
      if (appeal.status === 0) {
        return  lp.appeal.status0;
      } else if (appeal.status=== 1) {
        return  lp.appeal.status1;
      } else if (appeal.status === 2) {
        return lp.appeal.status2;
      } else if (appeal.status === 3) {
        return lp.appeal.status3;
      } else if (appeal.status === 4) {
        let name = appeal.updateStatusAdminPerson;
        if (name && name.indexOf("@") > -1) {
          name =  name.split("@")[0];
        }
        return lp.appeal.status4 +" ["+ name + "]";
      }  
    }
    return "";
  },
  // 处理
  clickDealAppeal(appeal) {
    var _self = this;
    o2.api.page.confirm(
      "warn",
      lp.alert,
      lp.appeal.confirmDealAppeal,
      300,
      100,
      function () {
        _self.dealAppeal(appeal.id);
        this.close();
      },
      function () {
        this.close();
      }
    );
  },
  async dealAppeal(id) {
    if (id) {
      await appealInfoAction("managerSetNormal", id);
      this.loadAppealList();
    }
  },
     
});
