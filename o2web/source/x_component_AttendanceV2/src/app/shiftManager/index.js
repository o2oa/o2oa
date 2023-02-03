import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
import template from './template.html';

export default content({
    template,
    bind(){
        return {lp};
    },
    addShift() {
        console.log('新增班次');
    }
});
