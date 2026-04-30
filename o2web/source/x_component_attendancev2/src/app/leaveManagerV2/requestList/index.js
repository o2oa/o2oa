import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { formatPersonName, hideLoading, isEmpty, showLoading } from "../../../utils/common";
import { leaveManagerActionListByPaging } from "../../../utils/actions";
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
            filterList: [],
            form: {
                startDate: "",
                endDate: "",
            },
            requestList: [],
            pagerData: {
                page: 1,
                totalCount: 0,
                totalPage: 1,
                size: 15,
            },
        };
    },
    beforeRender() {
        const today = new Date();
        const start = new Date(today);
        start.setDate(start.getDate() - 30);
        this.bind.form.startDate = this.formatDate(start);
        this.bind.form.endDate = this.formatDate(today);
    },
    clickBackTypeList() {
        this.$parent.clickBackTypeList();
    },
    search() {
        this.bind.pagerData.page = 1;
        this.queryData();
    },
    loadData(e) {
        if (e && e.detail && e.detail.module && e.detail.module.bind) {
            this.bind.pagerData.page = e.detail.module.bind.page || 1;
            this.queryData();
        }
    },
    async queryData() {
        if (this.queryLoading) {
            return;
        }
        if (this.bind.filterList.length < 1) {
            o2.api.page.notice(lp.leaveManagerV2.request.filterEmptyPlaceholder, "error");
            return;
        }
        if (isEmpty(this.bind.form.startDate) || isEmpty(this.bind.form.endDate)) {
            o2.api.page.notice(lp.leaveManagerV2.request.dateEmptyPlaceholder, "error");
            return;
        }
        if (new Date(this.bind.form.startDate).getTime() > new Date(this.bind.form.endDate).getTime()) {
            o2.api.page.notice(lp.leaveManagerV2.request.dateRangeError, "error");
            return;
        }
        this.queryLoading = true;
        try {
            await showLoading(this);
            const json = await leaveManagerActionListByPaging(
                "requestSearch",
                this.bind.pagerData.page,
                this.bind.pagerData.size,
                {
                    filterList: this.bind.filterList,
                    startDate: this.bind.form.startDate,
                    endDate: this.bind.form.endDate,
                }
            );
            if (json) {
                this.bind.requestList = json.data || [];
                this.bind.pagerData.totalCount = json.count || 0;
            }
        } finally {
            this.queryLoading = false;
            await hideLoading(this);
        }
    },
    formatPerson(person) {
        return formatPersonName(person);
    },
    formatLeaveType(request) {
        return request && request.leaveType ? request.leaveType.name : "";
    },
    formatDateTime(value) {
        if (!value) {
            return "";
        }
        if (typeof value === "string") {
            return value.length > 16 ? value.substring(0, 16) : value;
        }
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) {
            return "";
        }
        const ymd = this.formatDate(date);
        const hour = date.getHours() > 9 ? `${date.getHours()}` : `0${date.getHours()}`;
        const minute = date.getMinutes() > 9 ? `${date.getMinutes()}` : `0${date.getMinutes()}`;
        return `${ymd} ${hour}:${minute}`;
    },
    formatDate(date) {
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        return `${year}-${month > 9 ? month : `0${month}`}-${day > 9 ? day : `0${day}`}`;
    },
    formatDuration(duration) {
        if (duration === null || duration === undefined || duration === "") {
            return "";
        }
        const number = Number(duration);
        if (!Number.isFinite(number)) {
            return "";
        }
        return Number.isInteger(number) ? `${number}` : `${Number(number.toFixed(2))}`;
    },
    formatStatus(status) {
        if (status === "APPLYING") {
            return lp.leaveManagerV2.request.statusApplying;
        }
        if (status === "REJECTED") {
            return lp.leaveManagerV2.request.statusRejected;
        }
        if (status === "CANCELLED") {
            return lp.leaveManagerV2.request.statusCancelled;
        }
        return status || "";
    },
});
