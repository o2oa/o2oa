package com.x.processplatform.assemble.bam;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.processplatform.assemble.bam.timer.PeriodTimer;
import com.x.processplatform.assemble.bam.timer.StateTimer;

public class ThisApplication extends AbstractThisApplication {

	private static Logger logger = LoggerFactory.getLogger(ThisApplication.class);

	public static State state = new State();
	public static Period period = new Period();

	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		timerWithFixedDelay(new PeriodTimer(), 180, 60 * 30);
		/* state 运行统计需要读取组织库,如果开始运行的时候组织应用还没启动那么会为空 */
		timerWithFixedDelay(new StateTimer(), 180, 60 * 60 * 1);
	}

	public static void destroy() throws Exception {

	}

}
