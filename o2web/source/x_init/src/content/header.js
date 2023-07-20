import {component} from '@o2oa/oovm';
import {dom} from '@o2oa/util';
import template from './header.html?raw';
export default component({
    template,
    autoUpdate: true,

    afterRender(){
        window.setTimeout(()=>{
            dom.setStyles(this.headerNode, {
                opacity: 1
            });
        }, 100);
    }
});
