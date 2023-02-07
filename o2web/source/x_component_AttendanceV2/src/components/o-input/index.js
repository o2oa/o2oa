import {component as content} from '@o2oa/oovm';
import {lp, o2, component} from '@o2oa/component';
import style from './style.scope.css';
import template from './template.html';

export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            isRequried: false,
            value: "",
            label: ""
        };
    },
    showBind() {
      console.log(this.bind);
    },
    onValueChange() {
      console.log('onValueChange');
      this.dom.value = this.bind.value;
    }
     
});
