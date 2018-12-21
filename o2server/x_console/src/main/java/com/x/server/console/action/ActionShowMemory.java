package com.x.server.console.action;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Date;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowMemory extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionShowMemory.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(Integer interval, Integer repeat) {
		try {
			this.init();
			MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
			final Integer interval_adjust = Math.min(Math.max(interval, 1), 20);
			final Integer repeat_repeat = Math.min(Math.max(repeat, 1), 200);
			new Thread() {
				public void run() {
					try {
						for (int i = 0; i < repeat_repeat; i++) {
							System.out.println("show memory heap used:" + bean.getHeapMemoryUsage().getUsed() / 1048576
									+ "m, max:" + bean.getHeapMemoryUsage().getMax() / 1048576 + "m, committed:"
									+ bean.getHeapMemoryUsage().getCommitted() / 1048576 + "m, non heap used:"
									+ bean.getNonHeapMemoryUsage().getUsed() / 1048576 + "m, max:"
									+ bean.getNonHeapMemoryUsage().getMax() / 1048576 + "m, committed:"
									+ bean.getNonHeapMemoryUsage().getCommitted() / 1048576 + "m.");
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