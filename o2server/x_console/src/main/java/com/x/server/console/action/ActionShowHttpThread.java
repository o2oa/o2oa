package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.server.Servers;

public class ActionShowHttpThread extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionShowHttpThread.class);

	public boolean execute(Integer interval, Integer repeat) {
		try {
			final Integer interval_adjust = Math.min(Math.max(interval, 1), 20);
			final Integer repeat_repeat = Math.min(Math.max(repeat, 1), 200);
			new Thread() {
				public void run() {
					try {
						for (int i = 0; i < repeat_repeat; i++) {
							show();
							Thread.sleep(interval_adjust * 1000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void show() {
		StringBuffer sb = new StringBuffer();
		if (Servers.centerServerIsRunning()) {
			sb.append("center server:");
			sb.append(String.format("thread pool size:%d, idle:%d. ", Servers.centerServer.getThreadPool().getThreads(),
					Servers.centerServer.getThreadPool().getIdleThreads()));
		} else {
			sb.append("is not running. ");
		}
		if (Servers.applicationServerIsRunning()) {
			sb.append("application server:");
			sb.append(String.format("thread pool size:%d, idle:%d. ",
					Servers.applicationServer.getThreadPool().getThreads(),
					Servers.applicationServer.getThreadPool().getIdleThreads()));
		} else {
			sb.append("is not running. ");
		}
		if (Servers.webServerIsRunning()) {
			sb.append("web server:");
			sb.append(String.format("thread pool size:%d, idle:%d. ", Servers.webServer.getThreadPool().getThreads(),
					Servers.webServer.getThreadPool().getIdleThreads()));
		} else {
			sb.append("is not running. ");
		}
		System.out.println(sb.toString());
	}

}