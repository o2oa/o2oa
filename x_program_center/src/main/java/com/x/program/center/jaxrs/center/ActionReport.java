package com.x.program.center.jaxrs.center;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.application.Application;
import com.x.base.core.project.Report;
import com.x.base.core.project.server.ApplicationServer;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;
import com.x.program.center.ThisApplication;

public class ActionReport {

	public static ActionReport INSTANCE;

	private LinkedBlockingQueue<Report> queue;

	private ActionReport() {
		this.queue = new LinkedBlockingQueue<>();
		ExecuteThread executeThread = new ExecuteThread();
		executeThread.start();
	}

	public static void send(Report o) throws Exception {
		INSTANCE.queue.put(o);
	}

	public static void start() {
		if (INSTANCE == null) {
			synchronized (ActionReport.class) {
				if (INSTANCE == null) {
					INSTANCE = new ActionReport();
				}
			}
		}
	}

	public static void stop() {
		try {
			if (INSTANCE != null) {
				INSTANCE.queue.put(INSTANCE.new StopSignal());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ExecuteThread extends Thread {
		public void run() {
			while (true) {
				try {
					Report o = queue.take();
					if (o instanceof StopSignal) {
						break;
					}
					execute(o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class StopSignal extends Report {

	}

	private void execute(Report report) {
		try {
			Class<?> clz = Class.forName(report.getClassName());
			Application application = ThisApplication.applications.get(clz, report.getToken());
			if (null != application) {
				application.setReportDate(new Date());
			} else {
				ApplicationServer applicationServer = Config.nodes().applicationServers().get(report.getNode());
				application = new Application();
				application.setHost(report.getNode());
				application.setPort(applicationServer.getPort());
				application.setContext("/" + clz.getSimpleName());
				application.setToken(report.getToken());
				application.setWeight((null == report.getWeight()) ? 100 : report.getWeight());
				application.setReportDate(new Date());
				application.setProxyPort(applicationServer.getProxyPort());
				application.setProxyHost(applicationServer.getProxyHost());
				ThisApplication.applications.add(clz, application);
			}
			ThisApplication.applications.setToken(StringTools.uniqueToken());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
