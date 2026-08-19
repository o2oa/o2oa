import { component as content } from '@o2oa/oovm';
import { lp } from '@o2oa/component';
import { detailAction } from '../../utils/actions';
import { fieldWorkFormat, convertTo2DArray, formatDate, formatPersonName } from '../../utils/common';
import template from './template.html';
import style from './style.scope.css';

const RESULT_TEXT = {
    PreCheckIn: '待打卡',
    NotSigned: '未打卡',
    Normal: '正常',
    Late: '迟到',
    Early: '早退',
    SeriousLate: '严重迟到',
    Absenteeism: '旷工'
};
const NOT_CHECKED_RESULTS = ['PreCheckIn', 'NotSigned'];

const emptyStatistic = () => ({
    userId: '',
    workTimeDuration: 0,
    averageWorkTimeDuration: '0.0',
    attendance: 0,
    rest: 0,
    absenteeismDays: 0,
    lateTimes: 0,
    leaveEarlierTimes: 0,
    absenceTimes: 0,
    fieldWorkTimes: 0,
    leaveDays: 0,
    appealNums: 0,
    detailList: []
});

export default content({
    template,
    style,
    autoUpdate: true,
    bind(){
        return {
            lp,
            person: '',
            title: lp.menu.myAttendance,
            todayLabel: '今天',
            todayString: '',
            currentDateTime: 0,
            currentYear: 0,
            currentMonth: 0,
            cycleMonth: '',
            dayList: [],
            dateWithData: [],
            statistic: emptyStatistic(),
            loading: false,
            showRecordPanel: false,
            selectedDate: '',
            selectedRecordTitle: '上下班打卡',
            selectedRecordList: []
        };
    },
    afterRender() {
        this.initPerson();
        this.loadDayList();
        this.today = new Date();
        this.bind.todayString = formatDate(this.today);
        this.bind.currentDateTime = new Date(this.today.getFullYear(), this.today.getMonth(), 1).getTime();
        this.loadDataByDate(new Date(this.bind.currentDateTime));
    },
    initPerson() {
        if (!this.bind.person) {
            this.bind.person = (layout.session && layout.session.user) ? layout.session.user.distinguishedName : '';
        }
        const currentPerson = (layout.session && layout.session.user) ? layout.session.user.distinguishedName : '';
        if (this.bind.person && this.bind.person !== currentPerson) {
            this.bind.title = `${formatPersonName(this.bind.person)}的考勤`;
        }
    },
    loadDayList() {
        this.bind.dayList = [
            { name: lp.daySimple.Monday, day: 1 },
            { name: lp.daySimple.Tuesday, day: 2 },
            { name: lp.daySimple.Wednesday, day: 3 },
            { name: lp.daySimple.Thursday, day: 4 },
            { name: lp.daySimple.Friday, day: 5 },
            { name: lp.daySimple.Saturday, day: 6 },
            { name: lp.daySimple.Sunday, day: 0 }
        ];
    },
    clickToPreMonth() {
        const current = new Date(this.bind.currentDateTime || new Date().getTime());
        this.loadDataByDate(new Date(current.getFullYear(), current.getMonth() - 1, 1));
    },
    clickToNextMonth() {
        const current = new Date(this.bind.currentDateTime || new Date().getTime());
        this.loadDataByDate(new Date(current.getFullYear(), current.getMonth() + 1, 1));
    },
    clickToToday() {
        const today = new Date();
        this.loadDataByDate(new Date(today.getFullYear(), today.getMonth(), 1));
    },
    clickCalendarDay(item) {
        if (!item || !item.hasRecord || !item.detail) {
            return;
        }
        const recordList = this.getDisplayRecordList(item.detail);
        if (recordList.length < 1) {
            return;
        }
        this.bind.selectedDate = item.dateYmd;
        this.bind.selectedRecordList = this.buildRecordList(recordList);
        this.bind.selectedRecordTitle = this.buildSummaryTitle(Object.assign({}, item.detail, { recordList }));
        this.bind.showRecordPanel = true;
    },
    closeRecordPanel() {
        this.bind.showRecordPanel = false;
    },
    async loadDataByDate(date) {
        const year = date.getFullYear();
        const month = date.getMonth();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        const startDate = formatDate(new Date(year, month, 1));
        const endDate = formatDate(new Date(year, month, daysInMonth));
        const form = {
            filterList: this.bind.person ? [this.bind.person] : [],
            startDate,
            endDate
        };

        this.bind.loading = true;
        this.bind.currentDateTime = new Date(year, month, 1).getTime();
        this.bind.currentYear = year;
        this.bind.currentMonth = month + 1;
        this.bind.cycleMonth = `${year}${lp.year}${month + 1}${lp.month}`;

        try {
            const json = await detailAction('statistic', form);
            const statistic = this.normalizeStatistic(json);
            this.bind.statistic = statistic;
            this.bind.dateWithData = this.buildCalendar(year, month, statistic.detailList || []);
        } catch (e) {
            console.error(e);
            this.bind.statistic = emptyStatistic();
            this.bind.dateWithData = this.buildCalendar(year, month, []);
        } finally {
            this.bind.loading = false;
        }
    },
    normalizeStatistic(json) {
        if (Array.isArray(json)) {
            if (this.bind.person) {
                const matched = json.find((item) => {
                    return item && (item.userId === this.bind.person || item.person === this.bind.person || item.distinguishedName === this.bind.person);
                });
                return Object.assign(emptyStatistic(), matched || json[0] || {});
            }
            return Object.assign(emptyStatistic(), json[0] || {});
        }
        return Object.assign(emptyStatistic(), json || {});
    },
    buildCalendar(year, month, detailList) {
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        const firstDayOfMonth = new Date(year, month, 1).getDay();
        const daysToPadBefore = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1;
        const detailMap = {};

        (detailList || []).forEach((detail) => {
            if (detail && detail.recordDateString) {
                detailMap[detail.recordDateString] = detail;
            }
        });

        const dates = [];
        for (let i = 0; i < daysToPadBefore; i++) {
            dates.push({ inMonth: false });
        }
        for (let day = 1; day <= daysInMonth; day++) {
            const dateYmd = formatDate(new Date(year, month, day));
            const detail = detailMap[dateYmd] || null;
            dates.push({
                date: day,
                dateYmd,
                inMonth: true,
                isToday: dateYmd === this.bind.todayString,
                detail,
                hasRecord: this.hasRealRecord(detail)
            });
        }
        while (dates.length < 42) {
            dates.push({ inMonth: false });
        }
        return convertTo2DArray(dates, 7);
    },
    hasRealRecord(detail) {
        const recordList = (detail && detail.recordList) ? detail.recordList : [];
        if (recordList.length < 1) {
            return false;
        }
        if (detail.workDay === false) {
            return recordList.some((record) => {
                return record && record.checkInResult !== 'NotSigned';
            });
        }
        return true;
    },
    getDisplayRecordList(detail) {
        const recordList = (detail && detail.recordList) ? detail.recordList : [];
        if (detail && detail.workDay === false) {
            return recordList.filter((record) => {
                return record && record.checkInResult !== 'NotSigned';
            });
        }
        return recordList;
    },
    buildRecordList(list) {
        return (list || []).map((record, index) => {
            const result = record.checkInResult || 'PreCheckIn';
            const actualTime = this.formatTime(record.recordDate);
            const planTime = record.preDutyTime || actualTime || '--:--';
            return Object.assign({}, record, {
                planTime,
                actualTime,
                checkInTypeText: record.checkInType === 'OffDuty' ? lp.offDutySimple : lp.onDutySimple,
                sourceText: this.formatSource(record),
                resultText: this.formatRecordResult(record),
                statusClass: this.getStatusClass(result),
                lineClass: result === 'PreCheckIn' ? 'record-line muted' : 'record-line',
                isLast: index === list.length - 1
            });
        });
    },
    buildSummaryTitle(detail) {
        const recordList = detail.recordList || [];
        const duration = this.hasNotCheckedRecord(recordList) ? 0 : (detail.workTimeDuration || detail.workTimeMinutes || detail.duration || this.calcWorkDuration(recordList));
        if (!duration) {
            return '上下班打卡';
        }
        return `上下班打卡（工时 ${this.formatDuration(duration)}）`;
    },
    hasNotCheckedRecord(list) {
        return (list || []).some((record) => {
            return record && NOT_CHECKED_RESULTS.indexOf(record.checkInResult || 'PreCheckIn') > -1;
        });
    },
    calcWorkDuration(list) {
        const times = (list || []).map((record) => {
            const date = record.recordDate ? new Date(record.recordDate.replace(/-/g, '/')) : null;
            return date && !Number.isNaN(date.getTime()) ? date.getTime() : null;
        }).filter((time) => time);
        if (times.length < 2) {
            return 0;
        }
        return Math.max.apply(Math, times) - Math.min.apply(Math, times);
    },
    formatDuration(value) {
        let minutes = Number(value);
        if (Number.isNaN(minutes)) {
            return value;
        }
        if (minutes > 24 * 60) {
            minutes = Math.round(minutes / 60000);
        }
        const hours = Math.floor(minutes / 60);
        const restMinutes = minutes % 60;
        if (!hours) {
            return `${restMinutes}分钟`;
        }
        return `${hours}小时${restMinutes}分钟`;
    },
    formatRecordResult(record) {
        if (record.fieldWork) {
            return lp.appeal && lp.appeal.fieldWork ? fieldWorkFormat(record) : '外勤';
        }
        return RESULT_TEXT[record.checkInResult] || '';
    },
    formatSource(record) {
        if (record.fieldWork) {
            return lp.appeal && lp.appeal.fieldWork ? fieldWorkFormat(record) : '外勤';
        }
        switch (record.sourceType) {
            case 'USER_CHECK':
                return lp.record && lp.record.sourceTypeUser ? lp.record.sourceTypeUser : '用户打卡';
            case 'AUTO_CHECK':
                return '自动';
            case 'FAST_CHECK':
                return lp.record && lp.record.sourceTypeFast ? lp.record.sourceTypeFast : '极速打卡';
            case 'SYSTEM_IMPORT':
                return lp.record && lp.record.sourceTypeImport ? lp.record.sourceTypeImport : '数据导入';
            default:
                break;
        }
        if (record.sourceDevice) {
            return record.sourceDevice;
        }
        return '';
    },
    getStatusClass(result) {
        if (result === 'PreCheckIn') {
            return 'record-status muted';
        }
        if (result === 'Normal') {
            return 'record-status normal';
        }
        return 'record-status abnormal';
    },
    formatTime(dateString) {
        if (!dateString || dateString.length < 16) {
            return '';
        }
        return dateString.substring(11, 16);
    },
    formatDateClass(item) {
        let className = 'public-calendar-date';
        if (item && item.isToday) {
            className += ' today';
        }
        return className;
    },
    formatCalendarCellClass(item) {
        let className = 'public-calendar-cell';
        if (!item || !item.inMonth) {
            className += ' blank';
        }
        if (item && item.hasRecord) {
            className += ' has-record';
        }
        return className;
    },
    statisticValue(value) {
        return value || value === 0 ? value : 0;
    }
});
