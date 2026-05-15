package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActionLeaveTypeListWithAccountTest {

    @Test
    void attachAccountsOnlySetsAccountForQuotaLeaveType() {
        ActionLeaveTypeListWithAccount.Wo quota = new ActionLeaveTypeListWithAccount.Wo();
        quota.setId("quotaType");
        quota.setQuotaType(QuotaTypeEnum.QUOTA.getValue());
        ActionLeaveTypeListWithAccount.Wo unlimited = new ActionLeaveTypeListWithAccount.Wo();
        unlimited.setId("unlimitedType");
        unlimited.setQuotaType(QuotaTypeEnum.UNLIMITED.getValue());
        AttendanceV2LeaveAccount account = new AttendanceV2LeaveAccount();
        account.setLeaveTypeId("quotaType");
        account.setBalance(8.0);

        List<ActionLeaveTypeListWithAccount.Wo> wos = Arrays.asList(quota, unlimited);
        ActionLeaveTypeListWithAccount.attachAccounts(wos, Collections.singletonList(account));

        assertEquals(account, quota.getAccount());
        assertNull(unlimited.getAccount());
    }
}
