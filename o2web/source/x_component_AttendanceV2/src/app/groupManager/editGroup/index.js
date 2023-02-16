import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { isEmpty } from '../../../utils/common';
import { groupAction } from "../../../utils/actions";
import template from './temp.html';
import oInput from '../../../components/o-input'; 
import oOrgPersonSelector from '../../../components/o-org-person-selector'; 


export default content({
    template,
    components: {oInput, oOrgPersonSelector},
    autoUpdate: true,
    bind(){
        return {
            lp,
            fTitle: lp.groupAdd,
            form: {
                groupName: "",
                participateList: [],
                unParticipateList: [],
            },
             
        };
    },
    afterRender() {
        if (this.bind.form && this.bind.form.id && this.bind.form.id !== '') {
            this.bind.fTitle = this.bind.lp.groupUpdate;
        }
    },
    
    close() {
        this.$parent.closeGroup();
    },
    async submit() {
        debugger
        console.debug(this.bind);
        // this.close();
    },
   
});
