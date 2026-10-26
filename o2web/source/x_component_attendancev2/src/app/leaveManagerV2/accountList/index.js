import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { formatPersonName, showLoading, hideLoading, isEmpty } from "../../../utils/common";
import { leaveManagerAction } from "../../../utils/actions";
import oPager from "../../../components/o-pager";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import oDatePicker from "../../../components/o-date-picker";
import template from "./template.html";
import style from "../style.scope.css";

function defaultAdjustForm() {
    return {
        adjustedAmount: "",
        expireTime: "",
    };
}

export default content({
    template,
    style,
    components: { oPager, oDatePicker, oOrgPersonSelector },
    autoUpdate: true,
    bind() {
        return {
            lp,
            filterList: [], // Filter criteria for account list.
            form: {
                recursive: true,
            },
            accountList: [],
            leaveTypeList: [],
            personList: [],
            accountTableList: [],
            adjustFormShow: false,
            adjustSubmitting: false,
            currentAdjustData: null,
            adjustForm: defaultAdjustForm(),
        };
    },
    afterRender() {

    },
    clickBackTypeList() {
        this.$parent.clickBackTypeList();
    },
    search() {
        if (this.bind.filterList.length < 1) {
            o2.api.page.notice(lp.leaveManagerV2.account.filterEmptyPlaceholder, 'error');
            return;
        }
        this.queryData();
    },
    toggleRecursive() {
        this.bind.form.recursive = !this.bind.form.recursive;
    },
    async queryData(useLoading = true) {
        if (this.queryLoading) {
            return;
        }
        this.queryLoading = true;
        try {
            if (useLoading) {
                await showLoading(this);
            }
            const json = await leaveManagerAction("accountSearch", {
                filterList: this.bind.filterList,
                recursive: this.bind.form.recursive,
            });
            if (json) {
                this.bind.accountList = json.accountList || [];
                this.bind.leaveTypeList = json.leaveTypeList || [];
                this.bind.personList = json.personList || [];
                this.bind.accountTableList = this.buildAccountTableList(this.bind.personList, this.bind.leaveTypeList, this.bind.accountList);
            } else {
                 this.bind.accountList =  [];
                this.bind.leaveTypeList =  [];
                this.bind.personList =  [];
                this.bind.accountTableList = [];
            }
        } finally {
            this.queryLoading = false;
            if (useLoading) {
                await hideLoading(this);
            }
        }
    },
    buildAccountTableList(personList, leaveTypeList, accountList) {
        const accountMap = {};
        (accountList || []).forEach((account) => {
            accountMap[`${account.person}_${account.leaveTypeId}`] = account;
        });
        return (personList || []).map((person) => ({
            person,
            personName: formatPersonName(person),
            balanceList: (leaveTypeList || []).map((leaveType) => {
                const account = accountMap[`${person}_${leaveType.id}`];
                const accountValue = account ? this.formatAccountDetail(account) : this.formatEmptyAccountDetail();
                return {
                    leaveTypeId: leaveType.id,
                    leaveTypeName: leaveType.name,
                    quotaType: leaveType.quotaType,
                    limited: leaveType.quotaType == 'QUOTA',
                    value: (leaveType.quotaType == 'QUOTA') ? accountValue.value : lp.leaveManagerV2.type.quotaTypeUnlimited,
                    totalGranted: accountValue.totalGranted,
                    totalUsed: accountValue.totalUsed,
                    balance: accountValue.balance,
                    title: this.formatAccountTitle(accountValue),
                };
            }),
        }));
    },
    formatAccountValue(account) {
        return [
            this.formatNumber(account.totalGranted),
            this.formatNumber(account.totalUsed),
            this.formatNumber(account.balance),
        ].join("/");
    },
    formatAccountDetail(account) {
        const totalGranted = this.formatNumber(account.totalGranted);
        const totalUsed = this.formatNumber(account.totalUsed);
        const balance = this.formatNumber(account.balance);
        return {
            totalGranted,
            totalUsed,
            balance,
            value: [totalGranted, totalUsed, balance].join("/"),
        };
    },
    formatEmptyAccountDetail() {
        return {
            totalGranted: "0",
            totalUsed: "0",
            balance: "0",
            value: "0/0/0",
        };
    },
    formatAccountTitle(accountValue) {
        const accountLp = lp.leaveManagerV2.account;
        return `${accountLp.totalGranted}: ${accountValue.totalGranted} / ${accountLp.totalUsed}: ${accountValue.totalUsed} / ${accountLp.balance}: ${accountValue.balance}`;
    },
    formatNumber(value) {
        if (value === null || value === undefined || value === "") {
            return "0";
        }
        const number = Number(value);
        if (!Number.isFinite(number)) {
            return "0";
        }
        return Number.isInteger(number) ? `${number}` : `${Number(number.toFixed(2))}`;
    },
    clickOpenLedgerAdjust(row, balance) {
        if (!row || !balance || !balance.limited || this.bind.adjustSubmitting) {
            return;
        }
        this.bind.currentAdjustData = {
            person: row.person,
            personName: row.personName,
            leaveTypeId: balance.leaveTypeId,
            leaveTypeName: balance.leaveTypeName,
            balance: balance.balance,
        };
        this.bind.adjustForm = {
            adjustedAmount: balance.balance,
            expireTime: "",
        };
        this.bind.adjustFormShow = true;
    },
    closeLedgerAdjust(force) {
        if (this.bind.adjustSubmitting && force !== true) {
            return;
        }
        this.bind.adjustFormShow = false;
        this.bind.currentAdjustData = null;
        this.bind.adjustForm = defaultAdjustForm();
    },
    async submitLedgerAdjust() {
        if (this.bind.adjustSubmitting) {
            return;
        }
        const current = this.bind.currentAdjustData || {};
        const form = this.bind.adjustForm || {};
        if (isEmpty(current.person) || isEmpty(current.leaveTypeId)) {
            o2.api.page.notice(this.getAdjustLabel("dataEmpty"), "error");
            return;
        }
        const adjustedAmount = String(form.adjustedAmount === undefined || form.adjustedAmount === null ? "" : form.adjustedAmount).trim();
        if (!this.isValidAdjustedAmount(adjustedAmount)) {
            o2.api.page.notice(this.getAdjustLabel("adjustedAmountPlaceholder"), "error");
            return;
        }
        const payload = {
            person: current.person,
            leaveTypeId: current.leaveTypeId,
            adjustedAmount,
            expireTime: form.expireTime || "",
        };
        this.bind.adjustSubmitting = true;
        try {
            await showLoading(this);
            await leaveManagerAction("ledgerAdjust", payload);
            o2.api.page.notice(this.getAdjustLabel("success"), "success");
            this.closeLedgerAdjust(true);
            await this.queryData(false);
        } catch (e) {
            console.error("ledger adjust failed", e);
            o2.api.page.notice(this.getAdjustErrorMessage(e), "error");
        } finally {
            this.bind.adjustSubmitting = false;
            await hideLoading(this);
        }
    },
    isValidAdjustedAmount(value) {
        if (isEmpty(value) || !/^\d+(\.\d{1,2})?$/.test(value)) {
            return false;
        }
        return Number.isFinite(Number(value)) && Number(value) >= 0;
    },
    getAdjustErrorMessage(error) {
        return error && error.message ? error.message : this.getAdjustLabel("fail");
    },
    getAdjustLabel(key) {
        const accountLp = lp.leaveManagerV2 && lp.leaveManagerV2.account ? lp.leaveManagerV2.account : {};
        const text = {
            adjust: "调整",
            title: "调整假期额度",
            person: "人员",
            leaveType: "假期类型",
            currentBalance: "当前剩余额度",
            adjustedAmount: "调整后额度",
            expireTime: "过期时间",
            adjustedAmountPlaceholder: "请输入正确的调整后额度",
            optionalExpireTime: "请选择过期时间",
            submit: "提交",
            submitting: "提交中...",
            success: "调整成功",
            fail: "调整失败",
            dataEmpty: "调整数据为空",
        };
        return accountLp[key] || text[key] || "";
    },
});
