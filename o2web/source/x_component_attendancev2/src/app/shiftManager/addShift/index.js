import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { isPositiveInt, isEmpty, convertMinutesToHoursAndMinutes } from '../../../utils/common';
import { attendanceShiftAction } from "../../../utils/actions";
import template from './template.html';
import style from "./style.scope.css";
import oInput from '../../../components/o-input';
import oTimePicker from '../../../components/o-time-picker';
import oTimeMinutesSelector from '../../../components/o-time-minutes-selector';
import { setJSONValue } from '../../../utils/common';


export default content({
    template,
    style,
    components: {oInput, oTimePicker, oTimeMinutesSelector},
    autoUpdate: true,
    bind(){
        return {
            lp,
            fTitle: lp.addShift,
            form: {
                shiftName: "", //班次名称
                seriousTardinessLateMinutes: "0", // 严重迟到分钟数
                absenteeismLateMinutes: "0", // 旷工迟到分钟数
                lateAndEarlyOnTime: "", // 上班最多可晚时间
                lateAndEarlyOffTime: "", // 下班最多可早走时间
                workTime: 0, // 工作时长分钟数.
                needLimitWorkTime: true, // 工作时长不足是否记为早退.
            },
            timeType: 1,  // 打卡次数 1:每天两次 2:每次4次 3:每天6次
            time1: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: "",
                offDutyNextDay: false
            },
            time2: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: "",
                offDutyNextDay: false
            },
            time3: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: "",
                offDutyNextDay: false
            },
            typeList: [
                {
                    name: lp.shiftForm.oneTimeDayLabel,
                    value: 1
                },
                {
                    name: lp.shiftForm.secondTimeDayLabel,
                    value: 2
                }, {
                    name: lp.shiftForm.thirdTimeDayLabel,
                    value: 3
                }
            ]
        };
    },
    afterRender() {
        if (this.bind.form && this.bind.form.id && this.bind.form.id !== '') {
            this.bind.fTitle = lp.editShift;
        }
        this.calWorkTime();
        console.log(this.bind)
    },
    clickChangeLimitWorkTime(){
        this.bind.form.needLimitWorkTime = !this.bind.form.needLimitWorkTime;
    },
    clickChangeOffDutyNextDay(timeType) {
        if (timeType === 1) {
            this.bind.time1.offDutyNextDay = !this.bind.time1.offDutyNextDay;
        } else if (timeType === 2) {
            this.bind.time2.offDutyNextDay = !this.bind.time2.offDutyNextDay;
        }  else if (timeType === 3) {
            this.bind.time3.offDutyNextDay = !this.bind.time3.offDutyNextDay;
        }
        this.calWorkTime();
    },
    // 前端显示
    formatWorkTime(workTime) {
        return convertMinutesToHoursAndMinutes(workTime);
    },
    // 计算 workTime
    calWorkTime() {
        let workTime = 0;
        if (this.bind.timeType === 1) {
            workTime = this._calTimeMinute(this.bind.time1);
        } else if (this.bind.timeType === 2) {
            this.bind.time1.offDutyNextDay = false;// 最后一个下班才能是跨天的
            workTime += this._calTimeMinute(this.bind.time1);
            workTime += this._calTimeMinute(this.bind.time2);
        } else if (this.bind.timeType === 3) {
            this.bind.time1.offDutyNextDay = false;// 最后一个下班才能是跨天的
            this.bind.time2.offDutyNextDay = false;// 最后一个下班才能是跨天的
            workTime += this._calTimeMinute(this.bind.time1);
            workTime += this._calTimeMinute(this.bind.time2);
            workTime += this._calTimeMinute(this.bind.time3);
        }
        this.bind.form.workTime = workTime;
    },
    _calTimeMinute(time) {
        let workTime = 0;
        const onDutyTime = time.onDutyTime.split(":");
        const offDutyTime = time.offDutyTime.split(":");
        let onDutyDate = new Date();
        let offDutyDate = new Date();
        if (onDutyTime && onDutyTime.length > 1 && offDutyTime && offDutyTime.length > 1) {
            const onDutyTimehour = parseInt(onDutyTime[0]);
            const onDutyTimeminute = parseInt(onDutyTime[1]);
            onDutyDate.setHours(onDutyTimehour);
            onDutyDate.setMinutes(onDutyTimeminute);
            const offDutyTimehour = parseInt(offDutyTime[0]);
            const offDutyTimeminute = parseInt(offDutyTime[1]);
            offDutyDate.setHours(offDutyTimehour);
            offDutyDate.setMinutes(offDutyTimeminute);
            if (time.offDutyNextDay) {
                // 增加 1 天
                offDutyDate.setDate(offDutyDate.getDate() + 1);
            }
            workTime = (offDutyDate.getTime() - onDutyDate.getTime()) / 1000 / 60; // 分钟数
        }
        return workTime;
    },
    // o time picker 控件使用
    setTimeValue(key, value) {
        setJSONValue(key, value, this.bind);
        this.calWorkTime();
    },
    // o time minute selector 控件返回结果使用
    setSelectorValue(key, value) {
        setJSONValue(key, value, this.bind);
    },
    closeShift() {
        this.$topParent.publishEvent('shift', {});
        this.$parent.closeFormVm();
    },
    async submitAdd() {
        if (isEmpty(this.bind.form.shiftName)) {
            o2.api.page.notice(lp.shiftForm.shiftNameNotEmpty, 'error');
            return ;
        }
        let myForm = this.bind.form;
        myForm.properties = {
            timeList: []
        };
        if (this.bind.timeType === 1) {
            if (isEmpty(this.bind.time1.onDutyTime)) {
                o2.api.page.notice(lp.shiftForm.onDutyTimeNotEmpty, 'error');
                return ;
            }
            if (isEmpty(this.bind.time1.offDutyTime)) {
                o2.api.page.notice(lp.shiftForm.offDutyTimeNotEmpty, 'error');
                return ;
            }
            myForm.properties.timeList.push(this.bind.time1);
        } else if (this.bind.timeType === 2) {
            if (isEmpty(this.bind.time1.onDutyTime) || isEmpty(this.bind.time2.onDutyTime)) {
                o2.api.page.notice(lp.shiftForm.onDutyTimeNotEmpty, 'error');
                return ;
            }
            if (isEmpty(this.bind.time1.offDutyTime) || isEmpty(this.bind.time2.offDutyTime)) {
                o2.api.page.notice(lp.shiftForm.offDutyTimeNotEmpty, 'error');
                return ;
            }
            this.bind.time1.offDutyNextDay = false; // 最后一个下班才能是跨天的
            myForm.properties.timeList.push(this.bind.time1);
            myForm.properties.timeList.push(this.bind.time2);
        } else if (this.bind.timeType === 3) {
            if (isEmpty(this.bind.time1.onDutyTime) || isEmpty(this.bind.time2.onDutyTime) || isEmpty(this.bind.time3.onDutyTime)) {
                o2.api.page.notice(lp.shiftForm.onDutyTimeNotEmpty, 'error');
                return ;
            }
            if (isEmpty(this.bind.time1.offDutyTime ) || isEmpty(this.bind.time2.offDutyTime) || isEmpty(this.bind.time3.offDutyTime)) {
                o2.api.page.notice(lp.shiftForm.offDutyTimeNotEmpty, 'error');
                return ;
            }
            this.bind.time1.offDutyNextDay = false;// 最后一个下班才能是跨天的
            this.bind.time2.offDutyNextDay = false;// 最后一个下班才能是跨天的
            myForm.properties.timeList.push(this.bind.time1);
            myForm.properties.timeList.push(this.bind.time2);
            myForm.properties.timeList.push(this.bind.time3);
        }
        debugger
        if (myForm.seriousTardinessLateMinutes !== "0" && !isPositiveInt(myForm.seriousTardinessLateMinutes)) {
            o2.api.page.notice(lp.shiftForm.seriousTardinessLateMinutesNeedNumber, 'error');
            return ;
        }
        if (myForm.absenteeismLateMinutes !== "0" && !isPositiveInt(myForm.absenteeismLateMinutes)) {
            o2.api.page.notice(lp.shiftForm.absenteeismLateMinutesNeedNumber, 'error');
            return ;
        }
        if (myForm.id && myForm.id !== '') {
            const json = await attendanceShiftAction("shiftUpdate", myForm);
            console.debug('更新成功', json);
        } else {
            const json = await attendanceShiftAction("shiftCreate", myForm);
            console.debug('新增成功', json);
        }
        o2.api.page.notice(lp.shiftForm.success, 'success');
        this.closeShift();
    },
   
});
