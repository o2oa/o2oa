package com.x.program.center;

import com.x.base.core.application.Applications;
import com.x.base.core.project.AbstractThisApplication;
import com.x.program.center.jaxrs.center.ActionReport;
import com.x.program.center.timertask.CheckServerTimerTask;
import com.x.program.center.timertask.ClearApplicationsTimerTask;

public class ThisApplication extends AbstractThisApplication {

	public static void init() throws Exception {
		/* 启动报告任务 */
		// scheduler.scheduleWithFixedDelay(new ReportTask(), 1, 20,
		// TimeUnit.SECONDS);
		scheduleWithFixedDelay(new ClearApplicationsTimerTask(), 10, 30);
		scheduleWithFixedDelay(new CheckServerTimerTask(), 5, 60 * 5);
		// dataMappings = new DataMappings(nodeConfigs);
		// storageMappings = new StorageMappings(nodeConfigs);
		applications = new Applications();
		ActionReport.start();
	}

	public static void destroy() {
		ActionReport.stop();
	}

}