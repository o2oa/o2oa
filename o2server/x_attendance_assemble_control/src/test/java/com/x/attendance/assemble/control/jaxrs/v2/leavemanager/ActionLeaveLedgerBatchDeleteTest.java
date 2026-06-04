package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import org.junit.jupiter.api.Test;

class ActionLeaveLedgerBatchDeleteTest {

    @Test
    void validateParametersRequiresLeaveTypeId() {
        ActionLeaveLedgerBatchDelete.Wi wi = new ActionLeaveLedgerBatchDelete.Wi();
        wi.setGrantPeriod("2026");

        assertThrows(ExceptionEmptyParameter.class, () -> ActionLeaveLedgerBatchDelete.validateParameters(wi));
    }

    @Test
    void validateParametersRequiresGrantPeriod() {
        ActionLeaveLedgerBatchDelete.Wi wi = new ActionLeaveLedgerBatchDelete.Wi();
        wi.setLeaveTypeId("leaveTypeId");

        assertThrows(ExceptionEmptyParameter.class, () -> ActionLeaveLedgerBatchDelete.validateParameters(wi));
    }

    @Test
    void buildCancelTransactionUsesRemainingAmount() {
        AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
        ledger.setPerson("张三@P");
        ledger.setLeaveTypeId("leaveTypeId");
        ledger.setRemainingAmount(3.5);

        AttendanceV2LeaveTransaction transaction = ActionLeaveLedgerBatchDelete.buildCancelTransaction(ledger);

        assertEquals("张三@P", transaction.getPerson());
        assertEquals("leaveTypeId", transaction.getLeaveTypeId());
        assertEquals(ledger.getId(), transaction.getLedgerId());
        assertEquals(BizTypeEnum.CANCEL.getValue(), transaction.getBizType());
        assertEquals(3.5, transaction.getAmount());
    }

    @Test
    void buildCancelTransactionDefaultsEmptyRemainingAmountToZero() {
        AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();

        AttendanceV2LeaveTransaction transaction = ActionLeaveLedgerBatchDelete.buildCancelTransaction(ledger);

        assertEquals(0.0, transaction.getAmount());
    }
}
