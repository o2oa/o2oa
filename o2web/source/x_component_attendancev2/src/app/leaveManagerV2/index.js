import { component as content } from "@o2oa/oovm";
import { lp, o2, layout } from "@o2oa/component";
import ExcelJS from "exceljs";
import { lpFormat, formatPersonName, chooseSingleFile, hideLoading, isEmpty, showLoading } from "../../utils/common";
import { leaveManagerAction } from "../../utils/actions";
import oPager from "../../components/o-pager";
import oOrgPersonSelector from "../../components/o-org-person-selector";
import oDatePicker from "../../components/o-date-picker";
import template from "./template.html";
import style from "./style.scope.css";

export default content({
  template,
  style,
  components: { oPager, oDatePicker, oOrgPersonSelector },
  autoUpdate: true,
  bind() {
    return {
      lp,
      leaveTypeList: [],
      leaveTypePolicyShow: false, // 是否显示某一个假期类型的规则列表
      currentLeaveType: null, // 当前选中的假期类型
      importFormShow: false,
      importSubmitting: false,
      importForm: {
        grantPeriod: "",
        fileName: "",
      },
    };
  },
  afterRender() {
    this.loadTypeList();
    this.listenEventBus();
  },
  listenEventBus() {
    this.$topParent.listenEventBus("leaveType", (data) => {
      console.log("接收到了leaveType消息", data);
      this.loadTypeList();
    });
  },
  gotoOld() {
    this.$parent.openOldLeaveManager();
  },
  clickAddType() {
    this.$parent.openLeaveTypeForm();
  },
  clickEditLeaveType(id) {
    console.log("点击编辑假期类型", id);
    this.$parent.openLeaveTypeForm({ bind: { updateId: id } });
  },
  async clickCopyLeaveTypeId(id) {
    if (isEmpty(id)) {
      o2.api.page.notice("假期类型 ID 为空", "error");
      return;
    }
    try {
      await this.copyText(id);
      o2.api.page.notice("复制成功", "success");
    } catch (e) {
      console.error("复制假期类型 ID 失败", e);
      o2.api.page.notice("复制失败", "error");
    }
  },
  copyText(text) {
    const fallbackCopy = () => new Promise((resolve, reject) => {
      const input = document.createElement("textarea");
      input.value = text;
      input.setAttribute("readonly", "readonly");
      input.style.position = "fixed";
      input.style.left = "-9999px";
      input.style.top = "-9999px";
      document.body.appendChild(input);
      input.select();
      try {
        const success = document.execCommand("copy");
        document.body.removeChild(input);
        success ? resolve() : reject(new Error("execCommand copy failed"));
      } catch (e) {
        document.body.removeChild(input);
        reject(e);
      }
    });
    if (navigator.clipboard && navigator.clipboard.writeText) {
      return navigator.clipboard.writeText(text).catch(() => fallbackCopy());
    }
    return fallbackCopy();
  },
  clickOpenLedgerImport(type) {
    if (!type || type.quotaType !== "QUOTA") {
      return;
    }
    this.bind.currentLeaveType = type;
    this.bind.importForm = {
      grantPeriod: "",
      fileName: "",
    };
    this.ledgerImportFile = null;
    this.bind.importFormShow = true;
  },
  closeLedgerImport(force) {
    if (this.bind.importSubmitting && force !== true) {
      return;
    }
    this.bind.importFormShow = false;
    this.bind.currentLeaveType = null;
    this.bind.importForm = {
      grantPeriod: "",
      fileName: "",
    };
    this.ledgerImportFile = null;
  },
  async downloadLedgerImportTemplate() {
    const leaveType = this.bind.currentLeaveType || {};
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet("sheet1");
    worksheet.addRow(["人员", `${leaveType.name || ""}发放额度`, "过期日期"]);
    worksheet.columns = [
      { width: 28 },
      { width: 18 },
      { width: 18 },
    ];
    worksheet.getRow(1).eachCell((cell) => {
      cell.font = { bold: true };
      cell.alignment = { vertical: "middle", horizontal: "center" };
      cell.fill = {
        type: "pattern",
        pattern: "solid",
        fgColor: { argb: "FFE8F4FF" },
      };
      cell.border = {
        top: { style: "thin" },
        left: { style: "thin" },
        bottom: { style: "thin" },
        right: { style: "thin" },
      };
    });
    const buffer = await workbook.xlsx.writeBuffer();
    const excelLink = document.createElement("a");
    const objectUrl = window.URL.createObjectURL(new Blob([buffer]));
    excelLink.href = objectUrl;
    excelLink.download = `${leaveType.name || "假期"}额度导入模板.xlsx`;
    excelLink.click();
    setTimeout(() => window.URL.revokeObjectURL(objectUrl), 0);
  },
  selectLedgerImportFile() {
    if (this.bind.importSubmitting) {
      return;
    }
    chooseSingleFile((file) => {
      if (!this.checkExcelFile(file)) {
        return;
      }
      this.ledgerImportFile = file;
      this.bind.importForm.fileName = file.name;
    });
  },
  checkExcelFile(file) {
    if (!file || !file.name) {
      return false;
    }
    const fileExt = file.name.substring(file.name.lastIndexOf("."));
    if (fileExt.toLowerCase() !== ".xlsx") {
      o2.api.page.notice("请选择 .xlsx 格式的 Excel 文件", "error");
      return false;
    }
    return true;
  },
  async submitLedgerImport() {
    if (this.bind.importSubmitting) {
      return;
    }
    const leaveType = this.bind.currentLeaveType || {};
    const form = this.bind.importForm || {};
    if (!leaveType.id) {
      o2.api.page.notice("请选择假期类型", "error");
      return;
    }
    if (isEmpty(form.grantPeriod)) {
      o2.api.page.notice("请输入发放周期", "error");
      return;
    }
    const file = this.ledgerImportFile;
    if (!file) {
      o2.api.page.notice("请选择 Excel 文件", "error");
      return;
    }
    if (!this.checkExcelFile(file)) {
      return;
    }
    const formData = new FormData();
    formData.append("grantPeriod", form.grantPeriod);
    formData.append("leaveTypeId", leaveType.id);
    formData.append("file", file, file.name);
    formData.append("fileName", file.name);
    this.bind.importSubmitting = true;
    try {
      await showLoading(this);
      await this.uploadLedgerImport(formData);
      o2.api.page.notice("导入成功", "success");
      this.closeLedgerImport(true);
    } finally {
      this.bind.importSubmitting = false;
      await hideLoading(this);
    }
  },
  uploadLedgerImport(formData) {
    return new Promise((resolve, reject) => {
      try {
        const action = o2.Actions.load("x_attendance_assemble_control").LeaveManagerAction;
        action.ledgerImport(
          formData,
          "",
          (json) => {
            console.debug("导入结果", json);
            resolve(json && json.data ? json.data : json)
          },
          (error) => {
            console.error("导入失败", error);
            reject(error)
          }
        );
        // if (result && typeof result.then === "function") {
        //   result.then((json) => resolve(json && json.data ? json.data : json)).catch(reject);
        // }
      } catch (e) {
        reject(e);
      }
    });
  },
  async loadTypeList() {
    const list = await leaveManagerAction("typeListAll");
    this.bind.leaveTypeList = list || [];
  },
  clickDeleteType(typeId) {
    const type = this.bind.leaveTypeList.find((g) => g.id === typeId);
    var _self = this;
    const c = lpFormat(lp, "leaveManagerV2.confirmDelete", { name: type.name });
    o2.api.page.confirm(
      "warn",
      lp.alert,
      c,
      300,
      100,
      function () {
        _self.deleteType(typeId);
        this.close();
      },
      function () {
        this.close();
      }
    );
  },
  async deleteType(id) {
    this.$parent.closeFormVm();
    const result = await leaveManagerAction("typeDelete", id)
    console.debug(result);
    this.loadTypeList();
  },
  // 打开账号列表的搜索视图
  async clickOpenAccountSearchView() {
    const bindData = {  };
    const c = (await import('./accountList/index.js')).default;
    this.openOtherListViewVm(c, bindData);
  },
  // 打开请假申请的搜索视图
  async clickOpenRequestSearchView() {
    const bindData = { bind: {self: false} };
    const c = (await import('./requestList/index.js')).default;
    this.openOtherListViewVm(c, bindData);
  },
  // 打开节假日日历视图
  async clickOpenHolidayCalendarView() {
    const bindData = {  };
    const c = (await import('./calendar/index.js')).default;
    this.openOtherListViewVm(c, bindData);
  },
  async clickOpenLeaveDataView() {
    const bindData = { bind: {self: false} };
    const c = (await import('../leaveManager/index.js')).default;
    this.openOtherListViewVm(c, bindData);
  },
  // 打开某一个假期类型的规则列表
  clickOpenPolicyList(typeId) {
    console.log("点击打开配置规则列表", typeId);
    if (this.clickOpenPolicyLoading === true) {
      console.log("正在加载中，避免重复点击");
      return;
    }
    this.clickOpenPolicyLoading = true;
    this.$parent.closeFormVm();
    this.bind.leaveTypePolicyShow = true;
    // this.loadPolicyList(typeId);
    this.openPolicyListView(typeId);
  },
  async openPolicyListView(typeId) {
    const bindData = { bind: { typeId: typeId } };
    const c = (await import('./policyList/index.js')).default;
    this.openOtherListViewVm(c, bindData);
  },

  clickBackTypeList() {
    this.clickOpenPolicyLoading = false;
    this.$parent.closeFormVm();
    if (this.policyListVM) {
      this.policyListVM.destroy();
      this.policyListVM = null;
    }
    this.dom.querySelector("#otherListView").classList.remove("l-display-block");
    this.dom.querySelector("#otherListView").classList.add("l-display-none");
    this.dom.querySelector("#leaveTypeListView").classList.remove("l-display-none");
    this.dom.querySelector("#leaveTypeListView").classList.add("l-display-block");
  },

  async openOtherListViewVm(c, bindData) {
    this.policyListVM = await c.generate("#otherListView", bindData, this);
    this.dom.querySelector("#leaveTypeListView").classList.remove("l-display-block");
    this.dom.querySelector("#leaveTypeListView").classList.add("l-display-none");
    this.dom.querySelector("#otherListView").classList.remove("l-display-none");
    this.dom.querySelector("#otherListView").classList.add("l-display-block");

  },

});
