package com.x.attendance.assemble.control;

import com.x.attendance.assemble.control.processor.monitor.MonitorFileDataOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.assemble.control.schedule.*;
import com.x.attendance.assemble.control.service.AttendanceSettingService;
import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import org.apache.commons.lang3.BooleanUtils;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static DingdingAttendanceQueue dingdingQueue = new DingdingAttendanceQueue();
	public static QywxAttendanceSyncQueue qywxQueue = new QywxAttendanceSyncQueue();
	public static QywxUnitStatisticQueue unitQywxStatisticQueue = new QywxUnitStatisticQueue();
	public static QywxPersonStatisticQueue personQywxStatisticQueue = new QywxPersonStatisticQueue();

	public static DingdingPersonStatisticQueue personStatisticQueue = new DingdingPersonStatisticQueue();
	public static DingdingUnitStatisticQueue unitStatisticQueue = new DingdingUnitStatisticQueue();


	public static void init() throws Exception {
		try {
			new AttendanceSettingService().initAllSystemConfig();
			context.schedule(AttendanceStatisticTask.class, "0 0 0/4 * * ?");
			context.schedule(MobileRecordAnalyseTask.class, "0 0/10 * * * ?");
			if (BooleanUtils.isTrue(Config.dingding().getAttendanceSyncEnable())) {
				dingdingQueue.start();
				personStatisticQueue.start();
				unitStatisticQueue.start();
				context.schedule(DingdingAttendanceSyncScheduleTask.class, "0 0 1 * * ?");
				//已经将任务 放到了同步结束后执行 暂时不需要开定时任务了
//				context.schedule(DingdingAttendanceStatisticScheduleTask.class, "0 0 3 * * ?");
//				context.schedule(DingdingAttendanceStatisticPersonScheduleTask.class, "0 0 3 * * ?");
			}
			if (BooleanUtils.isTrue(Config.qiyeweixin().getAttendanceSyncEnable())) {
				qywxQueue.start();
				unitQywxStatisticQueue.start();
				personQywxStatisticQueue.start();
				context.schedule(QywxAttendanceSyncScheduleTask.class, "0 0 1 * * ?");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			DataProcessThreadFactory.getInstance().showdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			MonitorFileDataOpt.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			dingdingQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			personStatisticQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			unitStatisticQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			qywxQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			unitQywxStatisticQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			personQywxStatisticQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}