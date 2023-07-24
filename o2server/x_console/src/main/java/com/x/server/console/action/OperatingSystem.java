package com.x.server.console.action;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class OperatingSystem extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperatingSystem.class);

	private Integer count;

	public OperatingSystem(int count) {
		this.count = count;
	}

	@Override
	public void run() {
		OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		try {
			for (int i = 0; i < count; i++) {
				String msg = String.format(
						"cpu:%d, system load:%.2f, process load:%.2f. memory:%dm, free:%dm, committed virtual:%dm.",
						bean.getAvailableProcessors(), bean.getSystemCpuLoad(), bean.getProcessCpuLoad(),
						bean.getTotalPhysicalMemorySize() / (1024 * 1024),
						bean.getFreePhysicalMemorySize() / (1024 * 1024),
						bean.getCommittedVirtualMemorySize() / (1024 * 1024));
				LOGGER.print(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}