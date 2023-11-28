import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import {
  attendanceWorkPlaceV2Action,
  getPublicData,
} from "../../utils/actions";
import { lpFormat, isEmpty } from "../../utils/common";
import template from "./temp.html";

export default content({
  template,
  autoUpdate: true,
  components: {},
  bind() {
    return {
      lp,
      workAddressList: [],
      // 地图配置
      mapConfig: {
        mapType: "baidu", //  amap baidu
        baiduAccountKey: "",
        aMapAccountKey: "",
      },
    };
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
    this.$parent.openAddressForm({
      bind: { form: { positionType: this.bind.mapConfig.mapType } },
    });
  },
  // 查看位置
  clickOpenView(address) {
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
    const data = await attendanceWorkPlaceV2Action("delete", id);
    console.info(data);
    this.loadWorkAddressData();
  },
  async loadWorkAddressData() {
    const data = await attendanceWorkPlaceV2Action("listAll");
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
