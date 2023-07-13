import { component as content } from "@o2oa/oovm";
import { lp } from "@o2oa/component";
import template from "./temp.html";

export default content({
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.detailRecordList.title,
      recordList: [],
    };
  },
  afterRender() {},
  formatDutyType(dutyType) {
    if (dutyType === "OnDuty") {
      return lp.onDuty;
    } else {
      return lp.offDuty;
    }
  },
  formatTime(time) {
    if (time && time.length >= 16) {
      return time.substring(0, 16);
    } else {
      return time;
    }
  },
  formatResultClass(record) {
    let span = "";
    const result = record.checkInResult;
    if (result === "PreCheckIn") {
      span = "";
    } else if (result === "NotSigned") {
      span = "color-nosign";
    } else if (result === "Normal") {
      span = "color-normal";
    } else if (result === "Early") {
      span = "color-early";
    } else if (result === "Late") {
      span = "color-late";
    } else if (result === "SeriousLate") {
      span = "color-serilate";
    } else {
      span = "";
    }
    return span;
  },
  formatResult(record) {
    let span = "";
    const result = record.checkInResult;
    if (result === "PreCheckIn") {
      span = "";
    } else if (result === "NotSigned") {
      span = lp.appeal.notSigned;
    } else if (result === "Normal") {
      span = lp.appeal.normal;
    } else if (result === "Early") {
      span = lp.appeal.early;
    } else if (result === "Late") {
      span = lp.appeal.late;
    } else if (result === "SeriousLate") {
      span = lp.appeal.seriousLate;
    } else if (result === "Absenteeism") {
      span = lp.appeal.absenteeism;
    } else {
      span = "";
    }
    return span;
  },

  // 关闭当前窗口
  close() {
    this.$parent.closeFormVm();
  },
});
