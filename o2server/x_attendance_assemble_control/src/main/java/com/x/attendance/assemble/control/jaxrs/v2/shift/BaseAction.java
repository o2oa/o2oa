package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTimeProperties;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

    protected void checkShiftProperties(AttendanceV2ShiftCheckTimeProperties properties) throws Exception {
        if (properties == null || properties.getTimeList() == null || properties.getTimeList().isEmpty()) {
            throw new ExceptionEmptyParameter("班次上下班打卡时间");
        }
        List<AttendanceV2ShiftCheckTime> timeList = properties.getTimeList();
        AttendanceV2ShiftCheckTime time1 = timeList.get(0);
        checkOnDutyTimeAndOffDutyTime(time1);
        if (timeList.size() > 1) {
            checkOnDutyTimeAndOffDutyTime(timeList.get(1));
        }
        if (timeList.size() > 2) {
            checkOnDutyTimeAndOffDutyTime(timeList.get(2));
        }
    }

    private void checkOnDutyTimeAndOffDutyTime(AttendanceV2ShiftCheckTime time) throws Exception {
        if (StringUtils.isEmpty(time.getOnDutyTime())) {
            throw new ExceptionEmptyParameter("上班时间");
        }
        if (StringUtils.isEmpty(time.getOffDutyTime())) {
            throw new ExceptionEmptyParameter("下班时间");
        }
        String onDuty = time.getOnDutyTime().replace(":", "");
        String offDuty = time.getOffDutyTime().replace(":", "");
        if (Integer.parseInt(onDuty) > Integer.parseInt(offDuty)) {
            throw new ExceptionOnDutyOffDuty("上班时间不能大于下班时间");
        }
        if (StringUtils.isNotEmpty(time.getOnDutyTimeBeforeLimit())) {
            String onDutyBefore = time.getOnDutyTimeBeforeLimit().replace(":", "");
            if (Integer.parseInt(onDutyBefore) > Integer.parseInt(onDuty)) {
                throw new ExceptionOnDutyOffDuty("上班前时间不能大于上班时间");
            }
        }
        if (StringUtils.isNotEmpty(time.getOnDutyTimeAfterLimit())) {
            String onDutyAfter = time.getOnDutyTimeAfterLimit().replace(":", "");
            if (Integer.parseInt(onDutyAfter) < Integer.parseInt(onDuty)) {
                throw new ExceptionOnDutyOffDuty("上班后时间不能小于上班时间");
            }
        }
        if (StringUtils.isNotEmpty(time.getOffDutyTimeBeforeLimit())) {
            String offDutyBefore = time.getOffDutyTimeBeforeLimit().replace(":", "");
            if (Integer.parseInt(offDutyBefore) > Integer.parseInt(offDuty)) {
                throw new ExceptionOnDutyOffDuty("下班前时间不能大于下班时间");
            }
        }
        if (StringUtils.isNotEmpty(time.getOffDutyTimeAfterLimit())) {
            String offDutyAfter = time.getOffDutyTimeAfterLimit().replace(":", "");
            if (Integer.parseInt(offDutyAfter) < Integer.parseInt(offDuty)) {
                throw new ExceptionOnDutyOffDuty("下班后时间不能小于下班时间");
            }
        }

    }
}
