package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import java.util.List;
import java.util.stream.Collectors;

abstract class BaseAction extends StandardJaxrsAction {

    /**
     * 找出对应的打卡记录
     * @param recordList 记录列表
     * @param currentIndex 第几次
     * @param checkType OnDuty OffDuty
     * @return
     */
    protected AttendanceV2CheckInRecord hasCheckedRecord(List<AttendanceV2CheckInRecord> recordList, int currentIndex, String checkType) {
        if (recordList == null || recordList.isEmpty()) {
            return null;
        }
        List<AttendanceV2CheckInRecord> list = recordList.stream().filter(r -> r.getCheckInType().equals(checkType)).collect(Collectors.toList());
        if (!list.isEmpty() && currentIndex <= list.size()-1) {
            return list.get(currentIndex);
        }
        return null;
    }
}
