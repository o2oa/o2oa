package com.x.server.console.action;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

/**
 * 
 * @author ray
 *
 */
public class ThreadDump {

	private static Logger logger = LoggerFactory.getLogger(ThreadDump.class);

	public void execute(Integer count) {

		ExecutorService service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
				new BasicThreadFactory.Builder().namingPattern(ThreadDump.class.getName()).daemon(true).build());

		service.execute(() -> {
			try {
				RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
				String jvmName = runtimeBean.getName();
				long pid = Long.parseLong(jvmName.split("@")[0]);
				Path path = SystemUtils.getJavaHome().toPath();
				if (null != path) {
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
				}
			} catch (InterruptedException e) {
				logger.error(e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}
}