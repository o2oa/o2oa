import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';
import myMenu from '../menu';

export default content({
    template,
    style,
    autoUpdate: true,
    components: {
        myMenu,
        appContainer: {
            watch: ['menu.currentMenu'],
            async load() {
                const name = this.bind.menu.currentMenu.action;
                return (await import (`../${name}/index.js`)).default;
            }
        }
        
    },
    beforeRender() {
        let menu = this.normalMenuData();
        // 管理员 增加菜单
        if (o2.AC.isAttendanceManager() && o2.AC.isAdministrator()) {
            menu.push.apply(menu, this.adminMenuData()); // 合并数组
        }
        this.bind.menu.menuData = menu;
        this.bind.menu.currentMenu = menu[0].sub[0];
    },
    bind(){
        return {
            lp,
            menu: {
                currentMenu: null, // 当前菜单
                menuData: []
            }
            
        };
    },
    // 普通菜单数据
    normalMenuData() {
        return  [
            {
              title: lp.menu.myAttendance,
              sub: [
                {
                  id: '1-1',
                  title: lp.menu.myStatistic,
                  action: "myAttendance"
                },
                {
                  id: '1-3',
                  title: lp.menu.myAppealList,
                  action: "appealManager"
                }
              ]
            }];
    },
    // 管理员菜单数据
    adminMenuData() {
        return [
            {
              title: lp.menu.statistic,
              access: "admin",
              sub: [
                {
                  id: '2-1',
                  title: lp.menu.detailStatisticFilter,
                  action: "detailStatisticManager"
                }, 
                {
                  id: '2-2',
                  title: lp.menu.detailFilter,
                  action: "detailManager"
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
                  title: lp.menu.leavemanager,
                  action: "leaveManager"
                },
                {
                  id: '3-5',
                  title: lp.menu.configmanager,
                  action: "configManager"
                }
              ]
            }
          ]
    }
});
