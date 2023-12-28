package com.x.attendance.assemble.control;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.attendance.entity.StatisticDingdingUnitForDay;
import com.x.attendance.entity.StatisticDingdingUnitForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

/**
 * Created by fancyLou on 2020-04-05.
 * Copyright © 2020 O2. All rights reserved.
 */
public class QueueDingdingUnitStatistic extends AbstractQueue<Date> {
    private static final Logger logger = LoggerFactory.getLogger(QueueDingdingUnitStatistic.class);

    @Override
    protected void execute(Date date) throws Exception {
        logger.info("开始执行组织钉钉考勤统计，time:"+DateTools.format(date));
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
        List<String> units = business.dingdingAttendanceFactory().dingdingUnitDistinct(dateString);
        if (units != null && !units.isEmpty()) {
            for (String unit : units) {
                if (StringUtils.isEmpty(unit)){
                    continue;
                }
                List<String> ids = business.dingdingAttendanceFactory().getStatUnitForDayIds(year, month, day, unit);
                emc.beginTransaction(StatisticDingdingUnitForDay.class);
                if ( ListTools.isNotEmpty( ids ) ) {
                    for (String item : ids) {
                        StatisticDingdingUnitForDay statisticTopUnitForDay_tmp = emc.find(item, StatisticDingdingUnitForDay.class);
                        emc.remove(statisticTopUnitForDay_tmp);
                    }
                }
                //for day
                StatisticDingdingUnitForDay unitForDay = new StatisticDingdingUnitForDay();
                unitForDay.setO2Unit(unit);
                unitForDay.setStatisticYear(year);
                unitForDay.setStatisticMonth(month);
                unitForDay.setStatisticDate(day);
                Long on = business.dingdingAttendanceFactory().dingdingUnitForDayDutyTimesCount(dateString, unit, AttendanceDingtalkDetail.OnDuty);
                unitForDay.setWorkDayCount(on);
                unitForDay.setOnDutyTimes(on);
                unitForDay.setOffDutyTimes(business.dingdingAttendanceFactory().
                        dingdingUnitForDayDutyTimesCount(dateString, unit, AttendanceDingtalkDetail.OffDuty));
                unitForDay.setResultNormal(business.dingdingAttendanceFactory().dingdingUnitForDayTimeResultCount(dateString, unit,
                        AttendanceDingtalkDetail.TIMERESULT_NORMAL));
                unitForDay.setLateTimes(business.dingdingAttendanceFactory().dingdingUnitForDayTimeResultCount(dateString, unit,
                        AttendanceDingtalkDetail.TIMERESULT_Late));
                unitForDay.setLeaveEarlyTimes(business.dingdingAttendanceFactory().dingdingUnitForDayTimeResultCount(dateString, unit,
                        AttendanceDingtalkDetail.TIMERESULT_Early));
                unitForDay.setNotSignedCount(business.dingdingAttendanceFactory().dingdingUnitForDayTimeResultCount(dateString, unit,
                        AttendanceDingtalkDetail.TIMERESULT_NotSigned));
                unitForDay.setAbsenteeismTimes(business.dingdingAttendanceFactory().dingdingUnitForDayTimeResultCount(dateString, unit,
                        AttendanceDingtalkDetail.TIMERESULT_Absenteeism));
                unitForDay.setSeriousLateTimes(business.dingdingAttendanceFactory().dingdingUnitForDayTimeResultCount(dateString, unit,
                        AttendanceDingtalkDetail.TIMERESULT_SeriousLate));
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
            Long workDay = business.dingdingAttendanceFactory().sumWorkDayUnitForDayWithMonth(year, month, unit);
            Long onduty = business.dingdingAttendanceFactory().sumOnDutyUnitForDayWithMonth(year, month, unit);
            Long offDuty = business.dingdingAttendanceFactory().sumOffDutyUnitForDayWithMonth(year, month, unit);
            Long normal = business.dingdingAttendanceFactory().sumNormalUnitForDayWithMonth(year, month, unit);
            Long late = business.dingdingAttendanceFactory().sumLateTimesUnitForDayWithMonth(year, month, unit);
            Long leaveearly = business.dingdingAttendanceFactory().sumLeaveEarlyUnitForDayWithMonth(year, month, unit);
            Long notSign = business.dingdingAttendanceFactory().sumNotSignedUnitForDayWithMonth(year, month, unit);
            Long absenteeism = business.dingdingAttendanceFactory().sumAbsenteeismUnitForDayWithMonth(year, month, unit);
            Long serious = business.dingdingAttendanceFactory().sumSeriousLateUnitForDayWithMonth(year, month, unit);
            List<String> list = business.dingdingAttendanceFactory().getStatUnitForMonthIds(year, month, unit);
            emc.beginTransaction(StatisticDingdingUnitForMonth.class);
            if (list != null && list.size() > 0) {
                for (String item : list) {
                    StatisticDingdingUnitForMonth statisticTopUnitForMonth_tmp = emc.find(item, StatisticDingdingUnitForMonth.class);
                    emc.remove(statisticTopUnitForMonth_tmp);
                }
            }
            StatisticDingdingUnitForMonth unitForMonth = new StatisticDingdingUnitForMonth();
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
            unitForMonth.setSeriousLateTimes(serious);
            emc.persist(unitForMonth);
            emc.commit();
        }

    }
}
