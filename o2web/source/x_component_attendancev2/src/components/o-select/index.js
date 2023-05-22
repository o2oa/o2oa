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
            result: {}, // 选中的值
            showValue: "", // 显示
            itemList: [], // 下拉列表，里面是对象
            itemValueKey: "value", // 选项对应存储值的字段名
            itemNameKey: "name", // 选项对应存储显示名称的字段名
        };
    },
    // 组件事件
    afterRender() {
      this.selectorDom = this.dom.querySelector(".o-select-list");
      let bindDom = this.dom.querySelector(".input");
      bindDom.addEventListener('click', (event) => {
        this.selectorDom.show();
        this.addCloseEventToParent();
      });
      this.bind.showValue = this.showValueFun();
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
    // 显示内容
    showValueFun() {
      if (this.bind.result && this.bind.result[this.bind.itemNameKey]) {
        return this.bind.result[this.bind.itemNameKey];
      }
      return lp.components.selectPlaceholder
    },
    // 选择
    changeSelect(item) {
      this.bind.result = item;
      this.bind.showValue = this.showValueFun();
      this.selectorHide();
      this.component.updateModel(this.bind.result);
    },
    clearValue() {
      this.bind.result = {};
      this.bind.showValue = this.showValueFun();
      this.component.updateModel({});
    },     
     
});
