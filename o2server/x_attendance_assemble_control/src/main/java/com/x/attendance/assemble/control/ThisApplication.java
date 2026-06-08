package com.x.attendance.assemble.control;

import com.x.base.core.project.message.MessageConnector;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.schedule.v2.AttendanceV2DetailGenerateTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2HolidaySyncTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2LeaveLedgerExpireTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2LeavePolicyGrantTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2MessageSendTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2TodayMessageDataGenerateTask;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2Detail;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2LeavePolicyGrant;
import com.x.attendance.assemble.control.service.v2.AttendanceV2LeaveManagerService;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ThisApplication {


  private static final Logger LOGGER = LoggerFactory.getLogger(ThisApplication.class);


    private ThisApplication() {
        // nothing
    }

    protected static Context context;

    public static Context context() {
        return context;
    }
 
    public static final String ROLE_AttendanceManager = "AttendanceManager@AttendanceManagerSystemRole@R";

    // V2
    public static final QueueAttendanceV2Detail queueV2Detail = new QueueAttendanceV2Detail();
    public static final QueueAttendanceV2LeavePolicyGrant queueV2LeavePolicyGrant = new QueueAttendanceV2LeavePolicyGrant();

    public static void init() throws Exception {
        try {
            CacheManager.init(context.clazz().getSimpleName());
            MessageConnector.start(context());
            /////////////////// V2///
            // 处理考勤统计相关的队列
            context.startQueue(queueV2Detail);
            context.startQueue(queueV2LeavePolicyGrant);
            // 配置对象 考勤统计定时器可配置
            AttendanceV2Config config = null; 
            String cronString = null;
            boolean leaveTypeInitialized = false; // 考勤假勤类型的默认数据是否已经初始化
            boolean chineseHolidaySyncEnable = true; // 是否开启中国节假日数据同步
            try  {
                EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
                List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
                if (configs != null && !configs.isEmpty()) {
                    config = configs.get(0);
                }
                if (config != null) {
                    cronString = config.getDetailStatisticCronString();
                    leaveTypeInitialized = config.getProperties() != null && BooleanUtils.isTrue(config.getProperties().getLeaveTypeInitialized());
                    chineseHolidaySyncEnable = !(config.getProperties() != null && BooleanUtils.isFalse(config.getProperties().getChineseHolidaySyncEnable()));
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            if (StringUtils.isEmpty(cronString)) {
                cronString = "0 0 3 * * ?";
            }
            // 初始化考勤假勤类型的默认数据
            if (!leaveTypeInitialized){
                new AttendanceV2LeaveManagerService().initDefaultLeaveTypeData();
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("定时表达式 {}", cronString);
            }
            // 每天凌晨3点，计算前一天的考勤数据
            context.schedule(AttendanceV2DetailGenerateTask.class, cronString);
            // 每天凌晨 3 点半，重新计算当天要发送消息的数据。
            context.schedule(AttendanceV2TodayMessageDataGenerateTask.class, "0 30 3 * * ?");
            // 4点钟开始 每 5 分钟检查 发送考勤相关消息的任务
            context.schedule(AttendanceV2MessageSendTask.class, "0 0/5 4-23 * * ?");
            // 每天凌晨 2 点，开启假期数据发放任务。
            context.schedule(AttendanceV2LeavePolicyGrantTask.class, "0 0 2 * * ?");
            // 每天凌晨 2 点半，处理过期的假期额度批次。
            context.schedule(AttendanceV2LeaveLedgerExpireTask.class, "0 30 2 * * ?");
            // 每天凌晨 1 点，同步中国节假日数据。
            if (chineseHolidaySyncEnable) {
                context.schedule(AttendanceV2HolidaySyncTask.class, "0 0 1 * * ?");
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static void destroy() {
        try {
            CacheManager.shutdown();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
