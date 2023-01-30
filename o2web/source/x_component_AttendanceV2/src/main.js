import {loadComponent} from '@o2oa/component';
import index from './app/index';

loadComponent('AttendanceV2', (d, cb)=>{
    index.render(d).then(()=>{
        cb();
    });
}).then((c)=>{
    c.render();
});
