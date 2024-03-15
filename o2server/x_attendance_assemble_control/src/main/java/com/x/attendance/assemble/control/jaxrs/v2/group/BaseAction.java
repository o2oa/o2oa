package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2GroupWorkDayProperties;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

abstract class BaseAction extends StandardJaxrsAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);




    /**
     * 给properties对象查询班次对象并设置进去
     * @param emc
     * @param properties
     * @throws Exception
     */
    protected void setPropertiesShiftData(Business business, AttendanceV2GroupWorkDayProperties properties) throws Exception {
        for (Field field : AttendanceV2GroupWorkDayProperties.class.getDeclaredFields()) {
            AttendanceV2GroupWorkDayProperties.AttendanceV2GroupWorkDay day = (AttendanceV2GroupWorkDayProperties.AttendanceV2GroupWorkDay) FieldUtils.readField(field, properties, true);
            if (day != null && day.isChecked() && StringUtils.isNotEmpty(day.getShiftId())) {
                AttendanceV2Shift shift = business.getAttendanceV2ManagerFactory().pick(day.getShiftId(), AttendanceV2Shift.class);
                if (shift != null) {
                    day.setShift(shift);
                    FieldUtils.writeField(field, properties, day, true);
                }
            }
        }
    }


}
