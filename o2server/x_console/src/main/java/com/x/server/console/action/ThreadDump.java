package com.x.server.console.action;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;

/**
 * 
 * @author ray
 *
 */
public class ThreadDump {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadDump.class);

	public void execute() {
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
			LOGGER.print("thread dump to {}.{}", file, resp);
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}