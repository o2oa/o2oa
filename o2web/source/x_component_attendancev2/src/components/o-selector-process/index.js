import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
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
            label: "", // 标题文字
            isRequried: false, // 红星
            // selectorTitle: "", // 选择器上的标题
            // value: [], // 流程对象列表
            // showValue: "", // 流程对象name
            // placeholder: "", // 没有数据的时候显示的内容
        };
    },
    afterRender() {
        this.showValueFun();
    },
    showValueFun() {
      debugger;
      if (this.bind.value.length > 0) {
        let newShowValue = [];
        for (let index = 0; index < this.bind.value.length; index++) {
          const element = this.bind.value[index];
          newShowValue.push(element["name"]||""); // name字段
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
            "types": ["process"],
            "count": 1,
            "expand": false,
            "title": this.bind.selectorTitle,
            "values": this.bind.value,
            "onComplete": function(items) {
              this.changeValue(items);
            }.bind(this)
        };
        new MWF.O2Selector(document.body, options)
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
       if (items) {
        items.forEach(element => {
          if (element.data) {
            newValue.push(element.data);
          }
        });
        this.bind.value = newValue;
        this.showValueFun();
        // 反写到oo-model
        this.component.updateModel(newValue);
       }
    },
     
});
