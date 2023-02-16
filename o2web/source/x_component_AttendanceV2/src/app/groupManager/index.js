import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { groupActionListByPaging } from "../../utils/actions";
import oPager from "../../components/o-pager";
import template from "./template.html";

export default content({
  template,
  components: { oPager },
  bind() {
    return {
      lp,
      groupList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        size: 15, // 每页条目数
      },
    };
  },
  afterRender() {
    this.loadGroupList();
  },
  formatAttendanceTime(group) {
    if (group && group.shift) {
        return group.shift.shiftName + ' '+ group.shift.properties.timeList[0].onDutyTime + ' - ' + group.shift.properties.timeList[group.shift.properties.timeList.length-1].offDutyTime;
    }
    return "";
  },
  loadData(e) {
    if (
      e &&
      e.detail &&
      e.detail.module &&
      e.detail.module.bind &&
      e.detail.module.bind.pagerData
    ) {
      this.bind.pagerData.page = e.detail.module.bind.pagerData.page;
      this.loadGroupList();
    }
  },
  async loadGroupList() {
    const json = await groupActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      {}
    );
    if (json) {
      this.bind.groupList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  async clickEditGroup(id) {},
  async clickDeleteGroup(id, name) {},
  async addGroup() {
    // 添加
    const content = (await import(`./editGroup/index.js`)).default;
    this.addGroupVm = await content.generate(".form", {}, this);
  },
  closeGroup() {
    if (this.addGroupVm) {
      this.addGroupVm.destroy();
    }
    this.loadGroupList();
  },
});
