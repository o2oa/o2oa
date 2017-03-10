package com.x.organization.assemble.personal;

import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;

public class ThisApplication extends AbstractThisApplication {
	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
	}

	public static void destroy() throws Exception {

	}

	public static final int passwordStrengthLevel = 4;
}
