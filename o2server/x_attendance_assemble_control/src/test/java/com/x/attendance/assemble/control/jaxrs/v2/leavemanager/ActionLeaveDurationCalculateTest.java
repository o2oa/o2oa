package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2ShiftWorkTimeHelper;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.base.core.project.tools.DateTools;

class ActionLeaveDurationCalculateTest {

    @Test
    void listCalculateDateExcludesRestDayByDefault() {
        List<String> dateList = Arrays.asList("2026-10-01", "2026-10-02", "2026-10-03");
        List<String> restDateList = Arrays.asList("2026-10-01", "2026-10-03");

        List<String> result = ActionLeaveDurationCalculate.listCalculateDate(dateList, restDateList, null);

        assertEquals(Arrays.asList("2026-10-02"), result);
    }

    @Test
    void listCalculateDateDoesNotExcludeRestDayWhenParameterFalse() {
        List<String> dateList = Arrays.asList("2026-10-01", "2026-10-02", "2026-10-03");
        List<String> restDateList = Arrays.asList("2026-10-01", "2026-10-03");

        List<String> result = ActionLeaveDurationCalculate.listCalculateDate(dateList, restDateList, false);

        assertEquals(dateList, result);
    }

    @Test
    void listCalculateDateExcludesRestDayWhenParameterTrue() {
        List<String> dateList = Arrays.asList("2026-10-01", "2026-10-02", "2026-10-03");
        List<String> restDateList = Arrays.asList("2026-10-01", "2026-10-03");

        List<String> result = ActionLeaveDurationCalculate.listCalculateDate(dateList, restDateList, true);

        assertEquals(Arrays.asList("2026-10-02"), result);
    }

    @Test
    void listCalculateDateIncludesOrdinaryWeekendButExcludesLegalHoliday() {
        List<String> dateList = Arrays.asList("2026-10-01", "2026-10-02", "2026-10-03", "2026-10-04",
                "2026-10-05");
        List<String> restDateList = Arrays.asList("2026-10-01", "2026-10-03", "2026-10-04");
        List<String> weekendDateList = Arrays.asList("2026-10-03", "2026-10-04");
        List<String> legalHolidayDateList = Arrays.asList("2026-10-01", "2026-10-04", "2026-10-05");

        List<String> result = ActionLeaveDurationCalculate.listCalculateDate(dateList, restDateList,
                weekendDateList, legalHolidayDateList, true, true);

        assertEquals(Arrays.asList("2026-10-02", "2026-10-03"), result);
    }

    @Test
    void shiftWorkMinutesDeductsRestPeriod() throws Exception {
        AttendanceV2ShiftCheckTime checkTime = checkTime("09:00", "18:00", false, "12:00-13:00");

        long minutes = AttendanceV2ShiftWorkTimeHelper.standardWorkMinutes("2026-10-09", checkTime);

        assertEquals(480, minutes);
    }

    @Test
    void overlapWorkMinutesDoesNotCountRestPeriod() throws Exception {
        AttendanceV2ShiftCheckTime checkTime = checkTime("09:00", "18:00", false, "12:00-13:00");

        long minutes = AttendanceV2ShiftWorkTimeHelper.overlapWorkMinutes(
                date("2026-10-09 11:30:00"),
                date("2026-10-09 13:30:00"),
                "2026-10-09",
                checkTime);

        assertEquals(60, minutes);
    }

    @Test
    void overlapWorkMinutesSupportsNextDayRestPeriodForNightShift() throws Exception {
        AttendanceV2ShiftCheckTime checkTime = checkTime("22:00", "06:00", true, "02:00-03:00");

        long minutes = AttendanceV2ShiftWorkTimeHelper.overlapWorkMinutes(
                date("2026-10-09 22:00:00"),
                date("2026-10-10 06:00:00"),
                "2026-10-09",
                checkTime);

        assertEquals(420, minutes);
    }

    private static AttendanceV2ShiftCheckTime checkTime(String onDuty, String offDuty, boolean offDutyNextDay,
            String restPeriod) {
        AttendanceV2ShiftCheckTime checkTime = new AttendanceV2ShiftCheckTime();
        checkTime.setOnDutyTime(onDuty);
        checkTime.setOffDutyTime(offDuty);
        checkTime.setOffDutyNextDay(offDutyNextDay);
        checkTime.setRestPeriod(restPeriod);
        return checkTime;
    }

    private static java.util.Date date(String value) throws Exception {
        return DateTools.parse(value, DateTools.format_yyyyMMddHHmmss);
    }
}
