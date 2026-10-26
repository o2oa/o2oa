import { component as content } from '@o2oa/oovm';
import { lp, o2 } from '@o2oa/component';
import { myAction } from '../../utils/actions';
import { fieldWorkFormat } from '../../utils/common';
import template from './template.html';
import style from './style.scope.css';

const DAY_NAMES = ['日', '一', '二', '三', '四', '五', '六'];
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

function formatDateValue(date) {
    const pad = (value) => (value > 9 ? value : `0${value}`);
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        const today = new Date();
        const selectedDate = formatDateValue(today);
        return {
            lp,
            selectedDate,
            calendarTitle: '',
            weekList: [],
            recordList: [],
            summaryTitle: '上下班打卡',
            loading: false,
            emptyText: '暂无打卡记录'
        };
    },
    afterRender() {
        this.loadWeekByDate(new Date());
        this.loadRecords(this.bind.selectedDate);
    },
    loadWeekByDate(date) {
        const weekStart = this.getWeekStart(date);
        const selectedDate = this.bind.selectedDate;
        const today = this.formatDate(new Date());
        const weekList = [];
        for (let i = 0; i < 7; i++) {
            const itemDate = this.addDays(weekStart, i);
            const dateString = this.formatDate(itemDate);
            weekList.push({
                date: dateString,
                dayName: DAY_NAMES[itemDate.getDay()],
                day: itemDate.getDate(),
                isToday: dateString === today,
                selected: dateString === selectedDate
            });
        }
        this.bind.weekList = weekList;
        this.bind.calendarTitle = `${date.getFullYear()}年${date.getMonth() + 1}月`;
    },
    getWeekStart(date) {
        const start = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        start.setDate(start.getDate() - start.getDay());
        return start;
    },
    addDays(date, days) {
        const next = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        next.setDate(next.getDate() + days);
        return next;
    },
    selectDate(item) {
        if (!item || !item.date || item.date === this.bind.selectedDate) {
            return;
        }
        this.bind.selectedDate = item.date;
        this.loadWeekByDate(this.parseDate(item.date));
        this.loadRecords(item.date);
    },
    prevWeek() {
        this.changeWeek(-7);
    },
    nextWeek() {
        this.changeWeek(7);
    },
    changeWeek(days) {
        const nextDate = this.addDays(this.parseDate(this.bind.selectedDate), days);
        this.bind.selectedDate = this.formatDate(nextDate);
        this.loadWeekByDate(nextDate);
        this.loadRecords(this.bind.selectedDate);
    },
    onWeekTouchStart(e) {
        const touch = e && e.touches && e.touches.length ? e.touches[0] : null;
        this.touchStartX = touch ? touch.clientX : 0;
    },
    onWeekTouchEnd(e) {
        const touch = e && e.changedTouches && e.changedTouches.length ? e.changedTouches[0] : null;
        if (!touch || !this.touchStartX) {
            return;
        }
        const distance = touch.clientX - this.touchStartX;
        this.touchStartX = 0;
        if (Math.abs(distance) < 48) {
            return;
        }
        if (distance > 0) {
            this.prevWeek();
        } else {
            this.nextWeek();
        }
    },
    async loadRecords(date) {
        this.loadingDate = date;
        this.bind.loading = true;
        this.bind.recordList = [];
        try {
            const data = await myAction('listCheckInRecordWithDate', { date });
            if (this.loadingDate !== date) {
                return;
            }
            const normalized = this.normalizeRecordData(data);
            const recordList = this.getDisplayRecordList(normalized);
            this.bind.recordList = this.buildRecordList(recordList);
            this.bind.summaryTitle = this.buildSummaryTitle(Object.assign({}, normalized, { recordList }));
            this.bind.emptyText = '暂无打卡记录';
        } catch (err) {
            if (this.loadingDate !== date) {
                return;
            }
            console.error('查询打卡记录失败', err);
            this.bind.emptyText = '打卡记录加载失败';
            o2.api.page.notice('打卡记录加载失败，请重试！', 'error');
        } finally {
            if (this.loadingDate === date) {
                this.bind.loading = false;
            }
        }
    },
    normalizeRecordData(data) {
        if (Array.isArray(data)) {
            if (data.length === 1 && data[0] && Array.isArray(data[0].recordList)) {
                return Object.assign({}, data[0], { recordList: data[0].recordList });
            }
            return { recordList: data };
        }
        if (!data) {
            return { recordList: [] };
        }
        if (Array.isArray(data.recordList)) {
            return Object.assign({}, data, { recordList: data.recordList });
        }
        if (Array.isArray(data.checkItemList)) {
            return Object.assign({}, data, { recordList: data.checkItemList });
        }
        if (data.detail && Array.isArray(data.detail.recordList)) {
            return Object.assign({}, data.detail, { recordList: data.detail.recordList });
        }
        if (data.data && Array.isArray(data.data)) {
            return { recordList: data.data };
        }
        if (data.data && Array.isArray(data.data.recordList)) {
            return Object.assign({}, data.data, { recordList: data.data.recordList });
        }
        return { recordList: [] };
    },
    getDisplayRecordList(detail) {
        const recordList = detail.recordList || [];
        if (this.isRestDay(detail) && !this.hasCheckedRecord(recordList)) {
            return [];
        }
        return recordList;
    },
    isRestDay(detail) {
        const recordDetail = detail && detail.detail ? detail.detail : detail;
        return recordDetail && recordDetail.workDay === false;
    },
    hasCheckedRecord(list) {
        return (list || []).some((record) => record && NOT_CHECKED_RESULTS.indexOf(record.checkInResult || 'PreCheckIn') === -1);
    },
    hasNotCheckedRecord(list) {
        return (list || []).some((record) => record && NOT_CHECKED_RESULTS.indexOf(record.checkInResult || 'PreCheckIn') > -1);
    },
    buildRecordList(list) {
        return (list || []).map((record, index) => {
            const result = record.checkInResult || 'PreCheckIn';
            const actualTime = this.formatTime(record.recordDate);
            const planTime = record.preDutyTime || actualTime || '--:--';
            const resultText = this.formatRecordResult(record);
            return Object.assign({}, record, {
                planTime,
                actualTime,
                checkInTypeText: record.checkInType === 'OffDuty' ? lp.offDutySimple : lp.onDutySimple,
                sourceText: this.formatSource(record),
                resultText,
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
    calcWorkDuration(list) {
        if (this.hasNotCheckedRecord(list)) {
            return 0;
        }
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
    formatDate(date) {
        return formatDateValue(date);
    },
    parseDate(dateString) {
        const parts = dateString.split('-').map((part) => parseInt(part, 10));
        return new Date(parts[0], parts[1] - 1, parts[2]);
    },
    goAppealManagerPage() {
        let appealUrl = `appMobile.html?app=attendancev2&page=appealManager`;
        const url = window.location.href;
        if (url.indexOf("debugger") != -1) {
            appealUrl += "&debugger";
        }
        window.location.href = appealUrl;
    }
});
