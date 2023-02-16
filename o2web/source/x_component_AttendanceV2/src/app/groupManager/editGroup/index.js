import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { isEmpty } from "../../../utils/common";
import { groupAction } from "../../../utils/actions";
import template from "./temp.html";
import oInput from "../../../components/o-input";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import selectShift from "../../shiftManager/selectShift";

export default content({
  template,
  components: { oInput, oOrgPersonSelector, selectShift },
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.groupAdd,
      form: {
        groupName: "",
        participateList: [],
        unParticipateList: [],
        shift: null,
        shiftId: "",
        workPlaceIdList: [],
        workPlaceList: [],
      },
      workDateList: [], // 选中的days
      days: [
        {
          name: "周日",
          value: "0",
        },
        {
          name: "周一",
          value: "1",
        },
        {
          name: "周二",
          value: "2",
        },
        {
          name: "周三",
          value: "3",
        },
        {
          name: "周四",
          value: "4",
        },
        {
          name: "周五",
          value: "5",
        },
        {
          name: "周六",
          value: "6",
        },
      ],
    };
  },
  afterRender() {
    if (this.bind.form && this.bind.form.id && this.bind.form.id !== "") {
      this.bind.fTitle = this.bind.lp.groupUpdate;
    }
  },
  // 打开班次选择器
  async openShiftSelect() {
    const content = (await import("../../shiftManager/selectShift/index.js"))
      .default;
    const dm = document.body.querySelector("#app-attendance-v2");
    this.shiftSelectVm = await content.generate(dm, {}, this);
  },
  // 关闭班次选择器
  closeShiftSelect() {
    if (this.shiftSelectVm) {
      this.shiftSelectVm.destroy();
    }
  },
  // 班次选择 返回结果
  reciveShiftSelect(value) {
    // value是一个shift的对象
    this.bind.form.shift = value || {};
    this.bind.form.shiftId = this.bind.form.shift.id || "";
  },
  // 点击days的checkbox
  selectDay(value) {
    if (value.value) {
      const index = this.bind.workDateList.indexOf(value.value);
      if (index > -1) {
        this.bind.workDateList.splice(index, 1);
      } else {
        this.bind.workDateList.push(value.value);
      }
    }
  },
  // 打开工作场所选择器
  async openWorkPlaceSelector() {
    const content = (await import("../../addressManager/addressSelector/index.js"))
      .default;
    const dm = document.body.querySelector("#app-attendance-v2");
    this.workPlaceSelectVm = await content.generate(dm, {}, this);
  },
  // 关闭班次选择
  closeSelectWorkPlace() {
    if (this.workPlaceSelectVm) {
      this.workPlaceSelectVm.destroy();
    }
  },
  reciveWorkPlaceSelect(value) {
    console.debug(value);
    this.bind.form.workPlaceList = value;
  },
  close() {
    this.$parent.closeGroup();
  },
  async submit() {
    debugger;
    console.debug(this.bind);
    // this.close();
  },
});
