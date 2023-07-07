package com.x.server.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class Shadow {

	private static final Logger LOGGER = LoggerFactory.getLogger(Shadow.class);

	public static void main(String... args) throws Exception {
		File logFile = new File(Config.base(), "logs/out.log");
		if (!logFile.exists()) {
			LOGGER.print("can not find log file,server not running.");
		} else {
			new Thread(() -> {
				LOGGER.print("console start, type close to exit console.");
				try (RandomAccessFile randomFile = new RandomAccessFile(logFile, "r")) {
					randomFile.seek(randomFile.length());
					String line = null;
					while (true) {
						if ((line = randomFile.readLine()) != null) {
							line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
							LOGGER.print(line);
							continue;
						}
						sleep();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, Shadow.class.getName()).start();
			readCommand();
		}
	}

	private static void readCommand() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String cmd = "";
			while (true) {
				cmd = reader.readLine();
				if ((null == cmd) || (StringUtils.equalsIgnoreCase("close", StringUtils.trim(cmd)))) {
					System.exit(0);
					break;
				}
				try (RandomAccessFile file = new RandomAccessFile(getBasePath() + "/command.swap", "rw");
						FileChannel fileChannel = file.getChannel()) {
					FileLock lock = fileChannel.lock();// 锁定整个文件
					file.setLength(0);
					file.write(cmd.getBytes(StandardCharsets.UTF_8));
					lock.release();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(1500L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static String getBasePath() {
		String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		if (!file.isDirectory()) {
			file = file.getParentFile();
		}
		while (null != file) {
			File versionFile = new File(file, "version.o2");
			if (versionFile.exists()) {
				return file.getAbsolutePath();
			}
			file = file.getParentFile();
		}
		throw new IllegalStateException("can not define o2server base directory.");
	}

}
