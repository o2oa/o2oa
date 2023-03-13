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
      recordList: []
    };
  },
  afterRender() {
     
  },
  formatDutyType(dutyType) {
    if (dutyType === 'OnDuty') {
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
  formatResult(result) {
    if (result === 'PreCheckIn') {
      return '';
    } else if (result === 'NotSigned') {
      return lp.appeal.notSigned;
    }  else if (result === 'Normal') {
      return lp.appeal.normal;
    } else if (result === 'Early') {
      return lp.appeal.early;
    } else if (result === 'Late') {
      return lp.appeal.late;
    } else if (result === 'SeriousLate') {
      return lp.appeal.seriousLate;
    } else if (result === 'Absenteeism') {
      return lp.appeal.absenteeism;
    } else { 
      return '';
    }
  },
  
  // 关闭当前窗口
  close() {
    this.$parent.closeRecordList();
  }, 
});
