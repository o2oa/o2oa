package com.x.collect.service.transmit;

import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.collect.service.transmit.task.CollectTask;

public class ThisApplication extends AbstractThisApplication {
	public static void init() throws Exception {
		/* 启动报告任务 */
		scheduleWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		scheduleWithFixedDelay(new CollectTask(), 60, 60 * 5);
	}

	public static void destroy() throws Exception {

	}
}
