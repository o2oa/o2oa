import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import template from "./template.html";


export default content({
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.record.detailTitle,
      record: {}
    };
  },
  formatName(person) {
    if (person && person.indexOf("@") > -1) {
      return person.split("@")[0];
    }
    return person;
  },
  sourceTypeFormat(sourceType) {
    switch (sourceType) {
      case "USER_CHECK":
        return lp.record.sourceTypeUser;
      case "AUTO_CHECK":
        return lp.record.sourceTypeAuto;
      case "FAST_CHECK":
        return lp.record.sourceTypeFast;
      case "SYSTEM_IMPORT":
        return lp.record.sourceTypeImport;
      default:
        return "";
    }
  },
  recordDateFormat() {
    if (
      this.bind.record.checkInResult === "PreCheckIn" ||
      this.bind.record.checkInResult === "NotSigned"
    ) {
      return "";
    }
    return this.bind.record.recordDate;
  },
  formatRecordResultClass(record) {
    let span = "";
    if (record.fieldWork) {
      span = "color-fieldWork";
    } else {
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
    }
    return span;
  },
  formatRecordResult(record) {
    let span = "";
    if (record.fieldWork) {
      span = lp.appeal.fieldWork;
    } else {
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
      } else {
        span = "";
      }
    }
    return span;
  },
  close() {
    this.$parent.closeFormVm();
  }

});