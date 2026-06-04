package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

public class ActionLeaveTypeListWithAccount extends BaseAction {

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        List<AttendanceV2LeaveType> types = getLeaveTypeList(null);
        List<Wo> wos = types.stream().map(type -> {
            Wo wo = Wo.copier.copy(type);
            return wo;
        }).collect(Collectors.toList());

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<AttendanceV2LeaveAccount> accounts = emc.listEqual(AttendanceV2LeaveAccount.class,
                    AttendanceV2LeaveAccount.person_FIELDNAME, effectivePerson.getDistinguishedName());
            attachAccounts(wos, accounts);
        }

        result.setData(wos);
        return result;
    }

    static void attachAccounts(List<Wo> wos, List<AttendanceV2LeaveAccount> accounts) {
        Map<String, AttendanceV2LeaveAccount> accountMap = accounts == null ? Collections.emptyMap() : accounts.stream()
                .filter(account -> account.getLeaveTypeId() != null)
                .collect(Collectors.toMap(AttendanceV2LeaveAccount::getLeaveTypeId, account -> account,
                        (first, second) -> first));
        for (Wo wo : wos) {
            if (QuotaTypeEnum.QUOTA.getValue().equals(wo.getQuotaType())) {
                wo.setAccount(accountMap.get(wo.getId()));
            }
        }
    }

    public static class Wo extends AttendanceV2LeaveType {

        private static final long serialVersionUID = 6999089908489362626L;
        static WrapCopier<AttendanceV2LeaveType, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeaveType.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);

        @FieldDescribe("当前用户假期账户")
        private AttendanceV2LeaveAccount account;

        public AttendanceV2LeaveAccount getAccount() {
            return account;
        }

        public void setAccount(AttendanceV2LeaveAccount account) {
            this.account = account;
        }
    }
}
