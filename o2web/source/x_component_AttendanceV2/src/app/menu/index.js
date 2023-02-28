import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';
 

export default content({
    template,
    style,
    components: {},

    bind(){
        return {
            lp,
            currentMenuId: 5,
            menuData: [
              {
                title: lp.menu.myAttendance,
                sub: [
                  {
                    id: 1,
                    title: lp.menu.myAttendanceMonth,
                    action: "shiftManager"
                  },
                  {
                    id: 2,
                    title: lp.menu.myAttendanceStatistic,
                    action: "shiftManager"
                  }
                ]
              },
              {
                title: lp.menu.statistic,
                access: "admin",
                sub: [
                  {
                    id: 3,
                    title: lp.menu.detailFilter,
                    action: "detailManager"
                  },
                  {
                    id: 4,
                    title: "个人考勤统计",
                    action: "shiftManager"
                  }
                ]
              },
              {
                title: lp.menu.config,
                access: "admin",
                sub: [
                  {
                    id: 5,
                    title: lp.menu.shiftManager,
                    action: "shiftManager"
                  },
                  {
                    id: 6,
                    title: lp.menu.groupmanager,
                    action: "groupManager"
                  },
                  {
                    id: 7,
                    title: lp.menu.addressmanger,
                    action: "addressManager"
                  },
                  {
                    id: 8,
                    title: lp.menu.configmanager,
                    action: "configManager"
                  }
                ]
              }
            ]
        };
    },
    clickMenu(submenu) {
      this.bind.currentMenuId = submenu.id;
      for (let index = 0; index < this.bind.menuData.length; index++) {
        const element = this.bind.menuData[index];
        const subList = element.sub;
        if (subList && subList.length > 0) {
          for (let j = 0; j < subList.length; j++) {
            const s = subList[j];
            if (s && s.id == submenu.id) {
              this.$parent.openApp(s.action);
            }
          }
        }
      }
      
    }
});
