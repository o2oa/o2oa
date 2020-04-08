package com.x.server.console.action;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import com.x.server.console.server.Servers;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author
 */
public class ThreadStatus extends Thread {

	private Integer repeat;

	public ThreadStatus(Integer repeat) {
		this.repeat = repeat;
	}

	public void run() {
		try {
			ThreadMXBean bean = ManagementFactory.getThreadMXBean();
			for (int i = 0; i < repeat; i++) {
				List<String> list = new ArrayList<>();
				int deadLockedCount = bean.findDeadlockedThreads() == null ? 0 : bean.findDeadlockedThreads().length;
				list.add(String.format("thread total started:%d, count:%d, peak:%d, daemon:%d, dead:%d.",
						bean.getTotalStartedThreadCount(), bean.getThreadCount(), bean.getPeakThreadCount(),
						bean.getDaemonThreadCount(), deadLockedCount));
				if (BooleanUtils.isTrue(Servers.centerServerIsRunning())) {
					list.add(String.format("  +++ center server thread pool size:%d, idle:%d.",
							Servers.centerServer.getThreadPool().getThreads(),
							Servers.centerServer.getThreadPool().getIdleThreads()));
				}
				if (BooleanUtils.isTrue(Servers.applicationServerIsRunning())) {
					list.add(String.format("  +++ application server thread pool size:%d, idle:%d.",
							Servers.applicationServer.getThreadPool().getThreads(),
							Servers.applicationServer.getThreadPool().getIdleThreads()));
				}
				if (BooleanUtils.isTrue(Servers.webServerIsRunning())) {
					list.add(String.format("  +++ web server thread pool size:%d, idle:%d.",
							Servers.webServer.getThreadPool().getThreads(),
							Servers.webServer.getThreadPool().getIdleThreads()));
				}
				System.out.println(StringUtils.join(list, StringUtils.LF));
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}