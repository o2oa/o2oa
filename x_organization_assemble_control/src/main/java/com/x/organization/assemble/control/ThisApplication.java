package com.x.organization.assemble.control;

import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.organization.assemble.control.timertask.CheckRoleTask;

public class ThisApplication extends AbstractThisApplication {

	public static void init() throws Exception {
		/* 启动报告任务 */
		scheduleWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		schedule(new CheckRoleTask(), 5);
		// scheduleWithFixedDelay(new CheckRoleTask(), 2, 8);
	}

	public static void destroy() throws Exception {

	}

}
