package com.x.attendance.assemble.control;

import com.x.attendance.assemble.control.service.AttendanceSettingService;
import com.x.attendance.assemble.control.task.AttendanceStatisticTask;
import com.x.attendance.assemble.control.task.MobileRecordAnalyseTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.collaboration.core.message.Collaboration;

public class ThisApplication extends AbstractThisApplication {
	
	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		initStoragesFromCenters();
		timerWithFixedDelay(new AttendanceStatisticTask(), 60 * 20, 60 * 60 * 12 );
		timerWithFixedDelay( new MobileRecordAnalyseTask(), 60 * 30, 60 * 60 * 10 );
		Collaboration.start();
		initAllSystemConfig();
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
	}
	
	private static void initAllSystemConfig() {
		new AttendanceSettingService().initAllSystemConfig();
	}
}
