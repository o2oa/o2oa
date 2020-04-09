package com.x.server.console.action;

import java.lang.management.ManagementFactory;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;

import com.sun.management.OperatingSystemMXBean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowCpu extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionShowCpu.class);

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
							System.out.println("show cpu process cpu load:" + percent(bean.getProcessCpuLoad())
									+ ", system cpu load:" + percent(bean.getSystemCpuLoad()) + ".");
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

	private String percent(Double d) {
		return new Double((d * 100)).intValue() + "%";
	}

}