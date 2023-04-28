import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { myAction } from "../../utils/actions";
import template from './template.html';
import style from './style.scope.css';
 

export default content({
    template,
    style,
    autoUpdate: true,
    bind(){
        return {
            lp,
            year: 2023,
            month: 3
        };
    },
    afterRender() {
        this.loadMyStatistic();
    },
    async loadMyStatistic() {
        // 获取当前日期
        const today = new Date();
        // 获取当前月份
        const currentMonth = today.getMonth();
        // 获取当前年份
        const currentYear = today.getFullYear();
        // 获取当月的天数
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
        const startDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-01`;
        const endDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${daysInMonth}`;
        const reqBody = {
            startDate: startDate,
            endDate: endDate,
        };
        const statistic = (await myAction("statistic", reqBody));
        if (statistic) {
            this.bind.statistic = statistic;
        }
    },
    statisticItemClass(number) {
        if (number && number > 0) {
            return "statistic-item-child";
        }
        return "statistic-item-child zero"
    }
     
});
