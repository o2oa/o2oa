import { loadComponent, component } from '@o2oa/component';
import index from './app/index';
import mobile from './mobile/main';
import checkIn from './mobile/checkIn';
import myRecord from './mobile/myRecord';
import appealManager from './mobile/appealManager';
import publicStatistic from './mobile/publicStatistic';


loadComponent('attendancev2', (d, cb) => {

    const status = component.status || {};
    const navAction = status.navAction || "";
    if (navAction === "detailStatisticManager") { // 统计页面
        document.title = "统计";
        publicStatistic.render(d, { bind: { person: status.person || "" } }).then(() => {
            cb();
        });
    } else if ((layout.mobile || o2.session.isMobile)) {
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
        index.render(d).then((a) => {
            a.module.startOpenMenu(navAction);
            cb();
        });
    }
}).then((c) => {
    c.render();
});
