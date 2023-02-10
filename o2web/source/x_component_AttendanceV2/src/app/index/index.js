import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
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
            watch: ['action'],
            async load() {
                const name = this.bind.action;
                return (await import (`../${name}/index.js`)).default;
            }
        }
        
    },

    bind(){
        return {
            lp,
            action: "shiftManager"
        };
    },
    openApp(action) {
        this.bind.action = action;
    }
});
