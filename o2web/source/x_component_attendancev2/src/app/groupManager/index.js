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
        totalPage: 1,
        size: 15, // 每页条目数
      },
    };
  },
  afterRender() {
    this.loadGroupList();
    this.listenEventBus();
  },
  listenEventBus() {
    this.$topParent.listenEventBus("group", (data) => {
      console.log("接收到了group消息", data);
      this.loadGroupList();
    });
  },
  // 班次信息展现
  formatAttendanceTime(group) {
    if (!group) {
      return "";
    }
    if (group.checkType === "1" && group.workDateProperties) {
      // 如果是固定班制 展现周一或周二的班次信息
      let map = {};
      let restList = [];
      for (let key in group.workDateProperties) {
        const day = group.workDateProperties[key];
        const dayLp = this.day2Lang(key);
        if (dayLp === "") {
          continue;
        }
        if (day.checked) {
          if (map[day.shiftId]) {
            map[day.shiftId]["dayList"].push(dayLp);
          } else {
            map[day.shiftId] = {
              dayList: [dayLp],
              shift: day.shift,
            };
          }
        } else {
          restList.push(dayLp);
        }
      }
      let retStr = "";
      for (let id in map) {
        retStr +=
          map[id].dayList.join("、") +
          " " +
          map[id].shift.shiftName +
          " (" +
          map[id].shift.properties.timeList[0].onDutyTime +
          " - " +
          map[id].shift.properties.timeList[
            map[id].shift.properties.timeList.length - 1
          ].offDutyTime +
          ") | ";
      }
      if (restList.length > 0) {
        retStr += restList.join("、") + " " + lp.groupForm.shiftEmpty;
      } else {
        retStr = retStr.substring(0, retStr.length - 2);
      }
      return retStr;
    } else if (group.checkType === "2") {
      // 如果是自由工时 直接展现
      const dayList = group.workDateList.split(",").map((d) => {
        switch (d) {
          case "1":
            return lp.day.Monday;
          case "2":
            return lp.day.Tuesday;
          case "3":
            return lp.day.Wednesday;
          case "4":
            return lp.day.Thursday;
          case "5":
            return lp.day.Friday;
          case "6":
            return lp.day.Saturday;
          case "0":
            return lp.day.Sunday;
        }
        return "";
      });
      return dayList.join("、") + " " + lp.groupForm.checkTypeFree;
    } else if (group.checkType === "3") {
      return lp.groupForm.checkTypeArrangement;
    }
    return "";
  },
  day2Lang(day) {
    switch (day) {
      case "monday":
        return lp.day.Monday;
      case "tuesday":
        return lp.day.Tuesday;
      case "wednesday":
        return lp.day.Wednesday;
      case "thursday":
        return lp.day.Thursday;
      case "friday":
        return lp.day.Friday;
      case "saturday":
        return lp.day.Saturday;
      case "sunday":
        return lp.day.Sunday;
    }
    return "";
  },
  loadData(e) {
    if (e && e.detail && e.detail.module && e.detail.module.bind) {
      this.bind.pagerData.page = e.detail.module.bind.page || 1;
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
    // const content = (await import(`./editGroup/index.js`)).default;
    // this.addGroupVm = await content.generate(".form", {}, this);
    this.$parent.openGroupForm({});
  },
  // 修改
  async clickEditGroup(id) {
    const group = this.bind.groupList.find((g) => g.id === id);
    if (group) {
      // const content = (await import(`./editGroup/index.js`)).default;
      // this.addGroupVm = await content.generate(".form", {bind: {updateId: group.id}}, this);
      this.$parent.openGroupForm({ bind: { updateId: group.id } });
    }
  },
  // 删除
  async clickDeleteGroup(id, name) {
    var _self = this;
    const c = lpFormat(lp, "groupForm.confirmDelete", { name: name });
    o2.api.page.confirm(
      "warn",
      lp.alert,
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
    this.$topParent.closeFormVm(); // 关闭表单
    const json = await groupAction("delete", id);
    console.debug(json);
    this.loadGroupList();
  },
  // 点击事件 打开排班编辑
  async clickEditSchedule(group) {
    if (!group || !group.id) {
      console.log('数据异常！');
      return ;
    }
    this.$topParent.closeFormVm(); // 关闭表单
    const result = await groupAction("get", group.id);
    if (result) {
      const data = {
        bind: {
          trueParticipantList: result.trueParticipantList || [],
          groupId: result.id,
        },
      };
      this.openScheduleWindow(data);
    }
  },
  // 打开排班界面
  async openScheduleWindow(bind) {
    if (!bind || !bind.bind || !bind.bind.trueParticipantList || !bind.bind.groupId) {
      console.log('数据异常！');
      return ;
    }
    const c = (await import("./schedule/index.js")).default;
    this.scheduleVm = await c.generate("#scheduleFom", bind, this);
  },

  // 关闭表单页面
  closeGroup() {
    if (this.addGroupVm) {
      this.addGroupVm.destroy();
    }
    this.loadGroupList();
  },
});
