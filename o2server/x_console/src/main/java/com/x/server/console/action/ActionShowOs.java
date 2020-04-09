package com.x.server.console.action;

import java.lang.management.ManagementFactory;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;

import com.sun.management.OperatingSystemMXBean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowOs extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionShowOs.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(Integer interval, Integer repeat) {
		try {
			this.init();
			OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			final Integer interval_adjust = Math.min(Math.max(interval, 1), 20);
			final Integer repeat_repeat = Math.min(Math.max(repeat, 1), 200);
			new Thread() {
				public void run() {
					try {
						for (int i = 0; i < repeat_repeat; i++) {
							System.out.println("show os operating system processors:" + bean.getAvailableProcessors()
									+ ", total memory:" + bean.getTotalPhysicalMemorySize() / (1024 * 1024) + "m, free:"
									+ bean.getFreePhysicalMemorySize() / (1024 * 1024) + "m, committed virtual memory:"
									+ bean.getCommittedVirtualMemorySize() / (1024 * 1024) + "m.");
							Thread.sleep(interval_adjust * 1000);
						}
					} catch (Exception e) {
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

	private String percent(Double d) {
		return (d.intValue() * 100) + "%";
	}

}