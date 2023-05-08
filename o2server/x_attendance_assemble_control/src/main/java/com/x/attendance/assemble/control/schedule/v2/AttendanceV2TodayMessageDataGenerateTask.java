package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 每天凌晨 3 点半，生成当前需要发送消息的数据
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
                logger.debug("首先删除旧数据=====================");
            }
            emc.beginTransaction(AttendanceV2AlertMessage.class);
            List<AttendanceV2AlertMessage> list = emc.listAll(AttendanceV2AlertMessage.class);
            if (list != null && !list.isEmpty()) {
                for (AttendanceV2AlertMessage attendanceV2AlertMessage : list) {
                    emc.remove(attendanceV2AlertMessage);
                }
            }
            emc.commit();
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
                // 固定班制 其他班次暂时不需要消息 添加个人配置后再扩展
                if (!group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Fixed)) {
                    continue;
                }
                // 正常的班次id
                String shiftId = Optional.ofNullable(group.getWorkDateProperties().shiftIdWithDate(nowDate))
                        .orElseGet(() -> {
                            try {
                                // 是否特殊工作日
                                return AttendanceV2Helper.specialWorkDayShift(today, group);
                            } catch (Exception e) {
                                logger.error(e);
                                return null;
                            }
                        });
                if (StringUtils.isEmpty(shiftId) || AttendanceV2Helper.isSpecialRestDay(today, group)) {
                    continue;
                }

                AttendanceV2Shift shift = emc.find(shiftId, AttendanceV2Shift.class);
                if (shift == null) { // 有班次对象
                    continue;
                }
                List<AttendanceV2ShiftCheckTime> timeList = Optional.ofNullable(shift.getProperties().getTimeList())
                        .orElse(Collections.emptyList());
                if (timeList.isEmpty()) {
                    continue;
                }
                logger.info("开始生成消息数据，group: {} 的每个打卡成员生成对应的班次的上下班打卡提醒对象 ", group.getGroupName());
                // 条件满足了 根据人员生成消息数据
                for (String person : group.getTrueParticipantList()) {
                    for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
                        // 上班提醒消息
                        saveMessage(emc, today, person, shiftCheckTime.getOnDutyTime(), AttendanceV2CheckInRecord.OnDuty);
                        // 下班提醒消息
                        saveMessage(emc, today, person, shiftCheckTime.getOffDutyTime(), AttendanceV2CheckInRecord.OffDuty);
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
    private void saveMessage(EntityManagerContainer emc, String today, String person, String dutyTimeString, String dutyType) throws Exception {
        AttendanceV2AlertMessage messageOnDuty = new AttendanceV2AlertMessage();
        messageOnDuty.setUserId(person);
        String dutyTimeStringFull = today + " " + dutyTimeString;
        Date dutyTime = DateTools.parse(dutyTimeStringFull, DateTools.format_yyyyMMddHHmm);
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
