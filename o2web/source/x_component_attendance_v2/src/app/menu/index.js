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
            currentMenuId: '1-1',
            menuData: [
              {
                title: lp.menu.myAttendance,
                sub: [
                  {
                    id: '1-1',
                    title: lp.menu.myAttendanceMonth,
                    action: "shiftManager"
                  },
                  {
                    id: '1-2',
                    title: lp.menu.myAttendanceStatistic,
                    action: "shiftManager"
                  },
                  {
                    id: '1-3',
                    title: lp.menu.myAppealList,
                    action: "appealManager"
                  }
                ]
              },
              {
                title: lp.menu.statistic,
                access: "admin",
                sub: [
                  {
                    id: '2-1',
                    title: lp.menu.detailFilter,
                    action: "detailManager"
                  },
                  {
                    id: '2-2',
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
                    id: '3-1',
                    title: lp.menu.shiftManager,
                    action: "shiftManager"
                  },
                  {
                    id: '3-2',
                    title: lp.menu.groupmanager,
                    action: "groupManager"
                  },
                  {
                    id: '3-3',
                    title: lp.menu.addressmanger,
                    action: "addressManager"
                  },
                  {
                    id: '3-4',
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
