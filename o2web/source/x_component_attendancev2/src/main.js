import {loadComponent } from '@o2oa/component';
import index from './app/index';
import mobile from './mobile/main';

loadComponent('attendancev2', (d, cb)=>{
    // 移动端页面
    if ((layout.mobile || o2.session.isMobile)){
        mobile.render(d).then( () =>{
            cb();
        });
    } else {
        index.render(d).then(()=>{
            cb();
        });
    }
}).then((c)=>{
    c.render();
});
