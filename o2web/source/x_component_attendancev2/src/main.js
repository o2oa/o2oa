import { loadComponent, component } from '@o2oa/component';
import index from './app/index';
import mobile from './mobile/main';
import checkIn from './mobile/checkIn';
import myRecord from './mobile/myRecord';
import appealManager from './mobile/appealManager';


loadComponent('attendancev2', (d, cb) => {
    // 移动端页面
    if ((layout.mobile || o2.session.isMobile)) {
        const url = window.location.href;
        const uri = url.toURI();
        const page = uri.getData("page");
        switch (page) {
            case "qywx":
                document.title = "打卡";
                checkIn.render(d).then(() => {
                    cb();
                });
                break;
            case "myRecord":
                document.title = "我的记录";
                myRecord.render(d).then(() => {
                    cb();
                });
                break;
            case "appealManager":
                document.title = "考勤异常";
                appealManager.render(d).then(() => {
                    cb();
                });
                break;
            default:
                document.title = "考勤";
                mobile.render(d).then(() => {
                    cb();
                });
        }
    } else {
        const defaultOpenMenu = (component.status && component.status.navAction) ? component.status.navAction : "";
        index.render(d).then((a) => {
            a.module.startOpenMenu(defaultOpenMenu);
            cb();
        });
    }
}).then((c) => {
    c.render();
});
