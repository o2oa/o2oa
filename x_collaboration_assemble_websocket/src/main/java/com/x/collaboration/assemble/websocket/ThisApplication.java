package com.x.collaboration.assemble.websocket;

import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.server.Config;
import com.x.collaboration.assemble.websocket.timer.CleanConnectionsTimer;

public class ThisApplication extends AbstractThisApplication {

	public static final ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();

	public static void init() throws Exception {
		/* 启动报告任务 */
		scheduleWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		Config.workTimeConfig().initWorkTime();
		scheduleWithFixedDelay(new CleanConnectionsTimer(), 5, 60 * 30);
	}

	public static void destroy() throws Exception {

	}
}
