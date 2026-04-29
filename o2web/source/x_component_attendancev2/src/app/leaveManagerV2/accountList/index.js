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
            filterList: [], // 账号列表的筛选条件列表
            accountList: [], 
            leaveTypeList: [],
            userList: [],
        };
    },
    afterRender() {
         
    },
    clickBackTypeList() {
       this.$parent.clickBackTypeList();
    },
});
