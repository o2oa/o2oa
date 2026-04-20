import { component as content } from "@o2oa/oovm";
import { lp, o2, layout } from "@o2oa/component";
import { lpFormat } from "../../utils/common";
import { leaveManagerAction } from "../../utils/actions";
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
      leaveTypeList: [],
      leaveTypePolicyShow: false, // 是否显示某一个假期类型的规则列表
      currentLeaveType: null, // 当前选中的假期类型
      leaveTypePolicyList: [], // 某一个假期类型的规则列表
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
    this.$topParent.listenEventBus("leaveTypePolicy", (data) => {
      console.log("接收到了leaveTypePolicy消息", data);
      this.refreshPolicyList();
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
    const c = lpFormat(lp, "leaveManagerV2.type.confirmDelete", { name: type.name });
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
  clickOpenPolicyList(typeId) {
    console.log("点击打开配置规则列表", typeId);
    if (this.clickOpenPolicyLoading === true) {
      console.log("正在加载中，避免重复点击");
      return;
    }
    this.clickOpenPolicyLoading = true;
    this.loadPolicyList(typeId);
  },
  async refreshPolicyList() {
    if (!this.bind.currentLeaveType) {
      console.log("没有选中的假期类型，无法刷新规则列表");
      return;
    }
    const typeId = this.bind.currentLeaveType.id;
    await this.loadPolicyList(typeId);
  },
  async loadPolicyList(typeId) {
    const type = this.bind.leaveTypeList.find((g) => g.id === typeId);
    this.bind.currentLeaveType = type;
    const list = await leaveManagerAction("policyListWithTypeId", typeId)
    this.bind.leaveTypePolicyList = list || [];
    this.bind.leaveTypePolicyShow = true;
    this.clickOpenPolicyLoading = false;
  },
  clickBackTypeList() {
    this.bind.leaveTypePolicyShow = false;
    this.bind.leaveTypePolicyList = [];
    this.clickOpenPolicyLoading = false;
  },
  clickAddPolicy() {
    this.$parent.openLeaveTypePolicyForm({ bind: { form: { leaveTypeId: this.bind.currentLeaveType.id } } });
  },
  clickEditLeaveTypePolicy(id) {
    console.log("点击编辑假期类型规程", id);
    this.$parent.openLeaveTypePolicyForm({ bind: { updateId: id } });
  },
  
});
