import {component as content} from '@o2oa/oovm';
import {lp, o2, component} from '@o2oa/component';
import style from './style.scope.css';
import template from './temp.html';

export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            showClear: false,
            isRequried: false,
            label: "",
            selectorTitle: "",
            value: [],
            showValue: "",
            placeholder: "",
        };
    },
    afterRender() {
        if (this.bind.value.length > 0) {
          let newShowValue = [];
          for (let index = 0; index < this.bind.value.length; index++) {
            const element = this.bind.value[index];
            const a = element.split("@");
            if (a && a.length == 3) {
              newShowValue.push(a[0]+"@"+a[2]);
            } else {
              newShowValue.push(element);
            }
          }
          this.bind.showValue = newShowValue.join(", ");
        }
    },
    leaveIcon() {
      this.bind.showClear = false;
    },
    enterIcon() {
      this.bind.showClear = true;
    },
    clickOpenO2Selector() {
      MWF.requireApp("Selector","package", function(){
        var options = {
            "types": ["person", "unit"],
            "title": this.bind.selectorTitle,
            "values": this.bind.value,
            "onComplete": function(items) {
              this.changeValue(items);
            }.bind(this)
        };
        this.o2Selector = new MWF.O2Selector(document.body, options)
      }.bind(this));
    },
    clickIconClearValue() {
      if (this.bind.showClear) {
        this.clearValue();
      }
    },
    clearValue() {
      this.bind.value = [];
      this.bind.showValue = "";
      // 反写到oo-model
      this.component.updateModel([]);
    },
    changeValue(items) {
       let newValue = [];
       let newShowValue = [];
       if (items) {
        items.forEach(element => {
          if (element.data && element.data.distinguishedName) {
            newValue.push(element.data.distinguishedName);
            const a = element.data.distinguishedName.split("@");
            if (a && a.length == 3) {
              newShowValue.push(a[0]+"@"+a[2]);
            } else {
              newShowValue.push(element.data.distinguishedName);
            }
          }
        });
        this.bind.value = newValue;
        this.bind.showValue = newShowValue.join(", ");
        // 反写到oo-model
        this.component.updateModel(newValue);
       }
    },
     
});
