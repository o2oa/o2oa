package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.x.base.core.project.tools.DateTools;

class ActionOvertimeDurationCalculateTest {

    @Test
    void calculateOvertimeMinutesOnlyCountsOutsideNormalWorkTime() throws Exception {
        long minutes = ActionOvertimeDurationCalculate.calculateOvertimeMinutes(
                date("2026-10-09 17:00:00"),
                date("2026-10-09 20:00:00"),
                Collections.singletonList(range("2026-10-09 09:00:00", "2026-10-09 18:00:00")));

        assertEquals(120, minutes);
    }

    @Test
    void calculateOvertimeMinutesCountsAllTimeWithoutNormalWorkTime() throws Exception {
        long minutes = ActionOvertimeDurationCalculate.calculateOvertimeMinutes(
                date("2026-10-10 10:00:00"),
                date("2026-10-10 16:00:00"),
                Collections.emptyList());

        assertEquals(360, minutes);
    }

    @Test
    void calculateOvertimeMinutesDoesNotCountPreviousDayNightShiftAsRestDayOvertime() throws Exception {
        long minutes = ActionOvertimeDurationCalculate.calculateOvertimeMinutes(
                date("2026-10-10 03:00:00"),
                date("2026-10-10 08:00:00"),
                Collections.singletonList(range("2026-10-09 22:00:00", "2026-10-10 06:00:00")));

        assertEquals(120, minutes);
    }

    @Test
    void listOvertimeDateOnlyReturnsDatesWithOvertimeMinutes() throws Exception {
        List<String> result = ActionOvertimeDurationCalculate.listOvertimeDate(
                Arrays.asList("2026-10-09", "2026-10-10"),
                date("2026-10-09 17:00:00"),
                date("2026-10-10 08:00:00"),
                Arrays.asList(
                        range("2026-10-09 09:00:00", "2026-10-09 18:00:00"),
                        range("2026-10-09 22:00:00", "2026-10-10 06:00:00")));

        assertEquals(Arrays.asList("2026-10-09", "2026-10-10"), result);
    }

    private static ActionOvertimeDurationCalculate.TimeRange range(String start, String end) throws Exception {
        return new ActionOvertimeDurationCalculate.TimeRange(date(start), date(end));
    }

    private static java.util.Date date(String value) throws Exception {
        return DateTools.parse(value, DateTools.format_yyyyMMddHHmmss);
    }
}
