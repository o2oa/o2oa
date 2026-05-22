import { loadComponent, component } from '@o2oa/component';
import index from './app/index';
import mobile from './mobile/main';
import checkIn from './mobile/checkIn';
import myRecord from './mobile/myRecord';


loadComponent('attendancev2', (d, cb) => {
    // 移动端页面
    if ((layout.mobile || o2.session.isMobile)) {
        const url = window.location.href;
        const uri = url.toURI();
        const page = uri.getData("page");
        if (page === "myRecord") {
            myRecord.render(d).then(() => {
                cb();
            });
        } else {
            checkIn.render(d).then(() => {
                cb();
            });
        }
    } else {
        index.render(d).then(() => {
            cb();
        });
    }
}).then((c) => {
    c.render();
});
