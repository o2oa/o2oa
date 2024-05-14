import { component as content } from "@o2oa/oovm";
import { lp, component as app } from "@o2oa/component";
import { recordActionListByPaging } from "../../utils/actions";
import oPager from "../../components/o-pager";
import oOrgPersonSelector from "../../components/o-org-person-selector";
import oDatePicker from "../../components/o-date-picker";
import template from "./template.html";
import {lpFormat, isEmpty, chooseSingleFile} from "../../utils/common";

export default content({
  template,
  components: { oPager, oOrgPersonSelector, oDatePicker },
  autoUpdate: true,
  bind() {
    return {
      lp,
      recordList: [],
      form: {
        userId: "",
        startDate: "",
        endDate: "",
      },
      units: [], // 控制组织选择的范围
      filterList:[],
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 每页条目数
      },
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
    if (e && e.detail && e.detail.module && e.detail.module.bind) {
      this.bind.pagerData.page = e.detail.module.bind.page || 1;
      this.loadRecordlList();
    }
  },
  // 打开更多详细内容
  openMore(record) {
    this.$parent.openRecordDetailVm({bind: { record: record }});
  },
  search() {
    this.bind.pagerData.page = 1;
    this.loadRecordlList();
  },
  async loadRecordlList() {
    const form = this.bind.form;
    if (this.bind.filterList.length > 0) {
      form.userId = this.bind.filterList[0];
    } else {
      if (this.bind.units.length > 0) {
        o2.api.page.notice(lp.detailStatisticList.filterEmptyPlaceholder, 'error');
        return;
      }
      form.userId = "";
    }
    if ((isEmpty(form.startDate) && !isEmpty(form.endDate)) || (!isEmpty(form.startDate) && isEmpty(form.endDate))) {
      o2.api.page.notice(lp.record.searchDateError, 'error');
      return;
    }
    const json = await recordActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      form
    );
    if (json) {
      this.bind.recordList = json.data || [];
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
  sourceTypeFormat(sourceType) {
    switch (sourceType) {
      case "USER_CHECK":
        return lp.record.sourceTypeUser;
      case "AUTO_CHECK":
        return lp.record.sourceTypeAuto;
      case "FAST_CHECK":
        return lp.record.sourceTypeFast;
      case "SYSTEM_IMPORT":
        return lp.record.sourceTypeImport;
      default:
        return "";
    }
  },
  recordDateFormat(record) {
    if (
      record.checkInResult === "PreCheckIn" ||
      record.checkInResult === "NotSigned"
    ) {
      return "";
    }
    return record.recordDate;
  },
  formatRecordResultClass(record) {
    let span = "";
    if (record.fieldWork) {
      span = "color-fieldWork";
    } else {
      const result = record.checkInResult;
      if (result === "PreCheckIn") {
        span = "";
      } else if (result === "NotSigned") {
        span = "color-nosign";
      } else if (result === "Normal") {
        span = "color-normal";
      } else if (result === "Early") {
        span = "color-early";
      } else if (result === "Late") {
        span = "color-late";
      } else if (result === "SeriousLate") {
        span = "color-serilate";
      } else {
        span = "";
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
  formatAppealStatus(appeal) {
    if (appeal) {
      if (appeal.status === 0) {
        return lp.appeal.status0;
      } else if (appeal.status === 1) {
        return lp.appeal.status1;
      } else if (appeal.status === 2) {
        return lp.appeal.status2;
      } else if (appeal.status === 3) {
        return lp.appeal.status3;
      }
    }
    return "";
  },

   // excel导入请假数据
   importExcel() {
     chooseSingleFile((file)=> this._uploadExcel(file))
  },
  // 读取选择的文件并上传到服务器
  async _uploadExcel(file) {
    const fileExt = file.name.substring(file.name.lastIndexOf("."));
    console.debug("文件名", file.name, fileExt);
    if (fileExt.toLowerCase() !== ".xls" && fileExt.toLowerCase() !== ".xlsx") {
      o2.api.page.notice(lp.leave.importExcelFileError, 'error');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    formData.append('fileName', file.name);
    o2.Actions.load("x_attendance_assemble_control").RecordAction.input(formData, "", (json)=>{
      if (json && json.data) {
        this.downloadConfirm(json.data)
      }
    });
  },
  downloadConfirm(result) {
    if (result) {
      var _self = this;
      o2.api.page.confirm(
        "info",
        lp.alert,
        lpFormat(lp,  "record.importExcelFileSuccess", {number: result.errorRows}),
        300,
        100,
        function () {
          _self.downloadImportResult(result.flag);
          this.close();
        },
        function () {
          this.close();
        }
      );
    }
  },
  // 下载导入结果
  downloadImportResult(resultFlag) {
    if (resultFlag) {
      const dAction = o2.Actions.load("x_attendance_assemble_control").LeaveAction.action;
      let url =  dAction.getAddress() + dAction.actions.getResult.uri;
      url = url.replace("{flag}", encodeURIComponent(resultFlag));
      console.debug(url);
      window.open(o2.filterUrl(url));
    }
  },
  // 下载excel模板
  excelTemplateDownload() {
    const dAction = o2.Actions.load("x_attendance_assemble_control").RecordAction.action;
    let url =  dAction.getAddress() + dAction.actions.template.uri;
    console.debug(url);
    window.open(o2.filterUrl(url));
  }

});
