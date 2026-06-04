package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.base.core.project.tools.DateTools;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ActionLeaveLedgerImportExcelTest {

    @Test
    void parseGrantAmountSupportsDecimalValue() throws Exception {
        assertEquals(1.5, ActionLeaveLedgerImportExcel.parseGrantAmount("1.5"));
    }

    @Test
    void parseGrantAmountRejectsInvalidValue() {
        assertThrows(ExceptionWithMessage.class, () -> ActionLeaveLedgerImportExcel.parseGrantAmount("abc"));
    }

    @Test
    void parseGrantAmountRejectsNonFiniteValue() {
        assertThrows(ExceptionWithMessage.class, () -> ActionLeaveLedgerImportExcel.parseGrantAmount("NaN"));
    }

    @Test
    void parseExpireTimeSupportsDateValue() throws Exception {
        Date date = ActionLeaveLedgerImportExcel.parseExpireTime("2026-12-31");

        assertEquals("2026-12-31", DateTools.format(date, DateTools.format_yyyyMMdd));
    }

    @Test
    void parseExpireTimeRejectsInvalidValue() {
        assertThrows(ExceptionWithMessage.class, () -> ActionLeaveLedgerImportExcel.parseExpireTime("abc"));
    }
}
