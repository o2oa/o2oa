import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { formatPersonName, getAllDatesInMonth, formatDate } from "../../../utils/common";
import { groupAction, attendanceWorkPlaceV2Action, attendanceShiftAction } from "../../../utils/actions";
import style from "./style.scope.css";
import template from "./temp.html";
 

export default content({
  style,
  template,
  components: { },
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.scheduleForm.title,
      trueParticipantList: [], // 排班人员
      month: "",
      dateList: [] // 日期对象
    };
  },
  // 先查询数据
  async beforeRender() {
    if (!this.bind.trueParticipantList || this.bind.trueParticipantList.length < 1) {
      // 提示错误信息
    }
    const now = new Date();
    this.bind.currentDate = now;
    let dateList = [];
    const currentMonthDateList = getAllDatesInMonth(now);
    if (currentMonthDateList && currentMonthDateList.length > 0) {
      for (let index = 0; index < currentMonthDateList.length; index++) {
        const element = currentMonthDateList[index];
        const dName = this.dayName(element.getDay());
        dateList.push({
          date: element,
          text: `${element.getDate()}`,
          day: dName,
          dateString: formatDate(element)
        });
      }
    }
    this.bind.dateList = dateList;
  },
  // 
  afterRender() {
    const mask = this.dom.querySelector("#scheduleFormBox");
    mask.style["z-index"] = "2001";
    const dialog = this.dom.querySelector("#scheduleFormDialog");
    let width = mask.clientWidth;
    if (width > 1300) {
      width = 1300;
    }
    let height = mask.clientHeight - 200;
    if (height < 500) {
      height = mask.clientHeight;
    }
    let left = mask.clientWidth - width;
    if (left < 0) {
      left = 0;
    }
    left = left / 2;
    let top = mask.clientHeight - height;
    if (top < 0) {
      top = 0;
    }
    top = top / 2;
    dialog.style.width = width + "px" ;
    dialog.style.height = height + "px" ;
    dialog.style.left = left + "px";
    dialog.style.top = top + "px";
  },
  // 获取星期的中文名
  dayName(index) {
    switch(index) {
      case 0 :
        return lp.daySimple.Sunday;
      case 1 :
          return lp.daySimple.Monday;
      case 2 :
        return lp.daySimple.Tuesday;
      case 3 :
        return lp.daySimple.Wednesday;
      case 4 :
        return lp.daySimple.Thursday;
      case 5 :
        return lp.daySimple.Friday;
      case 6 :
        return lp.daySimple.Saturday;
      default:
        return "";             
    }
  },
  // 关闭当前页面
  closeSelf() {
    this.component.destroy();
  },
  submit() {

  },
  //
  personName(person) {
    return formatPersonName(person);
  },
  // 点击排班方格
  clickScheduleBox(e) {
    const target = e.currentTarget;
    console.log(target);
    debugger;
    const divWidth = target.offsetWidth; // 获取 div 元素的宽度
    const divHeight = target.offsetHeight; // 获取 div 元素的高度
    const containerRect =  this.dom.querySelector("#scheduleContainer").getBoundingClientRect(); // 获取上级元素(container)的位置信息
    const divRect = target.getBoundingClientRect(); // 获取 div 元素的位置信息
    const offsetX = divRect.left - containerRect.left; // 计算 div 元素相对于上级元素的横向偏移
    const offsetY = divRect.top - containerRect.top; // 计算 div 元素相对于上级元素的纵向偏移
    console.log(`宽度：${divWidth}px, 高度：${divHeight}px`);
    console.log(`横向偏移：${offsetX}px, 纵向偏移：${offsetY}px`);
    const selectedBox = this.dom.querySelector("#selectedFrame");
    selectedBox.style.width = divWidth + "px" ;
    selectedBox.style.height = divHeight + "px" ;
    selectedBox.style.left = offsetX+ "px" ;
    selectedBox.style.top = offsetY+ "px" ;
    selectedBox.style.display = "";

    const tPopup = this.dom.querySelector(".t-popup");
    if (offsetX < 500) {
      tPopup.style.inset = "0px auto auto 0px ";
      tPopup.style.transform = "translate3d("+divWidth+"px, 5px, 0px)";
    } else {
      tPopup.style.inset = "0px 0px auto auto";
      tPopup.style.transform = "translate3d(-"+divWidth+"px, 5px, 0px)";
    }
  }
});
