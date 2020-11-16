package com.x.server.console.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.server.console.server.Servers;

/**
 * @author
 */
public class HttpStatus extends Thread {

	private static Logger logger = LoggerFactory.getLogger(HttpStatus.class);

	private Integer repeat;

	public HttpStatus(Integer repeat) {
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
					File file = new File(Config.dir_logs(true),
							"centerServer_" + DateTools.compact(new Date()) + ".txt");
					list.add(String.format("  +++ center server thread pool size:%d, idle:%d, detail:%s.",
							Servers.centerServer.getThreadPool().getThreads(),
							Servers.centerServer.getThreadPool().getIdleThreads(), file.getAbsolutePath()));
					try (FileOutputStream stream = new FileOutputStream(file);
							OutputStreamWriter writer = new OutputStreamWriter(stream)) {
						Servers.centerServer.dump(writer);
					}
				}
				if (BooleanUtils.isTrue(Servers.applicationServerIsRunning())) {
					File file = new File(Config.dir_logs(true),
							"applicationServer_" + DateTools.compact(new Date()) + ".txt");
					list.add(String.format("  +++ application server thread pool size:%d, idle:%d, detail:%s.",
							Servers.applicationServer.getThreadPool().getThreads(),
							Servers.applicationServer.getThreadPool().getIdleThreads(), file.getAbsolutePath()));
					try (FileOutputStream stream = new FileOutputStream(file);
							OutputStreamWriter writer = new OutputStreamWriter(stream)) {
						Servers.applicationServer.dump(writer);
					}
				}
				if (BooleanUtils.isTrue(Servers.webServerIsRunning())) {
					File file = new File(Config.dir_logs(true), "webServer_" + DateTools.compact(new Date()) + ".txt");
					list.add(String.format("  +++ web server thread pool size:%d, idle:%d, detail:%s.",
							Servers.webServer.getThreadPool().getThreads(),
							Servers.webServer.getThreadPool().getIdleThreads(), file.getAbsolutePath()));
					try (FileOutputStream stream = new FileOutputStream(file);
							OutputStreamWriter writer = new OutputStreamWriter(stream)) {
						Servers.webServer.dump(writer);
					}
				}
				System.out.println(StringUtils.join(list, StringUtils.LF));
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}