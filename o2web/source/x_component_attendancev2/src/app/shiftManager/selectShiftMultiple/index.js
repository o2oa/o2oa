import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { shiftActionListByPaging } from "../../../utils/actions";
import oPager from '../../../components/o-pager';
import template from "./temp.html";

export default content({
  template,
  autoUpdate: true,
  components: {oPager},
  bind() {
    return {
      lp,
      title: lp.shiftSelector,
      shiftList: [],
      shiftSelected: [], // 
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 每页条目数
      },
     
    };
  },
  afterRender() {
    const mask = this.dom.querySelector("#selectShift");
    mask.style["z-index"] = "2002";
    const dialog = this.dom.querySelector("#selectShiftBox");
    dialog.style.width = "600px" ;
    dialog.style.height = "500px" ;
    dialog.style.left =  (mask.clientWidth - 600) / 2 + "px" ;
    dialog.style.top =  (mask.clientHeight - 500) / 2 + "px" ;
    this.loadShiftList();
  },
  closeSelectShift() {
    if (this.$parent && this.$parent.closeMultipleShiftSelector) {
      this.$parent.closeMultipleShiftSelector();
    }
  },
  loadData(e) {
    if (e && e.detail && e.detail.module && e.detail.module.bind && e.detail.module.bind.pagerData) {
      this.bind.pagerData.page = e.detail.module.bind.pagerData.page;
      this.loadShiftList();
    }
  },
  async loadShiftList() {
    const json = await shiftActionListByPaging( this.bind.pagerData.page, this.bind.pagerData.size, {});
    debugger;
    if (json) {
      this.bind.shiftList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  isContainShift(v) {
    let i = -1;
    for (let index = 0; index < this.bind.shiftSelected.length; index++) {
      const element = this.bind.shiftSelected[index];
      if (v.id === element.id) {
        i = index;
        break
      }
    }
    return i > -1;
  },
  chooseShift(v) {
    if (v && v.id) {
      let i = -1;
      for (let index = 0; index < this.bind.shiftSelected.length; index++) {
        const element = this.bind.shiftSelected[index];
        if (v.id === element.id) {
          i = index;
          break
        }
      }
      if (i > -1) {
        this.bind.shiftSelected.splice(i, 1);
      } else {
        this.bind.shiftSelected.push(v);
      }
    }
  },
  submit() {
    this.closeSelectShift();
  },
});
