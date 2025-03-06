import {
    dom, exec, hyphenate, typeOf, defer,
    cloneDate, lastTimeOfDate, lastDayOfMonth, clearTime, formatDate, weekNumberOfDate, increment
} from '@o2oa/util';
import html from './template/calendar.html?raw';
import css from './template/calendar.scope.css?inline';
import {OOComponent} from '../base';

export default class OOCalendar extends OOComponent {

    #datetimeRange2;
    #hourRange;
    #dateRange;
    #datetimeRange;
    #dateOnlyRange;
    #currentDate;
    #selectedYear;
    #selectedMonth;
    #selectedDate;
    #selectedHour;
    #selectedMinute;
    #selectedSecond;
    #yearEventSetted;
    #monthEventSetted;
    #dateEventSetted;
    #hourEventSetted;
    #minuteEventSetted;
    #secondEventSetted;
    #weekEventSetted;
    #selectedWeekNumber;
    #enableWeeksFun;
    #enableYearsFun;
    #enableMonthsFun;
    #enableDatesFun;
    #enableHoursFun;
    #enableMinutesFun;
    #enableSecondsFun;
    #oldValue;
    #sourceDatetimeRange;
    #sourceDateRange;
    #sourceTimeRange;
    #notFixHour;
    #notFixMinute;
    #notFixSecond;

    static prop = {
        view: 'datetime', //datetime date month year week time

        baseDate: null,

        yearOnly: false,
        monthOnly: false,
        dateOnly: false,
        weekOnly: false,
        timeOnly: false,

        secondEnable: true,

        cleanEnable: false,
        todayEnable: true,

        datetimeRange: '',
        dateRange: '',
        timeRange: '',

        value: '',

        format: '',

        weekBegin: 1, //0表示周日，1表示周一

        months: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
        daysTitles: ['日', '一', '二', '三', '四', '五', '六'],
        clean: '清除',
        year: '年',
        month: '月',
        date: '日',
        hour: '时',
        minute: '分',
        second: '秒',
        week: '周'

    };

    static get observedAttributes() {
        return Object.keys(this.prop).map((att) => {
            return hyphenate(att);
        });
    }

    static parseDate(date, isClone) {
        if (!date) return null;
        return typeOf(date) === 'date' ? (isClone ? new Date(date.getTime()) : date) : new Date(date);
    }

    //防抖 fun:方法，wait时间
    // static antiShake(fun, wait) {
    //     let time = null;
    //     return args => {
    //         if (time) {
    //             clearTimeout(time);
    //         }
    //         time = setTimeout(fun, wait);
    //     };
    // }

    _elements = {
        content: null,
        top: null,
        title: null,
        prev: null,
        next: null,
        middle: null,
        bottom: null,
        left: null,
        yearContent: null,
        monthContent: null,
        dateContent: null,
        right: null,
        time: null,
        hourContent: null,
        minuteContent: null,
        secondContent: null,
        line: null,
        clean: null,
        cleanTime: null,
        today: null,
    };
    #eventListeners = {};
    _setPropMap = {
        skin: () => {
            this._useSkin(this._props.skin);
        },
        view: () => {
            this.currentView = this._props.view;
            this.showView();
        },
        value: () => {
            if (this._props.value !== this.#oldValue) {
                this.#useValue();
                this.showView();
                this.#checkTimeSelected();
            }
        },
        baseDate: () => {
            this.#useBaseDate();
            this.showView();
        },
        yearOnly: () => {
            this._changeType();
        },
        monthOnly: () => {
            this._changeType();
        },
        dateOnly: () => {
            this._changeType();
        },
        weekOnly: () => {
            this._changeType();
        },
        timeOnly: () => {
            this._changeType();
        },
        secondEnable: () => {
            this.#checkClass();
            this.setContentEvent();
        },
        cleanEnable: () => {
            this.#checkClass();
        },
        todayEnable: () => {
            this.#checkClass();
        },
        datetimeRange: () => {
            this.setRange();
        },
        dateRange: () => {
            this.setRange();
        },
        timeRange: () => {
            this.setRange();
        },
        weekBegin: () => {
            if (this.currentView === 'date') this.showView();
        },
        $default: (key) => {
            //if (key==='value') this.value = this._props[key];
            //dom.toggleAttr(this._elements.button, key, this._props[key]);
        }
    };

    constructor() {
        super();
        this._initialize('oo-calendar', OOCalendar, html, css);
    }

    _setProps(key, oldValue) {
        const value = this._props[key];
        if (oldValue === value) return;
        this._setPropMap[key] ? this._setPropMap[key](oldValue, value) : this._setPropMap['$default'](key, oldValue, value);
    }

    _render() {
        this.currentView = this.#getDefaultView();

        this.setRange();

        this.#checkClass();

        this.#useBaseDate();

        this.#useValue();

        this.showView();

        this.#checkTimeSelected();

        if (!this._props.yearOnly || !this._props.monthOnly || !this._props.dateOnly || !this._props.weekOnly) {
            this.#checkHourContent();
            this.#checkMinuteContent();
            this.#checkSecondContent();
        }
    }

