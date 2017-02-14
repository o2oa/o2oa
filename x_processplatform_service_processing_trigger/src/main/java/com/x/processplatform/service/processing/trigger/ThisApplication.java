package com.x.processplatform.service.processing.trigger;

import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.server.Config;

public class ThisApplication extends AbstractThisApplication {
	public static void init() throws Exception {
		/* 启动报告任务 */
		scheduleWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		Config.workTimeConfig().initWorkTime();
	}

	public static void destroy() throws Exception {

	}
}
