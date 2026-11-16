import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import {
  attendanceWorkPlaceV2Action,
  getPublicData,
} from "../../utils/actions";
import { getDutyUnitNames, lpFormat, isEmpty } from "../../utils/common";
import template from "./temp.html";

export default content({
  template,
  autoUpdate: true,
  components: {},
  bind() {
    return {
      lp,
      showConfigBtn: false,
      workAddressList: [],
      // 地图配置
      mapConfig: {
        mapType: "", //  amap baidu tx
        baiduAccountKey: "",
        aMapAccountKey: "",
        txMapAccountKey: "",
      },
      filter:{
        belongs: [], //
      }
    };
  },
  beforeRender() {
     // 当前用户的管理范围
    if (content.groupUnitDutyList && content.groupUnitDutyList.length > 0) {
      this.bind.filter.belongs = getDutyUnitNames(content.groupUnitDutyList);
    }
    if (content.controls && content.controls.admin) {
      this.bind.showConfigBtn = true;
    }
  },
  afterRender() {
    this.listenEventBus();
    this.loadMapConfig();
    this.loadWorkAddressData();
  },
  async loadMapConfig() {
    const config = await getPublicData("attendanceMapConfig");
    if (config) {
      this.bind.mapConfig = config;
    }
  },
  async openBdAKConfig() {
    this.$parent.openBDMapConfigForm({
      bind: { mapConfig: this.bind.mapConfig },
    });
  },
  async clickAdd() {
    debugger;
    if (
      this.bind.mapConfig.mapType === "baidu" &&
      isEmpty(this.bind.mapConfig.baiduAccountKey)
    ) {
      o2.api.page.notice(lp.workAddressMapKeyConfigEmpty, "error");
      return;
    }
    if (
      this.bind.mapConfig.mapType === "amap" &&
      isEmpty(this.bind.mapConfig.aMapAccountKey)
    ) {
      o2.api.page.notice(lp.workAddressMapKeyConfigEmpty, "error");
      return;
    }
    if (this.bind.mapConfig.mapType === "tx" && isEmpty(this.bind.mapConfig.txMapAccountKey)) {
      o2.api.page.notice(lp.workAddressMapKeyConfigEmpty, "error");
      return;
    }
    this.$parent.openAddressForm({
      bind: { form: { positionType: this.bind.mapConfig.mapType, status: 'a' } },
    });
  },
  // 查看位置
  clickOpenEdit(address) {
    address.status = 'u'
    this.$parent.openAddressForm({ bind: { form: address } });
  },
  // 查看位置
  clickOpenView(address) {
    address.status = 'v'
    this.$parent.openAddressForm({ bind: { form: address } });
  },
  // 删除工作场所
  clickDeleteItem(id, name) {
    debugger;
    var _self = this;
    const c = lpFormat(lp, "workAddressForm.confirmDelete", { name: name });
    o2.api.page.confirm(
      "warn",
      lp.alert,
      c,
      300,
      100,
      function () {
        _self.deleteWorkplace(id);
        this.close();
      },
      function () {
        this.close();
      }
    );
  },
  async deleteWorkplace(id) {
    const data = await attendanceWorkPlaceV2Action("deleteByManager", {
      id: id,
      belongs: this.bind.filter.belongs,
    });
    console.info(data);
    this.loadWorkAddressData();
  },
  async loadWorkAddressData() {
    const filter = {
      belongs: this.bind.filter.belongs
    }
    const data = await attendanceWorkPlaceV2Action("listByManager", filter);
    this.bind.workAddressList = data || [];
  },
  closeAddForm() {
    if (this.addAddressVm) {
      this.addAddressVm.destroy();
    }
    this.loadWorkAddressData();
  },
  closeBDAkConfig() {
    if (this.configBdAKVm) {
      this.configBdAKVm.destroy();
    }
  },
  listenEventBus() {
    this.$topParent.listenEventBus("address", (data) => {
      console.log("接收到了address消息", data);
      this.loadMapConfig();
      this.loadWorkAddressData();
    });
  },
});
