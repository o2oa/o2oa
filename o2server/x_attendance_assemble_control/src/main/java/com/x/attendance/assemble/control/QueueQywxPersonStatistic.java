package com.x.attendance.assemble.control;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceQywxDetail;
import com.x.attendance.entity.StatisticQywxPersonForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

/**
 * Created by fancyLou on 2020-04-05.
 * Copyright © 2020 O2. All rights reserved.
 */
public class QueueQywxPersonStatistic extends AbstractQueue<Date> {
    private static final Logger logger = LoggerFactory.getLogger(QueueQywxPersonStatistic.class);

    @Override
    protected void execute(Date date) throws Exception {
        logger.info("开始执行人员企业微信考勤统计，time:"+DateTools.format(date));
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            saveStatisticPersonForMonth(business, emc, date);
        }
    }


    private void saveStatisticPersonForMonth(Business business, EntityManagerContainer emc, Date date) throws Exception {
        String dateString = DateTools.format(date, DateTools.format_yyyyMMdd);
        String year = dateString.substring(0, 4);
        String month = dateString.substring(5, 7);

        Application app = ThisApplication.context().applications().randomWithWeight(x_organization_assemble_control.class.getName());
        //开始分页查询人员
        boolean hasNextPerson = true;
        int personPageSize = 50;
        //人员查询地址
        String uri = "person/list/(0)/next/50";
        while (hasNextPerson) {
            List<Person> list = ThisApplication.context().applications()
                    .getQuery(false, app, uri).getDataAsList(Person.class);
            if (list != null && list.size() > 0) {
                for (Person person : list) {
                    List<String> ids = business.dingdingAttendanceFactory()
                            .getQywxStatPersonForMonthIds(year, month, person.getDistinguishedName());
                    emc.beginTransaction(StatisticQywxPersonForMonth.class);
                    if ( ListTools.isNotEmpty( ids ) ) {
                        for (String item : ids) {
                            StatisticQywxPersonForMonth personForMonth_temp = emc.find(item, StatisticQywxPersonForMonth.class);
                            emc.remove(personForMonth_temp);
                        }
                    }
                    StatisticQywxPersonForMonth personForMonth = new StatisticQywxPersonForMonth();
                    personForMonth.setStatisticYear(year);
                    personForMonth.setStatisticMonth(month);
                    personForMonth.setO2Unit(getUnitWithPerson(person.getDistinguishedName(), business));
                    personForMonth.setO2User(person.getDistinguishedName());
                    List<AttendanceQywxDetail> details = business.dingdingAttendanceFactory().qywxPersonForMonthList(year, month, person.getDistinguishedName());
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
                    personForMonth.setOnDutyTimes(onDutyTimes);
                    personForMonth.setOffDutyTimes(offDutyTimes);
                    personForMonth.setOutsideDutyTimes(outsideDutyTimes);
                    personForMonth.setWorkDayCount(workDayCount);
                    personForMonth.setResultNormal(resultNormal);
                    personForMonth.setLateTimes(lateTimes);
                    personForMonth.setLeaveEarlyTimes(leaveEarlyTimes);
                    personForMonth.setAbsenteeismTimes(absenteeismTimes);
                    personForMonth.setNotSignedCount(notSignedCount);
                    emc.persist(personForMonth);
                    emc.commit();
                }

                //是否还有更多用户
                if (list.size() < personPageSize) {
                    logger.info("统计企业微信考勤个人数据 结束！");
                    hasNextPerson = false;
                } else {
                    //还有更多用户继续查询
                    uri = "person/list/" + list.get(list.size() - 1).getDistinguishedName() + "/next/50";
                }
            }else {
                //没有用户查询到结束
                logger.info("统计企业微信考勤个人数据 结束！");
                hasNextPerson = false;
            }
        }
    }

    private String getUnitWithPerson(String person, Business business) throws Exception {
        String result = null;
        Integer level = 0;
        Unit unit = null;
        List<String> unitNames = business.organization().unit().listWithPerson( person );
        if( ListTools.isNotEmpty( unitNames ) ) {
            for( String unitName : unitNames ) {
                if( StringUtils.isNotEmpty( unitName ) && !"null".equals( unitName ) ) {
                    unit = business.organization().unit().getObject( unitName );
                    if( level < unit.getLevel() ) {
                        level = unit.getLevel();
                        result = unitName;
                    }
                }
            }
        }
        return result;
    }




}
