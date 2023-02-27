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
      return '上班打卡';
    } else {
      return '下班打卡';
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
      return '预存数据';
    } else if (result === 'NotSigned') {
      return '未打卡';
    }  else if (result === 'Normal') {
      return '正常';
    } else if (result === 'Early') {
      return '早退';
    } else if (result === 'Late') {
      return '迟到';
    } else if (result === 'SeriousLate') {
      return '严重迟到';
    } else if (result === 'Absenteeism') {
      return '旷工';
    } else { 
      return '未知';
    }
  },
  
  // 关闭当前窗口
  close() {
    this.$parent.closeRecordList();
  }, 
});
