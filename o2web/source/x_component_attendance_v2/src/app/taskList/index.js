import {component as content} from '@o2oa/oovm';
import {lp, o2, component} from '@o2oa/component';
import template from './template.html';

export default content({
    template,
    async bind() {
        const json = await o2.Actions.load('x_processplatform_assemble_surface').TaskAction.listMyPaging(1, 5);
        return {
            lp,
            data: json.data
        };
    },
    openTask(e, data){
        o2.api.page.openWork(data.value.work);
    },
    openCalendar(){
        o2.api.page.openApplication('Calendar');
    },
    openOrganization(){
        o2.api.page.openApplication('Org');
    },
    openInBrowser() {
        component.openInNewBrowser(true);
    },
    startProcess(){
        o2.api.page.startProcess();
    },
    createDocument(){
        o2.api.page.createDocument();
    }
});
