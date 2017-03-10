package com.x.organization.assemble.control;

import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.organization.assemble.control.timer.CheckRoleTask;

public class ThisApplication extends AbstractThisApplication {

	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		timer(new CheckRoleTask(), 5);
		// scheduleWithFixedDelay(new CheckRoleTask(), 2, 8);
	}

	public static void destroy() throws Exception {

	}

}
