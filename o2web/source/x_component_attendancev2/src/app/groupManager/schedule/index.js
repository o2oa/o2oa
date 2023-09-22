import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { formatPersonName, getAllDatesInMonth, formatDate, formatMonth, storageSet, storageGet, isEmpty } from "../../../utils/common";
import { groupScheduleAction } from "../../../utils/actions";
import selectShiftMultiple from "../../shiftManager/selectShiftMultiple";
import style from "./style.scope.css";
import template from "./temp.html";
 

export default content({
  style,
  template,
  components: { selectShiftMultiple },
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.scheduleForm.title,
      trueParticipantList: [], // 排班人员
      month: "",
      groupId: "",
      dateList: [], // 日期对象
      //  班次选择器使用
      shiftSelectorOpen: false,
      shiftSelector: {
        shiftSelected: []
      },
      // 排班数据
      scheduleList: []
    };
  },
  // 先查询数据
  async beforeRender() {
    if (!this.bind.trueParticipantList || this.bind.trueParticipantList.length < 1) {
      // 提示错误信息
    }
    const now = new Date();
    this.bind.month = formatMonth(now);
    this.bind.currentDate = now;
    this.loadLocalData();
    this.loadMonthScheduleList();
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
  afterRender() {
    const mask = this.dom.querySelector("#scheduleFormBox");
    mask.style["z-index"] = "1999";
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
  // 本地存储数据
  loadLocalData() {
    const shiftList = storageGet('shiftList');
    this.bind.shiftSelector.shiftSelected = shiftList || [];
  },
  // 获取月份数据
  async loadMonthScheduleList() {
    console.log("开始获取月份数据", this.bind.month, this.bind.groupId);
    const list = await groupScheduleAction("listMonth", this.bind.groupId, this.bind.month);
    this.bind.scheduleList = list || [];
  },
  shiftIndexClassName(index) {
    return "shift_color_item shift_color_"+index;
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
  scheduleDateShow(date, person) {
    for (let index = 0; index < this.bind.scheduleList.length; index++) {
      const element = this.bind.scheduleList[index];
      if (element.scheduleDateString === date && element.userId === person) {
        return element.shift.shiftName[0];
      }
    }
    return "";
  },
  // 方框的 class
  scheduleDateClass(date, person) {
    let className = "schedule_cell";
    let shiftId = "";
    for (let index = 0; index < this.bind.scheduleList.length; index++) {
      const element = this.bind.scheduleList[index];
      if (element.scheduleDateString === date && element.userId === person) {
        shiftId = element.shift.id;
        break;
      }
    }
    if (!isEmpty(shiftId)) {
      const index = this.bind.shiftSelector.shiftSelected.findIndex(function(e){
        return e.id === shiftId;
      });
      if (index !== -1) {
        className = "schedule_cell shift_color_"+index;
      }
    }
    return className;
  },
  // 关闭当前页面
  closeSelf() {
    this.component.destroy();
  },
  async submit() {
    console.log(this.bind);
    storageSet('shiftList', this.bind.shiftSelector.shiftSelected);
    const body = {
      groupId: this.bind.groupId,
      month: this.bind.month,
      scheduleList: this.bind.scheduleList
    };
    const result = await groupScheduleAction("postMonth", body);
    console.log(result);
    this.closeSelf(); 
  },
  //
  personName(person) {
    return formatPersonName(person);
  },
  // 打开班次选择器
  async openShiftSelector() {
    this.bind.shiftSelectorOpen = true;
  },
   // 关闭班次选择器
  closeMultipleShiftSelector() {
    this.bind.shiftSelectorOpen = false;
  },
  // 点击排班方格
  clickScheduleBox(date, person, e) {
    const target = e.currentTarget;
    const divWidth = target.offsetWidth; // 获取 div 元素的宽度
    const divHeight = target.offsetHeight; // 获取 div 元素的高度
    const containerRect =  this.dom.querySelector("#scheduleContainer").getBoundingClientRect(); // 获取上级元素(container)的位置信息
    const divRect = target.getBoundingClientRect(); // 获取 div 元素的位置信息
    const offsetX = divRect.left - containerRect.left; // 计算 div 元素相对于上级元素的横向偏移
    const offsetY = divRect.top - containerRect.top; // 计算 div 元素相对于上级元素的纵向偏移
    this._openChooseBox(divWidth, divHeight, offsetX, offsetY);
    // 绑定当前点击日期和人员
    this.bind.clickDate = date;
    this.bind.clickPerson = person;
  },
  // 打开下拉选择班次的框
  _openChooseBox(divWidth, divHeight, offsetX, offsetY) {
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
    this.hideEvent = this._chooseBoxOutHide.bind(this);
    this.dom.addEventListener('mousedown', this.hideEvent );
  },
  // 关闭下拉选择班次的框
  _closeChooseBox() {
    const selectedBox = this.dom.querySelector("#selectedFrame");
    selectedBox.style.display = "none";
    this.dom.removeEventListener('mousedown', this.hideEvent);
  },
   // 鼠标点击外部关闭
   _chooseBoxOutHide(e) {
    // 计算下拉框的范围，范围外点击隐藏
    const targetElement = this.dom.querySelector("#selectedFrame");
    var clickedElement = e.target;
    var isInsideTarget = false;
    // 从点击的目标元素开始向上遍历DOM层级
    while (clickedElement) {
      if (clickedElement === targetElement) {
        isInsideTarget = true;
        break;
      }
      clickedElement = clickedElement.parentNode;
    }
    if (!isInsideTarget) {
      this._closeChooseBox();
    }
  },
  // 选择班次
  chooseShiftOnDate(shift) {
    let exist = false;
    for (let index = 0; index < this.bind.scheduleList.length; index++) {
      const element = this.bind.scheduleList[index];
      if (element.scheduleDateString === this.bind.clickDate && element.userId === this.bind.clickPerson) {
         element.shift = shift;
         this.bind.scheduleList[index] = element;
         exist = true;
      }
    }
    if (!exist) {
      this.bind.scheduleList.push({
        scheduleDateString: this.bind.clickDate,
        userId: this.bind.clickPerson,
        shift: shift,
        shiftId: shift.id
      });
    }
    this._closeChooseBox();
  },
  // 清除班次
  clearShiftOnDate() {
    let i = -1;
    for (let index = 0; index < this.bind.scheduleList.length; index++) {
      const element = this.bind.scheduleList[index];
      if (element.scheduleDateString === this.bind.clickDate && element.userId === this.bind.clickPerson) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.scheduleList.splice(i, 1);
    }
    this._closeChooseBox();
  }
});
