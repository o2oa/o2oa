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
      // for (let index = 0; index < this.bind.menuData.length; index++) {
      //   const element = this.bind.menuData[index];
      //   const subList = element.sub;
      //   if (subList && subList.length > 0) {
      //     for (let j = 0; j < subList.length; j++) {
      //       const s = subList[j];
      //       if (s && s.id == submenu.id) {
      //         this.$parent.openApp(s);
      //       }
      //     }
      //   }
      // }
    }
});
