import { component as content } from "@o2oa/oovm";
import { lp, o2, layout } from "@o2oa/component";
import { leaveActionListByPaging, leaveAction } from "../../utils/actions";
import { chooseSingleFile } from "../../utils/common";
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
        person: "",
        startDate: "",
        endDate: "",
      },
      filterList: [],
      leaveList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 每页条目数
      },
    };
  },
  afterRender() {
    this.search();
  },
  search() {
    this.bind.pagerData.page = 1;
    this.loadLeaveList();
  },
  loadData(e) {
    if (e && e.detail && e.detail.module && e.detail.module.bind) {
      this.bind.pagerData.page = e.detail.module.bind.page || 1;
      this.loadLeaveList();
    }
  },
  async loadLeaveList() {
    let form = this.bind.form;
    if (this.bind.menu.id === "3-4") {
      /// 管理员
      if (this.bind.filterList && this.bind.filterList.length > 0) {
        form.person = this.bind.filterList[0];
      } else {
        form.person = "";
      }
    } else {
      form.person = layout.session.user.distinguishedName;
    }
    const json = await leaveActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      form
    );
    if (json) {
      this.bind.leaveList = json.data || [];
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
  //  删除
  clickDeleteData(id) {
    var _self = this;
    o2.api.page.confirm(
      "warn",
      lp.alert,
      lp.leave.deleteConfirm,
      300,
      100,
      function () {
        _self.deleteLeave(id);
        this.close();
      },
      function () {
        this.close();
      }
    );
  },
  async deleteLeave(id) {
    const json = await leaveAction("delete", id);
    console.debug(json);
    this.search();
  },
  // excel导入请假数据
  importExcel() {
    chooseSingleFile((file)=>this._uploadExcel(file))
  },

  async _uploadExcel(file) {
    const fileExt = file.name.substring(file.name.lastIndexOf("."));
    console.debug("文件名", file.name, fileExt);
    if (
        fileExt.toLowerCase() !== ".xls" &&
        fileExt.toLowerCase() !== ".xlsx"
    ) {
      o2.api.page.notice(lp.leave.importExcelFileError, "error");
      return;
    }
    const formData = new FormData();
    formData.append("file", file);
    formData.append("fileName", file.name);
    o2.Actions.load("x_attendance_assemble_control").LeaveAction.input(
        formData,
        "",
        (json)=> {
          if (json && json.data) {
            this.downloadConfirm(json.data);
          }
        }
    );
  },
  downloadConfirm(result) {
    if (result) {
      //o2.api.page.notice(lp.leave.importExcelFileSuccess, 'info');
      var _self = this;
      o2.api.page.confirm(
        "info",
        lp.alert,
        lp.leave.importExcelFileSuccess,
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
    this.search();
  },
  // 下载导入结果
  downloadImportResult(resultFlag) {
    if (resultFlag) {
      const dAction = o2.Actions.load("x_attendance_assemble_control")
        .LeaveAction.action;
      let url = dAction.getAddress() + dAction.actions.getResult.uri;
      url = url.replace("{flag}", encodeURIComponent(resultFlag));
      console.debug(url);
      window.open(o2.filterUrl(url));
    }
  },
  // 下载excel模板
  excelTemplateDownload() {
    const dAction = o2.Actions.load("x_attendance_assemble_control").LeaveAction
      .action;
    let url = dAction.getAddress() + dAction.actions.template.uri;
    console.debug(url);
    window.open(o2.filterUrl(url));
  },
});
