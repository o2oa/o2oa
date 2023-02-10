import {component as content} from '@o2oa/oovm';
import {lp, o2, component} from '@o2oa/component';
import style from './style.scope.css';
import template from './template.html';

export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            showClear: false,
            value: "", // 选择的分钟数 数字 如 30
            showValue: "", // 前端显示的时间 格式： x小时x分钟
            key:"", // 反写回去的对象key值，多层用.分割 如 form.startTime
            hourList: [],
            chooseHour: '',
            minuteList: [],
            chooseMinute: '',
        };
    },
    // 组件事件
    afterRender() {
      if (this.bind.value != '') {
        const v = parseInt(this.bind.value);
        let hour = parseInt(v/60);
        this.bind.chooseHour = `${hour}`;
        let y = v % 60;
        if (y == 0 && hour == 0) {
          this.bind.chooseMinute = '';
          this.bind.chooseHour = '';
        } else {
          this.bind.chooseMinute = `${y}`;
        }
      }
      this.showValueChange()
      // 初始化 hourList 
      this.initHourList();
      // 初始化 minuteList
      this.initMiniteList();
      this.selectorDom = this.dom.querySelector(".o-time-minutes-selctor");
      let bindDom = this.dom.querySelector(".input");
      bindDom.addEventListener('click', (event) => {
        this.selectorDom.show();
      });
    },
    initHourList() {
      let hourList = [];
      hourList.push('');
      for (let index = 0; index < 9; index++) {
        hourList.push(`${index}`);
      }
      this.bind.hourList = hourList;
    },
    initMiniteList() {
      let minuteList = [];
      if (this.bind.chooseHour == '') {
        minuteList.push('');
      } else {
        if (this.bind.chooseHour == '0') { // 0小时的时候不能有0分钟
          for (let index = 1; index < 60; index++) {
            minuteList.push(`${index}`);
          }
        } else { // 有小时的时候 可以有0分钟
          for (let index = 0; index < 60; index++) {
            minuteList.push(`${index}`);
          }
        }
      }
      this.bind.minuteList = minuteList;
      
    },
    // 切换
    changeChooseHour(hour) {
      debugger;
      console.debug("切换小时", hour);
      this.bind.chooseHour = hour
      this.initMiniteList();
      this.bind.chooseMinute = this.bind.minuteList[0];
      console.debug(this.bind);
    },
    leaveIcon() {
      this.bind.showClear = false;
    },
    enterIcon() {
      this.bind.showClear = true;
    },
    clickIconClearValue() {
      if (this.bind.showClear) {
        this.clearValue();
      }
    },
    clearValue() {
      this.bind.value = "";
      this.spitOutValue();
    },
    showValueChange() {
      const v = parseInt(this.bind.value);
      if (v < 60) {
        if (this.bind.chooseHour == '' && this.bind.chooseMinute == '' ) {
          this.bind.showValue = this.bind.lp.components.timeMinutesSelectorClosePlaceholder;
        } else {
          this.bind.showValue = `${this.bind.chooseMinute}${this.bind.lp.components.timeMinutesSelectorMinute}`;
        }
      } else {
        if (this.bind.chooseHour == '' && this.bind.chooseMinute == '' ) {
          this.bind.showValue = this.bind.lp.components.timeMinutesSelectorClosePlaceholder;
        } else {
          this.bind.showValue = `${this.bind.chooseHour}${this.bind.lp.components.timeMinutesSelectorHour}${this.bind.chooseMinute}${this.bind.lp.components.timeMinutesSelectorMinute}`;
        }
      }
    },
    selected(minute) {
      this.bind.chooseMinute = minute;
      // 显示
      this.showValueChange();
      this.selectorDom.hide();
      let v = 0;
      if (this.bind.chooseHour != '') {
        v += parseInt(this.bind.chooseHour) * 60;
        if (this.bind.chooseMinute != '') {
          v += parseInt(this.bind.chooseMinute);
        }
        this.bind.value = `${v}`;
      } else {
        this.bind.value = '';
      }
      this.spitOutValue();
    },
    spitOutValue() {
      if (this.$parent && this.$parent.setSelectorValue) {
        this.$parent.setSelectorValue(this.bind.key, this.bind.value);
      }
    }
     
});
