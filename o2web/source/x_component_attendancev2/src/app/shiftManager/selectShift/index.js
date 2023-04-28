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
      shiftSelected: {}, // 
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
    mask.style["z-index"] = "2001";
    const dialog = this.dom.querySelector("#selectShiftBox");
    dialog.style.width = "600px" ;
    dialog.style.height = "500px" ;
    dialog.style.left =  (mask.clientWidth - 600) / 2 + "px" ;
    dialog.style.top =  (mask.clientHeight - 500) / 2 + "px" ;
    this.loadShiftList();
  },
  closeSelectShift() {
    if (this.$parent && this.$parent.closeShiftSelect) {
      this.$parent.closeShiftSelect();
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
    if (json) {
      this.bind.shiftList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  chooseShift(v) {
    if (v && v.id) {
      this.bind.shiftSelected = v; 
    }
  },
  submit() {
    if (this.bind.shiftSelected && this.bind.shiftSelected.id) {
      //this.component.updateModel( this.bind.shiftSelected );
      // 返回对象给父组件
      if (this.$parent && this.$parent.setShiftData) {
        this.$parent.setShiftData(this.bind.shiftSelected);
      }
      this.closeSelectShift();
    } else {
      o2.api.page.notice(lp.shiftForm.selectShiftEmpty, 'error');
    }
  },
});
