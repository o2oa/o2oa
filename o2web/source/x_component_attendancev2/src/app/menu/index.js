import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';


export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            openMainMenus: ['1'], // 展开的菜单
        };
    },
    beforeRender() {
    },
    clickMainMenu(mainMenu) {
        if (!mainMenu || !mainMenu.id) {
            return
        }
        console.debug(`mainMenu`, mainMenu)
        const index = this.bind.openMainMenus.indexOf(mainMenu.id)
        if (index > -1) {
            this.bind.openMainMenus.splice(index, 1)
        } else {
            this.bind.openMainMenus.push(mainMenu.id)
        }
        console.debug(`mainMenu`, this.bind.openMainMenus)
    },
    clickMenu(submenu) {
        this.bind.currentMenu = submenu;
        this.$parent.closeFormVm();
    },
    loadSubMenuClass(menu, currentMenu) {
        if (this.bind.currentMenu.id === menu.id) {
            return 'index_page_nav_menu_title_container index_page_nav_sub_menu_container title_selected primary_color';
        }
        return 'index_page_nav_menu_title_container index_page_nav_sub_menu_container';
    }
});
