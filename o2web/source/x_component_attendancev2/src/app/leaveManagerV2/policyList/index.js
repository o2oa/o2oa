import { component as content } from "@o2oa/oovm";
import { lp, o2, layout } from "@o2oa/component";
import { lpFormat, formatPersonName } from "../../../utils/common";
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
            currentLeaveType: null, // 当前选中的假期类型
            leaveTypePolicyList: [], // 某一个假期类型的规则列表
        };
    },
    afterRender() {
        this.loadLeaveType();
        this.listenEventBus();
    },
    listenEventBus() {
        this.$topParent.listenEventBus("leaveTypePolicy", (data) => {
            console.log("接收到了leaveTypePolicy消息", data);
            this.refreshPolicyList();
        });
    },
    async loadLeaveType() {
        if (this.bind.typeId) {
            this.bind.currentLeaveType = await leaveManagerAction("typeGet", this.bind.typeId);
        }
        this.refreshPolicyList();
    },
    async refreshPolicyList() {
        if (!this.bind.currentLeaveType) {
            console.log("没有选中的假期类型，无法刷新规则列表");
            return;
        }
        const typeId = this.bind.currentLeaveType.id;
        await this.loadPolicyList(typeId);
    },
    async loadPolicyList(typeId) {
        const list = await leaveManagerAction("policyListWithTypeId", typeId)
        this.bind.leaveTypePolicyList = list || [];
    },
    formatScopeList(policy) {
        if (policy.grantScopeType === "ALL") {
            return lp.leaveManagerV2.policy.grantScopeTypeALL;
        } else {
            const list = policy.grantScopeList || [];
            const nameList = list.map((item) => formatPersonName(item));
            return nameList.join("|");
        }
    },
    formatGrantType(policy) {
        if (!policy || !policy.grantType) {
            return "";
        }
        if (policy.grantType === "ONE_TIME") {
            return lp.leaveManagerV2.policy.grantTypeONE_TIME;
        } else if (policy.grantType === "MONTHLY") {
            return `每月${policy.grantTypeValue.substring(3)}日，发放${policy.grantAmount}天`;
        } else {
            if (policy.grantAmountType ) { 
                if (policy.grantAmountType.type === "FIXED") {
                    return `每年${policy.grantTypeValue.substring(2, 4)}月${policy.grantTypeValue.substring(5)}日，发放${policy.grantAmountType.grantAmount}天`;
                } else {
                    return `每年${policy.grantTypeValue.substring(2, 4)}月${policy.grantTypeValue.substring(5)}日，按司龄发放`;
                }
            }
            return lp.leaveManagerV2.policy.grantTypeYEARLY;
        }
    },
    clickBackTypeList() {
       this.$parent.clickBackTypeList();
    },
    clickAddPolicy() {
        this.$topParent.openLeaveTypePolicyForm({ bind: { form: { leaveTypeId: this.bind.currentLeaveType.id } } });
    },
    clickEditLeaveTypePolicy(id) {
        console.log("点击编辑假期类型规程", id);
        this.$topParent.openLeaveTypePolicyForm({ bind: { updateId: id } });
    },
    clickDeletePolicy(id) {
        const policy = this.bind.leaveTypePolicyList.find((g) => g.id === id);
        var _self = this;
        const c = lpFormat(lp, "leaveManagerV2.confirmDelete", { name: policy.policyName });
        o2.api.page.confirm(
            "warn",
            lp.alert,
            c,
            300,
            100,
            function () {
                _self.deletePolicy(id);
                this.close();
            },
            function () {
                this.close();
            }
        );
    },
    async deletePolicy(id) {
        this.$topParent.closeFormVm();
        const result = await leaveManagerAction("policyDelete", id)
        console.debug(result);
        this.refreshPolicyList();
    },
});
