import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { isEmpty } from "../../../utils/common";
import { groupAction, attendanceWorkPlaceV2Action, attendanceShiftAction } from "../../../utils/actions";
import style from "./style.scope.css";
import template from "./temp.html";
 

export default content({
  style,
  template,
  components: { },
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.scheduleForm.title,
    };
  },
  // 先查询数据
  async beforeRender() {
     console.log(this.bind);
  },
  // 
  afterRender() {
    const mask = this.dom.querySelector("#scheduleFormBox");
    mask.style["z-index"] = "2001";
    const dialog = this.dom.querySelector("#scheduleFormDialog");
    dialog.style.width = "600px" ;
    dialog.style.height = "500px" ;
    dialog.style.left =  (mask.clientWidth - 600) / 2 + "px" ;
    dialog.style.top =  (mask.clientHeight - 500) / 2 + "px" ;
  },
  closeSelf() {
    this.component.destroy();
  },
  submit() {

  }
});
