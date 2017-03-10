package com.x.file.assemble.control;

import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.collaboration.core.message.Collaboration;
import com.x.file.assemble.control.jaxrs.file.FileRemoveQueue;

public class ThisApplication extends AbstractThisApplication {

	public static FileRemoveQueue fileRemoveQueue;

	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		initStoragesFromCenters();
		Collaboration.start();
		fileRemoveQueue = new FileRemoveQueue();
		fileRemoveQueue.start();
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
		fileRemoveQueue.stop();
	}
}
