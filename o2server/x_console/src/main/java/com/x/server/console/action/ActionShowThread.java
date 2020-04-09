package com.x.server.console.action;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Date;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowThread extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionShowThread.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(Integer interval, Integer repeat) {
		try {
			this.init();
			ThreadMXBean bean = ManagementFactory.getThreadMXBean();
			final Integer interval_adjust = Math.min(Math.max(interval, 1), 20);
			final Integer repeat_repeat = Math.min(Math.max(repeat, 1), 200);
			new Thread() {
				public void run() {
					try {
						for (int i = 0; i < repeat_repeat; i++) {
							int deadLockedCount = bean.findDeadlockedThreads() == null ? 0
									: bean.findDeadlockedThreads().length;
							System.out.println("show thread total started:" + bean.getTotalStartedThreadCount()
									+ ", count:" + bean.getThreadCount() + ", dead:" + deadLockedCount + ", daemon:"
									+ bean.getDaemonThreadCount() + ", peak:" + bean.getPeakThreadCount() + ".");
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

}