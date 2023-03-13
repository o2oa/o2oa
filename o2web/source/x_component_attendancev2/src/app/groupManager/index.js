import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { lpFormat } from "../../utils/common";
import { groupActionListByPaging, groupAction } from "../../utils/actions";
import oPager from "../../components/o-pager";
import template from "./template.html";

export default content({
  template,
  components: { oPager },
  autoUpdate: true,
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
        return group.shift.shiftName + ' ('+ group.shift.properties.timeList[0].onDutyTime + ' - ' + group.shift.properties.timeList[group.shift.properties.timeList.length-1].offDutyTime+')';
    }
    return "";
  },
  loadData(e) {
    if (
      e &&
      e.detail &&
      e.detail.module &&
      e.detail.module.bind
    ) {
      this.bind.pagerData.page =  e.detail.module.bind.page || 1;
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
  // 添加
  async addGroup() {
    const content = (await import(`./editGroup/index.js`)).default;
    this.addGroupVm = await content.generate(".form", {}, this);
  },
  // 修改
  async clickEditGroup(id) {
    const group = this.bind.groupList.find((g)=> g.id === id);
    if (group) {
      const content = (await import(`./editGroup/index.js`)).default;
      this.addGroupVm = await content.generate(".form", {bind: {updateId: group.id}}, this);
    }
    
  },
  // 删除
  async clickDeleteGroup(id, name) {
    var _self = this;
    const c = lpFormat(this.bind.lp, "groupForm.confirmDelete", { name: name });
    o2.api.page.confirm(
      "warn",
      this.bind.lp.alert,
      c,
      300,
      100,
      function () {
        _self.deleteGroup(id);
        this.close();
      },
      function () {
        this.close();
      }
    );
  },
  async deleteGroup(id) {
    const json = await groupAction("delete", id);
    console.debug(json);
    this.loadGroupList();
  },
  // 关闭表单页面
  closeGroup() {
    if (this.addGroupVm) {
      this.addGroupVm.destroy();
    }
    this.loadGroupList();
  },
});
