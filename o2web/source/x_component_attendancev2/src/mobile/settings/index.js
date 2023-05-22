import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
// import template from './template.html';
// import style from './style.scope.css';
 

export default content({
    template: "<div>设置</div>",
    autoUpdate: true,
    
    bind(){
        return {
            lp,
        };
    },
     
});
