import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { formatPersonName, getAllDatesInMonth, formatDate, formatMonth, storageSet, storageGet, isEmpty, showLoading, hideLoading } from "../../../utils/common";
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
    // if (!this.bind.trueParticipantList || this.bind.trueParticipantList.length < 1) {
    //   // 提示错误信息
    // }
    // if (isEmpty(this.bind.groupId)) {

    // }
    // 初始化月份日期数据
    const now = new Date();
    now.setDate(1);// 设置每月的 1 号
    this.bind.month = formatMonth(now);
    this.bind.currentDate = now;
    // 班次本地缓存数据
    this.loadLocalData();
    // 排班数据 根据月份查询
    this.loadMonthScheduleList();
    // 人员日期表格数据
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
    this.initUI();
    // 加载月份选择器
    this.loadMonthPicker();
  },
  // 初始化界面
  initUI() {
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
  loadMonthPicker() {
    MWF.require("MWF.widget.Calendar", function(){
      var options = {
          "style": "xform",
          "secondEnable" : false,
          "timeSelectType" : "select",
          "clearEnable": false,
          "isTime": false,
          "timeOnly": false,
          "monthOnly" : true,
          "yearOnly" : false,
          "defaultDate": null,
          "defaultView" : "month",
          "target":  document.body,
          "onComplate": function(formateDate, date){
              this.changeMonthValue(date);
          }.bind(this)
          
      };
      if (this.bind.month && this.bind.month != "") {
        options.baseDate  = new Date(this.bind.month);
      }
      let bindDom = this.dom.querySelector(".input");
      if (!bindDom) {
        bindDom = this.dom;
      }
      this.calendar = new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
  // 切换月份
  changeMonthValue(date) {
    this.bind.month = formatMonth(date);
    // 处理刷新
    this.loadMonthScheduleList();
  },
  // 本地存储数据
  loadLocalData() {
    const shiftList = storageGet('shiftList_'+this.bind.groupId);
    this.bind.shiftSelector.shiftSelected = shiftList || [];
  },
  // 获取月份数据
  async loadMonthScheduleList() {
    await showLoading(this);
    console.log("开始获取月份数据", this.bind.month, this.bind.groupId);
    const list = await groupScheduleAction("listMonth", this.bind.groupId, this.bind.month);
    const scheduleList = list || [];
    this.loadShift(scheduleList);
    this.bind.scheduleList = scheduleList;
    hideLoading(this);
  },
  // 如果本地缓存中没有班次数据，根据返回的排班结果中获取班次数据
  loadShift(scheduleList) {
    // 已经有班次数据 不需要处理了
    if (this.bind.shiftSelector.shiftSelected.length === 0 && scheduleList.length > 0) {
      let shiftList = [];
      for (let index = 0; index < scheduleList.length; index++) {
        const element = scheduleList[index].shift;
        const existingItemIndex = shiftList.findIndex(item => item.id === element.id);
        if (existingItemIndex === -1) {
          shiftList.push(element);
        }
      }
      this.bind.shiftSelector.shiftSelected = shiftList;
    }
  },
  // 班次列表的颜色样式
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
  // 方格内班次名显示
  scheduleDateShow(date, person) {
    for (let index = 0; index < this.bind.scheduleList.length; index++) {
      const element = this.bind.scheduleList[index];
      if (element.scheduleDateString === date && element.userId === person) {
        return element.shift.shiftName[0];
      }
    }
    return "";
  },
  // 方格内班次 样式
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
  // 提交排班数据
  async submit() {
    console.log(this.bind);
    storageSet('shiftList_'+this.bind.groupId, this.bind.shiftSelector.shiftSelected);
    const body = {
      groupId: this.bind.groupId,
      month: this.bind.month,
      scheduleList: this.bind.scheduleList
    };
    const result = await groupScheduleAction("postMonth", body);
    console.log(result);
    this.closeSelf(); 
  },
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
  },
  
});
