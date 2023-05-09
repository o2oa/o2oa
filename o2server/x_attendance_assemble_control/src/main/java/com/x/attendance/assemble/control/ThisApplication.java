package com.x.attendance.assemble.control;

import com.x.attendance.assemble.control.schedule.v2.AttendanceV2DetailGenerateTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2MessageSendTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2TodayMessageDataGenerateTask;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2Detail;
import org.apache.commons.lang3.BooleanUtils;

import com.x.attendance.assemble.control.processor.monitor.MonitorFileDataOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.assemble.control.schedule.AttendanceStatisticTask;
import com.x.attendance.assemble.control.schedule.DetailLastDayRecordAnalyseTask;
import com.x.attendance.assemble.control.schedule.DingdingAttendanceSyncScheduleTask;
import com.x.attendance.assemble.control.schedule.QywxAttendanceSyncScheduleTask;
import com.x.attendance.assemble.control.service.AttendanceSettingService;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.message.MessageConnector;

public class ThisApplication {

    private ThisApplication() {
        // nothing
    }

    protected static Context context;

    public static Context context() {
        return context;
    }

    public static final QueueDingdingAttendance dingdingQueue = new QueueDingdingAttendance();
    public static final QueueQywxAttendanceSync qywxQueue = new QueueQywxAttendanceSync();
    public static final QueueQywxUnitStatistic unitQywxStatisticQueue = new QueueQywxUnitStatistic();
    public static final QueueQywxPersonStatistic personQywxStatisticQueue = new QueueQywxPersonStatistic();
    public static final QueueDingdingPersonStatistic personStatisticQueue = new QueueDingdingPersonStatistic();
    public static final QueueDingdingUnitStatistic unitStatisticQueue = new QueueDingdingUnitStatistic();

    public static final QueuePersonAttendanceDetailAnalyse detailAnalyseQueue = new QueuePersonAttendanceDetailAnalyse();
    public static final QueueAttendanceDetailStatistic detailStatisticQueue = new QueueAttendanceDetailStatistic();
    public static final String ROLE_AttendanceManager = "AttendanceManager@AttendanceManagerSystemRole@R";

    // V2
    public static final QueueAttendanceV2Detail queueV2Detail = new QueueAttendanceV2Detail();

    public static void init() throws Exception {
        try {
            CacheManager.init(context.clazz().getSimpleName());
            new AttendanceSettingService().initAllSystemConfig();
            context.startQueue(detailAnalyseQueue);
            context.startQueue(detailStatisticQueue);
            MessageConnector.start(context());
            if (BooleanUtils.isTrue(Config.dingding().getAttendanceSyncEnable())) {
                context.startQueue(dingdingQueue);
                context.startQueue(personStatisticQueue);
                context.startQueue(unitStatisticQueue);
                context.schedule(DingdingAttendanceSyncScheduleTask.class, "0 0 1 * * ?");
                // 已经将任务 放到了同步结束后执行 暂时不需要开定时任务了
            }
            if (BooleanUtils.isTrue(Config.qiyeweixin().getAttendanceSyncEnable())) {
                context.startQueue(qywxQueue);
                context.startQueue(unitQywxStatisticQueue);
                context.startQueue(personQywxStatisticQueue);
                context.schedule(QywxAttendanceSyncScheduleTask.class, "0 0 1 * * ?");
            }
            context.schedule(AttendanceStatisticTask.class, "0 0 0/4 * * ?");
            // context.schedule(MobileRecordAnalyseTask.class, "0 0 * * * ?");
            // 每天凌晨1点，计算前一天所有的未签退和未分析的打卡数据
            context.schedule(DetailLastDayRecordAnalyseTask.class, "0 0 1 * * ?");

            /////////////////// V2///
            // 处理考勤统计相关的队列
            context.startQueue(queueV2Detail);
            // 每天凌晨3点，计算前一天的考勤数据
            context.schedule(AttendanceV2DetailGenerateTask.class, "0 0 3 * * ?");
            // 每天凌晨 3 点半，重新计算当前要发送消息的数据。
            context.schedule(AttendanceV2TodayMessageDataGenerateTask.class, "0 30 3 * * ?");
            // 4点钟开始 每 5 分钟检查 发送考勤相关消息的任务
            context.schedule(AttendanceV2MessageSendTask.class, "0 0/5 4-23 * * ?");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void destroy() {
        try {
            CacheManager.shutdown();
            DataProcessThreadFactory.getInstance().showdown();
            MonitorFileDataOpt.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}