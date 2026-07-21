import { component as content } from "@o2oa/oovm";
import { lp, o2, layout } from "@o2oa/component";
import ExcelJS from "exceljs";
import { lpFormat, formatPersonName, chooseSingleFile, hideLoading, isEmpty, showLoading } from "../../utils/common";
import { leaveManagerAction, definitionAction } from "../../utils/actions";
import oPager from "../../components/o-pager";
import oOrgPersonSelector from "../../components/o-org-person-selector";
import oDatePicker from "../../components/o-date-picker";
import template from "./template.html";
import style from "./style.scope.css";


const definitionHistoryKey = "leaveManagerV2LedgerImportHistory";

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
      importHistoryList: [], // 导入历史记录
      currentImportHistoryList: [], // 当前假期类型的导入历史记录
    };
  },
  afterRender() {
    this.loadTypeList();
    this.loadLedgerImportHistory();
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
    this.bind.currentImportHistoryList = this.getLedgerImportHistoryList(type.id);
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
    this.bind.currentImportHistoryList = [];
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
      o2.api.page.notice("请输入发放标识", "error");
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
      const result = await this.uploadLedgerImport(formData);
      if (!this.isLedgerImportSuccess(result)) {
        o2.api.page.notice(this.getLedgerImportResultMessage(result, "导入失败"), "error");
        return;
      }
      try {
        await this.addLedgerImportHistory(form.grantPeriod, leaveType.id);
      } catch (e) {
        console.error("保存导入历史记录失败", e);
        o2.api.page.notice("导入成功，导入历史记录保存失败", "info");
        this.closeLedgerImport(true);
        return;
      }
      o2.api.page.notice("导入成功", "success");
      this.closeLedgerImport(true);
    } catch (e) {
      console.error("导入失败", e);
      o2.api.page.notice(this.getLedgerImportErrorMessage(e), "error");
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
            resolve(json);
          },
          (error) => {
            console.error("导入失败", error);
            reject(this.normalizeLedgerImportError(error));
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
  isLedgerImportSuccess(result) {
    const response = result || {};
    const data = response && response.data && typeof response.data === "object" ? response.data : {};
    const type = `${response.type || data.type || ""}`.toLowerCase();
    if (type && type !== "success" && type !== "warn") {
      return false;
    }
    if (response.error || data.error || response.success === false || data.success === false || response.result === false || data.result === false) {
      return false;
    }
    const status = `${response.status || data.status || ""}`.toLowerCase();
    if (status && (status.indexOf("error") > -1 || status.indexOf("fail") > -1)) {
      return false;
    }
    const code = response.code !== undefined ? response.code : data.code;
    if (!type && code !== undefined && code !== null && code !== "" && !["0", "200", "success"].includes(`${code}`.toLowerCase())) {
      return false;
    }
    const errorCount = data.errorCount !== undefined ? data.errorCount : data.failCount;
    if (Number(errorCount) > 0) {
      return false;
    }
    return true;
  },
  getLedgerImportResultMessage(result, defaultMessage) {
    const response = result || {};
    const data = response && response.data && typeof response.data === "object" ? response.data : {};
    const message = response.message || response.errorMessage || data.message || data.errorMessage;
    return Array.isArray(message) ? message.join("\n") : (message || defaultMessage);
  },
  normalizeLedgerImportError(error) {
    if (!error || !error.responseText) {
      return error;
    }
    try {
      const json = JSON.parse(error.responseText);
      return new Error(this.getLedgerImportResultMessage(json, "导入失败"));
    } catch (e) {
      return error;
    }
  },
  getLedgerImportErrorMessage(error) {
    return error && error.message ? error.message : "导入失败";
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
  // 获取导入历史记录
  async loadLedgerImportHistory() {
    try {
      const historyString = await definitionAction("get", definitionHistoryKey);
      console.debug("导入历史记录", historyString);
      let historyList = [];
      if (historyString) {
        historyList = typeof historyString === "string" ? JSON.parse(historyString) : historyString;
      }
      this.bind.importHistoryList = Array.isArray(historyList) ? historyList : [];
      if (this.bind.currentLeaveType && this.bind.currentLeaveType.id) {
        this.bind.currentImportHistoryList = this.getLedgerImportHistoryList(this.bind.currentLeaveType.id);
      }
    } catch (e) {
      console.error("获取导入历史记录失败", e);
      this.bind.importHistoryList = [];
      this.bind.currentImportHistoryList = [];
    }
  },
  getLedgerImportHistoryList(leaveTypeId) {
    const historyItem = this.bind.importHistoryList.find((item) => item.leaveTypeId === leaveTypeId);
    const list = historyItem && Array.isArray(historyItem.list) ? historyItem.list : [];
    return list.slice().sort((a, b) => (b.time || 0) - (a.time || 0));
  },
  // 添加导入历史记录 根据leaveTypeId分类
  async addLedgerImportHistory(grantPeriod, leaveTypeId) {
    const historyItem = this.bind.importHistoryList.find((item) => item.leaveTypeId === leaveTypeId);
    if (historyItem) {
      let list = historyItem.list || [];
      list = list.filter((item) => item.grantPeriod !== grantPeriod);
      list.unshift({ grantPeriod, time: new Date().getTime() });
      historyItem.list = list;
    } else {
      const newHistoryItem = {
        leaveTypeId,
        list: [{ grantPeriod, time: new Date().getTime() }],
      };
      this.bind.importHistoryList.push(newHistoryItem);
    }
    await definitionAction("updateMockPutToPost", definitionHistoryKey, JSON.stringify(this.bind.importHistoryList));
    this.bind.currentImportHistoryList = this.getLedgerImportHistoryList(leaveTypeId);
  },
  // 删除导入历史记录
  async removeLedgerImportHistory(grantPeriod, leaveTypeId) {
    const historyItem = this.bind.importHistoryList.find((item) => item.leaveTypeId === leaveTypeId);
    if (historyItem) {
      historyItem.list = historyItem.list.filter((item) => item.grantPeriod !== grantPeriod);
      if (historyItem.list.length === 0) {
        this.bind.importHistoryList = this.bind.importHistoryList.filter((item) => item !== historyItem);
      }
    }
    await definitionAction("updateMockPutToPost", definitionHistoryKey, JSON.stringify(this.bind.importHistoryList));
    this.bind.currentImportHistoryList = this.getLedgerImportHistoryList(leaveTypeId);
  },
  formatImportHistoryTime(time) {
    if (!time) {
      return "";
    }
    const date = new Date(time);
    if (Number.isNaN(date.getTime())) {
      return "";
    }
    const pad = (value) => value > 9 ? `${value}` : `0${value}`;
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
  }
});
