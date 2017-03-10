package com.x.collaboration.assemble.websocket;

import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.collaboration.assemble.websocket.timer.CleanupConnectionsTimer;

public class ThisApplication extends AbstractThisApplication {

	public static final ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();

	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		timerWithFixedDelay(new CleanupConnectionsTimer(), 5, 60 * 30);
	}

	public static void destroy() throws Exception {

	}
}
