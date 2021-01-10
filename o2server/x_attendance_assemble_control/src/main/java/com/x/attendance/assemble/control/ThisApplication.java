package com.x.attendance.assemble.control;

import org.apache.commons.lang3.BooleanUtils;

import com.x.attendance.assemble.control.processor.monitor.MonitorFileDataOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.assemble.control.schedule.AttendanceStatisticTask;
import com.x.attendance.assemble.control.schedule.DetailLastDayRecordAnalyseTask;
import com.x.attendance.assemble.control.schedule.DingdingAttendanceSyncScheduleTask;
import com.x.attendance.assemble.control.schedule.MobileRecordAnalyseTask;
import com.x.attendance.assemble.control.schedule.QywxAttendanceSyncScheduleTask;
import com.x.attendance.assemble.control.service.AttendanceSettingService;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;

public class ThisApplication {

	private ThisApplication() {
		//nothing
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

	public static void init() throws Exception {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			LoggerFactory.setLevel(Config.logLevel().x_attendance_assemble_control());
			new AttendanceSettingService().initAllSystemConfig();
			context.startQueue(detailAnalyseQueue);
			context.startQueue(detailStatisticQueue);
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
			//context.schedule(MobileRecordAnalyseTask.class, "0 0 * * * ?");
			// 每天凌晨1点，计算前一天所有的未签退和未分析的打卡数据
			context.schedule(DetailLastDayRecordAnalyseTask.class, "0 0 1 * * ?");

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