package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by fancyLou on 2023/3/17.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2GroupWorkDayProperties extends JsonProperties {

    @FieldDescribe("周日")
    private AttendanceV2GroupWorkDay sunday;
    @FieldDescribe("周一")
    private AttendanceV2GroupWorkDay monday;
    @FieldDescribe("周二")
    private AttendanceV2GroupWorkDay tuesday;
    @FieldDescribe("周三")
    private AttendanceV2GroupWorkDay wednesday;
    @FieldDescribe("周四")
    private AttendanceV2GroupWorkDay thursday;
    @FieldDescribe("周五")
    private AttendanceV2GroupWorkDay friday;
    @FieldDescribe("周六")
    private AttendanceV2GroupWorkDay saturday;


    /**
     * 根据日期 获取班次id
     * @param date
     * @return
     */
    public String shiftIdWithDate(Date date) {
        // 周几
        try {
            AttendanceV2GroupWorkDay workDay = null;
            int day = DateTools.dayForWeekAttendanceV2(date);
            switch (day){
                case 0:
                    workDay = getSunday();
                    break;
                case 1:
                    workDay = getMonday();
                    break;
                case 2:
                    workDay = getTuesday();
                    break;
                case 3:
                    workDay = getWednesday();
                    break;
                case 4:
                    workDay = getThursday();
                    break;
                case 5:
                    workDay = getFriday();
                    break;
                case 6:
                    workDay = getSaturday();
                    break;
            }
            if (workDay != null) {
                return workDay.getShiftId();
            }
        } catch (Exception ignore) { }
        return null;
    }

    /**
     * 判断是否有数据
     * 有选中并且选中的日子有配置班次
     * @return
     */
    public boolean validateNotEmpty() {
        if (getSunday() != null) {
            if (getSunday().isChecked() && StringUtils.isNotEmpty(getSunday().getShiftId())) {
                return true;
            }
        }
        if (getMonday() != null) {
            if (getMonday().isChecked() && StringUtils.isNotEmpty(getMonday().getShiftId())) {
                return true;
            }
        }
        if (getTuesday() != null) {
            if (getTuesday().isChecked() && StringUtils.isNotEmpty(getTuesday().getShiftId())) {
                return true;
            }
        }
        if (getWednesday() != null) {
            if (getWednesday().isChecked() && StringUtils.isNotEmpty(getWednesday().getShiftId())) {
                return true;
            }
        }
        if (getThursday() != null) {
            if (getThursday().isChecked() && StringUtils.isNotEmpty(getThursday().getShiftId())) {
                return true;
            }
        }
        if (getFriday() != null) {
            if (getFriday().isChecked() && StringUtils.isNotEmpty(getFriday().getShiftId())) {
                return true;
            }
        }
        if (getSaturday() != null) {
            if (getSaturday().isChecked() && StringUtils.isNotEmpty(getSaturday().getShiftId())) {
                return true;
            }
        }
        return false;
    }




    public AttendanceV2GroupWorkDay getSunday() {
        return sunday;
    }

    public void setSunday(AttendanceV2GroupWorkDay sunday) {
        this.sunday = sunday;
    }

    public AttendanceV2GroupWorkDay getMonday() {
        return monday;
    }

    public void setMonday(AttendanceV2GroupWorkDay monday) {
        this.monday = monday;
    }

    public AttendanceV2GroupWorkDay getTuesday() {
        return tuesday;
    }

    public void setTuesday(AttendanceV2GroupWorkDay tuesday) {
        this.tuesday = tuesday;
    }

    public AttendanceV2GroupWorkDay getWednesday() {
        return wednesday;
    }

    public void setWednesday(AttendanceV2GroupWorkDay wednesday) {
        this.wednesday = wednesday;
    }

    public AttendanceV2GroupWorkDay getThursday() {
        return thursday;
    }

    public void setThursday(AttendanceV2GroupWorkDay thursday) {
        this.thursday = thursday;
    }

    public AttendanceV2GroupWorkDay getFriday() {
        return friday;
    }

    public void setFriday(AttendanceV2GroupWorkDay friday) {
        this.friday = friday;
    }

    public AttendanceV2GroupWorkDay getSaturday() {
        return saturday;
    }

    public void setSaturday(AttendanceV2GroupWorkDay saturday) {
        this.saturday = saturday;
    }

    public static class AttendanceV2GroupWorkDay extends GsonPropertyObject {

        @FieldDescribe("是否需要打卡")
        private boolean checked; //
        @FieldDescribe("班次id")
        private String shiftId; //
        @FieldDescribe("班次对象") // ，不存储，返回前端的时候提供
        private AttendanceV2Shift shift;


        public AttendanceV2Shift getShift() {
            return shift;
        }

        public void setShift(AttendanceV2Shift shift) {
            this.shift = shift;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getShiftId() {
            return shiftId;
        }

        public void setShiftId(String shiftId) {
            this.shiftId = shiftId;
        }
    }

}
