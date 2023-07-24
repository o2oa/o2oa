package com.x.attendance.assemble.control.jaxrs.v2.detail.model;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * Created by fancyLou on 2023/3/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class StatisticWo extends GsonPropertyObject {


    @FieldDescribe("用户标识")
    private String userId;

    @FieldDescribe("工作时长(分钟)")
    private Long workTimeDuration = 0L;
    @FieldDescribe("平均工时，工作时长/工作日数量，(小时)") //
    private String averageWorkTimeDuration = "0.0";


    @FieldDescribe("出勤天数")
    private Integer attendance = 0;
    @FieldDescribe("休息天数")
    private Integer rest = 0;
    @FieldDescribe("旷工天数")
    private Integer absenteeismDays = 0;


    @FieldDescribe("迟到次数")
    private Integer lateTimes = 0;
    @FieldDescribe("早退次数")//
    private Integer leaveEarlierTimes = 0;
    @FieldDescribe("缺卡次数") // 上下班次数相加
    private Integer absenceTimes = 0;
    @FieldDescribe("外勤打卡次数")
    private Integer fieldWorkTimes = 0;
    @FieldDescribe("请假天数")
    private Integer leaveDays = 0;
    @FieldDescribe("申诉次数")
    private Integer appealNums = 0;

    @FieldDescribe("每日数据")
    private List<DetailWo> detailList;


    

    public Integer getFieldWorkTimes() {
        return fieldWorkTimes;
    }

    public void setFieldWorkTimes(Integer fieldWorkTimes) {
        this.fieldWorkTimes = fieldWorkTimes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getWorkTimeDuration() {
        return workTimeDuration;
    }

    public void setWorkTimeDuration(Long workTimeDuration) {
        this.workTimeDuration = workTimeDuration;
    }

    public String getAverageWorkTimeDuration() {
        return averageWorkTimeDuration;
    }

    public void setAverageWorkTimeDuration(String averageWorkTimeDuration) {
        this.averageWorkTimeDuration = averageWorkTimeDuration;
    }

    public Integer getAttendance() {
        return attendance;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public Integer getAbsenteeismDays() {
        return absenteeismDays;
    }

    public void setAbsenteeismDays(Integer absenteeismDays) {
        this.absenteeismDays = absenteeismDays;
    }

    public Integer getLateTimes() {
        return lateTimes;
    }

    public void setLateTimes(Integer lateTimes) {
        this.lateTimes = lateTimes;
    }

    public Integer getLeaveEarlierTimes() {
        return leaveEarlierTimes;
    }

    public void setLeaveEarlierTimes(Integer leaveEarlierTimes) {
        this.leaveEarlierTimes = leaveEarlierTimes;
    }

    public Integer getAbsenceTimes() {
        return absenceTimes;
    }

    public void setAbsenceTimes(Integer absenceTimes) {
        this.absenceTimes = absenceTimes;
    }

    public Integer getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Integer leaveDays) {
        this.leaveDays = leaveDays;
    }

    public Integer getAppealNums() {
        return appealNums;
    }

    public void setAppealNums(Integer appealNums) {
        this.appealNums = appealNums;
    }

    public List<DetailWo> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DetailWo> detailList) {
        this.detailList = detailList;
    }
}
