import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { attendanceWorkPlaceV2Action } from "../../../utils/actions";
import oPager from '../../../components/o-pager';
import template from "./temp.html";

export default content({
  template,
  autoUpdate: true,
  components: {oPager},
  bind() {
    return {
      lp,
      title: lp.workAddressSelector,
      workAddressList: [],
      workAddressSelected: [], // 
    };
  },
  afterRender() {
    const mask = this.dom.querySelector("#selectShift");
    mask.style["z-index"] = "2001";
    const dialog = this.dom.querySelector("#selectShiftBox");
    dialog.style.width = "600px" ;
    dialog.style.height = "500px" ;
    dialog.style.left =  (mask.clientWidth - 600) / 2 + "px" ;
    dialog.style.top =  (mask.clientHeight - 500) / 2 + "px" ;
    this.loadWorkAddressData();
  },
  closeSelectWorkPlace() {
    if (this.$parent && this.$parent.closeShiftSelect) {
      this.$parent.closeSelectWorkPlace();
    }
  },
  async loadWorkAddressData() {
    const data = await attendanceWorkPlaceV2Action("listAll");
    this.bind.workAddressList = data || [];
  },
  isContainWorkPlace(v) {
    let i = -1;
    for (let index = 0; index < this.bind.workAddressSelected.length; index++) {
      const element = this.bind.workAddressSelected[index];
      if (v.id === element.id) {
        i = index;
        break
      }
    }
    return i > -1;
  },
  chooseWorkPlace(v) {
    if (v && v.id) {
      let i = -1;
      for (let index = 0; index < this.bind.workAddressSelected.length; index++) {
        const element = this.bind.workAddressSelected[index];
        if (v.id === element.id) {
          i = index;
          break
        }
      }
      if (i > -1) {
        this.bind.workAddressSelected.splice(i, 1);
      } else {
        this.bind.workAddressSelected.push(v);
      }
    }
  },
  submit() {
    if (this.bind.workAddressSelected && this.bind.workAddressSelected.length > 0) {
      // if (this.$parent.reciveWorkPlaceSelect) {
      //   this.$parent.reciveWorkPlaceSelect(this.bind.workAddressSelected);
      // } else {
      //   console.error("没有reciveWorkPlaceSelect方法");
      // }
      this.closeSelectWorkPlace();
    } else {
      o2.api.page.notice(lp.workAddressForm.selectWorkAddressEmpty, 'error');
    }
  },
});
