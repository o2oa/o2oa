import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { convertTo2DArray, formatDate, hideLoading, showLoading } from "../../../utils/common";
import { leaveManagerAction } from "../../../utils/actions";
import template from "./template.html";
import style from "./style.scope.css";

const SOLAR_TERM_NAMES = [
    "小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
    "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
    "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
    "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
];
const SOLAR_TERM_INFO = [
    0, 21208, 42467, 63836, 85337, 107014,
    128867, 150921, 173149, 195551, 218072, 240693,
    263343, 285989, 308563, 331033, 353350, 375494,
    397447, 419210, 440795, 462224, 483532, 504758
];
const FIXED_FESTIVAL_MAP = {
    "01-01": "元旦",
    "02-14": "情人节",
    "03-08": "妇女节",
    "03-12": "植树节",
    "05-01": "劳动节",
    "05-04": "青年节",
    "05-17": "电信日",
    "05-20": "520",
    "06-01": "儿童节",
    "09-10": "教师节",
    "10-01": "国庆节",
    "12-25": "圣诞节"
};

let lunarFormatter = null;

export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        const today = new Date();
        return {
            lp,
            currentYear: today.getFullYear(),
            currentMonth: today.getMonth() + 1,
            currentMonthText: "",
            currentTitle: "",
            todayString: formatDate(today),
            yearList: [],
            monthList: [],
            yearSelectorOpen: false,
            monthSelectorOpen: false,
            weekList: ["一", "二", "三", "四", "五", "六", "日"],
            calendarRows: [],
            workdayMap: {},
            offdayMap: {},
        };
    },
    beforeRender() {
        this.bind.yearList = this.buildYearList(this.bind.currentYear);
        this.bind.monthList = this.buildMonthList();
        this.buildCalendarRows();
    },
    afterRender() {
        this.loadHolidayData(this.bind.currentYear);
    },
    clickBackTypeList() {
        this.$parent.clickBackTypeList();
    },
    clickPreYear() {
        this.closeSelector();
        this.changeDate(this.bind.currentYear - 1, this.bind.currentMonth);
    },
    clickNextYear() {
        this.closeSelector();
        this.changeDate(this.bind.currentYear + 1, this.bind.currentMonth);
    },
    toggleYearSelector() {
        if (!this.bind.yearSelectorOpen) {
            this.bind.yearList = this.buildYearList(this.bind.currentYear);
        }
        this.bind.yearSelectorOpen = !this.bind.yearSelectorOpen;
        this.bind.monthSelectorOpen = false;
        if (this.bind.yearSelectorOpen) {
            this.scrollSelectorToCurrent("holidayYearDropdown", "leave-calendar-dropdown-item-current");
        }
    },
    chooseYearItem(year) {
        this.closeSelector();
        this.changeDate(Number(year), this.bind.currentMonth);
    },
    clickPreMonth() {
        this.closeSelector();
        let year = this.bind.currentYear;
        let month = this.bind.currentMonth - 1;
        if (month < 1) {
            month = 12;
            year -= 1;
        }
        this.changeDate(year, month);
    },
    clickNextMonth() {
        this.closeSelector();
        let year = this.bind.currentYear;
        let month = this.bind.currentMonth + 1;
        if (month > 12) {
            month = 1;
            year += 1;
        }
        this.changeDate(year, month);
    },
    toggleMonthSelector() {
         // 不会更新月份选择列表样式
        if (!this.bind.monthSelectorOpen) {
            this.bind.monthList = this.buildMonthList();
        }
        this.bind.monthSelectorOpen = !this.bind.monthSelectorOpen;
        this.bind.yearSelectorOpen = false;
        if (this.bind.monthSelectorOpen) {
            this.scrollSelectorToCurrent("holidayMonthDropdown", "leave-calendar-dropdown-item-current");
        }
    },
    chooseMonthItem(month) {
        this.closeSelector();
        this.changeDate(this.bind.currentYear, Number(month));
    },
    clickToday() {
        this.closeSelector();
        const today = new Date();
        this.bind.todayString = formatDate(today);
        this.changeDate(today.getFullYear(), today.getMonth() + 1);
    },
    changeDate(year, month) {
        if (month < 1 || month > 12) {
            return;
        }
        const oldYear = this.bind.currentYear;
        this.bind.currentYear = year;
        this.bind.currentMonth = month;
        this.bind.yearList = this.buildYearList(year);
        this.bind.currentMonthText = this.formatMonthText(month);
        if (oldYear !== year) {
            this.bind.workdayMap = {};
            this.bind.offdayMap = {};
            this.buildCalendarRows();
            this.loadHolidayData(year);
        } else {
            this.buildCalendarRows();
        }
    },
    async loadHolidayData(year) {
        if (this.loadingYear === year) {
            return;
        }
        this.loadingYear = year;
        try {
            await showLoading(this);
            const json = await leaveManagerAction("holidayListWithYear", year);
            const data = (json && json.data && (json.data.workdayList || json.data.offdayList)) ? json.data : (json || {});
            if (this.bind.currentYear !== year) {
                return;
            }
            this.bind.workdayMap = this.listToMap(data.workdayList);
            this.bind.offdayMap = this.listToMap(data.offdayList);
            this.buildCalendarRows();
        } catch (e) {
            console.error(e);
            o2.api.page.notice(lp.leaveManagerV2.calendar.loadError, "error");
        } finally {
            this.loadingYear = null;
            await hideLoading(this);
        }
    },
    listToMap(list) {
        const map = {};
        (list || []).forEach((item) => {
            if (item && item.dateString) {
                map[item.dateString] = item;
            }
        });
        return map;
    },
    buildYearList(currentYear) {
        const list = [];
        for (let i = currentYear - 5; i <= currentYear + 5; i++) {
            list.push(i);
        }
        return list;
    },
    buildMonthList() {
        const list = [];
        for (let i = 1; i <= 12; i++) {
            list.push({
                value: i,
                name: this.formatMonthText(i)
            });
        }
        return list;
    },
    formatMonthText(month) {
        return `${month > 9 ? month : `0${month}`}月`;
    },
    buildCalendarRows() {
        const year = this.bind.currentYear;
        const month = this.bind.currentMonth;
        const daysInMonth = new Date(year, month, 0).getDate();
        const firstDay = new Date(year, month - 1, 1).getDay();
        const startIndex = firstDay === 0 ? 6 : firstDay - 1;
        const dates = [];
        for (let i = 0; i < startIndex; i++) {
            dates.push({ empty: true });
        }
        for (let day = 1; day <= daysInMonth; day++) {
            dates.push(this.buildCalendarDay(year, month, day));
        }
        while (dates.length % 7 !== 0) {
            dates.push({ empty: true });
        }
        this.bind.calendarRows = convertTo2DArray(dates, 7);
        this.bind.currentTitle = `${year}年${month > 9 ? month : `0${month}`}月`;
        this.bind.currentMonthText = this.formatMonthText(month);
    },
    buildCalendarDay(year, month, day) {
        const date = new Date(year, month - 1, day);
        const dateString = formatDate(date);
        const workday = this.bind.workdayMap[dateString];
        const offday = this.bind.offdayMap[dateString];
        const isWeekend = date.getDay() === 0 || date.getDay() === 6;
        let type = "normal";
        let tagText = "";
        if (workday) {
            type = "workday";
            tagText = "班";
        } else if (offday) {
            type = "offday";
            tagText = "休";
        } else if (isWeekend) {
            type = "weekend";
            tagText = "末";
        }
        const label = this.buildDayLabel(date, workday, offday);
        return {
            empty: false,
            dateString,
            day,
            dayText: day > 9 ? `${day}` : `0${day}`,
            type,
            tagText,
            isToday: dateString === this.bind.todayString,
            showName: label.text,
            labelType: label.type,
        };
    },
    buildDayLabel(date, workday, offday) {
        if (offday && offday.name) {
            return { text: offday.name, type: "offday" };
        }
        if (workday && workday.name) {
            return { text: workday.name, type: "workday" };
        }
        // const festival = this.getFestivalName(date);
        // if (festival) {
        //     return { text: festival, type: "festival" };
        // }
        const solarTerm = this.getSolarTermName(date);
        if (solarTerm) {
            return { text: solarTerm, type: "solar" };
        }
        return { text: this.formatLunarDate(date), type: "lunar" };
    },
    getFestivalName(date) {
        const month = date.getMonth() + 1;
        const day = date.getDate();
        const md = `${month > 9 ? month : `0${month}`}-${day > 9 ? day : `0${day}`}`;
        if (FIXED_FESTIVAL_MAP[md]) {
            return FIXED_FESTIVAL_MAP[md];
        }
        if (month === 5 && date.getDay() === 0 && day > 7 && day <= 14) {
            return "母亲节";
        }
        if (month === 6 && date.getDay() === 0 && day > 14 && day <= 21) {
            return "父亲节";
        }
        return "";
    },
    getSolarTermName(date) {
        const year = date.getFullYear();
        const month = date.getMonth();
        const termIndex = month * 2;
        if (this.getSolarTermDay(year, termIndex) === date.getDate()) {
            return SOLAR_TERM_NAMES[termIndex];
        }
        if (this.getSolarTermDay(year, termIndex + 1) === date.getDate()) {
            return SOLAR_TERM_NAMES[termIndex + 1];
        }
        return "";
    },
    getSolarTermDay(year, index) {
        const baseTime = Date.UTC(1900, 0, 6, 2, 5);
        const time = 31556925974.7 * (year - 1900) + SOLAR_TERM_INFO[index] * 60000 + baseTime;
        return new Date(time).getUTCDate();
    },
    formatLunarDate(date) {
        try {
            if (!lunarFormatter) {
                lunarFormatter = new Intl.DateTimeFormat("zh-CN-u-ca-chinese", { month: "long", day: "numeric" });
            }
            const text = lunarFormatter.format(date);
            const matched = text.match(/^(.+?月)(\d+)日$/);
            if (matched) {
                const day = Number(matched[2]);
                return day === 1 ? matched[1] : this.formatLunarDay(day);
            }
            return text;
        } catch (e) {
            return "";
        }
    },
    formatLunarDay(day) {
        const names = ["", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"];
        if (day <= 10) {
            return `初${names[day]}`;
        }
        if (day < 20) {
            return `十${names[day - 10]}`;
        }
        if (day === 20) {
            return "二十";
        }
        if (day < 30) {
            return `廿${names[day - 20]}`;
        }
        return day === 30 ? "三十" : "";
    },
    calendarCellClass(day) {
        if (!day || day.empty) {
            return "leave-calendar-cell leave-calendar-cell-empty";
        }
        let className = `leave-calendar-cell leave-calendar-cell-${day.type}`;
        if (day.isToday) {
            className += " leave-calendar-cell-today";
        }
        return className;
    },
    calendarTagClass(day) {
        if (!day) {
            return "leave-calendar-tag";
        }
        return `leave-calendar-tag leave-calendar-tag-${day.type}`;
    },
    calendarLabelClass(day) {
        if (!day) {
            return "leave-calendar-label";
        }
        return `leave-calendar-label leave-calendar-label-${day.labelType}`;
    },
    yearItemClass(year) {
        let className = "leave-calendar-dropdown-item";
        if (year === this.bind.currentYear) {
            className += " leave-calendar-dropdown-item-current";
        }
        return className;
    },
    monthItemClass(month) {
        console.log("month", month, this.bind.currentMonth);
        let className = "leave-calendar-dropdown-item";
        if (month === this.bind.currentMonth) {
            className += " leave-calendar-dropdown-item-current";
        }
        return className;
    },
    closeSelector() {
        this.bind.yearSelectorOpen = false;
        this.bind.monthSelectorOpen = false;
    },
    scrollSelectorToCurrent(dropdownId, currentClassName) {
        setTimeout(() => {
            const dropdown = this.dom.querySelector(`#${dropdownId}`);
            if (!dropdown) {
                return;
            }
            const current = dropdown.querySelector(`.${currentClassName}`);
            if (current) {
                dropdown.scrollTop = current.offsetTop - dropdown.clientHeight / 2 + current.clientHeight / 2;
            }
        }, 0);
    },
});
