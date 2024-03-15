import { component as content } from "@o2oa/oovm";
import { lp } from "@o2oa/component";
import { myAction } from "../../utils/actions";
import { convertTo2DArray } from "../../utils/common";
import template from "./temp.html";
import style from "./style.scope.css";

export default content({
  template,
  style,
  autoUpdate: true,
  bind() {
    return {
      lp,
      startFromMonday: true, // 是否从星期一开始 
      dayList: [], // 星期列表
      dateWithData: [], // 日历数据包含业务数据
      statistic: {
        userId: "",
        workTimeDuration: 0,
        averageWorkTimeDuration: "0.0",
        attendance: 0,
        rest: 0,
        absenteeismDays: 0,
        lateTimes: 0,
        leaveEarlierTimes: 0,
        absenceTimes: 0,
        fieldWorkTimes: 0,
        leaveDays: 0,
        appealNums: 0
      } // 统计数据
    };
  },
  afterRender() {
    this.loadDayList();
    this.loadDate();
  },
  // 星期列表 
  loadDayList() {
    let dayList = [];
    if (this.bind.startFromMonday) {
      for (let index = 1; index < 8; index++) {
        if (index === 7) {
          dayList.push({
            name: this.dayName(0),
            day: 0
          });
        } else {
          dayList.push({
            name: this.dayName(index),
            day: index
          });
        }
      }
    } else {
      for (let index = 0; index < 7; index++) {
        dayList.push({
          name: this.dayName(index),
          day: index
        });
      }
    }
    this.bind.dayList = dayList;
  },
  // 获取星期的中文名
  dayName(index) {
    switch(index) {
      case 0 :
        return lp.day.Sunday;
      case 1 :
          return lp.day.Monday;
      case 2 :
        return lp.day.Tuesday;
      case 3 :
        return lp.day.Wednesday;
      case 4 :
        return lp.day.Thursday;
      case 5 :
        return lp.day.Friday;
      case 6 :
        return lp.day.Saturday;
      default:
        return "";             
    }
  },
  // 获取当前月份的所有日期，并补齐前后几天，形成完整的日历表
  // 默认从星期天开始  startFromMonday = true 从星期一开始
  loadDate() {
    let start = 0
    if (this.bind.startFromMonday) {
      start = 1;
    }
    // 获取当前日期
    const today = new Date();
    this.bind.currentDateTime = today.getTime();
    // 获取当前月份
    const currentMonth = today.getMonth();
    // 获取当前年份
    const currentYear = today.getFullYear();
    const currentDate = today.getDate();
    this.bind.todayString = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${(currentDate) < 10 ? "0" + (currentDate) : (currentDate)}`
    
    this.loadDataByDate(start, currentYear, currentMonth);
  },
  clickToNextMonth() {
    let start = 0
    if (this.bind.startFromMonday) {
      start = 1;
    }
    const preDateTime = this.bind.currentDateTime || new Date().getTime();
    const preDate = new Date(preDateTime);
    // 获取当前月份
    const preMonth = preDate.getMonth();
    // 获取当前年份
    let currentYear = preDate.getFullYear();
    let currentMonth = preMonth + 1;
    if (currentMonth == 12) {
      currentMonth = 0;
      currentYear = currentYear + 1;
    }
    this.bind.currentDateTime = new Date(currentYear, currentMonth, 1).getTime();// 下个月 1 号
    this.loadDataByDate(start, currentYear, currentMonth);
  },
  clickToPreMonth() {
    let start = 0
    if (this.bind.startFromMonday) {
      start = 1;
    }
    const lastDateTime = this.bind.currentDateTime || new Date().getTime();
    const lastDate = new Date(lastDateTime);
    // 获取当前月份
    const lastMonth = lastDate.getMonth();
     // 获取当前年份
     let currentYear = lastDate.getFullYear();
     let currentMonth = lastMonth - 1;
     if (currentMonth == -1) {
       currentMonth = 11;
       currentYear = currentYear - 1;
     }
     this.bind.currentDateTime = new Date(currentYear, currentMonth, 1).getTime();// 下个月 1 号
     this.loadDataByDate(start, currentYear, currentMonth);
  },
  // 加载数据
  // @param start 1 从星期一开始
  async loadDataByDate( start, currentYear, currentMonth) {
    // 获取当月的天数
    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate(); // 10-31
    // 获取当月第一天是星期几
    const firstDayOfMonth =  new Date(currentYear, currentMonth, 1).getDay(); // 星期天  0
    // 获取当月最后一天是星期几
    // 计算需要补齐的天数
    const daysToPad = (7 - ((daysInMonth + firstDayOfMonth) % 7) + start) % 7; //  4
    // 计算需要补齐的前一个月的天数
    const lastMonthEndDate = new Date(currentYear, currentMonth, 0);
    const daysInPrevMonth = lastMonthEndDate.getDate(); // 9-30  
    // 创建一个数组来存储日期
    let dates = [];
    // 查询当月的打卡数据
    const startDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-01`;
    const endDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${daysInMonth}`;
    const reqBody = {
      startDate: startDate,
      endDate: endDate,
    };
    let list = [];
    try {
      list = (await myAction("listDetailWithDate", reqBody));
    } catch (error) {
     console.error(error);  
    }
    // 添加需要补齐的前一个月的日期
    const preDay = firstDayOfMonth == 0 ? daysInPrevMonth - 6 + start : daysInPrevMonth - firstDayOfMonth + 1 + start 
    for (
      let i = preDay;
      i <= daysInPrevMonth;
      i++
    ) {
      dates.push({
        date: i,
        notCurrent: 1, // 是否当前月份
        dateYmd: `${lastMonthEndDate.getFullYear()}-${(lastMonthEndDate.getMonth() + 1)  < 10 ? "0" + (lastMonthEndDate.getMonth() + 1) : (lastMonthEndDate.getMonth() + 1) }-${i}`
      });
    }
    // 添加当前月份的所有日期
    for (let i = 1; i <= daysInMonth; i++) {
      const dateYmd = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${i < 10 ? "0"+i : i}`;
      dates.push({
        date: i,
        notCurrent: 0, 
        dateYmd: dateYmd,
      });
    }
    // 添加需要补齐的下一个月的日期
    const nextMonthFirstDate = new Date(currentYear, currentMonth + 1, 1);
    for (let i = 1; i <= daysToPad; i++) {
      dates.push({
        date: i,
        notCurrent: 1,
        dateYmd: `${nextMonthFirstDate.getFullYear()}-${ (nextMonthFirstDate.getMonth() + 1) < 10 ? "0" + (nextMonthFirstDate.getMonth() + 1) : (nextMonthFirstDate.getMonth() + 1) }-0${i}`
      });
    }
    // 数据合并到日历中
    const newDates = dates.map(date => {
      const detail = list.find(element => element.recordDateString === date.dateYmd) || null;
      date.detail = detail;
      date.workDay = true; // 默认白色背景
      return date;
    });
    const checkWorkDayList = dates.map(date => {
      return date.dateYmd;
    });
    // 输出日期数组
    this.bind.dateWithData = convertTo2DArray(newDates, 7);
    // 当前统计周期 月份和日期
    this.bind.cycleMonth = `${currentYear}${lp.year}${(currentMonth + 1)}${lp.month}`;
    const showMonth = (currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1);
    this.bind.cycleDate = `${showMonth}${lp.month}01 - ${showMonth}${lp.month}${daysInMonth}`;
    // 统计信息
    this.loadMyStatistic(reqBody);
    // 查询是否工作日
    this.loadIsWorkDay(checkWorkDayList);
  },
  async loadMyStatistic(body) {
    const statistic = (await myAction("statistic", body));
    if (statistic) {
      this.bind.statistic = statistic;
    }
  },
  async loadIsWorkDay(checkIsMyRestDay) {
    const old = this.bind.dateWithData;
    let list = [];
    try {
      const result = (await myAction("checkIsMyRestDay", { dateList : checkIsMyRestDay}));
      if (result && result.restDateList) {
        list = result.restDateList;
      }
    } catch (error) {
     console.error(error);  
    }
    for (let i = 0; i < old.length; i++) {
      let line = old[i];
      for (let index = 0; index < line.length; index++) {
        const element = line[index];
        if (list.indexOf(element.dateYmd) > -1) {
            element.workDay = false;
            line[index] = element;
        }
      }
    }
  },
  // 日历方块的class
  formatCalItemClass(item) {
    let className = 'item';
    if (item.notCurrent === 1) {
      className += ' out';
    }
    if (item.workDay === false) {
      className += ' rest';
    }
    return className;
  },
  // 打卡
  formatRecord(record) {
    let time = "";
    let type = lp.offDutySimple;
    if (record && record.recordDate) {
      time = record.recordDate.substring(11, 16);
    }
    if (record && record.checkInType === 'OnDuty') {
      type = lp.onDutySimple;
    }
    return `${time}`;
  },
  // 日期的 class
  formatDateClass(date) {
    if (date === this.bind.todayString) {
      return 'item-cal-date-text item-cal-date-today';
    }
    return 'item-cal-date-text'
  },
  formatRecordResultTagClass(record) {
    let statusClassName = '';
    if (record && record.checkInResult) {
      switch (record.checkInResult) {
        case 'Early':
          statusClassName =  "item-record-status-tag record-status-early";
          break;
        case 'Late':
        case 'SeriousLate':
            statusClassName =  "item-record-status-tag record-status-late";
          break;
        case 'NotSigned':
            statusClassName =  "item-record-status-tag record-status-nosign";
          break;
        default:
          statusClassName =  "";
      }
      if (record.fieldWork == true) {
        statusClassName =  "item-record-status-tag record-status-fieldwork";
      } else if (record.leaveData) {
        statusClassName =  "item-record-status-tag record-status-leave";
      }  else if (record.appealId && statusClassName === "") {
        // 管理员处理 算正常
        if (record.appealData && record.appealData.status === 4) {
          statusClassName =  "";
        } else {
          statusClassName =  "item-record-status-tag record-status-appeal";
        }
      }
    }
    return statusClassName;
  },
  formatRecordResultTagName(record) {
    let tagName = '';
    if (record && record.checkInResult) {
      switch (record.checkInResult) {
        case 'Early':
          tagName =  lp.appeal.early;
          break;
        case 'Late':
        case 'SeriousLate':
          tagName =  lp.appeal.late;
          break;
        case 'NotSigned':
          tagName =  lp.appeal.notSigned;
          break;
        default:
          tagName =  "";
      }
      if (record.fieldWork == true) {
        tagName =  lp.appeal.fieldWork;
      } else if (record.leaveData) {
        tagName =  lp.appeal.leave;
      } else if (record.appealId && tagName === "") {
        // 管理员处理 算正常
        if (record.appealData && record.appealData.status === 4) {
          tagName =  "";
        } else {
          tagName =  lp.appeal.appeal;
        }
      }
    }
    return tagName;
  },
  
});
