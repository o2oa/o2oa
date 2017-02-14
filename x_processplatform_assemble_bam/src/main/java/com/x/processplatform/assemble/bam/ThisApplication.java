package com.x.processplatform.assemble.bam;

import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.processplatform.assemble.bam.timer.PeriodTimer;
import com.x.processplatform.assemble.bam.timer.StateTimer;

public class ThisApplication extends AbstractThisApplication {

	public static State state = new State();
	public static Period period = new Period();

	public static void init() throws Exception {
		/* 启动报告任务 */
		scheduleWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		scheduleWithFixedDelay(new PeriodTimer(), 60, 60 * 30);
		/*state 运行统计需要读取组织库,如果开始运行的时候组织应用还没启动那么会为空*/
		scheduleWithFixedDelay(new StateTimer(), 60, 60 * 60 * 1);
	}

	public static void destroy() throws Exception {

	}

}