    #getDefaultView() {
        return (this._props.yearOnly && 'year') ||
            (this._props.monthOnly && 'month') ||
            (this._props.dateOnly && 'date') ||
            (this._props.weekOnly && 'week') ||
            (this._props.timeOnly && 'time') ||
            this._props.view;
    };

    // #useValue(){
    //     if( !this._props.value )return;
    //     let value = this._props.value, arr;
    //     if( typeOf( value ) === "date" ){
    //         value = formatDate(value, this.getDefaultFormat());
    //     }
    //     if( this._props.yearOnly && this.#isEnableYear(value) ){
    //         this.#selectedYear = parseInt(value);
    //     }else if( this._props.monthOnly && this.#isEnableMonth(value) ){
    //         arr = value.split("-");
    //         this.#selectedYear = parseInt(arr[0]);
    //         this.#selectedMonth = parseInt(arr[1])-1;
    //     }else if( this._props.dateOnly && this.#isEnableDate(value) ) {
    //         arr = value.split("-");
    //         this.#selectedYear = parseInt(arr[0]);
    //         this.#selectedMonth = parseInt(arr[1]) - 1;
    //         this.#selectedDate = parseInt(arr[2]);
    //     }else if( this._props.weekOnly ){
    //         arr = value.split(" ");
    //         this.#selectedYear = parseInt(arr[0]);
    //         this.#selectedWeekNumber = parseInt(arr[1]);
    //     }else if( this._props.timeOnly ){
    //         arr = value.split(":");
    //         if( this.#isEnableHour(arr[0]) )this.#selectedHour = parseInt(arr[0]);
    //         if( this.#isEnableMinute(arr[1]) )this.#selectedMinute = parseInt(arr[1]);
    //         if( arr[2] && this.#isEnableSecond(arr[2]) )this.#selectedSecond = parseInt(arr[2]);
    //     }else{
    //         const date = OOCalendar.parseDate( value, true );
    //         if( this.#isEnableDate(date) ){
    //             this.#selectedYear = date.getFullYear();
    //             this.#selectedMonth = date.getMonth();
    //             this.#selectedDate = date.getDate();
    //         }
    //         if( this.#isEnableHour(date.getHours()) )this.#selectedHour = date.getHours();
    //         if( this.#isEnableMinute(date.getMinutes()) )this.#selectedMinute = date.getMinutes();
    //         if( this.#isEnableSecond(date.getSeconds()) )this.#selectedSecond = date.getSeconds();
    //     }
    //     if( typeOf(this.#selectedYear) !== 'null' ) this.#currentDate.setFullYear(this.#selectedYear);
    //     if( typeOf(this.#selectedMonth) !== 'null' )this.#currentDate.setMonth(this.#selectedMonth);
    //     if( typeOf(this.#selectedDate) !== 'null' )this.#currentDate.setDate(this.#selectedDate);
    //     if( typeOf(this.#selectedHour) !== 'null' ) this.#currentDate.setHours(this.#selectedHour);
    //     if( typeOf(this.#selectedMinute) !== 'null' )this.#currentDate.setMinutes(this.#selectedMinute);
    //     if( typeOf(this.#selectedSecond) !== 'null' )this.#currentDate.setSeconds(this.#selectedSecond);
    // }


    #useValueSet = [
        value => (this._props.yearOnly && this.#isEnableYear(value)) && (this.#selectedYear = parseInt(value) || true),
        value => (this._props.monthOnly && this.#isEnableMonth(value)) && ((() => {
            const [year, month] = value.split('-');
            this.#selectedYear = parseInt(year);
            this.#selectedMonth = parseInt(month) - 1;
        })() || true),
        value => (this._props.dateOnly && this.#isEnableDate(value)) && ((() => {
            const [year, month, date] = value.split('-');
            this.#selectedYear = parseInt(year);
            this.#selectedMonth = parseInt(month) - 1;
            this.#selectedDate = parseInt(date);
        })() || true),
        value => this._props.weekOnly && ((() => {
            const [year, weekNumber] = value.split(' ');
            this.#selectedYear = parseInt(year);
            this.#selectedWeekNumber = parseInt(weekNumber);
        })() || true),
        value => this._props.timeOnly && ((() => {
            const [hour, minute, second] = value.split(':');
            if (this.#isEnableHour(hour)) this.#selectedHour = parseInt(hour);
            if (this.#isEnableMinute(minute)) this.#selectedMinute = parseInt(minute);
            if (second && this.#isEnableSecond(second)) this.#selectedSecond = parseInt(second);
        })() || true),
        value => {
            const date = OOCalendar.parseDate(value, true);
            if (this.#isEnableDate(date)) {
                this.#selectedYear = date.getFullYear();
                this.#selectedMonth = date.getMonth();
                this.#selectedDate = date.getDate();
            }
            if (this.#isEnableHour(date.getHours())) this.#selectedHour = date.getHours();
            if (this.#isEnableMinute(date.getMinutes())) this.#selectedMinute = date.getMinutes();
            if (this.#isEnableSecond(date.getSeconds())) this.#selectedSecond = date.getSeconds();
        }
    ];

    #useValue() {
        if (!this._props.value) return;
        const value = typeOf(this._props.value) === 'date' ? formatDate(this._props.value, this.getDefaultFormat()) : this._props.value;

        this.#useValueSet.some(fun => fun(value));

        if (!isNaN(this.#selectedYear)) this.#currentDate.setFullYear(this.#selectedYear);
        if (!isNaN(this.#selectedMonth)) this.#currentDate.setMonth(this.#selectedMonth);
        if (!isNaN(this.#selectedDate)) this.#currentDate.setDate(this.#selectedDate);
        if (!isNaN(this.#selectedHour)) this.#currentDate.setHours(this.#selectedHour);
        if (!isNaN(this.#selectedMinute)) this.#currentDate.setMinutes(this.#selectedMinute);
        if (!isNaN(this.#selectedSecond)) this.#currentDate.setSeconds(this.#selectedSecond);
    }


    #useBaseDate() {
        // if( this._props.baseDate ){
        //     const selectedDate = OOCalendar.parseDate( this._props.baseDate, true );
        //     if( this.#isEnableDate(selectedDate) )this.#selectedDate = selectedDate;
        //     if( this.#isEnableHour(selectedDate.getHours()) )this.#selectedHour = selectedDate.getHours();
        //     if( this.#isEnableMinute(selectedDate.getMinutes()) )this.#selectedMinute = selectedDate.getMinutes();
        //     if( this.#isEnableSecond(selectedDate.getSeconds()) )this.#selectedSecond = selectedDate.getSeconds();
        // }else{
        //     this.#selectedDate = null;
        //     this.#selectedHour = null;
        //     this.#selectedMinute = null;
        //     this.#selectedSecond = null;
        // }

        this.#currentDate = this.#getDefaultCurrentDate();
    }

    #getDefaultCurrentDate() {
        if (this._props.baseDate && this.#isEnableDate(this._props.baseDate)) {
            return OOCalendar.parseDate(this._props.baseDate, true);
        } else if (this.#isEnableDate(new Date())) {
            return new Date();
        } else if (this._props?.datetimeRange.length) {
            return cloneDate(this.#datetimeRange[0][0] || this.#datetimeRange[0][1]);
        } else if (this._props?.dateRange.length) {
            return cloneDate(this.#dateRange[0][0] || this.#dateRange[0][1]);
        }
    }

    _changeType() {
        this.currentView = this.#getDefaultView();
        this.#checkClass();
        this.setContentEvent();
        this.showView();
    }

    _setEvent() {
        this._elements.title.addEventListener('click', () => {
            this.changeView();
        });
        this._elements.prev.addEventListener('click', () => {
            this.gotoPrev();
        });
        this._elements.next.addEventListener('click', () => {
            this.gotoNext();
        });
        this._elements.today.addEventListener('click', () => {
            this.gotoToday();
        });
        this._elements.clean.addEventListener('click', () => {
            this.clear();
        });
        this._elements.cleanTime.addEventListener('click', () => {
            this.clear();
        });

        this.setContentEvent();
    }

    setContentEvent() {
        if (!this._props.timeOnly) this.setYearEvent();

        if (!this._props.timeOnly && !this._props.yearOnly) this.setMonthEvent();

        if (!this._props.timeOnly && !this._props.yearOnly && !this._props.monthOnly) {
            this._props.weekOnly ? this.setWeekEvent() : this.setDateEvent();
        }

        if (!this._props.yearOnly && !this._props.monthOnly && !this._props.dateOnly && !this._props.weekOnly) {
            this.setHourEvent();
            this.setMinuteEvent();
            if (this._props.secondEnable) this.setSecondEvent();
        }
    }

    getDate() {
        const date = cloneDate(this.#currentDate);
        if (typeOf(this.#selectedYear) !== 'null') date.setFullYear(this.#selectedYear);
        if (typeOf(this.#selectedMonth) !== 'null') date.setMonth(this.#selectedMonth);
        if (typeOf(this.#selectedDate) !== 'null') date.setDate(this.#selectedDate);
        date.setHours(this.#selectedHour || 0);
        date.setMinutes(this.#selectedMinute || 0);
        date.setSeconds(this.#selectedSecond || 0);
        return date;
    }

    getSelectedDate() {
        if (typeOf(this.#selectedYear) !== 'null' && typeOf(this.#selectedMonth) !== 'null' && typeOf(this.#selectedDate) !== 'null') {
            return new Date(this.#selectedYear, this.#selectedMonth, this.#selectedDate);
        }
        return null;
    }

    selectDate(td) {
        const selectedTd = td.parentNode.parentNode.querySelector('td.selected');
        if (selectedTd) dom.removeClass(selectedTd, 'selected');
        dom.addClass(td, 'selected');

        this.dispatchChangeEvent();
    }

    selectTime(li) {
        const selectedLi = li.parentNode.querySelector('li.selected');
        if (selectedLi) dom.removeClass(selectedLi, 'selected');
        dom.addClass(li, 'selected');

        this.dispatchChangeEvent();
    }

    getDefaultFormat() {
        return (this._props.yearOnly && 'YYYY') ||
            (this._props.monthOnly && 'YYYY-MM') ||
            (this._props.dateOnly && 'YYYY-MM-DD') ||
            (this._props.weekOnly && 'YYYY ww') ||
            (this._props.timeOnly && (this._props.secondEnable ? 'HH:mm:ss' : 'HH:mm')) ||
            'YYYY-MM-DD HH:mm:ss';
    }

    dispatchChangeEvent() {
        const date = this.getDate();

        const format = this._props.format || this.getDefaultFormat();

        this.#oldValue = formatDate(date, format);
        this.dispatchEvent(new CustomEvent('change', {
            detail: {
                date: date,
                value: this.#oldValue
            }
        }));
    }

    dispatchWeekEvent(weekNumber, start, end) {
        const format = this._props.format || this.getDefaultFormat();
        const startDate = OOCalendar.parseDate(start);
        const endDate = OOCalendar.parseDate(end);
        this.#oldValue = formatDate(startDate, format);
        this.dispatchEvent(new CustomEvent('change', {
            detail: {
                date: startDate,
                value: this.#oldValue,
                startDate,
                endDate,
                weekNumber
            }
        }));
    }

    clear() {
        this.#selectedYear = null;
        this.#selectedMonth = null;
        this.#selectedDate = null;
        this.#selectedHour = null;
        this.#selectedMinute = null;
        this.#selectedSecond = null;
        var els = this._elements.content.querySelectorAll('.selected');
        els.forEach((el) => { dom.removeClass(el, 'selected'); });
        this.dispatchEvent(new CustomEvent('change', {
            detail: {
                date: null,
                value: ''
            }
        }));
    }

    gotoToday() {
        const today = new Date();
        this.#currentDate.setFullYear(today.getFullYear());
        this.#currentDate.setMonth(today.getMonth());
        this.#currentDate.setDate(today.getDate());
        this.showView();
    }

    gotoPrev() {
        switch (this.currentView) {
            case 'year':
                increment(this.#currentDate, 'year', -16);
                this.changeViewToYear();
                break;
            case 'month':
                increment(this.#currentDate, 'year', -1);
                this.changeViewToMonth();
                break;
            case 'date':
                increment(this.#currentDate, 'month', -1);
                this.changeViewToDate();
                break;
            case 'week':
                increment(this.#currentDate, 'month', -1);
                this.changeViewToWeek();
                break;
        }
    }

    gotoNext() {
        switch (this.currentView) {
            case 'year':
                increment(this.#currentDate, 'year', 16);
                this.changeViewToYear();
                break;
            case 'month':
                increment(this.#currentDate, 'year', 1);
                this.changeViewToMonth();
                break;
            case 'date':
                increment(this.#currentDate, 'month', 1);
                this.changeViewToDate();
                break;
            case 'week':
                increment(this.#currentDate, 'month', 1);
                this.changeViewToWeek();
                break;
        }
    }

    setTitle(yearTitle) {
        let options;
        switch (this.currentView) {
            case 'year':
                if (yearTitle) this._elements.title.textContent = yearTitle;
                return;
            case 'month':
                options = {year: 'numeric'};
                break;
            case 'date':
            case 'week':
                options = {year: 'numeric', month: 'long'};
                break;
            default:
                options = {year: 'numeric', month: 'long', day: 'numeric'};
        }
        this._elements.title.textContent = new Intl.DateTimeFormat('zh-CN', options).format(this.#currentDate);
    }

    showView() {
        switch (this.currentView) {
            case 'year':
                this.changeViewToYear();
                break;
            case 'month':
                this.changeViewToMonth();
                break;
            case 'date':
                this.setWeekTitle();
                this.changeViewToDate();
                break;
            case 'week':
                this.setWeekTitle();
                this.changeViewToWeek();
                break;
        }
    }

    changeView() {
        switch (this.currentView) {
            case 'date' :
            case 'week' :
                this.changeViewToMonth();
                break;
            case 'month' :
                this.changeViewToYear();
                break;
            case 'year' :
                if (this._props.yearOnly) {
                    break;
                } else if (this._props.monthOnly) {
                    this.changeViewToMonth();
                } else if (this._props.weekOnly) {
                    this.changeViewToWeek();
                } else {
                    this.changeViewToDate();
                }
                break;
            // case "time" :
            //     this.changeViewToDate(); break;
            // default :
        }
    }

    #checkClass() {
        dom.checkClass(this._elements.left, 'hide', this._props.timeOnly);
        dom.checkClass(this._elements.yearContent, 'hide', this.currentView !== 'year');
        dom.checkClass(this._elements.monthContent, 'hide', this.currentView !== 'month');
        dom.checkClass(this._elements.dateContent, 'hide', this.currentView !== 'date');
        dom.checkClass(this._elements.right, 'hide', this._props.yearOnly || this._props.monthOnly || this._props.dateOnly || this._props.weekOnly);
        dom.checkClass(this._elements.time, 'timeWithClean', this._props.cleanEnable && this._props.timeOnly);
        dom.checkClass(this._elements.secondContent, 'hide', !this._props.secondEnable);
        dom.checkClass(this._elements.line, 'hide', this._props.yearOnly || this._props.monthOnly || this._props.dateOnly || this._props.weekOnly || this._props.timeOnly);
        dom.checkClass(this._elements.bottom, 'hide', !this._props.cleanEnable);
        dom.checkClass(this._elements.cleanTime, 'hide', !this._props.cleanEnable || !this._props.timeOnly);
        dom.checkClass(this._elements.today, 'hide', !this._props.todayEnable || !this.#isEnableDate(new Date()));
    }

    #checkTimeSelected() {
        this.#checkHourSelected();
        this.#checkMinuteSelected();
        this.#checkSecondContent();
    }

    #checkViewClass(leftView) {
        dom.checkClass(this._elements.yearContent, 'hide', leftView !== 'year');
        dom.checkClass(this._elements.monthContent, 'hide', leftView !== 'month');
        dom.checkClass(this._elements.dateContent, 'hide', !['date', 'week'].includes(leftView));
    }

    changeViewToYear(year) {
        this.currentView = 'year';
        this.#checkViewClass('year');
        this.setYearContent(year);
    }

    setYearContent() {
        this.setYearEvent();
        const tds = this._elements.yearContent.querySelectorAll('td');

        const todayYear = new Date().getFullYear();
        const selectedYear = this.#selectedYear;
        const year = this.#currentDate.getFullYear();

        let date = new Date(year, 1, 1);
        date.setFullYear(date.getFullYear() - 2);
        const beginYear = date.getFullYear();
        date.setFullYear(date.getFullYear() + tds.length - 1);
        const endYear = date.getFullYear();

        this.setTitle(beginYear + '-' + endYear);

        tds.forEach((td, idx) => {
            const y = beginYear + idx;
            td.querySelector('span').textContent = y;
            td.dataset.year = y.toString();

            dom.checkClass(td, 'selected', selectedYear === y);
            dom.checkClass(td, 'today', todayYear === y);

            if (this._props.enableYear) {
                dom.checkClass(td, 'disabled', !this.#isEnableYear(y));
            }
        });
    }

    setYearEvent() {
        if (this.#yearEventSetted) return;
        this._elements.yearContent.querySelectorAll('td').forEach((td) => {
            td.addEventListener('click', () => {
                let year = td.dataset.year;
                if (this.#isEnableYear(year)) {
                    this.#currentDate.setFullYear(year);
                    if (this._props.yearOnly) {
                        this.#selectedYear = parseInt(year);
                        this.selectDate(td);
                    } else {
                        this.changeViewToMonth();
                    }
                }
            });
        });
        this.#yearEventSetted = true;
    }

    changeViewToMonth() {
        this.currentView = 'month';
        this.setTitle();
        this.#checkViewClass('month');
        this.setMonthContent();
    }

    setMonthContent() {
        const todayYear = new Date().getFullYear();
        const todayMonth = new Date().getMonth();

        const selectedYear = this.#selectedYear;
        const selectedMonth = this.#selectedMonth;

        const year = this.#currentDate.getFullYear();

        const tds = this._elements.monthContent.querySelectorAll('td');

        tds.forEach((td, idx) => {
            td.querySelector('span').textContent = this._props.months[idx].substr(0, 2);
            td.dataset.year = year.toString();
            td.dataset.month = (idx + 1).toString();
            dom.checkClass(td, 'selected', selectedYear === year && selectedMonth === idx);
            dom.checkClass(td, 'today', todayYear === year && todayMonth === idx);

            if (this.#enableMonthsFun) {
                const month = td.dataset.month;
                dom.checkClass(td, 'disabled', !this.#isEnableMonth(year + '-' + month));
            }
        });
    }

    setMonthEvent() {
        if (this.#monthEventSetted) return;
        this._elements.monthContent.querySelectorAll('td').forEach((td) => {
            td.addEventListener('click', () => {
                let year = td.dataset.year, month = td.dataset.month;
                if (this.#isEnableMonth(year + '-' + month)) {
                    this.#currentDate.setFullYear(year);
                    this.#currentDate.setMonth(parseInt(month) - 1);
                    if (this._props.monthOnly) {
                        this.#selectedYear = parseInt(year);
                        this.#selectedMonth = parseInt(month) - 1;
                        this.selectDate(td);
                    } else if (this._props.weekOnly) {
                        this.changeViewToWeek(year, month);
                    } else {
                        this.changeViewToDate(year, month);
                    }
                }
            });
        });
        this.#monthEventSetted = true;
    }

    changeViewToDate() {
        this.currentView = 'date';
        this.setTitle();
        this.#checkViewClass('date');
        this.setDateContent();
    }

    setWeekTitle() {
        const ths = this._elements.dateContent.querySelectorAll('th');
        ths.forEach((th, i) => {
            if (i === 0) {
                dom.checkClass(th, 'hide', !this._props.weekOnly);
            } else {
                th.textContent = this._props.daysTitles[((i - 1) + this._props.weekBegin) % 7];
            }
        });
    }

    setDateContent() {
        const todayMillisecond = clearTime(new Date()).getTime();
        const selectedDate = this.getSelectedDate();
        const selectedMillisecond = selectedDate ? selectedDate.getTime() : null;

        const baseDate = clearTime(this.#currentDate, true);
        baseDate.setDate(1);

        const date = cloneDate(baseDate);
        const day = (7 + date.getDay() + 1 - this._props.weekBegin) % 7;
        date.setDate(date.getDate() - day);

        const tds = this._elements.dateContent.querySelectorAll('td');
        tds.forEach((td, i) => {
            if (i % 8 === 0) return;
            date.setDate(date.getDate() + 1);
            td.querySelector('span').textContent = date.getDate().toString();
            td.dataset.dateValue = formatDate(date, 'YYYY-MM-DD');
            dom.checkClass(td, 'selected', date.getTime() === selectedMillisecond);
            dom.checkClass(td, 'today', date.getTime() === todayMillisecond);
            dom.checkClass(td, 'other', date.getMonth() !== this.#currentDate.getMonth());
            if (this.#enableDatesFun) {
                dom.checkClass(td, 'disabled', !this.#isEnableDate(date));
            }
        });
    }

    setDateEvent() {
        if (this.#dateEventSetted) return;
        this._elements.dateContent.querySelectorAll('td').forEach((td, i) => {
            if (i % 8 === 0) return;
            td.addEventListener('click', () => {
                let dateValue = td.dataset.dateValue;
                if (this.#isEnableDate(dateValue)) {
                    const arr = dateValue.split('-');
                    this.#currentDate.setFullYear(arr[0]);
                    this.#currentDate.setMonth(parseInt(arr[1]) - 1);
                    this.#currentDate.setDate(arr[2]);

                    this.#selectedYear = parseInt(arr[0]);
                    this.#selectedMonth = parseInt(arr[1]) - 1;
                    this.#selectedDate = parseInt(arr[2]);

                    if (!this._props.dateOnly) {
                        this.#checkHourContent();
                        this.#checkMinuteContent();
                        this.#checkSecondContent();
                    }
                    this.selectDate(td);
                }
            });
        });
        this.#dateEventSetted = true;
    }

    changeViewToWeek() {
        this.currentView = 'week';
        this.setTitle();
        this.#checkViewClass('week');
        this.setWeekContent();
    }

    setWeekContent() {
        const todayMillisecond = clearTime(new Date()).getTime();

        const baseDate = clearTime(this.#currentDate, true);
        baseDate.setDate(1);

        const date = cloneDate(baseDate);
        const day = (7 + date.getDay() + 1 - this._props.weekBegin) % 7;
        date.setDate(date.getDate() - day);

        const tds = this._elements.dateContent.querySelectorAll('td');
        tds.forEach((td, i) => {
            if (i % 8 === 0) {
                const tmpDate = cloneDate(date);
                tmpDate.setDate(tmpDate.getDate() + 1);

                const year = tmpDate.getFullYear();
                const weekNumber = weekNumberOfDate(tmpDate, this._props.weekBegin);
                td.querySelector('span').textContent = weekNumber;

                td.parentNode.dataset.weekNumber = weekNumber;
                td.parentNode.dataset.year = year;

                dom.removeClass(td, 'hide');
                dom.checkClass(td.parentNode, 'week-row-selected', year === this.#selectedYear && weekNumber === this.#selectedWeekNumber);
            } else {
                date.setDate(date.getDate() + 1);
                td.querySelector('span').textContent = date.getDate().toString();
                td.dataset.dateValue = formatDate(date, 'YYYY-MM-DD');
                dom.checkClass(td, 'today', date.getTime() === todayMillisecond);
                dom.checkClass(td, 'other', date.getMonth() !== this.#currentDate.getMonth());
                if (this.#enableDatesFun) {
                    dom.checkClass(td, 'disabled', !this.#isEnableDate(date));
                }
            }
        });
    }

    setWeekEvent() {
        if (this.#weekEventSetted) return;
        this._elements.dateContent.querySelectorAll('tr').forEach((tr, i) => {
            tr.addEventListener('mouseenter', () => {
                if (this.#enableWeeksFun) {
                    const start = tr.querySelector('td:nth-child(2)').dataset.dateValue;
                    const end = tr.querySelector('td:last-child').dataset.dateValue;
                    if (this.#isEnableWeek(start, end)) {
                        dom.addClass(tr, 'week-row-over');
                    }
                } else {
                    dom.addClass(tr, 'week-row-over');
                }
            });
            tr.addEventListener('mouseleave', () => {
                dom.removeClass(tr, 'week-row-over');
            });
            tr.addEventListener('click', () => {
                const start = tr.querySelector('td:nth-child(2)').dataset.dateValue;
                const end = tr.querySelector('td:last-child').dataset.dateValue;
                if (this.#enableWeeksFun) {
                    if (this.#isEnableWeek(start, end)) {
                        this.selectWeek(tr, start, end);
                    }
                } else {
                    this.selectWeek(tr, start, end);
                }
            });
        });
        this.#weekEventSetted = true;
    }

    selectWeek(tr, start, end) {
        const selectedTr = tr.parentNode.querySelector('tr.week-row-selected');
        if (selectedTr) dom.removeClass(selectedTr, 'week-row-selected');
        dom.addClass(tr, 'week-row-selected');

        const [year, month, date] = start.split('-');
        this.#currentDate.setFullYear(year);
        this.#currentDate.setMonth(parseInt(month) - 1);
        this.#currentDate.setDate(date);

        this.#selectedYear = parseInt(year);
        this.#selectedMonth = parseInt(month) - 1;
        this.#selectedDate = parseInt(date);


        const weekNumber = tr.dataset.weekNumber;
        this.#selectedWeekNumber = parseInt(weekNumber);
        this.dispatchWeekEvent(weekNumber, start, end);
    }


    #checkHourContent() {
        this._elements.hourContent.querySelectorAll('li').forEach((li, i) => {
            const enable = this.#isEnableHour(i);
            dom.checkClass(li, 'disabled', !enable);
            if (!enable && this.#selectedHour === i) {
                this.#selectedHour = null;
                dom.removeClass(li, 'selected');
            }
        });
    }

    #checkHourSelected() {
        this._elements.hourContent.querySelectorAll('li').forEach((li, i) => {
            dom.checkClass(li, 'selected', this.#selectedHour === i);
            if (this.#selectedHour === i) setTimeout(() => {
                if (i !== 0) this.#notFixHour = true;
                // li.scrollIntoView({block: 'start'});
                li.scrollIntoView({block: 'nearest'});
            }, 100);
        });
    }

    setHourEvent() {
        if (this.#hourEventSetted) return;
        // const hourScroll = OOCalendar.antiShake(() => {
        //     if (!this.#notFixHour) this.#fixTimeNode(this._elements.hourContent);
        //     this.#notFixHour = false;
        // }, 100);
        const hourScroll = () => {
            if (!this.#notFixHour) this.#fixTimeNode(this._elements.hourContent);
            this.#notFixHour = false;
        };

        this._elements.hourContent.addEventListener('scroll', () => { defer(hourScroll, 100, this); });
        this._elements.hourContent.querySelectorAll('li').forEach((li, i) => {
            const hourString = i.toString().padStart(2, '0');
            li.dataset.hour = hourString;
            li.addEventListener('click', () => {
                if (this.#isEnableHour(hourString)) {
                    this.#currentDate.setHours(hourString);
                    this.#selectedHour = parseInt(hourString);
                    this.#checkMinuteContent();
                    this.#checkSecondContent();
                    this.selectTime(li);
                }
            });
        });
        this.#hourEventSetted = true;
    }

    #checkMinuteContent() {
        this._elements.minuteContent.querySelectorAll('li').forEach((li, i) => {
            const enable = this.#isEnableMinute(i);
            dom.checkClass(li, 'disabled', !enable);
            if (!enable && this.#selectedMinute === i) {
                this.#selectedMinute = null;
                dom.removeClass(li, 'selected');
            }
        });
    }

    #checkMinuteSelected() {
        this._elements.minuteContent.querySelectorAll('li').forEach((li, i) => {
            dom.checkClass(li, 'selected', this.#selectedMinute === i);
            if (this.#selectedMinute === i) setTimeout(() => {
                if (i !== 0) this.#notFixMinute = true;
                // li.scrollIntoView({block: 'start'});
                li.scrollIntoView({block: 'nearest'});
            }, 100);
        });
    }

    setMinuteEvent() {
        if (this.#minuteEventSetted) return;
        // const minuteScroll = OOCalendar.antiShake(() => {
        //     if (!this.#notFixMinute) this.#fixTimeNode(this._elements.minuteContent);
        //     this.#notFixMinute = false;
        // }, 100);
        const minuteScroll = () => {
            if (!this.#notFixMinute) this.#fixTimeNode(this._elements.minuteContent);
            this.#notFixMinute = false;
        };
        this._elements.minuteContent.addEventListener('scroll', () => { defer(minuteScroll, 100, this); });
        this._elements.minuteContent.querySelectorAll('li').forEach((li, i) => {
            const minuteString = i.toString().padStart(2, '0');
            li.dataset.minute = minuteString;
            li.addEventListener('click', () => {
                if (this.#isEnableMinute(minuteString)) {
                    this.#currentDate.setMinutes(minuteString);
                    this.#selectedMinute = parseInt(minuteString);
                    this.#checkSecondContent();
                    this.selectTime(li);
                }
            });
        });
        this.#minuteEventSetted = true;
    }


    #checkSecondContent() {
        if (!this._props.secondEnable) return;
        this._elements.secondContent.querySelectorAll('li').forEach((li, i) => {
            const enable = this.#isEnableSecond(i);
            dom.checkClass(li, 'disabled', !enable);
            if (!enable && this.#selectedSecond === i) {
                this.#selectedSecond = null;
                dom.removeClass(li, 'selected');
            }
        });
    }

    #checkSecondSelected() {
        if (!this._props.secondEnable) return;
        this._elements.secondContent.querySelectorAll('li').forEach((li, i) => {
            dom.checkClass(li, 'selected', this.#selectedSecond === i);
            if (this.#selectedSecond === i) setTimeout(() => {
                if (i !== 0) this.#notFixSecond = true;
                // li.scrollIntoView({block: 'start'});
                li.scrollIntoView({block: 'nearest'});
            }, 100);
        });
    }

    setSecondEvent() {
        if (this.#secondEventSetted) return;
        // const secondScroll = OOCalendar.antiShake(() => {
        //     if (!this.#notFixSecond) this.#fixTimeNode(this._elements.secondContent);
        //     this.#notFixSecond = false;
        // }, 100);

        const secondScroll = () => {
            if (!this.#notFixSecond) this.#fixTimeNode(this._elements.secondContent);
            this.#notFixSecond = false;
        };

        this._elements.secondContent.addEventListener('scroll', () => { defer(secondScroll, 100, this); });
        this._elements.secondContent.querySelectorAll('li').forEach((li, i) => {
            const secondString = i.toString().padStart(2, '0');
            li.dataset.second = secondString;
            li.addEventListener('click', () => {
                if (this.#isEnableSecond(secondString)) {
                    this.#currentDate.setSeconds(secondString);
                    this.#selectedSecond = parseInt(secondString);
                    this.selectTime(li);
                }
            });
        });
        this.#secondEventSetted = true;
    }

    #fixTimeNode(node) {
        const li = node.querySelector('li');
        const height = dom.getSize(li).y;
        const remainder = node.scrollTop % height;
        if (remainder < (height / 2)) {
            node.scrollTop = Math.floor(node.scrollTop / height) * height;
        } else {
            node.scrollTop = Math.ceil(node.scrollTop / height) * height;
        }
    }

    setRange() {
        this.#enableYearsFun = null;
        this.#enableMonthsFun = null;
        this.#enableDatesFun = null;
        this.#enableWeeksFun = null;
        this.#enableHoursFun = null;
        this.#enableMinutesFun = null;
        this.#enableSecondsFun = null;
        if (this._props.datetimeRange && this._props.datetimeRange.length) {
            this.#sourceDatetimeRange = exec(`return ${this._props.datetimeRange}`);
            this.setDatetimeRange();
        } else {
            if (this._props.dateRange && this._props.dateRange.length) {
                this.#sourceDateRange = exec(`return ${this._props.dateRange}`);
                this.setDateRange();
            }
            if (this._props.timeRange && this._props.timeRange.length) {
                this.#sourceTimeRange = exec(`return ${this._props.timeRange}`);
                this.setTimeRange();
            }
        }
    }

    setDatetimeRange() {
        const arr = this.#sourceDatetimeRange;
        const _2dArray = typeOf(arr[0]) !== 'array' ? [arr] : arr;
        if (!_2dArray[0][0] && !_2dArray[0][1]) return;

        this.setDateRange(this.#sourceDatetimeRange);

        this.#datetimeRange = _2dArray.map((a) => {
            return [
                OOCalendar.parseDate(a[0]),
                OOCalendar.parseDate(a[1])
            ];
        });

        this.#dateOnlyRange = _2dArray.map((a) => {
            return [
                clearTime(a[0], true),
                clearTime(a[1], true)
            ];
        });

        this.#enableHoursFun = (date) => {
            if (!date) return [0, 23];
            const d = clearTime(date, true);
            const hours = [];
            for (let i = 0; i < this.#dateOnlyRange.length; i++) {
                const ar = this.#dateOnlyRange[i];
                const equal1 = this.#isEquals(ar[0], d), equal2 = this.#isEquals(d, ar[1]);
                if (equal1 || equal2) {
                    hours.push([
                        equal1 ? this.#datetimeRange[i][0].getHours() : 0,
                        equal2 ? this.#datetimeRange[i][1].getHours() : 23]
                    );
                }
            }
            return hours.length ? OOCalendar.RangeArrayUtils.union(hours) : [0, 23];
        };

        this.#enableMinutesFun = (date, hour) => {
            if (!date) return [0, 59];
            const d = clearTime(date, true);
            const minutes = [];
            for (let i = 0; i < this.#dateOnlyRange.length; i++) {
                const ar = this.#dateOnlyRange[i];
                const ardt = this.#datetimeRange[i];
                const equal1 = (this.#isEquals(ar[0], d) && hour === ardt[0].getHours());
                const equal2 = (this.#isEquals(d, ar[1]) && hour === ardt[1].getHours());
                if (equal1 || equal2) {
                    minutes.push([
                        equal1 ? ardt[0].getMinutes() : 0,
                        equal2 ? ardt[1].getMinutes() : 59
                    ]);
                }
            }
            return minutes.length ? OOCalendar.RangeArrayUtils.union(minutes) : [0, 59];
        };

        this.#enableSecondsFun = (date, hour, minute) => {
            if (!date) return [0, 59];
            const d = clearTime(date, true);
            const seconds = [];
            for (let i = 0; i < this.#dateOnlyRange.length; i++) {
                const ar = this.#dateOnlyRange[i];
                const ardt = this.#datetimeRange[i];
                const equal1 = (this.#isEquals(ar[0], d) && hour === ardt[0].getHours() && minute === ardt[0].getMinutes());
                const equal2 = (this.#isEquals(d, ar[1]) && hour === ardt[1].getHours() && minute === ardt[1].getMinutes());
                if (equal1 || equal2) {
                    seconds.push([
                        equal1 ? ardt[0].getSeconds() : 0,
                        equal2 ? ardt[1].getSeconds() : 59
                    ]);
                }
            }
            return seconds.length ? OOCalendar.RangeArrayUtils.union(seconds) : [0, 59];
        };
    }

    setDateRange(arr = this.#sourceDateRange) {
        const _2dArray = typeOf(arr[0]) !== 'array' ? [arr] : arr;
        if (!_2dArray[0][0] && !_2dArray[0][1]) return;

        this.#dateRange = _2dArray.map((a) => {
            return [
                clearTime(a[0], true),
                lastTimeOfDate(a[1], true)
            ];
        });

        this.#enableYearsFun = (year) => {
            const start = new Date(year + '-01-01'), end = new Date(year + '-12-31');
            for (let i = 0; i < this.#dateRange.length; i++) {
                const ar = this.#dateRange[i];
                if (OOCalendar.RangeArrayUtils.isIntersection(ar, [start, end])) return true;
            }
            return false;
        };

        this.#enableMonthsFun = (month) => {
            const start = new Date(month + '-01'), end = lastDayOfMonth(...(month.split('-')));
            for (let i = 0; i < this.#dateRange.length; i++) {
                const ar = this.#dateRange[i];
                if (OOCalendar.RangeArrayUtils.isIntersection(ar, [start, end])) return true;
            }
            return false;
        };

        this.#enableDatesFun = (date) => {
            const d = clearTime(date, true);
            for (let i = 0; i < this.#dateRange.length; i++) {
                const ar = this.#dateRange[i];
                if (!ar[0] && this.#isLessEquals(d, ar[1])) return true;
                if (!ar[1] && this.#isLessEquals(ar[0], d)) return true;
                if (this.#isLessEquals(ar[0], d) && this.#isLessEquals(d, ar[1])) return true;
            }
            return false;
        };

        this.#enableWeeksFun = (start, end) => {
            do {
                const enable = this.#enableDatesFun(start);
                if (enable) return true;
                start = start.setDate(start.getDate() + 1);
            } while (start <= end);
            return false;
        };
    }

    setTimeRange() {
        const arr = this.#sourceTimeRange;
        const _2dArray = typeOf(arr[0]) !== 'array' ? [arr] : arr;
        if (!_2dArray[0][0] && !_2dArray[0][1]) return;

        this.#datetimeRange2 = _2dArray.map((a) => {
            return [
                a[0] ? new Date('2020-01-01 ' + a[0]) : null,
                a[1] ? new Date('2020-01-01 ' + a[1]) : null
            ];
        });

        this.#enableHoursFun = (date) => {
            if (this.#hourRange) return this.#hourRange;
            const hours = [];
            for (let i = 0; i < this.#datetimeRange2.length; i++) {
                const ar = this.#datetimeRange2[i];
                hours.push([
                    ar[0] ? ar[0].getHours() : 0,
                    ar[1] ? ar[1].getHours() : 23
                ]);
            }
            this.#hourRange = OOCalendar.RangeArrayUtils.union(hours);
            return this.#hourRange;
        };

        this.#enableMinutesFun = (date, hour) => {
            const minutes = [];
            for (let i = 0; i < this.#datetimeRange2.length; i++) {
                const ar = this.#datetimeRange2[i];
                const equal1 = (ar[0] && hour === ar[0].getHours());
                const equal2 = (ar[1] && hour === ar[1].getHours());
                if (equal1 || equal2) {
                    minutes.push([
                        equal1 ? ar[0].getMinutes() : 0,
                        equal2 ? ar[1].getMinutes() : 59
                    ]);
                }
            }
            return minutes.length ? OOCalendar.RangeArrayUtils.union(minutes) : [0, 59];
        };

        this.#enableSecondsFun = (date, hour, minute) => {
            const seconds = [];
            for (let i = 0; i < this.#datetimeRange2.length; i++) {
                const ar = this.#datetimeRange2[i];
                const equal1 = (ar[0] && hour === ar[0].getHours() && minute === ar[0].getMinutes());
                const equal2 = (ar[1] && hour === ar[1].getHours() && minute === ar[1].getMinutes());
                if (equal1 || equal2) {
                    seconds.push([
                        equal1 ? ar[0].getSeconds() : 0,
                        equal2 ? ar[1].getSeconds() : 59
                    ]);
                }
            }
            return seconds.length ? OOCalendar.RangeArrayUtils.union(seconds) : [0, 59];
        };
    }

    #isGreatEquals(d1, d2) {
        return (d1 > d2) || ((d1 - d2) === 0);
    }

    #isLessEquals(d1, d2) {
        return (d1 < d2) || ((d1 - d2) === 0);
    }

    #isEquals(d1, d2) {
        return (d1 - d2) === 0;
    }

    #isEnable(fun, ...d) {
        return !fun || typeOf(fun) !== 'function' || fun(...d);
    }

    #isEnableYear(year) {
        return this.#isEnable(this.#enableYearsFun, year);
    }

    #isEnableMonth(month) {
        return this.#isEnable(this.#enableMonthsFun, month);
    }

    #isEnableDate(date) {
        return this.#isEnable(this.#enableDatesFun, OOCalendar.parseDate(date));
    }

    #isEnableWeek(start, end) {
        return this.#isEnable(this.#enableWeeksFun, OOCalendar.parseDate(start), OOCalendar.parseDate(end));
    }

    #getEnableHours(date) {
        const fun = this.#enableHoursFun;
        if (fun && typeOf(fun) === 'function') {
            return fun(OOCalendar.parseDate(date));
        }
        return [0, 23];
    }

    // #getDisabledHours(date){
    //     let ar = this.#getEnableHours(date);
    //     if( !ar || !ar.length || (ar[0] === 0 && ar[1] === 23) )return [];
    //     if( typeOf(ar[0]) !== "array" )ar = [ar];
    //     return OOCalendar.RangeArrayUtils.complementary([0, 23], ar, null, 1);
    // }
    #isEnableHour(hour, thisDate) {
        hour = parseInt(hour);
        const hs = this.#getEnableHours(thisDate || this.getSelectedDate());
        if (!hs || !hs.length || (hs[0] === 0 && hs[1] === 23)) return true;
        if (typeOf(hs[0]) === 'array') {
            for (let i = 0; i < hs.length; i++) {
                const dhs = hs[i];
                if (dhs[0] <= hour && hour <= dhs[1]) return true;
            }
        } else {
            if (hs[0] <= hour && hour <= hs[1]) return true;
        }
        return false;
    }

    #getEnableMinutes(date, h) {
        const fun = this.#enableMinutesFun;
        if (fun && typeOf(fun) === 'function') {
            return fun(OOCalendar.parseDate(date), h);
        }
        return [0, 59];
    }

    // #getDisabledMinutes(date, h){
    //     let ar = this.#getEnableMinutes(date, h);
    //     if( !ar || !ar.length || (ar[0] === 0 && ar[1] === 59))return [];
    //     if( typeOf(ar[0]) !== "array" )ar = [ar];
    //     return OOCalendar.RangeArrayUtils.complementary([0, 59], ar, null, 1);
    // }
    #isEnableMinute(minute, hour = this.#selectedHour, thisDate) {
        minute = parseInt(minute);
        const ms = this.#getEnableMinutes(thisDate || this.getSelectedDate(), hour);
        if (!ms || !ms.length || (ms[0] === 0 && ms[1] === 59)) return true;
        if (typeOf(ms[0]) === 'array') {
            for (let i = 0; i < ms.length; i++) {
                const dms = ms[i];
                if (dms[0] <= minute && minute <= dms[1]) return true;
            }
        } else {
            if (ms[0] <= minute && minute <= ms[1]) return true;
        }
        return false;
    }

    #getEnableSeconds(date, h, m) {
        const fun = this.#enableSecondsFun;
        if (fun && typeOf(fun) === 'function') {
            return fun(OOCalendar.parseDate(date), h, m);
        }
        return [0, 59];
    }

    // #getDisabledSeconds(date, h, m){
    //     let ar = this.#getEnableSeconds(date, h, m);
    //     if( !ar || !ar.length || (ar[0] === 0 && ar[1] === 59))return [];
    //     if( typeOf(ar[0]) !== "array" )ar = [ar];
    //     return OOCalendar.RangeArrayUtils.complementary([0, 59], ar, null, 1);
    // }
    #isEnableSecond(second, minute = this.#selectedMinute, hour = this.#selectedHour, thisDate) {
        second = parseInt(second);
        const ss = this.#getEnableSeconds(thisDate || this.getSelectedDate(), hour, minute);
        if (!ss || !ss.length || (ss[0] === 0 && ss[1] === 59)) return true;
        if (typeOf(ss[0]) === 'array') {
            for (let i = 0; i < ss.length; i++) {
                const dss = ss[i];
                if (dss[0] <= second && second <= dss[1]) return true;
            }
        } else {
            if (ss[0] <= second && second <= ss[1]) return true;
        }
        return false;
    }
}


OOCalendar.RangeArrayUtils = {
    //补集 range [ start, end ]  rangeList  [ [start1, end1], [ start2, end2 ] ... ]
    complementary(range, rangeList, type) {
        if (!range) return range;
        const r = this.getRangeObject(range);
        if (!rangeList || rangeList.length === 0) return this.parse([r], type);
        const unitedList = this.union(rangeList);

        const newRange = {};
        if (unitedList[0][0] > r.start) {
            newRange.start = r.start;
        } else if (r.end > unitedList[0][1]) {
            newRange.start = unitedList[0][1];
            unitedList.shift();
        } else {
            return [];
        }
        const newList = [];
        while (unitedList.length > 0) {
            if (unitedList[0][0] >= r.end) {
                newRange.end = r.end;
                newList.push({...newRange});
                return this.parse(newList, type);
            } else if (r.end <= unitedList[0][1]) {
                newRange.end = unitedList[0][0];
                newList.push({...newRange});
                return this.parse(newList, type);
            } else {
                newRange.end = unitedList[0][0];
                newList.push({...newRange});
                newRange.start = unitedList[0][1];
                unitedList.shift();
            }
        }
        newRange.end = r.end;
        newList.push({...newRange});
        return this.parse(newList, type);
    },
    //取区域并集rangeList  [ [start1, end1], [ start2, end2 ] ... ]
    union(ranges, type) {
        if (!ranges || ranges.length === 0) return ranges; //this.parse(this.getRangeObject( ranges ) ) ;

        const rangeList = ranges.map((range) => {
            return this.getRangeObject(range);
        }).sort((a, b) => {
            return a.start - b.start;
        });

        const newRangeList = [];
        let newRange = rangeList.shift(), nextRange;
        while (rangeList.length > 0) {
            nextRange = rangeList.shift();
            if (this.isIntersection(newRange, nextRange)) {
                newRange.end = Math.max(newRange.end, nextRange.end);
            } else {
                newRangeList.push({...newRange});
                newRange = nextRange;
            }
        }
        if (!nextRange) {
            newRangeList.push({...newRange});
        } else if (this.isIntersection(newRange, nextRange)) {
            newRange.end = Math.max(newRange.end, nextRange.end);
            newRangeList.push({...newRange});
        } else {
            newRangeList.push({...newRange});
        }

        return this.parse(newRangeList, type);
    },
    //取区域交集rangeList  [ [start1, end1], [ start2, end2 ] ... ]，需要测试
    intersection(ranges, type) {
        if (!ranges || ranges.length === 0) return ranges; //this.parse(this.getRangeObject( ranges ) ) ;
        if (ranges.length === 1) return ranges[1];

        const rangeList = ranges.map((range) => {
            return this.getRangeObject(range);
        }).sort((a, b) => {
            return a.start - b.start;
        });

        const newRange = rangeList.shift();
        while (rangeList.length > 0) {
            const nextRange = rangeList.shift();
            if (this.isIntersection(newRange, nextRange)) {
                newRange.start = nextRange.start;
                newRange.end = Math.min(newRange.end, nextRange.end);
            } else {
                return [];
            }
        }

        if (type && type === 'date') {
            return [new Date(newRange.start), new Date(newRange.end)];
        } else {
            return [newRange.start, newRange.end];
        }
    },
    //区域是否相交
    isIntersection(range1, range2) {
        const r1 = typeOf(range1) === 'object' ? range1 : this.getRangeObject(range1);
        const r2 = typeOf(range2) === 'object' ? range2 : this.getRangeObject(range2);
        if (r1.start > r2.end) return false;
        if (r2.start > r1.end) return false;
        return true;
    },
    parse(objectList, type) {
        return objectList.map((range) => {
            if (type && type === 'date') {
                return [new Date(range.start), new Date(range.end)];
            } else {
                return [range.start, range.end];
            }
        });
    },
    getRangeObject(range) {
        if (range[0] && range[1]) {
            return {
                start: Math.min(range[0], range[1]),
                end: Math.max(range[0], range[1])
            };
        } else if (!range[0] && range[1]) {
            return {
                start: -Infinity,
                end: range[1]
            };
        } else if (range[0] && !range[1]) {
            return {
                start: range[0],
                end: Infinity
            };
        }
    }
};
