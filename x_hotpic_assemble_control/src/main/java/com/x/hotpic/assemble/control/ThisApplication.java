package com.x.hotpic.assemble.control;

import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.collaboration.core.message.Collaboration;
import com.x.hotpic.assemble.control.timertask.InfoExistsCheckTask;

public class ThisApplication extends AbstractThisApplication {

	public static void init() throws Exception {
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		initStoragesFromCenters();
		Collaboration.start();
		initTimertasks();
	}

	private static void initTimertasks() throws Exception {
		timerWithFixedDelay( new InfoExistsCheckTask(), 60 * 10, 60 * 60 );
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
	}
}
