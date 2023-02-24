package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionParticipateConflict;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 新版考勤定时任务
 * 根据 打卡考勤记录AttendanceV2CheckInRecord 生成对应的 考勤详细数据AttendanceV2Detail
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2DetailGenerateTask  extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2DetailGenerateTask.class);


    // 定时任务 第一步 处理所有考勤组，将人员、组织全部换成人员DN ，这个步骤是为了有新加入的成员自动添加到考勤组范围内
    // 第二步 将考勤组内需要考勤人员一个个进行数据处理 ， 根据 打卡考勤记录AttendanceV2CheckInRecord 生成对应的 考勤详细数据AttendanceV2Detail
    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
         logger.info("======================新版考勤定时任务开始==============================");
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<AttendanceV2Group> list = emc.listAll(AttendanceV2Group.class);
            if (list != null && !list.isEmpty()) {
                for (AttendanceV2Group group : list) {
                    List<String> trueList = calTruePersonFromMixList(business, group.getId(), list, group.getParticipateList(), group.getUnParticipateList());
                    group.setTrueParticipantList(trueList);
                    emc.beginTransaction(AttendanceV2Group.class);
                    emc.persist(group, CheckPersistType.all);
                    emc.commit();
                    // 开始处理第二步 因为这个定时任务是凌晨3点执行，这里处理的是前一天的数据
                    Date yesterday = DateTools.addDay(new Date(), -1);
                    String yesterdayString = DateTools.format(yesterday, DateTools.format_yyyyMMdd);
                    logger.info("开始处理考勤组【{}】，考勤人员数：{}", group.getGroupName(), ""+trueList.size());
                    for (String person : trueList) {
                        ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(person, yesterdayString));
                    }
                }
            } else {
                logger.info("考勤组列表为空，无需处理！！！！！！！");
            }
        }
    }



    // 重新计算考勤组 考勤人员
    private List<String> calTruePersonFromMixList( Business business, String groupId, List<AttendanceV2Group> groups, List<String> participateList, List<String> unParticipateList) throws Exception {
        // 处理考勤组
        List<String> peopleList = new ArrayList<>();
        for (String p : participateList) {
            if (p.endsWith("@P")) {
                peopleList.add(p);
            } else if (p.endsWith("@I")) {
                String person = business.organization().person().getWithIdentity(p);
                peopleList.add(person);
            }else if (p.endsWith("@U")) { // 递归查询人员
                List<String> pList = business.organization().person().listWithUnitSubNested( p );
                peopleList.addAll(pList);
            } else {
                logger.info("错误的标识？ " + p);
            }
        }
        // 删除排除的人员
        if (unParticipateList != null && !unParticipateList.isEmpty()) {
            for (String p: unParticipateList) {
                peopleList.remove(p);
            }
        }
        // 去重复
        HashSet<String> peopleSet = new HashSet<>(peopleList);
        // 判断是否和其它考勤组内的成员冲突
        List<String> conflictPersonInOtherGroup = new ArrayList<>();
        if (groups != null && !groups.isEmpty()) {
            for (String person : peopleSet) {
                for (AttendanceV2Group oldG : groups) {
                    // 自己不用处理
                    if (oldG.getId().equals(groupId)) {
                        continue;
                    }
                    if (oldG.getTrueParticipantList().contains(person)) {
                        conflictPersonInOtherGroup.add(person);
                        break;
                    }
                }
            }
        }
        if (!conflictPersonInOtherGroup.isEmpty()) {
            throw new ExceptionParticipateConflict(conflictPersonInOtherGroup);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("最终考勤组人员数：" + peopleSet.size());
        }

        return new ArrayList<>(peopleSet);
    }
}
