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
            monthOnly: false,
            showClear: false,
            value: "",
            key:"" // 反写回去的对象key值，多层用.分割 如 form.startTime
        };
    },
    afterRender() {
        MWF.require("MWF.widget.Calendar", function(){
          var defaultView = this.bind.monthOnly? "month" : "day";
          var options = {
              "style": "xform",
              "secondEnable" : false,
              "timeSelectType" : "select",
              "clearEnable": false,
              "isTime": false,
              "timeOnly": false,
              "monthOnly" : this.bind.monthOnly,
              "yearOnly" : false,
              "defaultDate": null,
              "defaultView" : defaultView,
              "target":  document.body,
              "onComplate": function(formateDate, date){
                  this.changeValue(date);
              }.bind(this),
              "onClear": function(){
                  this.clearValue();
              }.bind(this)
              
          };
          if (this.bind.value && this.bind.value != "") {
            options.baseDate  = new Date(this.bind.value);
          }
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
       // 反写到oo-model
       this.component.updateModel("");
    },
    changeValue(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1) > 9 ? `${date.getMonth() + 1}`:`0${(date.getMonth() + 1)}`;
      const day = (date.getDate()) > 9 ? `${date.getDate()}`:`0${date.getDate()}`;
      if (this.bind.monthOnly) {
        const chooseDate = `${year}-${month}`;
        this.bind.value = chooseDate;
        // 反写到oo-model
        this.component.updateModel(chooseDate);
      } else {
        const chooseDate = `${year}-${month}-${day}`;
        this.bind.value = chooseDate;
        // 反写到oo-model
        this.component.updateModel(chooseDate);
      }
      
    },
     
});
