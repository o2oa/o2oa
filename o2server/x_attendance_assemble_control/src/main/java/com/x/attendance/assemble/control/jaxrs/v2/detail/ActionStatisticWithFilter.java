package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 2023/3/8.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionStatisticWithFilter extends BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionStatisticWithFilter.class);

    ActionResult<List<Wo>> execute(JsonElement jsonElement) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getFilter())) {
                throw new ExceptionEmptyParameter("过滤人员或组织");
            }
            if (StringUtils.isEmpty(wi.getStartDate())) {
                throw new ExceptionEmptyParameter("开始日期");
            }
            if (StringUtils.isEmpty(wi.getEndDate())) {
                throw new ExceptionEmptyParameter("结束日期");
            }

            Date startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd); // 检查格式
            Date endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd); // 检查格式
            if (startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            int different = DateTools.differentDays(startDate, endDate);
            // 包含前后 所以+1
            different += 1;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("时间天数：" + different);
            }
            List<String> userList = new ArrayList<>();
            Business business = new Business(emc);
            if (wi.getFilter().endsWith("@U")) { // 组织转化成人员列表
                List<String> users = business.organization().person().listWithUnitSubDirect(wi.getFilter());
                if (users != null && !users.isEmpty()) {
                    userList.addAll(users);
                }
            } else if (wi.getFilter().endsWith("@P")) {
                userList.add(wi.getFilter());
            }
            if (userList.isEmpty()) {
                throw new ExceptionEmptyParameter("过滤人员或组织");
            }
            // 根据人员循环查询 并统计数据
            List<Wo> wos = new ArrayList<>();
            statisticDetail(wi, different, userList, business, wos);
            result.setData(wos);
            return result;
        }
    }

    /**
     * 统计数据
     * @param wi
     * @param different
     * @param userList
     * @param business
     * @param wos
     * @throws Exception
     */
    private void statisticDetail(Wi wi, int different, List<String> userList, Business business, List<Wo> wos) throws Exception {
        for (String person : userList) {
            Wo wo = new Wo();
            wo.setUserId(person); //
            List<AttendanceV2Detail> list = business.getAttendanceV2ManagerFactory().listDetailWithPersonAndStartEndDate(person, wi.getStartDate(), wi.getEndDate());
            if (list != null && !list.isEmpty()) {
                 Long workTimeDuration = 0L;
                 Integer attendance = 0;
                 Integer rest = 0;
                 Integer absenteeismDays = 0;
                 Integer lateTimes = 0;
                 Integer leaveEarlierTimes = 0;
                 Integer absenceTimes = 0;
                for (AttendanceV2Detail attendanceV2Detail : list) {
                    if (attendanceV2Detail.getWorkTimeDuration() > 0) {
                        workTimeDuration += attendanceV2Detail.getWorkTimeDuration();
                    }
                    if (attendanceV2Detail.getAttendance() > 0) {
                        attendance += attendanceV2Detail.getAttendance();
                    }
                    if (attendanceV2Detail.getRest() > 0) {
                        rest += attendanceV2Detail.getRest();
                    }
                    if (attendanceV2Detail.getAbsenteeismDays() > 0) {
                        absenteeismDays += attendanceV2Detail.getAbsenteeismDays();
                    }
                    if (attendanceV2Detail.getLateTimes() > 0) {
                        lateTimes += attendanceV2Detail.getLateTimes();
                    }
                    if (attendanceV2Detail.getLeaveEarlierTimes() > 0) {
                        leaveEarlierTimes += attendanceV2Detail.getLeaveEarlierTimes();
                    }
                    if (attendanceV2Detail.getOnDutyAbsenceTimes() > 0) {
                        absenceTimes += attendanceV2Detail.getOnDutyAbsenceTimes();
                    }
                    if (attendanceV2Detail.getOffDutyAbsenceTimes() > 0) {
                        absenceTimes += attendanceV2Detail.getOffDutyAbsenceTimes();
                    }
                }
                DecimalFormat df = new DecimalFormat("0.0");
                wo.setAverageWorkTimeDuration(df.format((float)workTimeDuration.intValue() / different));
                wo.setWorkTimeDuration(workTimeDuration);
                wo.setAttendance(attendance);
                wo.setRest(rest);
                wo.setAbsenteeismDays(absenteeismDays);
                wo.setLateTimes(lateTimes);
                wo.setLeaveEarlierTimes(leaveEarlierTimes);
                wo.setAbsenceTimes(absenceTimes);
            }
            wos.add(wo);
        }
    }


    public static class Wo extends GsonPropertyObject {


        @FieldDescribe("用户标识")
        private String userId;

        @FieldDescribe("工作时长(分钟)")
        private Long workTimeDuration = 0L;
        @FieldDescribe("平均工作时长(分钟)") // 除查询天数
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
    }

    public static class Wi extends GsonPropertyObject {


        @FieldDescribe("过滤人员或组织，组织只支持单层: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
        private String filter;


        @FieldDescribe("开始日期，包含")
        private String startDate;
        @FieldDescribe("结束日期， 包含")
        private String endDate;

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}
