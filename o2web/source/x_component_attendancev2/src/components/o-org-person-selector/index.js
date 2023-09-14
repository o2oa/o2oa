import {component as content} from '@o2oa/oovm';
import {lp, o2, component} from '@o2oa/component';
import {lpFormat} from '../../utils/common';
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
            units:[], // 控制选择范围
            showValue: "",
            placeholder: "",
            types: ["identity", "unit"], // 选择器类型
            count: 0 , // 0是多选 其它是固定数据选择
            selectedResult: [] //
        };
    },
    afterRender() {
        if (this.bind.value.length > 0) {
          let newShowValue = [];
          let selectedResult = [];
          for (let index = 0; index < this.bind.value.length; index++) {
            const element = this.bind.value[index];
            selectedResult.push(element);
            const a = element.split("@");
            if (a && a.length > 0 ) {
              newShowValue.push(a[0]);
            } else {
              newShowValue.push(element);
            }
          }
          this.bind.showValue = newShowValue.join(", ");
          this.bind.selectedResult = selectedResult;
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
            "types": this.bind.types,
            "count": this.bind.count,
            "title": this.bind.selectorTitle,
            "units": this.bind.units,
            "firstLevelSelectable": true,
            "values": this.bind.selectedResult,
            "resultType": "person",
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
       let selectedResult = [];
       if (items) {
        if (this.bind.count > 0 && items.length > this.bind.count) {
          const message = lpFormat(lp, "components.selectOrgPersonOverCount", {count: this.bind.count});
          o2.api.page.notice(message, 'error');
          return;
        }
        items.forEach(element => {
          if (element.data && element.data.distinguishedName) {
            newValue.push(element.data.distinguishedName);
            selectedResult.push(element.data);
            const a = element.data.distinguishedName.split("@");
            if (a && a.length > 0 ) {
              newShowValue.push(a[0]);
            } else {
              newShowValue.push(element.data.distinguishedName);
            }
          }
        });
        this.bind.selectedResult = selectedResult;
        this.bind.value = newValue;
        this.bind.showValue = newShowValue.join(", ");
        // 反写到oo-model
        this.component.updateModel(newValue);
       }
    },
     
});
