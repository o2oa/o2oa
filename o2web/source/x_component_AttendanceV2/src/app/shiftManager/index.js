import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
import style from './style.scope.css';
import template from './template.html';

export default content({
    style,
    template,
    autoUpdate: true,
    bind(){
        return {
            lp,
        };
    },
    async addShift() {
        console.log('新增班次');
        const addBind = {};
        addBind.form = {
            shiftName: ""
        };
        addBind.timeType = 1; //上下班打开
        addBind.time1 = {
            onDutyTime: "09:00",
            onDutyTimeBeforeLimit: "",
            onDutyTimeAfterLimit: "",
            offDutyTime: "18:00",
            offDutyTimeBeforeLimit: "",
            offDutyTimeAfterLimit: ""
        };
        // 添加
        const content = (await import (`./addShift/index.js`)).default;
        this.addShiftVm = await content.generate('.form' , {bind: addBind}, this);
    },
    closeShift() {
        if (this.addShiftVm) {
            this.addShiftVm.destroy();
        }
    }
   
});
