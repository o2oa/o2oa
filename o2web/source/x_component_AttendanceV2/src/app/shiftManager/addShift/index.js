import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
import template from './template.html';
import oInput from '../../../components/o-input';
import oTimePicker from '../../../components/o-time-picker';
import { setJSONValue } from '../../../utils/common';


export default content({
    template,
    components: {oInput, oTimePicker},
    autoUpdate: true,
    bind(){
        return {
            lp,
            fTitle: lp.addShift,
            form: {
                shiftName: ""
            },
            timeType: 1,  // 打卡次数 1:每天两次 2:每次4次 3:每天6次
            time1: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: ""
            },
            time2: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: ""
            },
            time3: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: ""
            },
            typeList: [
                {
                    name: "1天1次上下班",
                    value: 1
                },
                {
                    name: "1天2次上下班",
                    value: 2
                }, {
                    name: "1天3次上下班",
                    value: 3
                }
            ]
        };
    },
    // o time picker 控件使用
    setTimeValue(key, value) {
        console.debug(`修改 key ${key} value ${value}`);
        setJSONValue(key, value, this.bind);
    },
    closeShift() {
        console.debug('remove 组件');
        this.$parent.closeShift();
    },
    submitAdd() {
        console.log(this.bind);
        this.closeShift();
    },
   
});
