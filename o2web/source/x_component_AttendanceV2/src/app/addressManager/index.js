import { component as content } from "@o2oa/oovm";
import { lp } from "@o2oa/component";
import { attendanceWorkPlaceV2Action } from '../../utils/actions';
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
  async clickAdd() {
     // 添加
     const content = (await import(`./addAddress/index.js`)).default;
     this.addAddressVm = await content.generate(".form", {}, this);
  },
  clickDeleteItem(id, name) {

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
});