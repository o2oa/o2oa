import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { isEmpty, setJSONValue } from "../../../utils/common";
import { leaveManagerAction } from "../../../utils/actions";
import template from "./template.html";
import oInput from "../../../components/o-input";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import oMonthDaySelector from "../../../components/o-month-day-selector";

export default content({
    template,
    components: { oInput, oOrgPersonSelector, oMonthDaySelector },
    autoUpdate: true,
    bind() {
        return {
            lp,
            fTitle: lp.leaveManagerV2.addTypePolicy,
            form: {
                policyName: "",
                leaveTypeId: "",
                grantScopeType: "ALL", // ALL | DEPARTMENT
                grantScopeList: [], // 发放范围的具体列表
                grantType: "YEARLY", // YEARLY | MONTHLY | ONE_TIME
                grantTypeValue: "Y:01-01", // 发放方式日期规则配置： Y:01-01/MS:1,ME:1/ONE_TIME
                grantAmount: 0, // 发放数量
                grantAmountType: {},
                expireType: "RELATIVE", // 过期类型 NEVER / RELATIVE
                expireValue: 1, // 过期值，单位为年
                carryForward: false, // 是否允许结转
                maxCarryForward: 0, // 最大结转数量
                policyVersion: "1"
            },
            grantTypeValueForYear: '',
            grantTypeValueForMonth: 1, // 默认每月1号发放, 1-28之间
            leaveType: null
        }
    },
    // 先查询数据
    async beforeRender() {
        if (this.bind.updateId) {
            const policy = await leaveManagerAction("policyGet", this.bind.updateId);
            if (policy) {
                this.bind.form = policy;
                this.bind.fTitle = lp.leaveManagerV2.editTypePolicy;
            }
        }
    },
    async afterRender() {
        console.debug("  leave type policy form after render", this.bind);
        if (this.bind.form.leaveTypeId) {
            const leaveType = await leaveManagerAction("typeGet", this.bind.form.leaveTypeId);
            this.bind.leaveType = leaveType;
        }
    },
    clickChangeGrantScopeType(type) {
        this.bind.form.grantScopeType = type;
    },
    clickChangeGrantType(type) {
        this.bind.form.grantType = type;
    },
    // o month day selector 控件返回结果使用
    setSelectorValue(key, value) {
        setJSONValue(key, value, this.bind);
    },
    async submit() {
        const form = this.bind.form;
        if (isEmpty(form.policyName)) {
            o2.api.page.notice(lp.leaveManagerV2.policy.policyNamePlaceholder, 'error');
            return;
        }
        if (form.grantScopeType !== "ALL" && form.grantScopeList.length === 0) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantScopeListPickerPlaceholder, 'error');
            return;
        }
        if (form.grantType === "YEARLY" && isEmpty(this.bind.grantTypeValueForYear)) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantTypeYearPlaceholder, 'error');
            return;
        }
        if (form.grantType === "MONTHLY" && (isValidGrantTypeMonthValue(this.bind.grantTypeValueForMonth))) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantTypeMonthPlaceholder, 'error');
            return;
        }
        if (form.grantType === "ONE_TIME") {
            form.grantTypeValue = `ONE_TIME`;
        } else if (form.grantType === "YEARLY") {
            form.grantTypeValue = `Y:${this.bind.grantTypeValueForYear}`;
        } else if (form.grantType === "MONTHLY") {
            form.grantTypeValue = `MS:${this.bind.grantTypeValueForMonth}`;
        }
        if ((form.grantType === "MONTHLY" || form.grantType === "ONE_TIME") && !isValidGrantAmount(form.grantAmount)) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantAmountPlaceholder, 'error');
            return;
        }
        const result = await leaveManagerAction("policyPost", form);
        console.log(result);
        o2.api.page.notice(lp.saveSuccess, 'success');
        this.close();
    },
    isValidGrantTypeMonthValue(input) {
        // 先判断是不是纯数字（避免 "1e2" 这种）
        if (!/^\d+$/.test(input)) return false;
        const num = Number(input);
        return num >= 1 && num <= 28;
    },
    isValidGrantAmount(input) {
        // 先判断是不是纯数字（避免 "1e2" 这种）
        if (!/^\d+$/.test(input)) return false;
        const num = Number(input);
        return num >= 1;
    },

    // 关闭当前窗口
    close() {
        this.$parent.publishEvent('leaveTypePolicy', {});
        this.$parent.closeFormVm();
    },
});