package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考勤相关提醒消息发送任务
 * <p>
 * Created by fancyLou on 2023/5/8.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2MessageSendTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2MessageSendTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤消息发送定时器开始执行==============================");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            AttendanceV2Config config;
            if (list != null && !list.isEmpty()) {
                config = list.get(0);
            } else {
                config = new AttendanceV2Config();
            }
            // 异常数据每天提醒
            exceptionMsgAlert(emc, config);
            // 打卡消息提醒
            alertCheckinMessage(emc, config);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤消息发送定时器执行完成==============================");
        }
    }

    /**
     * 打卡提醒 消息发送
     *
     * @param emc
     */
    private void alertCheckinMessage(EntityManagerContainer emc, AttendanceV2Config config) {
        // 启用消息
        if (!BooleanUtils.isTrue(config.getCheckInAlertEnable())) {
            return;
        }
        try {
            Business business = new Business(emc);
            Date now = new Date();
            List<AttendanceV2AlertMessage> messageList = business.getAttendanceV2ManagerFactory().listAlertMessageBeforeDate(now);
            if (messageList == null || messageList.isEmpty()) {
                return;
            }
            for (AttendanceV2AlertMessage message : messageList) {
                String title;
                if (AttendanceV2CheckInRecord.OnDuty.equals(message.getCheckInType())) {
                    title = "即将开始上班，请别忘记打卡哦！";
                } else {
                    title = "已经下班啦，请别忘记打卡哦！";
                }
                MessageConnector.send(MessageConnector.TYPE_ATTENDANCE_CHECK_IN_ALERT, title, message.getUserId(), message);
                AttendanceV2AlertMessage old = emc.find(message.getId(), AttendanceV2AlertMessage.class);
                old.setSendStatus(true); // 已经发送
                emc.beginTransaction(AttendanceV2AlertMessage.class);
                emc.persist(old, CheckPersistType.all);
                emc.commit();
            }
        }catch (Exception e) {
            logger.error(e);
        }

    }

    /**
     * 异常数据提醒
     *
     * @param emc
     */
    private void exceptionMsgAlert(EntityManagerContainer emc, AttendanceV2Config config) {
        try {
            // 异常消息提醒
            if (!BooleanUtils.isTrue(config.getAppealEnable()) || !BooleanUtils.isTrue(config.getExceptionAlertEnable())) {
                return;
            }
            String date = config.getExceptionAlertDate();
            String time = config.getExceptionAlertTime();
            if (StringUtils.isEmpty(time)) {
                time = "09:30"; // 默认 9 点半 执行
            }
            Date now = new Date();
            String today = DateTools.format(now, DateTools.format_yyyyMMdd);
            Date alertTime = DateTools.parse(today + " " + time, DateTools.format_yyyyMMddHHmm);
            if (today.equals(date) || alertTime.after(now)) { // 今天已经提醒过了 或者 时间没到
                return;
            }
            logger.info("开始发送异常数据提醒，当前时间：{} , 异常数据提醒时间：{}", now.toString(), alertTime.toString());
            sendExceptionMsgAlert(emc);
            // 更新配置 today 表示已经发送过提醒
            config.setExceptionAlertDate(today);
            emc.beginTransaction(AttendanceV2Config.class);
            emc.persist(config, CheckPersistType.all);
            emc.commit();
        } catch (Exception e) {
            logger.error(e);
        }
    }


    /**
     * 发送异常数据提醒
     */
    private void sendExceptionMsgAlert(EntityManagerContainer emc) {
        try {
            Business business = new Business(emc);
            // 昨天产生的数据 今天发送消息
            Date yesterday = DateTools.addDay(new Date(), -1);
            String yesterdayString = DateTools.format(yesterday, DateTools.format_yyyyMMdd);
            List<AttendanceV2AppealInfo> list = business.getAttendanceV2ManagerFactory().listAppealInfoWithRecordDateString(yesterdayString);
            if (list == null || list.isEmpty()) {
                return;
            }
            Map<String, List<AttendanceV2AppealInfo>> map = list.stream().collect(Collectors.groupingBy(AttendanceV2AppealInfo::getUserId));
            for (Map.Entry<String, List<AttendanceV2AppealInfo>> entry : map.entrySet()) {
                List<AttendanceV2AppealInfo> appealInfoList = entry.getValue();
                if (appealInfoList != null && !appealInfoList.isEmpty()) {
                    String title = "您有" + appealInfoList.size() + "条异常打卡数据产生，请及时处理！";
                    MessageConnector.send(MessageConnector.TYPE_ATTENDANCE_CHECK_IN_EXCEPTION, title, entry.getKey(), appealInfoList.get(0));
                    if (logger.isDebugEnabled()) {
                        logger.debug("发送异常数据提醒消息，person: {}", entry.getKey());
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }
}
