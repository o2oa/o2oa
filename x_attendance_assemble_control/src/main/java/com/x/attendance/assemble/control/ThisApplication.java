package com.x.attendance.assemble.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.AttendanceAppealInfoAction;
import com.x.attendance.assemble.control.service.AttendanceSettingService;
import com.x.attendance.assemble.control.task.AttendanceStatisticTask;
import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.server.Config;
import com.x.collaboration.core.message.Collaboration;

public class ThisApplication extends AbstractThisApplication {
	
	public static void init() throws Exception {
		/* 启动报告任务 */
		scheduleWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		initStoragesFromCenters();
		Config.workTimeConfig().initWorkTime();
		scheduleWithFixedDelay(new AttendanceStatisticTask(), 60 * 5, 60 * 10);
		Collaboration.start();
		initAllSystemConfig();
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
	}
	
	private static void initAllSystemConfig() {
		try {
			new AttendanceSettingService().initAllSystemConfig();
		} catch (Exception e) {
			LoggerFactory.getLogger( ThisApplication.class ).error( "attendance system check all system config got an exception.", e );
		}
	}
}
