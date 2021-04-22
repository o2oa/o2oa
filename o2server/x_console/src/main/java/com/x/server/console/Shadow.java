package com.x.server.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.DateTools;

public class Shadow {

	private static boolean tag = true;

	public static void main(String[] args) throws Exception {
		tag = true;
		File logFile = new File(Config.base(), "logs/" + DateTools.format(new Date(), "yyyy_MM_dd") + ".out.log");
		if (!logFile.exists()) {
			System.out.println("can not find log file,server not running.");
		} else {
			new Thread() {
				@Override
				public void run() {
					try (FileReader fr = new FileReader(logFile); BufferedReader br = new BufferedReader(fr)) {
						System.out.println("console start, type close to exit console.");
						br.skip(logFile.length());
						String line = null;
						while (tag) {
							if ((line = br.readLine()) != null) {
								System.out.println(line);
								continue;
							}
							try {
								Thread.sleep(2000L);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
				String cmd = "";
				while (tag) {
					cmd = reader.readLine();
					if ((null == cmd) || (StringUtils.equalsIgnoreCase("close", StringUtils.trim(cmd)))) {
						tag = false;
						System.exit(0);
					}
					try (RandomAccessFile raf = new RandomAccessFile(getBasePath() + "/command.swap", "rw")) {
						FileChannel fc = raf.getChannel();
						MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0, 256);
						FileLock flock = null;
						flock = fc.lock();
						mbb.put(cmd.getBytes());
						flock.release();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String getBasePath() throws Exception {
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
		throw new Exception("can not define o2server base directory.");
	}

}
