import { loadComponent, component } from '@o2oa/component';
import index from './app/index';
import mobile from './mobile/main';
import checkIn from './mobile/checkIn';


loadComponent('attendancev2', (d, cb) => {
    // 移动端页面
    if ((layout.mobile || o2.session.isMobile)) {
        // const status = component.status;
        // if (status && status.qywx === true) {
            checkIn.render(d).then(() => {
                cb();
            });
        // } else {
        //     mobile.render(d).then(() => {
        //         cb();
        //     });
        // }
    } else {
        index.render(d).then(() => {
            cb();
        });
    }
}).then((c) => {
    c.render();
});
