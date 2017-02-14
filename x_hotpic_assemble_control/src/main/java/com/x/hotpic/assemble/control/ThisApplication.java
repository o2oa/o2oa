package com.x.hotpic.assemble.control;

import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.collaboration.core.message.Collaboration;
import com.x.hotpic.assemble.control.timertask.InfoExistsCheckTask;

public class ThisApplication extends AbstractThisApplication {

	public static void init() throws Exception {
		scheduleWithFixedDelay( new ReportTask(), 1, 20 );
		initDatasFromCenters();
		initStoragesFromCenters();
		Collaboration.start();
		initTimertasks();
	}
	
	private static void initTimertasks() {
		scheduleWithFixedDelay( new InfoExistsCheckTask(), 60 * 2, 60 * 10 );
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
	}
}
