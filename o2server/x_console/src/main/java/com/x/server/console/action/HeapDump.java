package com.x.server.console.action;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

public class HeapDump {

	private static Logger logger = LoggerFactory.getLogger(HeapDump.class);

	private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

	public void execute() throws Exception {
		try {
			Date start = new Date();
			File file = new File(Config.dir_logs(),
					"heapDump_" + DateTools.format(start, DateTools.formatCompact_yyyyMMddHHmmss) + ".hprof");
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server, HOTSPOT_BEAN_NAME,
					HotSpotDiagnosticMXBean.class);
			bean.dumpHeap(file.getAbsolutePath(), true);
			logger.print(
					"generate java heap dump to {}, elapsed: {}ms, parses file see url: https://docs.oracle.com/javase/6/docs/technotes/tools/share/jhat.html",
					file.getAbsoluteFile(), System.currentTimeMillis() - start.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}