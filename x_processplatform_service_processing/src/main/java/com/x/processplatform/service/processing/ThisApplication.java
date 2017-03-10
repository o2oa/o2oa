package com.x.processplatform.service.processing;

import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.base.core.project.server.Config;
import com.x.collaboration.core.message.Collaboration;

public class ThisApplication extends AbstractThisApplication {
	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		initStoragesFromCenters();
		ScriptHelperFactory.initialScriptText = Config.initialScriptText();
		Collaboration.start();
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
	}

}
