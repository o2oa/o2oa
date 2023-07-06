import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { lpFormat } from "../../utils/common";
import { shiftActionListByPaging, attendanceShiftAction } from "../../utils/actions";
import oPager from '../../components/o-pager';
import style from "./style.scope.css";
import template from "./template.html";

export default content({
  style,
  template,
  autoUpdate: true,
  components: {oPager},
  bind() {
    return {
      lp,
      shiftList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 每页条目数
      },
     
    };
  },
  afterRender() {
    // let a = [...new Array(2).keys()];
    this.listenEventBus();
    this.loadShiftList();
  },
  async addShift() {
    const addBind = {};
    addBind.form = {
      shiftName: "",
    };
    addBind.timeType = 1;
    addBind.time1 = {
      onDutyTime: "09:00",
      onDutyTimeBeforeLimit: "",
      onDutyTimeAfterLimit: "",
      offDutyTime: "18:00",
      offDutyTimeBeforeLimit: "",
      offDutyTimeAfterLimit: "",
    };
    // 添加
    // const content = (await import(`./addShift/index.js`)).default;
    // this.addShiftVm = await content.generate(".form", { bind: addBind }, this);
    this.$parent.openShiftForm({ bind: addBind });
  },
  loadData(e) {
    if (e && e.detail && e.detail.module && e.detail.module.bind) {
      this.bind.pagerData.page =  e.detail.module.bind.page || 1;
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
  // 修改
  async clickEditShift(id) {
    const shiftFilter = this.bind.shiftList.filter(s => s.id == id);
    if (shiftFilter && shiftFilter.length>0) {
      const shift = shiftFilter[0];
      let shiftData = {};
      if (shift && shift.properties && shift.properties.timeList) {
        shiftData.timeType = shift.properties.timeList.length;
        shiftData.time1 = shift.properties.timeList[0];
        if (shift.properties.timeList.length > 1) {
          shiftData.time2 = shift.properties.timeList[1];
        }
        if (shift.properties.timeList.length > 2) {
          shiftData.time3 = shift.properties.timeList[2];
        }
        // 修改
        shiftData.form = shift; // 多一层的
        // const content = (await import(`./addShift/index.js`)).default;
        // this.addShiftVm = await content.generate(".form", { bind: shiftData }, this);
        this.$parent.openShiftForm({ bind: shiftData });
      } else {
        o2.api.page.notice(lp.dataError, 'error');
      }
    } else {
      o2.api.page.notice(lp.dataError, 'error');
    }
  },
  clickDeleteShift(id, name) {
    var _self = this;
    const c = lpFormat(lp, "shiftForm.confirmDelete", { name: name });
    o2.api.page.confirm(
      "warn",
      lp.alert,
      c,
      300,
      100,
      function () {
        _self.deleteShift(id);
        this.close();
      },
      function () {
        this.close();
      }
    );
  },
  async deleteShift(id) {
    const json = await attendanceShiftAction("delete", id);
    console.debug(json);
    this.loadShiftList();
  },
  closeShift() {
    if (this.addShiftVm) {
      this.addShiftVm.destroy();
    }
    this.loadShiftList();
  },
  listenEventBus() {
    this.$topParent.listenEventBus('shift', (data) => {
      console.log('接收到了shift消息', data);
      this.loadShiftList();
    });
  },
});
