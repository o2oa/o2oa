import {component as content} from '@o2oa/oovm';
import {lp, component} from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';


export default content({
    template,
    style,
    autoUpdate: true,
    components: {
        appContainer: {
            watch: ['menu.currentMenu'],
            async load() {
                const name = this.bind.menu.currentMenu.action;
                return (await import (`./${name}/index.js`)).default;
            }
        }

    },
    beforeRender() {
        if (component.options && component.options.theme && component.options.theme === 'dark') {
            this.bind.isDarkTheme = true;
        }
        const menuList = [
            {action: "calendarView", name: lp.mobile.menu.statistic},
        ];
        this.bind.menu.menuData = menuList;
        this.bind.menu.currentMenu = menuList[0];
    },
    bind(){
        return {
            lp,
            isDarkTheme: false,
            menu: {
                currentMenu:  null,
                menuData: []
            }
        };
    },
    openMenu() {
        const menuNode = this.dom.querySelector('.mobile-main-menu');
        if (menuNode) {
            menuNode.classList.toggle('show');
        }
    }
});
