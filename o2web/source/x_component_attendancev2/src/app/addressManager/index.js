import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { attendanceWorkPlaceV2Action, getPublicData } from '../../utils/actions';
import { lpFormat } from '../../utils/common';
import style from "./style.scope.css";
import template from "./temp.html";

export default content({
  style,
  template,
  autoUpdate: true,
  components: {},
  bind() {
    return {
      lp,
     workAddressList: []
    };
  },
  afterRender() {
    this.loadWorkAddressData();
  },
  async openBdAKConfig() {
    const bdKey = (await getPublicData("baiduAccountKey")) || "";
    // const content = (await import('./bdAkConfig/index.js')).default;
    // this.configBdAKVm = await content.generate(".form", {bind: {"baiduAccountKey": bdKey}} , this);
    this.$parent.openBDMapConfigForm({bind: {"baiduAccountKey": bdKey}});
  },
  async clickAdd() {
     // 添加
    //  const content = (await import(`./addAddress/index.js`)).default;
    //  this.addAddressVm = await content.generate(".form", {}, this);
     this.$parent.openAddressForm({});
  },
  clickDeleteItem(id, name) {
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
  }
});