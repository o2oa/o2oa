import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';
import taskList from '../taskList';

export default content({
    template,
    style,
    components: {taskList},

    bind(){
        return {lp};
    }
});
