import { component as content } from "@o2oa/oovm";
import { lp, o2, layout } from "@o2oa/component";
import { lpFormat, formatPersonName, showLoading, hideLoading } from "../../../utils/common";
import { leaveManagerAction } from "../../../utils/actions";
import oPager from "../../../components/o-pager";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import oDatePicker from "../../../components/o-date-picker";
import template from "./template.html";

export default content({
    template,
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
    async queryData() {
        if (this.queryLoading) {
            return;
        }
        this.queryLoading = true;
        try {
            await showLoading(this);
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
            await hideLoading(this);
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
});
