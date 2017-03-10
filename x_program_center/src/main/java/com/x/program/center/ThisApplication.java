package com.x.program.center;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.x.base.core.application.Applications;
import com.x.base.core.project.AbstractThisApplication;
import com.x.program.center.jaxrs.center.ActionReport;
import com.x.program.center.timertask.CheckServerTimerTask;
import com.x.program.center.timertask.ClearApplicationsTimerTask;

public class ThisApplication extends AbstractThisApplication {

	private static ScheduledExecutorService centerLocalscheduledExecutorService;

	public static void init() throws Exception {
		/* 启动报告任务 */
		// scheduler.scheduleWithFixedDelay(new ReportTask(), 1, 20,
		// TimeUnit.SECONDS);
		// timerWithFixedDelay(new ClearApplicationsTimerTask(),);
		// timerWithFixedDelay(new CheckServerTimerTask(), 5, 60 * 5);
		centerLocalscheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		centerLocalscheduledExecutorService.scheduleWithFixedDelay(new ClearApplicationsTimerTask(), 10, 30,
				TimeUnit.SECONDS);
		centerLocalscheduledExecutorService.scheduleWithFixedDelay(new CheckServerTimerTask(), 5, 60 * 5,
				TimeUnit.SECONDS);
		// dataMappings = new DataMappings(nodeConfigs);
		// storageMappings = new StorageMappings(nodeConfigs);
		applications = new Applications();
		ActionReport.start();
	}

	public static void destroy() {
		ActionReport.stop();
		centerLocalscheduledExecutorService.shutdownNow();
	}

}