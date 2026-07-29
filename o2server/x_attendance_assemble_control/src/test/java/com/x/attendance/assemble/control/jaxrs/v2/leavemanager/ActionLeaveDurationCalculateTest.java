package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

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
}
