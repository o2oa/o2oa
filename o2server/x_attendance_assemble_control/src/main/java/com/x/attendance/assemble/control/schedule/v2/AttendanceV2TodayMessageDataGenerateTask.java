package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 每天凌晨 3 点半，生成当天需要发送消息的数据
 * Created by fancyLou on 2023/5/8.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2TodayMessageDataGenerateTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2TodayMessageDataGenerateTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Date nowDate = new Date();
            logger.info("======================新版考勤消息生成定时器开始执行，日期：{}=============================", nowDate.toString());
            if (logger.isDebugEnabled()) {
                logger.debug("首先删除旧数据=====================7 天以前的数据");
            }
            Business business = new Business(emc);
            Date now = new Date();
            now = DateTools.addDay(now, -7);
            List<AttendanceV2AlertMessage> messageList = business.getAttendanceV2ManagerFactory().listAlertMessageBeforeDate(now);
            if (messageList != null && !messageList.isEmpty()) {
                List<String> ids = new ArrayList<>();
                for (int i = 0; i < messageList.size(); i++) {
                    ids.add(messageList.get(i).getId());
                }
                emc.beginTransaction(AttendanceV2AlertMessage.class);
                emc.delete(AttendanceV2AlertMessage.class, ids);
                emc.commit();
            }
            if (logger.isDebugEnabled()) {
                logger.debug(" 开始创建新的数据=====================");
            }
            List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
            AttendanceV2Config config;
            if (configs != null && !configs.isEmpty()) {
                config = configs.get(0);
            } else {
                config = new AttendanceV2Config();
            }
            // 启用消息
            if (!BooleanUtils.isTrue(config.getCheckInAlertEnable())) {
                return;
            }
            String today = DateTools.format(nowDate, DateTools.format_yyyyMMdd);
            if (logger.isDebugEnabled()) {
                logger.debug("日期：{}", today);
            }
            List<AttendanceV2Group> groups = emc.listAll(AttendanceV2Group.class);
            if (groups == null || groups.isEmpty()) {
                return;
            }
            for (AttendanceV2Group group : groups) {
                // 无效考勤组
                if (group.getStatus() == AttendanceV2Group.status_auto) {
                    continue;
                }
                if (!group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Fixed) && !group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Arrangement)) {
                    continue;
                }
                for (String person : group.getTrueParticipantList()) {
                    WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(person, today);
                    AttendanceV2Shift shift = woGroupShift.getShift();
                    if (shift == null) { // 没有班次对象 不需要发送消息
                        continue;
                    }
                    List<AttendanceV2ShiftCheckTime> timeList = Optional.ofNullable(shift.getProperties().getTimeList())
                            .orElse(Collections.emptyList());
                    if (timeList.isEmpty()) {
                        continue;
                    }
                    for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
                        // 上班提醒消息
                        saveMessage(emc, today, person, shiftCheckTime.getOnDutyTime(), AttendanceV2CheckInRecord.OnDuty, false);
                        // 下班提醒消息
                        saveMessage(emc, today, person, shiftCheckTime.getOffDutyTime(), AttendanceV2CheckInRecord.OffDuty, BooleanUtils.isTrue( shiftCheckTime.getOffDutyNextDay()));
                    }
                }
            }
        }
    }

    /**
     * 保存数据到数据库
     *
     * @param emc
     * @param today
     * @param person
     * @param dutyTimeString
     * @param dutyType
     * @throws Exception
     */
    private void saveMessage(EntityManagerContainer emc, String today, String person, String dutyTimeString, String dutyType, boolean offDutyNext) throws Exception {
        AttendanceV2AlertMessage messageOnDuty = new AttendanceV2AlertMessage();
        messageOnDuty.setUserId(person);
        String dutyTimeStringFull = today + " " + dutyTimeString;
        Date dutyTime = DateTools.parse(dutyTimeStringFull, DateTools.format_yyyyMMddHHmm);
        if (offDutyNext) { // 跨天
            dutyTime = DateTools.addDay(dutyTime, 1);
        }
        // 上班打卡 提前 10 分钟， 下班打卡按照打卡时间来。 后续个人配置中可调整时间
        if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
            dutyTime = DateTools.addMinutes(dutyTime, -10);
        }
        messageOnDuty.setCheckInType(dutyType);
        messageOnDuty.setSendDateTime(dutyTime);
        messageOnDuty.setSendStatus(false);
        emc.beginTransaction(AttendanceV2AlertMessage.class);
        emc.persist(messageOnDuty, CheckPersistType.all);
        emc.commit();
    }
}
