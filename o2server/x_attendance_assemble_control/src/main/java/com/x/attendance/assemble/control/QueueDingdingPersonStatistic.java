package com.x.attendance.assemble.control;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.attendance.entity.StatisticDingdingPersonForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
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
public class QueueDingdingPersonStatistic extends AbstractQueue<Date> {
    private static final Logger logger = LoggerFactory.getLogger(QueueDingdingPersonStatistic.class);

    @Override
    protected void execute(Date date) throws Exception {
        logger.info("开始执行人员钉钉考勤统计，time:"+DateTools.format(date));
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
                            .getStatPersonForMonthIds(year, month, person.getDistinguishedName());
                    emc.beginTransaction(StatisticDingdingPersonForMonth.class);
                    if ( ListTools.isNotEmpty( ids ) ) {
                        for (String item : ids) {
                            StatisticDingdingPersonForMonth personForMonth_temp = emc.find(item, StatisticDingdingPersonForMonth.class);
                            emc.remove(personForMonth_temp);
                        }
                    }
                    StatisticDingdingPersonForMonth personForMonth = new StatisticDingdingPersonForMonth();
                    personForMonth.setStatisticYear(year);
                    personForMonth.setStatisticMonth(month);
                    personForMonth.setO2Unit(getUnitWithPerson(person.getDistinguishedName(), business));
                    personForMonth.setO2User(person.getDistinguishedName());
                    Long onduty = business.dingdingAttendanceFactory().dingdingPersonForMonthDutyTimesCount(year, month,
                            person.getDistinguishedName(), AttendanceDingtalkDetail.OnDuty);
                    personForMonth.setWorkDayCount(onduty);
                    personForMonth.setOnDutyTimes(onduty);
                    personForMonth.setOffDutyTimes(business.dingdingAttendanceFactory().dingdingPersonForMonthDutyTimesCount(year, month,
                            person.getDistinguishedName(), AttendanceDingtalkDetail.OffDuty));
                    personForMonth.setResultNormal(business.dingdingAttendanceFactory().dingdingPersonForMonthTimeResultCount(year, month,
                            person.getDistinguishedName(), AttendanceDingtalkDetail.TIMERESULT_NORMAL));
                    personForMonth.setLateTimes(business.dingdingAttendanceFactory().dingdingPersonForMonthTimeResultCount(year, month,
                            person.getDistinguishedName(), AttendanceDingtalkDetail.TIMERESULT_Late));
                    personForMonth.setLeaveEarlyTimes(business.dingdingAttendanceFactory().dingdingPersonForMonthTimeResultCount(year, month,
                            person.getDistinguishedName(), AttendanceDingtalkDetail.TIMERESULT_Early));
                    personForMonth.setAbsenteeismTimes(business.dingdingAttendanceFactory().dingdingPersonForMonthTimeResultCount(year, month,
                            person.getDistinguishedName(), AttendanceDingtalkDetail.TIMERESULT_Absenteeism));
                    personForMonth.setNotSignedCount(business.dingdingAttendanceFactory().dingdingPersonForMonthTimeResultCount(year, month,
                            person.getDistinguishedName(), AttendanceDingtalkDetail.TIMERESULT_NotSigned));
                    personForMonth.setSeriousLateTimes(business.dingdingAttendanceFactory().dingdingPersonForMonthTimeResultCount(year, month,
                            person.getDistinguishedName(), AttendanceDingtalkDetail.TIMERESULT_SeriousLate));
                    emc.persist(personForMonth);
                    emc.commit();
                }

                //是否还有更多用户
                if (list.size() < personPageSize) {
                    logger.info("统计钉钉考勤个人数据 结束！");
                    hasNextPerson = false;
                } else {
                    //还有更多用户继续查询
//                    uri = "person/list/" + list.get(list.size() - 1).getDistinguishedName() + "/next/50";
                    uri = Applications.joinQueryUri("person","list", list.get(list.size() - 1).getDistinguishedName(), "next", "50");
                }
            }else {
                //没有用户查询到结束
                logger.info("统计钉钉考勤个人数据 结束！");
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
