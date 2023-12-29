package com.x.attendance.assemble.control;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceQywxDetail;
import com.x.attendance.entity.StatisticQywxUnitForDay;
import com.x.attendance.entity.StatisticQywxUnitForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;

/**
 * Created by fancyLou on 2020-04-05.
 * Copyright © 2020 O2. All rights reserved.
 */
public class QueueQywxUnitStatistic extends AbstractQueue<Date> {
    private static final Logger logger = LoggerFactory.getLogger(QueueQywxUnitStatistic.class);

    @Override
    protected void execute(Date date) throws Exception {
        logger.info("开始执行组织企业微信考勤统计，time:"+DateTools.format(date));
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            saveStatisticUnitForDay(business, emc, date);
        }
    }



    /**
     * 单位 日统计
     * @param business
     * @param emc
     * @param date
     * @throws Exception
     */
    private void saveStatisticUnitForDay(Business business, EntityManagerContainer emc, Date date) throws Exception{
        String dateString = DateTools.format(date, DateTools.format_yyyyMMdd);
        String year = dateString.substring(0, 4);
        String month = dateString.substring(5, 7);
        String day = dateString.substring(8, 10);
        List<String> units = business.dingdingAttendanceFactory().qywxUnitDistinct(dateString);
        if (units != null && !units.isEmpty()) {
            for (String unit : units) {
                if (StringUtils.isEmpty(unit)){
                    continue;
                }
                List<String> ids = business.dingdingAttendanceFactory().getQywxStatUnitForDayIds(year, month, day, unit);
                emc.beginTransaction(StatisticQywxUnitForDay.class);
                if (ids != null && ids.size() > 0) {
                    for (String item : ids) {
                        StatisticQywxUnitForDay statisticTopUnitForDay_tmp = emc.find(item, StatisticQywxUnitForDay.class);
                        emc.remove(statisticTopUnitForDay_tmp);
                    }
                }
                //for day
                StatisticQywxUnitForDay unitForDay = new StatisticQywxUnitForDay();
                unitForDay.setO2Unit(unit);
                unitForDay.setStatisticYear(year);
                unitForDay.setStatisticMonth(month);
                unitForDay.setStatisticDate(day);
                List<AttendanceQywxDetail> details = business.dingdingAttendanceFactory().qywxUnitForDayList(year, month, day, unit);
                long workDayCount = 0L;
                long onDutyTimes = 0L;
                long offDutyTimes = 0L;
                long outsideDutyTimes = 0L;
                long resultNormal = 0L;
                long lateTimes = 0L;
                long leaveEarlyTimes = 0L;
                long absenteeismTimes = 0L;
                long notSignedCount = 0L;
                if ( details!=null && !details.isEmpty()) {
                    workDayCount = (long) (details.size() / 2);
                    onDutyTimes = details.stream().filter(d -> d.getCheckin_type().equals(AttendanceQywxDetail.CHECKIN_TYPE_ON)).count();
                    offDutyTimes = details.stream().filter(d -> d.getCheckin_type().equals(AttendanceQywxDetail.CHECKIN_TYPE_OFF)).count();
                    outsideDutyTimes = details.stream().filter(d -> d.getCheckin_type().equals(AttendanceQywxDetail.CHECKIN_TYPE_OUTSIDE)).count();
                    resultNormal = details.stream().filter(d -> d.getException_type().equals(AttendanceQywxDetail.EXCEPTION_TYPE_NORMAL)).count();
                    lateTimes = details.stream().filter(d ->
                            (d.getException_type().contains(AttendanceQywxDetail.EXCEPTION_TYPE_TIME) &&
                                    d.getCheckin_type().equals(AttendanceQywxDetail.CHECKIN_TYPE_ON))).count();
                    leaveEarlyTimes = details.stream().filter(d ->
                            (d.getException_type().contains(AttendanceQywxDetail.EXCEPTION_TYPE_TIME) &&
                                    d.getCheckin_type().equals(AttendanceQywxDetail.CHECKIN_TYPE_OFF))).count();
                    List<AttendanceQywxDetail> ondutys = details.stream().filter(d->
                            d.getCheckin_type().equals(AttendanceQywxDetail.CHECKIN_TYPE_ON)).collect(Collectors.toList());
                    List<AttendanceQywxDetail> offdutys = details.stream().filter(d->
                            d.getCheckin_type().equals(AttendanceQywxDetail.CHECKIN_TYPE_OFF)).collect(Collectors.toList());
                    for (int i = 0; i < ondutys.size(); i++) {
                        AttendanceQywxDetail detail = ondutys.get(i);
                        if (detail.getException_type().contains(AttendanceQywxDetail.EXCEPTION_TYPE_NOSIGN)) {
                            notSignedCount++;
                            String checkDate = DateTools.format(detail.getCheckin_time_date(), DateTools.format_yyyyMMdd);
                            Optional<AttendanceQywxDetail> op = offdutys.stream().filter(d->
                                    checkDate.equals(DateTools.format(d.getCheckin_time_date(), DateTools.format_yyyyMMdd))).findFirst();
                            if (op.isPresent()) {
                                AttendanceQywxDetail detail1 = op.get();
                                if(detail1.getException_type().contains(AttendanceQywxDetail.EXCEPTION_TYPE_NOSIGN)) {
                                    notSignedCount++;
                                    absenteeismTimes++;
                                }
                            }
                        }
                    }
                }
                unitForDay.setOnDutyTimes(onDutyTimes);
                unitForDay.setOffDutyTimes(offDutyTimes);
                unitForDay.setOutsideDutyTimes(outsideDutyTimes);
                unitForDay.setWorkDayCount(workDayCount);
                unitForDay.setResultNormal(resultNormal);
                unitForDay.setLateTimes(lateTimes);
                unitForDay.setLeaveEarlyTimes(leaveEarlyTimes);
                unitForDay.setAbsenteeismTimes(absenteeismTimes);
                unitForDay.setNotSignedCount(notSignedCount);
                emc.persist(unitForDay);
                emc.commit();
            }
            saveStatisticUnitForMonth(business, emc, units, year, month);
        }


    }

    /**
     * 单位月统计
     * @param business
     * @param emc
     * @param units
     * @param year
     * @param month
     * @throws Exception
     */
    private void saveStatisticUnitForMonth(Business business, EntityManagerContainer emc, List<String> units,
                                           String year, String month) throws Exception {

        for (String unit : units) {
            if (StringUtils.isEmpty(unit)){
                continue;
            }
            Long workDay = business.dingdingAttendanceFactory().sumQywxWorkDayUnitForDayWithMonth(year, month, unit);
            Long onduty = business.dingdingAttendanceFactory().sumQywxOndutyUnitForDayWithMonth(year, month, unit);
            Long offDuty = business.dingdingAttendanceFactory().sumQywxOffDutyUnitForDayWithMonth(year, month, unit);
            Long outside = business.dingdingAttendanceFactory().sumQywxOutsideUnitForDayWithMonth(year, month, unit);
            Long normal = business.dingdingAttendanceFactory().sumQywxResultNormalUnitForDayWithMonth(year, month, unit);
            Long late = business.dingdingAttendanceFactory().sumQywxLatetimeUnitForDayWithMonth(year, month, unit);
            Long leaveearly = business.dingdingAttendanceFactory().sumQywxLeaveEarlyUnitForDayWithMonth(year, month, unit);
            Long notSign = business.dingdingAttendanceFactory().sumQywxNotSignUnitForDayWithMonth(year, month, unit);
            Long absenteeism = business.dingdingAttendanceFactory().sumQywxAbsenteeismUnitForDayWithMonth(year, month, unit);

            List<String> list = business.dingdingAttendanceFactory().getQywxStatUnitForMonthIds(year, month, unit);
            emc.beginTransaction(StatisticQywxUnitForMonth.class);
            if (list != null && list.size() > 0) {
                for (String item : list) {
                    StatisticQywxUnitForMonth statisticTopUnitForMonth_tmp = emc.find(item, StatisticQywxUnitForMonth.class);
                    emc.remove(statisticTopUnitForMonth_tmp);
                }
            }
            StatisticQywxUnitForMonth unitForMonth = new StatisticQywxUnitForMonth();
            unitForMonth.setO2Unit(unit);
            unitForMonth.setStatisticYear(year);
            unitForMonth.setStatisticMonth(month);
            unitForMonth.setWorkDayCount(workDay);
            unitForMonth.setOnDutyTimes(onduty);
            unitForMonth.setOffDutyTimes(offDuty);
            unitForMonth.setResultNormal(normal);
            unitForMonth.setLateTimes(late);
            unitForMonth.setLeaveEarlyTimes(leaveearly);
            unitForMonth.setNotSignedCount(notSign);
            unitForMonth.setAbsenteeismTimes(absenteeism);
            unitForMonth.setOutsideDutyTimes(outside);
            emc.persist(unitForMonth);
            emc.commit();
        }

    }
}
