import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { myAction } from "../../utils/actions";
import { convertTo2DArray, convertMinutesToHoursAndMinutes } from "../../utils/common";
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
      // statistic 统计数据
    };
  },
  afterRender() {
    this.loadDayList();
    this.loadDate(this.bind.startFromMonday);
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
  async loadDate(startFromMonday) {
    let start = 0
    if (typeof startFromMonday ==="boolean" && startFromMonday) {
      start = 1;
    }
    // 获取当前日期
    const today = new Date();
    // 获取当前月份
    const currentMonth = today.getMonth();
    // 获取当前年份
    const currentYear = today.getFullYear();
    // 获取当月的天数
    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
    // 获取当月第一天是星期几
    const firstDayOfMonth =  new Date(currentYear, currentMonth, 1).getDay();
    // 获取当月最后一天是星期几
    // 计算需要补齐的天数
    const daysToPad = (7 - ((daysInMonth + firstDayOfMonth) % 7) + start) % 7;
    // 计算需要补齐的前一个月的天数
    const lastMonthEndDate = new Date(currentYear, currentMonth, 0);
    const daysInPrevMonth = lastMonthEndDate.getDate();
    // 创建一个数组来存储日期
    let dates = [];
    // 查询当月的打卡数据
    const startDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-01`;
    const endDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${daysInMonth}`;
    const reqBody = {
      startDate: startDate,
      endDate: endDate,
    };
    const list = (await myAction("listDetailWithDate", reqBody)) || [];
    // 添加需要补齐的前一个月的日期
    for (
      let i = daysInPrevMonth - firstDayOfMonth + 1 + start;
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
      return date;
    });
    // 输出日期数组
    this.bind.dateWithData = convertTo2DArray(newDates, 7);
    this.bind.cycleDate = startDate + " - " + endDate;
    // 统计信息
    this.loadMyStatistic(reqBody);
  },
  async loadMyStatistic(body) {
    const statistic = (await myAction("statistic", body));
    if (statistic) {
      this.bind.statistic = statistic;
    }
  },
  // 日历方块的class
  formatCalItemClass(item) {
    let className = 'item';
    if (item.notCurrent === 1) {
      className = 'item out';
    }
    if (item.detail && item.detail.recordList && item.detail.recordList.length > 0) {
      className = 'item full';
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
    return `${type} - ${time}`;
  },
  // 打卡结果 标识
  formatRecordStatusClass(record) {
    let statusClassName = 'item-record-status';
    if (record && record.checkInResult) {
      switch (record.checkInResult) {
        case 'Early':
          statusClassName =  "item-record-status record-status-early";
          break;
        case 'Late':
            statusClassName =  "item-record-status record-status-late";
          break;
        case 'SeriousLate':
            statusClassName =  "item-record-status record-status-serilate";
          break;
        case 'NotSigned':
            statusClassName =  "item-record-status record-status-nosign";
          break;
        default:
          statusClassName =  "item-record-status record-status-normal";
      }
    }
    // 有申诉记录
    if (record.appealId) {
      statusClassName =  "item-record-status record-status-appeal";
    }
    return statusClassName;
  },
   // 格式化工作时长
   formatWorkTimeDuration(workTime) {
    return convertMinutesToHoursAndMinutes(workTime);
  }
});
