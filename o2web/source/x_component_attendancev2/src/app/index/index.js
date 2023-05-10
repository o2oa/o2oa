import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { personalAction } from "../../utils/actions";
import template from "./template.html";
import style from "./style.scope.css";
import myMenu from "../menu";

export default content({
  template,
  style,
  autoUpdate: true,
  components: {
    myMenu,
    appContainer: {
      watch: ["menu.currentMenu"],
      async load() {
        const name = this.bind.menu.currentMenu.action;
        return (await import(`../${name}/index.js`)).default;
      },
    },
  },
  bind() {
    return {
      lp,
      admin: "", // admin readAdmin
      menu: {
        currentMenu: {}, // 当前菜单
        menuData: [],
      },
    };
  },
  async beforeRender() {
    await this.loadCurrentPersonInfo();
    const menu = this.getCurrentPersonMenu();
    this.bind.menu.menuData = menu;
    if (menu.length) {
      this.bind.menu.currentMenu = menu[0].sub[0];
    }
  },
  async loadCurrentPersonInfo() {
    content.myDutyList = [];
    this.bind.admin = "";
    if (o2.AC.isAttendanceManager() && o2.AC.isAdministrator()) {
      this.bind.admin = "admin";
    } else {
      const personInfo = await personalAction("get");
      let isReadAdmin = false;
      let dutyList = [];
      if (personInfo && personInfo.woIdentityList) {
        for (const { woUnitDutyList } of personInfo.woIdentityList) {
          if (woUnitDutyList && woUnitDutyList.some((duty) => duty.name === "考勤管理员")) {
            dutyList.push(...woUnitDutyList.filter((duty) => duty.name === "考勤管理员"));
            isReadAdmin = true;
          }
        }
      }
      if (isReadAdmin) {
        this.bind.admin = "readAdmin";
        content.myDutyList = dutyList;
      }
    }
  }
  ,
  // 普通菜单数据
  getCurrentPersonMenu() {
    const menus = this.menuDataAll();
    let access = 0;
    if (this.bind.admin === "readAdmin") {
      access = 1;
    } else if (this.bind.admin === "admin") {
      access = 2;
    }
    return menus.filter((menu) => menu.access <= access);
  },
 
  menuDataAll() {
    return [
      {
        title: lp.menu.myAttendance,
        access: 0,
        sub: [
          {
            id: "1-1",
            title: lp.menu.myStatistic,
            action: "myAttendance",
          },
          {
            id: "1-2",
            title: lp.menu.myAppealList,
            action: "appealManager",
          },
          {
            id: "1-3",
            title: lp.menu.leavemanager,
            action: "leaveManager",
          },
        ],
      },
      {
        title: lp.menu.statistic,
        access: 1,
        sub: [
          {
            id: "2-1",
            title: lp.menu.detailStatisticFilter,
            action: "detailStatisticManager",
          },
          {
            id: "2-2",
            title: lp.menu.detailFilter,
            action: "detailManager",
          },
          {
            id: "2-3",
            title: lp.menu.recordList,
            action: "recordManager",
          },
        ],
      },
      {
        title: lp.menu.config,
        access: 2,
        sub: [
          {
            id: "3-1",
            title: lp.menu.shiftManager,
            action: "shiftManager",
          },
          {
            id: "3-2",
            title: lp.menu.groupmanager,
            action: "groupManager",
          },
          {
            id: "3-3",
            title: lp.menu.addressmanger,
            action: "addressManager",
          },
          {
            id: "3-4",
            title: lp.menu.leavemanager,
            action: "leaveManager",
          },
          {
            id: "3-5",
            title: lp.menu.configmanager,
            action: "configManager",
          },
        ],
      },
    ];
  },
});
