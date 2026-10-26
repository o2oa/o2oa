package com.x.attendance.assemble.control.jaxrs.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.project.tools.DateTools;

public class AttendanceV2RestDayHelper {

    private AttendanceV2RestDayHelper() {
        // tools class
    }

    /**
     * 根据人员和日期列表查询休息日，规则与 v2/my/rest/date/check 保持一致。
     */
    public static List<String> listRestDate(Business business, String personDn, List<String> dateList) throws Exception {
        List<String> restDateList = new ArrayList<>();
        if (dateList == null || dateList.isEmpty()) {
            return restDateList;
        }
        List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory().listGroupWithPerson(personDn);
        if (groups == null || groups.isEmpty()) {
            return listWeekend(dateList);
        }
        for (String date : dateList) {
            if (isRestDay(business, personDn, date)) {
                restDateList.add(date);
            }
        }
        return restDateList;
    }

    public static boolean isRestDay(Business business, String personDn, String date) throws Exception {
        Date d = null;
        try {
            d = DateTools.parse(date, DateTools.format_yyyyMMdd);
        } catch (Exception e) {
            return false;
        }
        WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(personDn, date);
        AttendanceV2Group group = woGroupShift.getGroup();
        if (group == null) {
            return false;
        }
        AttendanceV2Shift shift = woGroupShift.getShift();
        int day = DateTools.dayForWeekAttendanceV2(d);
        boolean isWorkDay = false;
        if (AttendanceV2Group.CHECKTYPE_Free.equals(group.getCheckType())) {
            if (StringUtils.isEmpty(group.getWorkDateList())) {
                return false;
            }
            String[] workDayList = group.getWorkDateList().split(",");
            List<Integer> dayList = Arrays.stream(workDayList).map(Integer::parseInt).collect(Collectors.toList());
            isWorkDay = dayList.contains(day);
            if (isWorkDay && AttendanceV2Helper.isSpecialRestDay(date, group)) {
                isWorkDay = false;
            }
        } else {
            if (shift != null) {
                isWorkDay = true;
            }
        }
        return !isWorkDay;
    }

    public static List<String> listWeekend(List<String> dateList) throws Exception {
        List<String> restDateList = new ArrayList<>();
        for (String date : dateList) {
            Date d = null;
            try {
                d = DateTools.parse(date, DateTools.format_yyyyMMdd);
            } catch (Exception e) {
                continue;
            }
            int day = DateTools.dayForWeekAttendanceV2(d);
            if (day == 0 || day == 6) {
                restDateList.add(date);
            }
        }
        return restDateList;
    }

    public static List<String> listDateRange(Date startDate, Date endDate) {
        List<String> dateList = new ArrayList<>();
        Date cursor = startDate;
        while (!cursor.after(endDate)) {
            dateList.add(DateTools.format(cursor, DateTools.format_yyyyMMdd));
            cursor = DateTools.addDay(cursor, 1);
        }
        return dateList;
    }
}
