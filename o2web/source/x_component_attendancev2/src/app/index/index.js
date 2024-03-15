import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { myAction } from "../../utils/actions";
import { EventBus } from "../../utils/eventBus";
import template from "./template.html";
import style from "./style.scope.css";
import myMenu from "../menu";

export default content({
  style,
  template,
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
    this._initEventBus();
  },
  // 初始化 EventBus
  _initEventBus() {
    this.eventBus = new EventBus();
  },
  // 添加监听
  listenEventBus(eventName, callback) {
    this.eventBus.subscribe(eventName, callback);
  },
  // 发送事件
  publishEvent(eventName, data) {
    this.eventBus.publish(eventName, data);
  },
  // 打开 打卡记录的详情
  async openRecordDetailVm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../recordManager/detail/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 打开 原始记录
  async openRecordListVm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../detailManager/recordList/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 打开 打卡地点表单
  async openBDMapConfigForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../addressManager/bdAkConfig/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 打开 打卡地点表单
  async openAddressForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../addressManager/addAddress/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 打开班次表单
  async openShiftForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../shiftManager/addShift/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 打开考勤组表单
  async openGroupForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../groupManager/editGroup/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 打开编辑表单页面
  async openFomVm(c, bindData) {
    this.formVm = await c.generate("#form", bindData, this);
    this.dom.querySelector("#form").classList.add("index_page_form_container");
    this.dom.querySelector("#formMask").classList.replace("cc-mask-hide", "cc-mask-show");
  },
  // 关闭表单
  closeFormVm() {
    if (this.formVm) {
      this.formVm.destroy();
    }
    this.dom.querySelector("#form").classList.remove("index_page_form_container");
    this.dom.querySelector("#formMask").classList.replace("cc-mask-show", "cc-mask-hide");
  },
  
  async loadCurrentPersonInfo() {
    content.myDutyList = [];
    this.bind.admin = "";
    const controls = await myAction("controls");
    if (controls) {
      if (controls.admin) { // 管理员
        this.bind.admin = "admin";
      } else {
        if (controls.readAdmin) { // 有考勤管理员职务 查看统计权限
          this.bind.admin = "readAdmin";
          content.myDutyList = controls.unitDutyList || [];
        }
        if (controls.assistAdmin) { // 考勤组协助管理员
          this.bind.assistAdmin = true;
        } 
      }
    }
    // if (o2.AC.isAttendanceManager() || o2.AC.isAdministrator()) {
    //   this.bind.admin = "admin";
    // } else {
    //   const personInfo = await personalAction("get");
    //   let isReadAdmin = false;
    //   let dutyList = [];
    //   if (personInfo && personInfo.woIdentityList) {
    //     for (const { woUnitDutyList } of personInfo.woIdentityList) {
    //       if (woUnitDutyList && woUnitDutyList.some((duty) => duty.name === "考勤管理员")) {
    //         dutyList.push(...woUnitDutyList.filter((duty) => duty.name === "考勤管理员"));
    //         isReadAdmin = true;
    //       }
    //     }
    //   }
    //   if (isReadAdmin) {
    //     this.bind.admin = "readAdmin";
    //     content.myDutyList = dutyList;
    //   }
    // }
  },
  // 普通菜单数据
  getCurrentPersonMenu() {
    const menus = this.menuDataAll();
    let access = 0;
    if (this.bind.admin === "readAdmin") {
      access = 1;
    } else if (this.bind.admin === "admin") {
      access = 2;
    }
    let accessMenus = menus.filter((menu) => menu.access <= access);
    if (this.bind.assistAdmin) { // 协助管理员 添加考勤组管理菜单
      if ( !accessMenus.some((m)=> m.access == 2) ) {
        accessMenus.push(
          {
            title: lp.menu.config,
            access: 2,
            sub: [
              {
                id: "3-2",
                title: lp.menu.groupmanager,
                action: "groupManager",
                icon: "o2icon-icon_kaoqinzu"
              }
            ],
          }
        );
      }
    }
    return accessMenus;
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
            icon: "o2icon-icon_tongji"
          },
          {
            id: "1-2",
            title: lp.menu.myAppealList,
            action: "appealManager",
            icon: "o2icon-icon_kaoiqinyichang"
          },
          {
            id: "1-3",
            title: lp.menu.leavemanager,
            action: "leaveManager",
            icon: "o2icon-icon_shijian"
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
            icon: "o2icon-icon_huizong"
          },
          {
            id: "2-2",
            title: lp.menu.detailFilter,
            action: "detailManager",
            icon: "o2icon-icon_meirihuizong"
          },
          {
            id: "2-3",
            title: lp.menu.recordList,
            action: "recordManager",
            icon: "o2icon-icon_yuanshijilu"
          },
          {
            id: "2-4",
            title: lp.menu.appealList,
            action: "appealAdminManager",
            icon: "o2icon-icon_kaoiqinyichang"
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
            icon: "o2icon-icon_banciguanli"
          },
          {
            id: "3-2",
            title: lp.menu.groupmanager,
            action: "groupManager",
            icon: "o2icon-icon_kaoqinzu"
          },
          {
            id: "3-3",
            title: lp.menu.addressmanger,
            action: "addressManager",
            icon: "o2icon-icon_changsuo"
          },
          {
            id: "3-4",
            title: lp.menu.leavemanager,
            action: "leaveManager",
            icon: "o2icon-icon_qingjia"
          },
          {
            id: "3-5",
            title: lp.menu.configmanager,
            action: "configManager",
            icon: "o2icon-icon_peizhi"
          },
        ],
      },
    ];
  },
});
