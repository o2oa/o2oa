import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { convertTo2DArray, formatDate, hideLoading, isEmpty, showLoading } from "../../../utils/common";
import { leaveManagerAction } from "../../../utils/actions";
import oDatePicker from "../../../components/o-date-picker";
import template from "./template.html";
import style from "./style.scope.css";

const SOLAR_TERM_NAMES = lp.leaveManagerV2.calendar.solarTermNames || [];
const SOLAR_TERM_INFO = [
    0, 21208, 42467, 63836, 85337, 107014,
    128867, 150921, 173149, 195551, 218072, 240693,
    263343, 285989, 308563, 331033, 353350, 375494,
    397447, 419210, 440795, 462224, 483532, 504758
];
const FIXED_FESTIVAL_MAP = lp.leaveManagerV2.calendar.fixedFestivalMap || {};

let lunarFormatter = null;

export default content({
    template,
    style,
    components: { oDatePicker },
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
            holidayFormShow: false,
            holidayForm: {
                name: "",
                dateString: formatDate(today),
                offDay: true,
            },
            holidayImportShow: false,
            holidayImportForm: {
                overwrite: false,
                jsonText: "",
            },
            holidayImportExample: "",
            holidayImportResult: null,
            weekList: lp.leaveManagerV2.calendar.weekList || [],
            calendarRows: [],
            workdayMap: {},
            offdayMap: {},
        };
    },
    beforeRender() {
        this.bind.yearList = this.buildYearList(this.bind.currentYear);
        this.bind.monthList = this.buildMonthList();
        this.bind.holidayImportExample = this.buildHolidayImportExample(this.bind.currentYear);
        this.bind.holidayImportForm.jsonText = this.bind.holidayImportExample;
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
         // Keep month selector styles in sync.
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
    clickOpenHolidayForm() {
        this.closeSelector();
        this.bind.holidayForm = {
            name: "",
            dateString: this.defaultHolidayDateString(),
            offDay: true,
        };
        this.bind.holidayFormShow = true;
    },
    closeHolidayForm() {
        this.bind.holidayFormShow = false;
    },
    clickOpenHolidayImport() {
        this.closeSelector();
        this.bind.holidayImportForm = {
            overwrite: false,
            jsonText: this.buildHolidayImportExample(this.bind.currentYear),
        };
        this.bind.holidayImportExample = this.bind.holidayImportForm.jsonText;
        this.bind.holidayImportResult = null;
        this.bind.holidayImportShow = true;
    },
    closeHolidayImport() {
        this.bind.holidayImportShow = false;
    },
    clickChangeImportOverwrite() {
        this.bind.holidayImportForm.overwrite = !this.bind.holidayImportForm.overwrite;
    },
    async submitHolidayImport() {
        if (this.importLoading) {
            return;
        }
        const form = this.bind.holidayImportForm || {};
        const jsonText = (form.jsonText || "").trim();
        if (!jsonText) {
            o2.api.page.notice(lp.leaveManagerV2.calendar.importJsonEmpty, "error");
            return;
        }
        let payload = null;
        try {
            payload = JSON.parse(jsonText);
        } catch (e) {
            o2.api.page.notice(lp.leaveManagerV2.calendar.importJsonError, "error");
            return;
        }
        if (!this.checkHolidayImportPayload(payload)) {
            o2.api.page.notice(lp.leaveManagerV2.calendar.importDataEmpty, "error");
            return;
        }
        if (Array.isArray(payload)) {
            payload = {
                overwrite: form.overwrite === true,
                days: payload,
            };
        } else {
            payload.overwrite = form.overwrite === true;
        }
        this.importLoading = true;
        try {
            await showLoading(this);
            const result = await leaveManagerAction("holidayImport", payload);
            this.bind.holidayImportResult = result || {};
            o2.api.page.notice(this.formatHolidayImportResult(result), "success");
            await this.refreshByImportPayload(payload);
        } catch (e) {
            console.error(e);
            o2.api.page.notice(lp.leaveManagerV2.calendar.importFail, "error");
        } finally {
            this.importLoading = false;
            await hideLoading(this);
        }
    },
    clickChangeHolidayType(offDay) {
        this.bind.holidayForm.offDay = offDay;
    },
    async submitHolidayForm() {
        if (this.submitLoading) {
            return;
        }
        const form = this.bind.holidayForm || {};
        if (isEmpty(form.name)) {
            o2.api.page.notice(lp.leaveManagerV2.calendar.nameEmptyPlaceholder, "error");
            return;
        }
        if (!this.isDateString(form.dateString)) {
            o2.api.page.notice(lp.leaveManagerV2.calendar.dateError, "error");
            return;
        }
        this.submitLoading = true;
        let savedDateString = "";
        try {
            await showLoading(this);
            await leaveManagerAction("holidayPost", {
                name: form.name,
                dateString: form.dateString,
                offDay: form.offDay === true,
            });
            savedDateString = form.dateString;
            o2.api.page.notice(lp.saveSuccess, "success");
            this.bind.holidayFormShow = false;
        } finally {
            this.submitLoading = false;
            await hideLoading(this);
        }
        if (savedDateString) {
            await this.refreshByDateString(savedDateString);
        }
    },
    clickDeleteHoliday(holiday) {
        if (!holiday || holiday.source !== "API") {
            return;
        }
        const _self = this;
        const c = `${lp.leaveManagerV2.calendar.deleteConfirm}${holiday.name || holiday.dateString}`;
        o2.api.page.confirm(
            "warn",
            lp.alert,
            c,
            300,
            100,
            function () {
                _self.deleteHoliday(holiday);
                this.close();
            },
            function () {
                this.close();
            }
        );
    },
    async deleteHoliday(holiday) {
        if (this.deleteLoading || !holiday || !holiday.id) {
            return;
        }
        this.deleteLoading = true;
        let deletedDateString = "";
        try {
            await showLoading(this);
            await leaveManagerAction("holidayDelete", holiday.id);
            deletedDateString = holiday.dateString;
        } finally {
            this.deleteLoading = false;
            await hideLoading(this);
        }
        if (deletedDateString) {
            await this.refreshByDateString(deletedDateString);
        }
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
    buildHolidayImportExample(year) {
        return JSON.stringify({
            year,
            overwrite: false,
            days: [
                {
                    name: lp.leaveManagerV2.calendar.importExampleNewYear,
                    date: `${year}-01-01`,
                    isOffDay: true
                },
                {
                    name: lp.leaveManagerV2.calendar.importExampleSpringFestivalWorkday,
                    dateString: `${year}-02-15`,
                    offDay: false
                }
            ]
        }, null, 2);
    },
    checkHolidayImportPayload(payload) {
        if (Array.isArray(payload)) {
            return payload.length > 0;
        }
        if (!payload || typeof payload !== "object") {
            return false;
        }
        return this.getHolidayImportItems(payload).length > 0 || !!(payload.date || payload.dateString);
    },
    getHolidayImportItems(payload) {
        if (!payload || Array.isArray(payload)) {
            return Array.isArray(payload) ? payload : [];
        }
        if (payload.date || payload.dateString) {
            return [payload];
        }
        return payload.days || payload.holidayList || payload.holidays || [];
    },
    formatHolidayImportResult(result) {
        const data = result || {};
        const text = lp.leaveManagerV2.calendar.importResult;
        return text
            .replace("{total}", data.total || 0)
            .replace("{inserted}", data.inserted || 0)
            .replace("{updated}", data.updated || 0)
            .replace("{skipped}", data.skipped || 0)
            .replace("{errors}", data.errors || 0);
    },
    async refreshByImportPayload(payload) {
        const year = this.getHolidayImportTargetYear(payload);
        if (year) {
            if (year !== this.bind.currentYear) {
                this.changeDate(year, this.bind.currentMonth);
                return;
            }
            await this.loadHolidayData(year);
            return;
        }
        await this.loadHolidayData(this.bind.currentYear);
    },
    getHolidayImportTargetYear(payload) {
        if (payload && !Array.isArray(payload) && payload.year) {
            return Number(payload.year);
        }
        const items = this.getHolidayImportItems(payload);
        for (let i = 0; i < items.length; i++) {
            const dateString = items[i] && (items[i].dateString || items[i].date);
            const date = this.parseDateString(dateString);
            if (date) {
                return date.getFullYear();
            }
        }
        return null;
    },
    defaultHolidayDateString() {
        const today = new Date();
        if (today.getFullYear() === this.bind.currentYear && today.getMonth() + 1 === this.bind.currentMonth) {
            return formatDate(today);
        }
        return `${this.bind.currentYear}-${this.bind.currentMonth > 9 ? this.bind.currentMonth : `0${this.bind.currentMonth}`}-01`;
    },
    async refreshByDateString(dateString) {
        const date = this.parseDateString(dateString);
        if (!date) {
            await this.loadHolidayData(this.bind.currentYear);
            return;
        }
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        if (year !== this.bind.currentYear) {
            this.changeDate(year, month);
        } else {
            this.bind.currentMonth = month;
            this.buildCalendarRows();
            await this.loadHolidayData(year);
        }
    },
    isDateString(value) {
        return !!this.parseDateString(value);
    },
    parseDateString(value) {
        if (!/^\d{4}-\d{2}-\d{2}$/.test(value || "")) {
            return null;
        }
        const parts = value.split("-").map((item) => Number(item));
        const date = new Date(parts[0], parts[1] - 1, parts[2]);
        if (date.getFullYear() !== parts[0] || date.getMonth() !== parts[1] - 1 || date.getDate() !== parts[2]) {
            return null;
        }
        return date;
    },
    formatMonthText(month) {
        const text = lp.leaveManagerV2.calendar.monthText || "{month}";
        return text.replace("{month}", month > 9 ? month : `0${month}`);
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
        this.bind.currentTitle = (lp.leaveManagerV2.calendar.titleText || "{year}-{month}")
            .replace("{year}", year)
            .replace("{month}", month > 9 ? month : `0${month}`);
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
            tagText = lp.leaveManagerV2.calendar.tagWorkday;
        } else if (offday) {
            type = "offday";
            tagText = lp.leaveManagerV2.calendar.tagOffday;
        } else if (isWeekend) {
            type = "weekend";
            tagText = lp.leaveManagerV2.calendar.tagWeekend;
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
            holiday: workday || offday || null,
            canDelete: this.canDeleteHoliday(workday || offday),
        };
    },
    canDeleteHoliday(holiday) {
        return !!(holiday && holiday.source === "API");
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
            return lp.leaveManagerV2.calendar.motherDay;
        }
        if (month === 6 && date.getDay() === 0 && day > 14 && day <= 21) {
            return lp.leaveManagerV2.calendar.fatherDay;
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
            const matched = text.match(new RegExp(lp.leaveManagerV2.calendar.lunarMonthPattern));
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
        const names = lp.leaveManagerV2.calendar.lunarNumberNames || [];
        if (day <= 10) {
            return `${lp.leaveManagerV2.calendar.lunarDayPrefix}${names[day]}`;
        }
        if (day < 20) {
            return `${names[10]}${names[day - 10]}`;
        }
        if (day === 20) {
            return lp.leaveManagerV2.calendar.lunarTwenty;
        }
        if (day < 30) {
            return `${lp.leaveManagerV2.calendar.lunarTwentyPrefix}${names[day - 20]}`;
        }
        return day === 30 ? lp.leaveManagerV2.calendar.lunarThirty : "";
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
