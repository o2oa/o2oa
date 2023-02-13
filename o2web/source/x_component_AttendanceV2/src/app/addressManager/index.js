import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
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
  clickAdd() {

  },
  clickDeleteItem(id, name) {

  },
  async loadWorkAddressData() {
    const json = await o2.Actions.load(
      "x_attendance_assemble_control"
    ).WorkPlaceV2Action.listAll();
    if (json) {
      this.bind.workAddressList = json.data || [];
    }
  },
});