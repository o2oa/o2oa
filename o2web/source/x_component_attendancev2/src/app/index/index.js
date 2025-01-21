import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { myAction } from "../../utils/actions";
import { EventBus } from "../../utils/eventBus";
import template from "./template.html";
import style from "./style.scope.css";

export default content({
  style,
  template,
  autoUpdate: true,
  components: {
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
      this.bind.menu.currentMenu = menu[0].children[0];
    }
    this._initEventBus()
  },
  async afterRender() {
    this._initOONav()
  },
  // 初始化 EventBus
  _initEventBus() {
    this.eventBus = new EventBus();
  },
  _initOONav() {
    const ooNav = document.querySelector('oo-nav')
    ooNav.addEventListener('select', (e) => {
      this.bind.menu.currentMenu = e.detail.data
    })
    ooNav.setMenu(this.bind.menu.menuData)
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
  },
  // 关闭表单
  closeFormVm() {
    if (this.formVm) {
      this.formVm.destroy();
    }
    this.dom.querySelector("#form").classList.remove("index_page_form_container");
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
  },
  // 普通菜单数据
  getCurrentPersonMenu() {
    const menus = this.navDataAll();
    let access = 0;
    if (this.bind.admin === "readAdmin") {
      access = 1;
    } else if (this.bind.admin === "admin") {
      access = 2;
    }
    let accessMenus = menus.filter((menu) => menu.access <= access);
    if (this.bind.assistAdmin) { // 协助管理员 添加考勤组管理菜单
      if ( !accessMenus.some((m)=> m.access === 2) ) {
        accessMenus.push(
          {
            "title": lp.menu.config,
            "text": lp.menu.config,
            "name": '3',
            "access": 2,
            "children": [
              {
                "name": "3-2",
                "text": lp.menu.groupmanager,
                "action": "groupManager",
                icon: "ooicon-group"
              }
            ],
          }
        );
      }
    }
    return accessMenus;
  },
  navDataAll() {
    return [
      {
        "name": "1",
        "icon": "",
        "title": lp.menu.myAttendance,
        "text": lp.menu.myAttendance,
        "access": 0,
        "children": [
          {
            "name": "1-1",
            "text": lp.menu.myStatistic,
            "action": "myAttendance",
            "icon": "ooicon-data-center",
            "selected": true,
          },
          {
            "name": "1-2",
            "text": lp.menu.myAppealList,
            "action": "appealManager",
            "icon": "ooicon-emoji-prompt"
          },
          {
            "name": "1-3",
            "text": lp.menu.leavemanager,
            "action": "leaveManager",
            "icon": "ooicon-clock"
          }
        ]
      },
      {
        "title": lp.menu.statistic,
        "text": lp.menu.statistic,
        "name": '2',
        "access": 1,
        "children": [
          {
            "name": "2-1",
            "text": lp.menu.detailStatisticFilter,
            "action": "detailStatisticManager",
            "icon": "ooicon-work-management"
          },
          {
            "name": "2-2",
            "text": lp.menu.detailFilter,
            "action": "detailManager",
            "icon": "ooicon-work-management2"
          },
          {
            "name": "2-3",
            "text": lp.menu.recordList,
            "action": "recordManager",
            "icon": "ooicon-list-alt"
          },
          {
            "name": "2-4",
            "text": lp.menu.appealList,
            "action": "appealAdminManager",
            "icon": "ooicon-emoji-prompt"
          },
        ],
      },
      {
        "title": lp.menu.config,
        "text": lp.menu.config,
        "name": '3',
        "access": 2,
        "children": [
          {
            "name": "3-1",
            "text": lp.menu.shiftManager,
            "action": "shiftManager",
            "icon": "ooicon-onduty"
          },
          {
            "name": "3-2",
            "text": lp.menu.groupmanager,
            "action": "groupManager",
            icon: "ooicon-group"
          },
          {
            "name": "3-3",
            "text": lp.menu.addressmanger,
            "action": "addressManager",
            "icon": "ooicon-workcenter"
          },
          {
            "name": "3-4",
            "text": lp.menu.leavemanager,
            "action": "leaveManager",
            "icon": "ooicon-clock"
          },
          {
            "name": "3-5",
            "text": lp.menu.configmanager,
            "action": "configManager",
            "icon": "ooicon-config"
          },
        ],
      }
    ]
  },

});
