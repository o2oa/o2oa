package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ActionLeaveLedgerPostTest {

    @Test
    void buildLedgerDefaultsAmountsAndGrantTime() throws Exception {
        Date now = new Date();
        ActionLeaveLedgerPost.Wi wi = new ActionLeaveLedgerPost.Wi();
        wi.setPerson("张三@P");
        wi.setLeaveTypeId("leaveTypeId");
        wi.setGrantPeriod("2026");
        wi.setGrantAmount(10.0);

        AttendanceV2LeaveLedger ledger = ActionLeaveLedgerPost.buildLedger(wi, now, true);

        assertEquals("张三@P", ledger.getPerson());
        assertEquals("leaveTypeId", ledger.getLeaveTypeId());
        assertEquals("2026", ledger.getGrantPeriod());
        assertEquals(10.0, ledger.getGrantAmount());
        assertEquals(0.0, ledger.getUsedAmount());
        assertEquals(10.0, ledger.getRemainingAmount());
        assertEquals(now, ledger.getGrantTime());
        assertEquals(true, ledger.getActive());
    }

    @Test
    void buildLedgerRequiresPerson() {
        Date now = new Date();
        ActionLeaveLedgerPost.Wi wi = new ActionLeaveLedgerPost.Wi();
        wi.setLeaveTypeId("leaveTypeId");
        wi.setGrantPeriod("2026");
        wi.setGrantAmount(10.0);

        assertThrows(ExceptionEmptyParameter.class, () -> ActionLeaveLedgerPost.buildLedger(wi, now, true));
    }

    @Test
    void buildLedgerRequiresGrantAmount() {
        Date now = new Date();
        ActionLeaveLedgerPost.Wi wi = new ActionLeaveLedgerPost.Wi();
        wi.setPerson("张三@P");
        wi.setLeaveTypeId("leaveTypeId");
        wi.setGrantPeriod("2026");

        assertThrows(ExceptionEmptyParameter.class, () -> ActionLeaveLedgerPost.buildLedger(wi, now, false));
    }
}
