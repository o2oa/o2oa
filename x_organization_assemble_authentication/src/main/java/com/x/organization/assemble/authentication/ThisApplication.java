package com.x.organization.assemble.authentication;

import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.organization.assemble.authentication.schedule.CleanupBind;

public class ThisApplication extends AbstractThisApplication {

	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		schedule(new CleanupBind(), 90, 600);
	}

	public static void destroy() throws Exception {

	}

}
