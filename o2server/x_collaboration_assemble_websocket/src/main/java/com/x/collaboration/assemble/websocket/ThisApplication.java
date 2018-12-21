package com.x.collaboration.assemble.websocket;

import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.collaboration.assemble.websocket.timer.CleanupConnectionsTimer;

public class ThisApplication {

	protected static Context context;

	public static final ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_collaboration_assemble_websocket());
			context.scheduleLocal(CleanupConnectionsTimer.class, 5, 60 * 30);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}