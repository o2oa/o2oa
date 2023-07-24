import {component} from '@o2oa/oovm';
import {dom} from '@o2oa/util';
import template from './footer.html?raw';
export default component({
    template,
    autoUpdate: true,
    // components: {header, main, footer}

    afterRender(){
        window.setTimeout(()=>{
            dom.setStyles(this.footerNode, {
                opacity: 1
            });
        }, 1600);

    }
});
