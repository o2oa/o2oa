package com.x.message.assemble.communicate;

import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import com.x.base.core.project.Context;

public class ThisApplication {

	protected static Context context;

	public static final ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
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
