import { component as content } from "@o2oa/oovm";
import { lp, o2, layout } from "@o2oa/component";
import { lpFormat, formatPersonName } from "../../utils/common";
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
    const bindData = {  };
    const c = (await import('./requestList/index.js')).default;
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
