package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import org.junit.jupiter.api.Test;

class ActionHolidayPostTest {

    @Test
    void buildHolidaySetsYearAndApiSource() throws Exception {
        ActionHolidayPost.Wi wi = new ActionHolidayPost.Wi();
        wi.setDateString("2026-10-01");
        wi.setName("国庆节");
        wi.setOffDay(true);

        AttendanceV2Holiday holiday = ActionHolidayPost.buildHoliday(wi);

        assertEquals(2026, holiday.getYear());
        assertEquals("2026-10-01", holiday.getDateString());
        assertEquals("国庆节", holiday.getName());
        assertEquals(true, holiday.getOffDay());
        assertEquals(AttendanceV2Holiday.SOURCE_API, holiday.getSource());
    }

    @Test
    void buildHolidayRequiresOffDay() {
        ActionHolidayPost.Wi wi = new ActionHolidayPost.Wi();
        wi.setDateString("2026-10-01");

        assertThrows(ExceptionEmptyParameter.class, () -> ActionHolidayPost.buildHoliday(wi));
    }
}
