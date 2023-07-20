import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';
 

export default content({
    template,
    style,
    autoUpdate: true,
    bind(){
        return {
            lp,
        };
    },
    beforeRender() {
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
