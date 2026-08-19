package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ActionLeaveLedgerAdjustTest {

    @Test
    void validateParametersRequiresAdjustedAmount() {
        ActionLeaveLedgerAdjust.Wi wi = new ActionLeaveLedgerAdjust.Wi();
        wi.setPerson("张三@P");
        wi.setLeaveTypeId("leaveTypeId");

        assertThrows(ExceptionEmptyParameter.class,
                () -> ActionLeaveLedgerAdjust.validateParameters(wi, false));
    }

    @Test
    void validateParametersRejectsNegativeAdjustedAmount() {
        ActionLeaveLedgerAdjust.Wi wi = new ActionLeaveLedgerAdjust.Wi();
        wi.setPerson("张三@P");
        wi.setLeaveTypeId("leaveTypeId");
        wi.setAdjustedAmount(-1.0);

        assertThrows(ExceptionWithMessage.class,
                () -> ActionLeaveLedgerAdjust.validateParameters(wi, true));
    }

    @Test
    void buildAdjustmentLedgerCreatesNewActiveLedger() {
        Date now = new Date(123456789L);
        Date expireTime = new Date(223456789L);
        ActionLeaveLedgerAdjust.Wi wi = new ActionLeaveLedgerAdjust.Wi();
        wi.setLeaveTypeId("leaveTypeId");
        wi.setAdjustedAmount(6.0);
        wi.setExpireTime(expireTime);

        AttendanceV2LeaveLedger ledger = ActionLeaveLedgerAdjust.buildAdjustmentLedger("张三@P", wi, now);

        assertEquals("张三@P", ledger.getPerson());
        assertEquals("leaveTypeId", ledger.getLeaveTypeId());
        assertEquals("ADJUST_123456789", ledger.getGrantPeriod());
        assertEquals(6.0, ledger.getGrantAmount());
        assertEquals(0.0, ledger.getUsedAmount());
        assertEquals(6.0, ledger.getRemainingAmount());
        assertEquals(now, ledger.getGrantTime());
        assertEquals(expireTime, ledger.getExpireTime());
        assertTrue(ledger.getActive());
    }

    @Test
    void buildNewLedgerAdjustmentCreatesAdjustTransaction() {
        AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
        ledger.setPerson("张三@P");
        ledger.setLeaveTypeId("leaveTypeId");

        ActionLeaveLedgerAdjust.Adjustment adjustment = ActionLeaveLedgerAdjust.buildNewLedgerAdjustment(ledger, 6.0);

        assertEquals(0.0, adjustment.getBeforeAmount());
        assertEquals(6.0, adjustment.getAdjustedAmount());
        assertEquals(6.0, adjustment.getAdjustAmount());
        assertEquals(1, adjustment.getTransactions().size());
        assertEquals(BizTypeEnum.ADJUST.getValue(), adjustment.getTransactions().get(0).getBizType());
        assertEquals(6.0, adjustment.getTransactions().get(0).getAmount());
        assertEquals(ledger.getId(), adjustment.getTransactions().get(0).getLedgerId());
    }

    @Test
    void applyAdjustmentAddsBalanceToLatestLedger() throws Exception {
        AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
        ledger.setUsedAmount(2.0);
        ledger.setRemainingAmount(8.0);
        ledger.setGrantAmount(10.0);
        Date expireTime = new Date();

        ActionLeaveLedgerAdjust.Adjustment adjustment = ActionLeaveLedgerAdjust.applyAdjustment(
                Arrays.asList(ledger), 12.0, expireTime, true);

        assertEquals(8.0, adjustment.getBeforeAmount());
        assertEquals(12.0, adjustment.getAdjustedAmount());
        assertEquals(4.0, adjustment.getAdjustAmount());
        assertEquals(14.0, ledger.getGrantAmount());
        assertEquals(12.0, ledger.getRemainingAmount());
        assertEquals(expireTime, ledger.getExpireTime());
        assertEquals(1, adjustment.getTransactions().size());
        assertEquals(4.0, adjustment.getTransactions().get(0).getAmount());
    }

    @Test
    void applyAdjustmentDeductsBalanceFromLatestLedgers() throws Exception {
        AttendanceV2LeaveLedger latestLedger = new AttendanceV2LeaveLedger();
        latestLedger.setPerson("张三@P");
        latestLedger.setLeaveTypeId("leaveTypeId");
        latestLedger.setUsedAmount(0.0);
        latestLedger.setRemainingAmount(2.0);
        latestLedger.setGrantAmount(2.0);
        AttendanceV2LeaveLedger previousLedger = new AttendanceV2LeaveLedger();
        previousLedger.setPerson("张三@P");
        previousLedger.setLeaveTypeId("leaveTypeId");
        previousLedger.setUsedAmount(3.0);
        previousLedger.setRemainingAmount(5.0);
        previousLedger.setGrantAmount(8.0);

        ActionLeaveLedgerAdjust.Adjustment adjustment = ActionLeaveLedgerAdjust.applyAdjustment(
                Arrays.asList(latestLedger, previousLedger), 4.0, null, false);

        assertEquals(7.0, adjustment.getBeforeAmount());
        assertEquals(4.0, adjustment.getAdjustedAmount());
        assertEquals(-3.0, adjustment.getAdjustAmount());
        assertEquals(0.0, latestLedger.getGrantAmount());
        assertEquals(0.0, latestLedger.getRemainingAmount());
        assertEquals(7.0, previousLedger.getGrantAmount());
        assertEquals(4.0, previousLedger.getRemainingAmount());
        assertEquals(2, adjustment.getTransactions().size());
        assertEquals(-2.0, adjustment.getTransactions().get(0).getAmount());
        assertEquals(-1.0, adjustment.getTransactions().get(1).getAmount());
    }

    @Test
    void applyAdjustmentCanClearExpireTimeWhenFieldPresent() throws Exception {
        AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
        ledger.setRemainingAmount(2.0);
        ledger.setExpireTime(new Date());

        ActionLeaveLedgerAdjust.applyAdjustment(Arrays.asList(ledger), 2.0, null, true);

        assertEquals(2.0, ledger.getRemainingAmount());
        assertNull(ledger.getExpireTime());
    }

    @Test
    void applyAdjustmentRejectsDeductingMoreThanRemainingAmount() {
        AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
        ledger.setRemainingAmount(2.0);
        ledger.setGrantAmount(2.0);

        assertThrows(ExceptionWithMessage.class,
                () -> ActionLeaveLedgerAdjust.applyAdjustment(Arrays.asList(ledger), -1.0, null, false));
    }

    @Test
    void buildAdjustTransactionUsesAdjustAmount() {
        AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
        ledger.setPerson("张三@P");
        ledger.setLeaveTypeId("leaveTypeId");

        AttendanceV2LeaveTransaction transaction = ActionLeaveLedgerAdjust.buildAdjustTransaction(ledger, -2.5);

        assertEquals("张三@P", transaction.getPerson());
        assertEquals("leaveTypeId", transaction.getLeaveTypeId());
        assertEquals(ledger.getId(), transaction.getLedgerId());
        assertEquals(BizTypeEnum.ADJUST.getValue(), transaction.getBizType());
        assertEquals(-2.5, transaction.getAmount());
    }
}
