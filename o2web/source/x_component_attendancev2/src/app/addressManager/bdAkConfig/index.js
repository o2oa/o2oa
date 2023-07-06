import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { getPublicData, putPublicData } from '../../../utils/actions';
import { isEmpty } from '../../../utils/common';
import template from './temp.html';
import oInput from '../../../components/o-input';


export default content({
    template,
    components: {oInput},
    autoUpdate: true,
    bind(){
        return {
            lp,
            baiduAccountKey: "",
        };
    },
    afterRender() {
    },
    close() {
        this.$parent.closeFormVm();
    },
    async submitAdd() {
        if (isEmpty(this.bind.baiduAccountKey)) {
          o2.api.page.notice(lp.workAddressBDSecretTitlePlaceholder, 'error');
          return ;
        }
        const bdKey = await putPublicData("baiduAccountKey", this.bind.baiduAccountKey);
        console.debug('配置成功', bdKey);
        this.close();
    },
   
});
