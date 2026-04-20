import {component as content} from '@o2oa/oovm';
import {lp, o2, component} from '@o2oa/component';
import style from './style.scope.css';
import template from './template.html';

// 月份日期选择组件 
// 返回结果 MM-DD 格式
export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            showClear: false,
            value: "", // 最终结果 MM-DD  
            showValue: "", // 前端显示的时间 格式： x月x日
            key:"", // 反写回去的对象key值，多层用.分割 如 form.startTime
            monthList: [],
            chooseMonth: '',
            dayList: [],
            chooseDay: '',
        };
    },
    // 组件事件
    afterRender() {
      if (this.bind.value != '') {
        const valueArr = this.bind.value.split("-");
        this.bind.chooseMonth = parseInt(valueArr[0]);
        this.bind.chooseDay = parseInt(valueArr[1]);
      }
      this.showValueChange()
      // 初始化 monthList 
      this.initMonthList();
      // 初始化 dayList
      this.initDayList();
      this.selectorDom = this.dom.querySelector(".o-time-minutes-selctor");
      let bindDom = this.dom.querySelector(".input");
      bindDom.addEventListener('click', (event) => {
        this.selectorDom.show();
        this.addCloseEventToParent();
      });
      
    },
    selectorHide() {
        this.selectorDom.hide();
        this.$parent.dom.removeEventListener('mousedown', this.hideEvent);
    },
    // 选择器关闭
    selectoOutHide(e) {
      // 计算下拉框的范围，范围外点击隐藏
        const eCoor = this.selectorDom.getBoundingClientRect();
        const elementCoords =  {
          width: this.selectorDom.clientWidth,
          height: this.selectorDom.clientHeight,
          top: eCoor.top,
          bottom: eCoor.bottom,
          left: eCoor.left,
          right: eCoor.right
        }
        if(((e.pageX < elementCoords.left || e.pageX > (elementCoords.left + elementCoords.width)) ||
				(e.pageY < elementCoords.top || e.pageY > (elementCoords.top + elementCoords.height))) ) {
          this.selectorHide();
        }
    },
    // 添加选择器关闭时间
    addCloseEventToParent() {
      this.hideEvent = this.selectoOutHide.bind(this);
      this.$parent.dom.addEventListener('mousedown', this.hideEvent );
    },
    initMonthList() {
      let monthList = [];
      for (let index = 1; index <= 12; index++) {
        monthList.push(index);
      }
      this.bind.monthList = monthList;
    },
    initDayList() {
      let dayList = [];
      if (this.bind.chooseMonth == 2) {// 2月 28 天
        for (let index = 1; index <= 28; index++) {
          dayList.push(index);
        }
      } else {
         // 1，3，5，7，8，10，12月 31天
        if (this.bind.chooseMonth == 1 || this.bind.chooseMonth == 3 || this.bind.chooseMonth == 5 || this.bind.chooseMonth == 7 || this.bind.chooseMonth == 8 || this.bind.chooseMonth == 10 || this.bind.chooseMonth == 12) {
          for (let index = 1; index <= 31; index++) {
            dayList.push(index);
          }
        } else { // 其他月30天
          for (let index = 1; index <= 30; index++) {
            dayList.push(index);
          }
        }
      }
      this.bind.dayList = dayList;
      
    },
    // 切换
    changeChooseMonth(month) {
      this.bind.chooseMonth = month
      this.initDayList();
      this.bind.chooseDay = this.bind.dayList[0];
    },
    showValueChange() {
      const month = this.bind.chooseMonth < 10 ? `0${this.bind.chooseMonth}` : this.bind.chooseMonth;
      const dayStr = this.bind.chooseDay < 10 ? `0${this.bind.chooseDay}` : this.bind.chooseDay;
      this.bind.showValue = `${month}${lp.components.monthDaySelectorMonth}${dayStr}${lp.components.monthDaySelectorDay}`;
    },
    selected(day) {
      this.bind.chooseDay = day;
      const month = this.bind.chooseMonth < 10 ? `0${this.bind.chooseMonth}` : this.bind.chooseMonth;
      const dayStr = this.bind.chooseDay < 10 ? `0${this.bind.chooseDay}` : this.bind.chooseDay;
      this.bind.value = `${month}-${dayStr}`;
      // 显示
      this.showValueChange();
      this.selectorHide();
      this.spitOutValue();
    },
    spitOutValue() {
      if (this.$parent && this.$parent.setSelectorValue) {
        this.$parent.setSelectorValue(this.bind.key, this.bind.value);
      }
    }
     
});
