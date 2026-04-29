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
            filterList: [], // 账号列表的筛选条件列表
            accountList: [],
            leaveTypeList: [],
            personList: [],
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
    async queryData() {
        if (this.queryLoading) {
            return;
        }
        this.queryLoading = true;
        try {
            await showLoading(this);
            const json = await leaveManagerAction("accountSearch", { filterList: this.bind.filterList });
            this.bind.accountList = json.accountList || [];
            this.bind.leaveTypeList = json.leaveTypeList || [];
            this.bind.personList = json.personList || [];
        } finally {
            this.queryLoading = false;
            await hideLoading(this);
        }
        console.log("result", this.bind);
    },
});
