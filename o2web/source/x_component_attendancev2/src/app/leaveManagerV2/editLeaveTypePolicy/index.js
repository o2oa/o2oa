import { component as content } from "@o2oa/oovm";
import { lp, o2, component as c } from "@o2oa/component";
import { hideLoading, isEmpty, setJSONValue, showLoading } from "../../../utils/common";
import { leaveManagerAction } from "../../../utils/actions";
import template from "./template.html";
import style from "./style.scope.css";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import oMonthDaySelector from "../../../components/o-month-day-selector";

function defaultGrantAmountType() {
    return {
        type: "FIXED",
        grantAmount: 5,
        tenureLeaveRules: [
            { maxYears: 2, amount: 5 },
            { minYears: 2, amount: 10 }
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
    style,
    template,
    components: { oOrgPersonSelector, oMonthDaySelector },
    autoUpdate: true,
    bind() {
        return {
            lp,
            fTitle: lp.leaveManagerV2.addTypePolicy,
            form: {
                policyName: "",
                leaveTypeId: "",
                grantCron: "", // 定时器
                grantScopeType: "ALL", // ALL | DEPARTMENT
                grantScopeList: [], // Concrete grant scope list.
                grantExcludeList: [], // Excluded identities for the grant scope.
                grantAmountTypeUseScript: false, // 是否启用脚本计算额度
                grantAmount: 0, // Grant amount.
                grantScript: "", // 脚本内容
                expireType: "AFTER_GRANT", // AFTER_GRANT / THIS_YEAR / NEXT_YEAR
                expireValue: "", // 当 expireType = AFTER_GRANT 时使用.
                expireMonthDay: "",
                carryForward: false, // Whether carry-forward is allowed.
                maxCarryForward: 0, // Maximum carry-forward amount.
                policyVersion: "1",
                active: true, // 是否启用
            },
            expireTypeList: [
                { key: "THIS_YEAR", name: lp.leaveManagerV2.policy.expireTypeTHIS_YEAR },
                { key: "NEXT_YEAR", name: lp.leaveManagerV2.policy.expireTypeNEXT_YEAR },
                { key: "AFTER_GRANT", name: lp.leaveManagerV2.policy.expireTypeAFTER_GRANT }
            ],
            leaveType: null
        }
    },
    // Load data first.
    async beforeRender() {
        if (this.bind.updateId) {
            const policy = await leaveManagerAction("policyGet", this.bind.updateId);
            if (policy) {
                this.bind.form = policy;
                this.normalizeExpireFields();
                this.bind.fTitle = lp.leaveManagerV2.editTypePolicy;
                if (this.bind.form.grantAmountTypeUseScript) {
                    this.loadScriptEditor();
                }
            }
        }
    },
    async afterRender() {
        console.debug("  leave type policy form after render", this.bind);
        if (this.bind.form.leaveTypeId) {
            const leaveType = await leaveManagerAction("typeGet", this.bind.form.leaveTypeId);
            this.bind.leaveType = leaveType;
        }
        this.loadGrantCronClick();
    },
    normalizeExpireFields() {
        const form = this.bind.form;
        if (!form.expireType || !this.isValidExpireType(form.expireType)) {
            form.expireType = "AFTER_GRANT";
        }
        if (form.expireValue === null || form.expireValue === undefined) {
            form.expireValue = "";
        }
        if (form.expireMonthDay === null || form.expireMonthDay === undefined) {
            form.expireMonthDay = "";
        }
    },
    // 定时器表达式工具加载
    loadGrantCronClick() {
        const cronTarget = this.dom.querySelector("#grantCronNode");
        o2.requireApp("Template", "widget.CronPicker", () => {
            this.cronPicker = new MWF.xApplication.Template.widget.CronPicker(
                c.content,
                cronTarget,
                c,
                {},
                {
                    style: "design",
                    position: {
                        //node 固定的位置
                        x: "right",
                        y: "auto",
                    },
                    onSelect: (value) => {
                        this.bind.form.grantCron = value;
                    },
                    onQueryLoad: () => {
                        console.log(this.bind.form.grantCron);
                        if (!this.cronPicker.node) {
                            this.cronPicker.options.value = this.bind.form.grantCron;
                        } else {
                            this.cronPicker.setCronValue(this.bind.form.grantCron);
                        }
                    },
                }
            );
        });
    },
    async tick() {
        return new Promise((resovle, reject) => {
            setTimeout(() => {
                resovle()
            }, 150);

        });
    },
    // 额度脚本编辑器加载
    async loadScriptEditor() {
        await this.tick();
        // MWF.require("MWF.widget.ScriptArea", null, false);
        this.grantScriptNode = this.dom.querySelector("#grantScriptNode")
        this.grantScriptNode.innerHTML = "";
        o2.require("MWF.widget.ScriptArea", () => {
            this.grantScriptArea = new MWF.widget.ScriptArea(this.grantScriptNode, {
                "type": "service",
                "api": "../api/index.html#module-print",
                "title": lp.leaveManagerV2.policy.grantAmountScriptLabel,
                //"isload" : true,
                "isbind": false,
                // "forceType": "ace",
                "maxObj": c.content,
                "onChange": function () {
                    this.bind.form.grantScript = this.grantScriptArea.toJson().code;
                }.bind(this)
            });
            //
            const host = window.location.origin;
            if (!host) host = window.location.protocol + "//" + window.location.host;
            const defaultText = "/********************\n" +
                "API Document: " + host + "/api\n" +
                "this.org; //组织快速访问方法\n" +
                "grantPerson; //需要计算的用户对象，如：{\"distinguishedName\":\"张三@zhangsan@P\",\"unique\":\"zhangsan\",\"name\":\"张三\"}\n" +
                "根据这个 grantPerson 用户计算出这个用户本次需要发放的额度，然后 return 返回\n" +
                "return 10; //返回额度10天\n" +
                "********************/";
            const v = this.bind.form.grantScript || defaultText;
            this.grantScriptArea.load({ code: v });
        });
    },
    clickChangeGrantAmountTypeUseScript() {
        this.bind.form.grantAmountTypeUseScript = !this.bind.form.grantAmountTypeUseScript;
        if (this.bind.form.grantAmountTypeUseScript) {
            this.loadScriptEditor();
        }
    },
    clickChangeGrantScopeType(type) {
        this.bind.form.grantScopeType = type;
        if (!this.bind.form.grantScopeList) {
            this.bind.form.grantScopeList = [];
        }
        if (!this.bind.form.grantExcludeList) {
            this.bind.form.grantExcludeList = [];
        }
    },
    changeExpireType(e) {
        const expireType = e.target.value;
        this.bind.form.expireType = expireType;
        if (expireType === "AFTER_GRANT") {
            this.bind.form.expireMonthDay = "";
        } else {
            this.bind.form.expireValue = "";
        }
    },
    // Used by the o-month-day-selector return value.
    setSelectorValue(key, value) {
        setJSONValue(key, value, this.bind);
    },
    async submit() {
        const form = this.bind.form;
        form.policyName = this.bind.leaveType.name + "_发放规则";
        if (form.grantScopeType !== "ALL" && form.grantScopeList.length === 0) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantScopeListPickerPlaceholder, 'error');
            return;
        }
        if (isEmpty(form.grantCron)) {
            o2.api.page.notice(lp.leaveManagerV2.policy.grantCronNotEmpty, 'error');
            return;
        }
        if (form.grantAmountTypeUseScript) {
            if (isEmpty(form.grantScript)) {
                o2.api.page.notice(lp.leaveManagerV2.policy.grantAmountScriptNotEmpty, 'error');
                return;
            }
        } else {
            if (!this.isValidGrantAmount(form.grantAmount)) {
                o2.api.page.notice(lp.leaveManagerV2.policy.grantAmountPlaceholder, 'error');
                return;
            }
        }
        const postForm = Object.assign({}, form);
        if (!this.validateExpireFields(postForm)) {
            return;
        }
        delete postForm.expireValueExtendDay;
        if (postForm.expireType === "AFTER_GRANT") {
            postForm.expireValue = Number(postForm.expireValue);
            postForm.expireMonthDay = "";
        } else {
            postForm.expireValue = null;
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
        // Ensure this is a plain integer and avoid values like "1e2".
        if (!/^\d+$/.test(input)) return false;
        const num = Number(input);
        return num >= 1 && num <= 28;
    },
    isValidGrantAmount(input) {
        // Ensure this is a plain integer and avoid values like "1e2".
        if (!/^\d+$/.test(input)) return false;
        const num = Number(input);
        return num >= 1;
    },
    isValidExpireType(type) {
        return ["THIS_YEAR", "NEXT_YEAR", "AFTER_GRANT"].indexOf(type) > -1;
    },
    isValidExpireMonthDay(value) {
        if (!/^\d{2}-\d{2}$/.test(value || "")) {
            return false;
        }
        const arr = value.split("-");
        const month = Number(arr[0]);
        const day = Number(arr[1]);
        const monthDays = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
        return month >= 1 && month <= 12 && day >= 1 && day <= monthDays[month - 1];
    },
    validateExpireFields(form) {
        if (!this.isValidExpireType(form.expireType)) {
            o2.api.page.notice(lp.leaveManagerV2.policy.expireTypePlaceholder, "error");
            return false;
        }
        if (form.expireType === "AFTER_GRANT") {
            if (!this.isValidGrantAmount(form.expireValue)) {
                o2.api.page.notice(lp.leaveManagerV2.policy.expireValuePlaceholder, "error");
                return false;
            }
        } else if (!this.isValidExpireMonthDay(form.expireMonthDay)) {
            o2.api.page.notice(lp.leaveManagerV2.policy.expireMonthDayPlaceholder, "error");
            return false;
        }
        return true;
    },
    isValidExtendDay(input) {
        // Extra expiration days can be 0.
        if (!/^\d+$/.test(input)) return false;
        const num = Number(input);
        return num >= 0;
    },
    // Close current window.
    close() {
        this.$parent.publishEvent('leaveTypePolicy', {});
        this.$parent.closeFormVm();
    },
});
