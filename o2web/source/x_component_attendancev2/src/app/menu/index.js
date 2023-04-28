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
    }
});
