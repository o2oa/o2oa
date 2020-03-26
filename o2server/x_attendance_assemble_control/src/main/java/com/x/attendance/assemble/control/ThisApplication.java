package com.x.attendance.assemble.control;

import com.x.attendance.assemble.control.processor.monitor.MonitorFileDataOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.assemble.control.schedule.AttendanceStatisticTask;
import com.x.attendance.assemble.control.schedule.DingdingAttendanceSyncScheduleTask;
import com.x.attendance.assemble.control.schedule.MobileRecordAnalyseTask;
import com.x.attendance.assemble.control.schedule.QywxAttendanceSyncScheduleTask;
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

	public static void init() throws Exception {
		try {
			new AttendanceSettingService().initAllSystemConfig();
			context.schedule(AttendanceStatisticTask.class, "0 0 0/4 * * ?");
			context.schedule(MobileRecordAnalyseTask.class, "0 0/10 * * * ?");
			if (BooleanUtils.isTrue(Config.dingding().getAttendanceSyncEnable())) {
				dingdingQueue.start();
				context.schedule(DingdingAttendanceSyncScheduleTask.class, "0 0 1 * * ?");
			}
			if (BooleanUtils.isTrue(Config.qiyeweixin().getAttendanceSyncEnable())) {
				qywxQueue.start();
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
			qywxQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}