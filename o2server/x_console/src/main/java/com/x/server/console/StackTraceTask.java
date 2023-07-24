package com.x.server.console;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;

public class StackTraceTask implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(StackTraceTask.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			Date now = new Date();
			String pid = Files.readString(Paths.get(Config.base(), "pid.log"));
			String file = Config.dir_logs().getAbsolutePath() + "/jstack_" + Config.node() + "_"
					+ DateTools.format(now, DateTools.formatCompact_yyyyMMddHHmmss) + ".txt";
			String command = Config.command_jstack_path().toString() + " -l -e " + pid + " > " + file;
			java.lang.ProcessBuilder processBuilder = new java.lang.ProcessBuilder();
			if (SystemUtils.IS_OS_WINDOWS) {
				processBuilder.command("cmd", "/c", command);
			} else {
				processBuilder.command("sh", "-c", command);
			}
			Process p = processBuilder.start();
			String resp = IOUtils.toString(p.getErrorStream(), DefaultCharset.charset_utf_8);
			LOGGER.print("schedule stack trace to {}, {}.", file, resp);
			p.destroy();
			clean(now);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private void clean(Date date) throws Exception {
		final long millis = DateUtils.addDays(date, -1).getTime();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(Config.dir_logs().toPath(), p -> {
			String name = p.getFileName().toString();
			return name.startsWith("jstack_") && name.endsWith(".txt");
		})) {
			ds.forEach(p -> {
				try {
					BasicFileAttributes attributes = Files.readAttributes(p, BasicFileAttributes.class);
					if (attributes.creationTime().toMillis() < millis) {
						Files.delete(p);
					}
				} catch (IOException e) {
					LOGGER.error(e);
				}
			});
		} catch (IOException ex) {
			LOGGER.error(ex);
		}
	}
}