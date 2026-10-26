package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import org.junit.jupiter.api.Test;

class ActionHolidayDeleteTest {

    @Test
    void validateCanDeleteAllowsApiSource() {
        AttendanceV2Holiday holiday = new AttendanceV2Holiday();
        holiday.setSource(AttendanceV2Holiday.SOURCE_API);

        assertDoesNotThrow(() -> ActionHolidayDelete.validateCanDelete(holiday));
    }

    @Test
    void validateCanDeleteRejectsSyncSource() {
        AttendanceV2Holiday holiday = new AttendanceV2Holiday();
        holiday.setSource(AttendanceV2Holiday.SOURCE_SYNC);

        assertThrows(ExceptionWithMessage.class, () -> ActionHolidayDelete.validateCanDelete(holiday));
    }
}
