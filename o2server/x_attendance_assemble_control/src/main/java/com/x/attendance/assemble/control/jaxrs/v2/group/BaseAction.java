package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.x.attendance.entity.v2.AttendanceV2GroupWorkDayProperties;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
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
    protected void setPropertiesShiftData(EntityManagerContainer emc, AttendanceV2GroupWorkDayProperties properties) throws Exception {
        for (Field field : AttendanceV2GroupWorkDayProperties.class.getDeclaredFields()) {

            AttendanceV2GroupWorkDayProperties.AttendanceV2GroupWorkDay day = (AttendanceV2GroupWorkDayProperties.AttendanceV2GroupWorkDay) FieldUtils.readField(field, properties, true);
            if (day != null && day.isChecked() && StringUtils.isNotEmpty(day.getShiftId())) {
                AttendanceV2Shift shift = emc.find(day.getShiftId(), AttendanceV2Shift.class);
                if (shift != null) {
                    day.setShift(shift);
//                    field.set(properties, day);
                    FieldUtils.writeField(field, properties, day, true);
                }
            }
        }
    }
//
//
//    protected void setPropertiesShiftData(EntityManagerContainer emc, AttendanceV2GroupWorkDayProperties properties) throws Exception {
//
//        // 周一
//        if (properties.getMonday() != null && properties.getMonday().isChecked() && StringUtils.isNotEmpty(properties.getMonday().getShiftId())) {
//            AttendanceV2Shift shift = emc.find(properties.getMonday().getShiftId(), AttendanceV2Shift.class);
//            if (shift != null) {
//                properties.getMonday().setShift(shift);
//            }
//        }
//        // 周二
//        if (properties.getTuesday() != null && properties.getTuesday().isChecked() && StringUtils.isNotEmpty(properties.getTuesday().getShiftId())) {
//            AttendanceV2Shift shift = emc.find(properties.getTuesday().getShiftId(), AttendanceV2Shift.class);
//            if (shift != null) {
//                properties.getTuesday().setShift(shift);
//            }
//        }
//        // 周三
//        if (properties.getWednesday() != null && properties.getWednesday().isChecked() && StringUtils.isNotEmpty(properties.getWednesday().getShiftId())) {
//            AttendanceV2Shift shift = emc.find(properties.getWednesday().getShiftId(), AttendanceV2Shift.class);
//            if (shift != null) {
//                properties.getWednesday().setShift(shift);
//            }
//        }
//        // 周四
//        if (properties.getThursday() != null && properties.getThursday().isChecked() && StringUtils.isNotEmpty(properties.getThursday().getShiftId())) {
//            AttendanceV2Shift shift = emc.find(properties.getThursday().getShiftId(), AttendanceV2Shift.class);
//            if (shift != null) {
//                properties.getThursday().setShift(shift);
//            }
//        }
//        // 周五
//        if (properties.getFriday() != null && properties.getFriday().isChecked() && StringUtils.isNotEmpty(properties.getFriday().getShiftId())) {
//            AttendanceV2Shift shift = emc.find(properties.getFriday().getShiftId(), AttendanceV2Shift.class);
//            if (shift != null) {
//                properties.getFriday().setShift(shift);
//            }
//        }
//        // 周六
//        if (properties.getSaturday() != null && properties.getSaturday().isChecked() && StringUtils.isNotEmpty(properties.getSaturday().getShiftId())) {
//            AttendanceV2Shift shift = emc.find(properties.getSaturday().getShiftId(), AttendanceV2Shift.class);
//            if (shift != null) {
//                properties.getSaturday().setShift(shift);
//            }
//        }
//        // 周五
//        if (properties.getSunday() != null && properties.getSunday().isChecked() && StringUtils.isNotEmpty(properties.getSunday().getShiftId())) {
//            AttendanceV2Shift shift = emc.find(properties.getSunday().getShiftId(), AttendanceV2Shift.class);
//            if (shift != null) {
//                properties.getSunday().setShift(shift);
//            }
//        }
//    }


}
