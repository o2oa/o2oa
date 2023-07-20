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
            placeholder: lp.components.chooseTimePlaceholder,
            value: "",
            key:"" // 反写回去的对象key值，多层用.分割 如 form.startTime
        };
    },
    afterRender() {
        MWF.require("MWF.widget.Calendar", function(){
          var defaultView = "day";
          var options = {
              "style": "xform",
              "secondEnable" : false,
              "timeSelectType" : "select",
              "isTime": true,
              "timeOnly": true,
              "monthOnly" : false,
              "yearOnly" : false,
              "defaultDate": null,
              // "clearEnable": false, // 清除按钮
              "defaultView" : defaultView,
              "target":  this.$parent.dom,
              "onComplate": function(formateDate, date){
                  this.changeValue(date);
              }.bind(this),
              "onClear": function(){
                  this.clearValue();
              }.bind(this)
              
          };
          var baseDate = new Date();
          if (this.bind.value && this.bind.value != "" && this.bind.value.includes(":")) {
            var s = this.bind.value.split(":");
            if (s && s.length > 1) {
              var hour = parseInt(s[0]);
              var minute = parseInt(s[1]);
              baseDate.setHours(hour);
              baseDate.setMinutes(minute);
            }
          }
          options.baseDate = baseDate;
          let bindDom = this.dom.querySelector(".input");
          if (!bindDom) {
            bindDom = this.dom;
          }
          this.calendar = new MWF.widget.Calendar(bindDom, options);
          
        }.bind(this));
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
      if (this.$parent && this.$parent.setTimeValue) {
        this.$parent.setTimeValue(this.bind.key, "");
      }
    },
    changeValue(date) {
      var hour = date.getHours() > 9 ? `${date.getHours()}`:`0${date.getHours()}`;
      var minute = date.getMinutes() > 9 ? `${date.getMinutes()}`:`0${date.getMinutes()}`;
      this.bind.value = `${hour}:${minute}`;
      if (this.$parent && this.$parent.setTimeValue) {
        this.$parent.setTimeValue(this.bind.key, this.bind.value);
      }
    },
     
});
