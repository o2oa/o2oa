package com.x.server.console.action;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;

import org.apache.commons.lang3.StringUtils;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

public class ActionHeapDump extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionHeapDump.class);

	private Date start;

	private Date end;
	// This is the name of the HotSpot Diagnostic MBean
	private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
	// field to store the hotspot diagnostic MBean
	private static volatile HotSpotDiagnosticMXBean hotspotMBean;

	public boolean execute(String password) {
		try {
			if (!StringUtils.equals(Config.token().getPassword(), password)) {
				logger.print("password not match.");
				return false;
			}
			start = new Date();
			File file = new File(Config.dir_logs(),
					"heapDump_" + DateTools.format(start, DateTools.formatCompact_yyyyMMddHHmmss) + ".hprof");
			dumpHeap(file.getAbsolutePath());
			end = new Date();

			logger.print(
					"generate java heap dump to {}, elapsed: {}ms, parses file see url: https://docs.oracle.com/javase/6/docs/technotes/tools/share/jhat.html",
					file.getAbsoluteFile(), end.getTime() - start.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	static void dumpHeap(String fileName) {
		// initialize hotspot diagnostic MBean
		initHotspotMBean();
		try {
			hotspotMBean.dumpHeap(fileName, true);
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception exp) {
			throw new RuntimeException(exp);
		}
	}

	// initialize the hotspot diagnostic MBean field
	private static void initHotspotMBean() {
		if (hotspotMBean == null) {
			synchronized (ActionHeapDump.class) {
				if (hotspotMBean == null) {
					hotspotMBean = getHotspotMBean();
				}
			}
		}
	}

	// get the hotspot diagnostic MBean from the
	// platform MBean server
	private static HotSpotDiagnosticMXBean getHotspotMBean() {
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server, HOTSPOT_BEAN_NAME,
					HotSpotDiagnosticMXBean.class);
			return bean;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception exp) {
			throw new RuntimeException(exp);
		}
	}

}