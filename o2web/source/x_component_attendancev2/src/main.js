import {loadComponent, component } from '@o2oa/component';
import index from './app/index';
import mobile from './mobile/index';

loadComponent('attendancev2', (d, cb)=>{
    // 移动端页面
    if (component.options && component.options.route && component.options.route === 'mobile') {
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
