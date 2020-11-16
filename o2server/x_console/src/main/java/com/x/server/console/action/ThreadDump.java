package com.x.server.console.action;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Path;
import java.util.Date;

import org.apache.commons.lang3.SystemUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

public class ThreadDump {

	private static Logger logger = LoggerFactory.getLogger(ThreadDump.class);

	public void execute(Integer count) throws Exception {
		new Thread() {
			public void run() {
				try {
					RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
					String jvmName = runtimeBean.getName();
					long pid = Long.parseLong(jvmName.split("@")[0]);
					Path path = SystemUtils.getJavaHome().toPath();
					path = path.getParent().resolve("bin/jcmd").toAbsolutePath();
					for (int i = 0; i < count; i++) {
						Path out = Config.dir_logs(true).toPath()
								.resolve("jcmd_Thread.print_" + pid + "_"
										+ DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".txt")
								.toAbsolutePath();
						String cmd = path.toString() + " " + pid + " Thread.print >" + out.toString();
						ProcessBuilder processBuilder = new ProcessBuilder();
						if (SystemUtils.IS_OS_WINDOWS) {
							processBuilder.command("cmd", "/c", cmd);
						} else {
							processBuilder.command("sh", "-c", cmd);
						}
						processBuilder.start();
						logger.print("thread dump to file:{}.", out.toString());
						Thread.sleep(1000);
					}
					logger.print("{} thread dump completed.", count);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}