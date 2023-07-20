import '@o2oa/ui';
import index from './content/index.js';

const load = ()=>{
    return index.generate(document.body);
}
load();
