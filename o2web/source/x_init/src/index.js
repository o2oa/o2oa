import '@o2oa/ui';
import index from './content/index.js';
import {serverStatus} from './common/action.js';

const load = async () => {
    const status = await serverStatus();
    if (status.status==='starting'){
        window.location = '/';
    }else{
        index.generate(document.body);
    }
}
load();
