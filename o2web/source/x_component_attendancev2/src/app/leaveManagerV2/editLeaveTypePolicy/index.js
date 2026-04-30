import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { hideLoading, isEmpty, setJSONValue, showLoading } from "../../../utils/common";
import { leaveManagerAction } from "../../../utils/actions";
import template from "./template.html";
import oInput from "../../../components/o-input";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import oMonthDaySelector from "../../../components/o-month-day-selector";

function defaultGrantAmountType() {
    return {
        type: "FIXED",
        grantAmount: 5,
        tenureLeaveRules: [
            {maxYears: 2, amount: 5},
            {minYears: 2, amount: 10}
        ]
    };
}

function toPositiveNumber(value) {
    if (value === null || value === undefined || value === "") {
        return null;
    }
    const str = String(value).trim();
    if (!/^\d+(\.\d+)?$/.test(str)) {
        return null;
    }
    const num = Number(str);
    return num > 0 ? num : null;
}

function trimNumber(value) {
    return Number(Number(value).toFixed(2));
}

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
                grantAmountType: { // 额度类型配置
                    type: "FIXED", // FIXED | SERVICELEN  固定额度｜工龄额度
                    grantAmount: 5, // 发放额度
                    tenureLeaveRules: [ // 工龄额度规则，按照工龄年限递增的规则列表，示例：[{maxYears: 2, amount: 5}, {minYears: 2, maxYears: 5, amount: 7}, {minYears: 5, amount: 10}]
                        {maxYears: 2, amount: 5}, 
                        {minYears: 2, maxYears: 5, amount: 7},
                        {minYears: 5, amount: 10}]
                }, 
                expireType: "RELATIVE", // 过期类型 NEVER / RELATIVE
                expireValue: 1, // 过期值 
                expireValueExtendDay: 0, // 过期值延长天数
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
                if (this.bind.form.expireValueExtendDay === null || this.bind.form.expireValueExtendDay === undefined || this.bind.form.expireValueExtendDay === "") {
                    this.bind.form.expireValueExtendDay = 0;
                }
                this.ensureGrantAmountType();
                if (this.bind.form.grantAmountType.type === "SERVICELEN") {
                    this.normalizeTenureLeaveRules();
                }
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
        this.bind.form.expireType = type === "ONE_TIME" ? "NEVER" : "RELATIVE";
    },
    clickChangeGrantAmountType(type) {
        this.ensureGrantAmountType();
        this.bind.form.grantAmountType.type = type;
        if (type === "SERVICELEN") {
            this.normalizeTenureLeaveRules();
        }
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
        if (form.grantType === "MONTHLY" && !this.isValidGrantTypeMonthValue(this.bind.grantTypeValueForMonth)) {
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
        if ((form.grantType === "MONTHLY" || form.grantType === "ONE_TIME") && !this.isValidGrantAmount(form.grantAmount)) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantAmountPlaceholder, 'error');
            return;
        }
        if (form.grantType === "YEARLY" && !this.validateGrantAmountType()) {
            return;
        }
        if (form.expireType === "RELATIVE" && !this.isValidGrantAmount(form.expireValue)) {
            o2.api.page.notice(lp.leaveManagerV2.policy.expireValuePlaceholder, 'error');
            return;
        }
        if (form.expireType === "RELATIVE" && !this.isValidExtendDay(form.expireValueExtendDay)) {
            o2.api.page.notice("过期值延长天数必须是大于等于0的整数！", 'error');
            return;
        }
        const postForm = Object.assign({}, form);
        if (form.grantType === "YEARLY") {
            postForm.expireValue = Number(form.expireValue) * 365 + Number(form.expireValueExtendDay || 0);
        } else if (form.grantType === "MONTHLY") {
            postForm.expireValue = Number(form.expireValue) * 30 + Number(form.expireValueExtendDay || 0);
        }
        if (!form.id) { // 新增默认立即发放
            postForm.isGrantImmediately = true; 
        }
        if (this.submitLoading) {
            return;
        }
        this.submitLoading = true;
        try {
            await showLoading(this);
            console.debug("submit form", postForm);
            const result = await leaveManagerAction("policyPost", postForm);
            console.log(result);
            o2.api.page.notice(lp.saveSuccess, 'success');
            this.close();
        } finally {
            this.submitLoading = false;
            await hideLoading(this);
        }
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
    isValidExtendDay(input) {
        // 过期延长天数允许为 0
        if (!/^\d+$/.test(input)) return false;
        const num = Number(input);
        return num >= 0;
    },
    ensureGrantAmountType() {
        if (!this.bind.form.grantAmountType) {
            this.bind.form.grantAmountType = defaultGrantAmountType();
            return;
        }
        const grantAmountType = this.bind.form.grantAmountType;
        if (grantAmountType.type !== "FIXED" && grantAmountType.type !== "SERVICELEN") {
            grantAmountType.type = "FIXED";
        }
        if (grantAmountType.grantAmount === null || grantAmountType.grantAmount === undefined || grantAmountType.grantAmount === "") {
            grantAmountType.grantAmount = 5;
        }
        if (!grantAmountType.tenureLeaveRules || grantAmountType.tenureLeaveRules.length < 2) {
            grantAmountType.tenureLeaveRules = defaultGrantAmountType().tenureLeaveRules;
        }
    },
    normalizeTenureLeaveRules() {
        this.ensureGrantAmountType();
        const rules = this.bind.form.grantAmountType.tenureLeaveRules;
        while (rules.length < 2) {
            const lastBoundary = rules.length > 0 ? toPositiveNumber(rules[rules.length - 1].maxYears || rules[rules.length - 1].minYears) : 2;
            const boundary = lastBoundary || rules.length + 1;
            rules.push({minYears: boundary, amount: 5});
        }
        let currentMin = 0;
        for (let i = 0; i < rules.length; i++) {
            const rule = rules[i];
            const amount = toPositiveNumber(rule.amount);
            rule.amount = amount === null ? rule.amount : amount;
            if (i === 0) {
                rule.minYears = null;
            } else {
                rule.minYears = currentMin;
            }
            if (i === rules.length - 1) {
                rule.maxYears = null;
            } else {
                let maxYears = toPositiveNumber(rule.maxYears);
                if (maxYears === null || maxYears <= currentMin) {
                    maxYears = currentMin + 1;
                }
                rule.maxYears = trimNumber(maxYears);
                currentMin = rule.maxYears;
            }
        }
        this.bind.form.grantAmountType.tenureLeaveRules = rules;
    },
    addTenureLeaveRule() {
        this.normalizeTenureLeaveRules();
        const rules = this.bind.form.grantAmountType.tenureLeaveRules;
        const lastRule = rules[rules.length - 1];
        const minYears = toPositiveNumber(lastRule.minYears) || rules.length;
        rules.splice(rules.length - 1, 0, {
            minYears,
            maxYears: trimNumber(minYears + 1),
            amount: lastRule.amount || 5
        });
        this.normalizeTenureLeaveRules();
    },
    deleteTenureLeaveRule(rule) {
        this.normalizeTenureLeaveRules();
        const rules = this.bind.form.grantAmountType.tenureLeaveRules;
        if (rules.length <= 2) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantTypeRuleLengthLabel, "error");
            return;
        }
        for (let i = 0; i < rules.length; i++) {
            if (rules[i] === rule) {
                rules.splice(i, 1);
                break;
            }
        }
        this.normalizeTenureLeaveRules();
    },
    changeTenureRuleMaxYears(rule, event) {
        rule.maxYears = event.target.value;
        this.normalizeTenureLeaveRules();
    },
    changeTenureRuleAmount(rule, event) {
        rule.amount = event.target.value;
    },
    validateGrantAmountType() {
        debugger;
        // this.ensureGrantAmountType();
        const grantAmountType = this.bind.form.grantAmountType;
        if (grantAmountType.type === "FIXED") {
            const grantAmount = toPositiveNumber(grantAmountType.grantAmount);
            if (grantAmount === null) {
                o2.api.page.notice(lp.leaveManagerV2.policy.grantAmountPlaceholder, "error");
                return false;
            }
            grantAmountType.grantAmount = grantAmount;
            return true;
        }
        // this.normalizeTenureLeaveRules();
        const rules = grantAmountType.tenureLeaveRules;
        if (!rules || rules.length < 2) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantTypeRuleLengthLabel, "error");
            return false;
        }
        const result = [];
        for (let i = 0; i < rules.length; i++) {
            const rule = rules[i];
            const amount = toPositiveNumber(rule.amount);
            if (amount === null) {
                o2.api.page.notice(lp.leaveManagerV2.policy.grantServiceAmountPlaceholder, "error");
                return false;
            }
            const item = {amount};
            if (i > 0) {
                item.minYears = Number(rule.minYears);
            }
            if (i < rules.length - 1) {
                item.maxYears = Number(rule.maxYears);
            }
            result.push(item);
        }
        grantAmountType.tenureLeaveRules = result;
        return true;
    },

    // 关闭当前窗口
    close() {
        this.$parent.publishEvent('leaveTypePolicy', {});
        this.$parent.closeFormVm();
    },
});
